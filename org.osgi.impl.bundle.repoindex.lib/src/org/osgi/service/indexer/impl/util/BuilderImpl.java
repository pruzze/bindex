package org.osgi.service.indexer.impl.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.indexer.Builder;

public class BuilderImpl implements Builder {

	private String namespace = null;
	private Resource resource;
	private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	private final Map<String, String> directives = new LinkedHashMap<String, String>();
	
	public Builder setResource(Resource resource) {
		this.resource = resource;
		return this;
	}

	public Builder setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	public Builder addAttribute(String name, Object value) {
		attributes.put(name, value);
		return this;
	}

	public Builder addDirective(String name, String value) {
		directives.put(name, value);
		return this;
	}

	public Capability buildCapability() throws IllegalStateException {
		if (namespace == null)
			throw new IllegalStateException("Namespace not set");

		return addToRes(new CapabilityImpl(resource, namespace, new LinkedHashMap<String, Object>(attributes), new LinkedHashMap<String, String>(directives)));
	}

	public Requirement buildRequirement() throws IllegalStateException {
		if (namespace == null)
			throw new IllegalStateException("Namespace not set");

		return addToRes(new RequirementImpl(resource, namespace, new LinkedHashMap<String, Object>(attributes), new LinkedHashMap<String, String>(directives)));
	}

	private Capability addToRes(Capability cap) {
		if(resource != null)
			resource.getCapabilities(namespace).add(cap);
		return cap;
	}

	private Requirement addToRes(Requirement req) {
		if(resource != null)
			resource.getRequirements(namespace).add(req);
		return req;
	}
}
