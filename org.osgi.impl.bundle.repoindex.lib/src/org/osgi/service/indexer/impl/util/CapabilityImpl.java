package org.osgi.service.indexer.impl.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.osgi.resource.Capability;
import org.osgi.resource.Resource;

public class CapabilityImpl implements Capability {

	private final Resource resource;
	private final String namespace;
	private final Map<String, Object> attributes;
	private final Map<String, String> directives;

	CapabilityImpl(Resource resource, String namespace, Map<String, Object> attributes, Map<String, String> directives) {
		this.resource = resource;
		this.namespace = namespace;
		this.attributes = attributes;
		this.directives = directives;
	}
	
	CapabilityImpl(Resource resource, Capability cap) {
		this.resource = resource;
		this.namespace = cap.getNamespace();
		this.attributes = new LinkedHashMap<String, Object>(cap.getAttributes());
		this.directives = new LinkedHashMap<String, String>(cap.getDirectives());
	}

	public Resource getResource() {
		return resource;
	}

	public String getNamespace() {
		return namespace;
	}

	public Map<String, Object> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	public Map<String, String> getDirectives() {
		return Collections.unmodifiableMap(directives);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Capability [namespace=").append(namespace).append(", attributes=").append(attributes).append(", directives=").append(directives).append("]");
		return builder.toString();
	}
	
}
