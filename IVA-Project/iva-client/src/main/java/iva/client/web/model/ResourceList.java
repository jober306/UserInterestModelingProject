package iva.client.web.model;

import iva.client.core.model.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceList<T extends Entity> {

	@JsonProperty("_embedded")
	private final Map<String, List<Resource<T>>> embedded;

	protected ResourceList() {
		embedded = Collections.singletonMap("empty", Collections.emptyList());
	}

	public ResourceList(String name, Iterable<Resource<T>> resources) {
		List<Resource<T>> resourceList = new ArrayList<>();
		for (Resource<T> resource : resources) {
			resourceList.add(resource);
		}
		embedded = Collections.singletonMap(name, resourceList);
	}

	public List<Resource<T>> toList() {
		Assert.isTrue(embedded.size() == 1, "Internal map '_embedded' is not a singleton.");
		return embedded.values().stream().findAny().get();
	}

	@Override
	public String toString() {
		return String.format("{\"_embedded\":%s}", embedded);
	}

}
