package iva.client.web.repositories;

import iva.client.core.model.Entity;
import iva.client.web.model.CollectionResource;
import iva.client.web.model.Resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public abstract class RestRepository<T extends Entity> implements CrudRepository<T, URI> {

	protected static final String baseUri = "http://localhost:8080";
	
	protected static final RestTemplate rest = new RestTemplate();
	
	protected final String entityRel;
	protected final String entityUri;
	protected final Class<? extends Resource<T>> responseType;
	
	public RestRepository(Class<? extends Resource<T>> resourceType, String entityName) {
		this(resourceType, entityName, entityName+"s");
	}
	
	public RestRepository(Class<? extends Resource<T>> resourceType, String entityName, String collectionName) {
		this.responseType = resourceType;
		this.entityRel = entityName;
		this.entityUri = baseUri+"/"+collectionName;
	}
	
	/**
	 * Override this method to process an entity after it is received from the 
	 * REST endpoint. An example use is populating associations.
	 * @param entity received entity with id link already
	 */
	protected abstract void processReceivedEntity(T entity);
	
	@SuppressWarnings("unchecked")
	@Override
	public <S extends T> S save(S entity) {
		HttpEntity<T> request = new HttpEntity<>(entity);
		
		URI id;
		if(entity.isNew()) {
			id = rest.postForLocation(entityUri, request);
		} else {
			id = entity.getId();
			rest.put(id, request);
		}
		T response = findOne(id);
		return (S) response;
	}

	@Override
	public <S extends T> List<S> save(Iterable<S> entities) {
		List<S> response = new ArrayList<>();
		entities.forEach(entity -> response.add(save(entity)));
		return response;
	}

	@Override
	public T findOne(URI id) {
		RequestEntity<?> request = RequestEntity.get(id).build();
		ResponseEntity<? extends Resource<T>> response = rest.exchange(request, responseType);
		T entity = response.getBody().getContent();
		processReceivedEntity(entity);
		return entity;
	}

	@Override
	public boolean exists(URI id) {
		try {
			ResponseEntity<?> response = rest.getForEntity(id, Void.class);
			return response.getStatusCode().equals(HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				return false;
			} else {
				throw e;
			}
		}
	}

	@Override
	public List<T> findAll() {
		return findAllIds().stream()
				.map(id -> findOne(id))
				.collect(Collectors.toList());
	}

	public List<URI> findAllIds() {
		return rest.getForEntity(entityUri, CollectionResource.class)
				.getBody().getLinks().stream()
				.filter(link -> link.getRel().equals(entityRel))
				.map(link -> URI.create(link.getHref()))
				.collect(Collectors.toList());
	}

	@Override
	public List<T> findAll(Iterable<URI> ids) {
		List<T> response = new ArrayList<>();
		ids.forEach(id -> response.add(findOne(id)));
		return response;
	}

	@Override
	public long count() {
		return findAllIds().size();
	}

	@Override
	public void delete(URI id) {
		rest.delete(id);
	}

	@Override
	public void delete(T entity) {
		delete(entity.getId());
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		entities.forEach(entity -> delete(entity));
	}

	@Override
	public void deleteAll() {
		deleteAll(findAllIds());
	}

	public void deleteAll(Iterable<URI> ids) {
		ids.forEach(id -> delete(id));
	}

	public static Long getIdAsLong(URI id) {
		String idString = id.toString();
		String idValue = idString.substring(idString.lastIndexOf("/") + 1);
		return Long.valueOf(idValue);
	}

}