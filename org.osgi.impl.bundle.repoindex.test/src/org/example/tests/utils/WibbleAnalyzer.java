package org.example.tests.utils;

import java.util.List;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.service.indexer.IndexableResource;
import org.osgi.service.indexer.ResourceAnalyzer;

public class WibbleAnalyzer implements ResourceAnalyzer {

	public void analyzeResource(IndexableResource resource, List<Capability> capabilities, List<Requirement> requirements) throws Exception {
		capabilities.add(resource.newBuilder().setNamespace("wibble").buildCapability());
	}
}
