package iva.client.web.model;

import iva.client.core.model.Entity;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class Resource<T extends Entity> implements Identifiable<Link> {

	@JsonUnwrapped
	private final T content;

	@JsonProperty("_links")
	private final Map<String, Link> linkMap = new LinkedHashMap<>();

	/**
	 * Creates an empty {@link Resource}.
	 */
	protected Resource() {
		this.content = null;
	}

	/**
	 * Creates a new {@link Resource} with the given content and {@link Link}s (optional).
	 * @param content must not be {@literal null}.
	 * @param links the links to add to the {@link Resource}.
	 */
	public Resource(T content, Link... links) {
		this(content, Arrays.asList(links));
	}

	/**
	 * Creates a new {@link Resource} with the given content and {@link Link}s.
	 * @param content must not be {@literal null}.
	 * @param links the links to add to the {@link Resource}.
	 */
	public Resource(T content, Iterable<Link> links) {
		Assert.notNull(content, "Content must not be null!");
		Assert.isTrue(!(content instanceof Collection), "Content must not be a collection! Use Resources instead!");
		this.content = content;
		this.putAllLinks(links);
	}

	/**
	 * Returns the underlying entity.
	 * @return the content
	 */
	public T getContent() {
		content.setId(URI.create(getId().getHref()));
		return content;
	}

	/**
	 * Adds the given link to the resource 
	 * or replaces an existing link of the same relation.
	 * @param link
	 */
	public void putLink(Link link) {
		link = Objects.requireNonNull(link, "Link must not be null!");
		linkMap.put(link.getRel(), link);
	}

	/**
	 * Adds all given {@link Link}s to the resource 
	 * replacing any existing links of the same relation.
	 * @param links
	 */
	public void putAllLinks(Iterable<Link> links) {
		links = Objects.requireNonNull(links, "Given links must not be null!");
		for (Link candidate : links) {
			putLink(candidate);
		}
	}

	/**
	 * Removes all {@link Link}s added to the resource so far.
	 */
	public void removeLinks() {
		this.linkMap.clear();
	}

	/**
	 * Returns a new list of all {@link Link}s contained in this resource.
	 * @return
	 */
	public List<Link> getLinks() {
		List<Link> links = new ArrayList<>(linkMap.size());
		for(String rel : linkMap.keySet()) {
			links.add(getLink(rel));
		}
		return links;
	}

	/**
	 * Returns the link with the given rel.
	 * @param rel
	 * @return the link with the given rel or {@literal null} if none found.
	 */
	public Link getLink(String rel) {
		if(hasLink(rel)) {
			return linkMap.get(rel).withRel(rel);
		} else {
			return null;
		}
	}

	/**
	 * Returns whether the resource contains {@link Link}s at all.
	 * @return true if resource has links
	 */
	public boolean hasLinks() {
		return !linkMap.isEmpty();
	}

	/**
	 * Returns whether the resource contains a {@link Link} with the given rel.
	 * @param rel
	 * @return
	 */
	public boolean hasLink(String rel) {
		return linkMap.containsKey(rel);
	}

	@Override
	public Link getId() {
		return getLink(Link.REL_SELF);
	}

	@Override
	public String toString() {
		return String.format("Resource={content:%s, links:%s}", content, linkMap);
	}

}
