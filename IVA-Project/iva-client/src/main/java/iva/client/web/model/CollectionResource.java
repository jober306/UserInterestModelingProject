package iva.client.web.model;

import java.util.Collections;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

public class CollectionResource extends Resources<Void> {

	public CollectionResource() {
		super();
	}

	public CollectionResource(Iterable<Link> links) {
		super(Collections.emptySet(), links);
	}

	public CollectionResource(Link... links) {
		super(Collections.emptySet(), links);
	}

}
