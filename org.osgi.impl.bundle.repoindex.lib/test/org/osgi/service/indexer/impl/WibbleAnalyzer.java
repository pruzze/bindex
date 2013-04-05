package org.osgi.service.indexer.impl;

import java.util.List;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.service.indexer.IndexableResource;
import org.osgi.service.indexer.ResourceAnalyzer;
import org.osgi.service.indexer.impl.util.Builder;

public class WibbleAnalyzer implements ResourceAnalyzer {

	public void analyzeResource(IndexableResource resource, List<Capability> capabilities, List<Requirement> requirements) throws Exception {
		capabilities.add(new Builder().setNamespace("wibble").buildCapability());
	}

}
