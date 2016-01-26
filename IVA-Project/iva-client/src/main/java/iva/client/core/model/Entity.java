package iva.client.core.model;

import java.io.Serializable;
import java.net.URI;

import org.springframework.hateoas.Identifiable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Entity implements Identifiable<URI>, Serializable {
	private static final long serialVersionUID = -5095978688489032467L;
	
	@JsonIgnore
	private URI id;
	
	public Entity() {
		super();
	}
	
	@Override
	public URI getId() {
		return id;
	}
	
	public void setId(URI id) {
		this.id = id;
	}
	
	public boolean isNew() {
		return (id == null);
	}
	
	@Override
	public String toString() {
		return String.format("BaseEntity [id=%s, isNew()=%s]", id, isNew());
	}
	
}