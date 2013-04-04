package org.osgi.service.indexer;

import java.util.List;

public class Resource {

	private List<Requirement> reqs;
	
	private List<Capability> caps;

	public Resource(List<Requirement> reqs, List<Capability> caps) {
		this.reqs = reqs;
		this.caps = caps;
	}

	public List<Requirement> getRequirements(String namespace) {
		return reqs;
	}

	public List<Capability> getCapabilities(String namespace) {
		return caps;
	}
}
