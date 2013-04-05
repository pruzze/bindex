package org.osgi.service.indexer;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public interface Builder {

	Builder setResource(Resource resource);

	Builder setNamespace(String namespace);

	Builder addAttribute(String name, Object value);

	Builder addDirective(String name, String value);

	Capability buildCapability() throws IllegalStateException;

	Requirement buildRequirement() throws IllegalStateException;

}