package org.osgi.service.indexer.impl;

import java.util.List;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.service.indexer.IndexableResource;
import org.osgi.service.indexer.ResourceAnalyzer;

public class NaughtyAnalyzer implements ResourceAnalyzer {

	// Tries to remove a capability: should be disallowed
	public void analyzeResource(IndexableResource resource, List<Capability> capabilities, List<Requirement> requirements) throws Exception {
		capabilities.remove(0);
	}

}
