package org.example.tests.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.indexer.IndexableResource;
import org.osgi.service.indexer.ResourceAnalyzer;

public class WibbleAnalyzer implements ResourceAnalyzer {

	public void analyzeResource(IndexableResource resource, List<Capability> capabilities, List<Requirement> requirements) throws Exception {
		capabilities.add(new Capability() {

			public String getNamespace() {
				return "wibble";
			}

			public Map<String, String> getDirectives() {
				return Collections.emptyMap();
			}

			public Map<String, Object> getAttributes() {
				return Collections.emptyMap();
			}

			public Resource getResource() {
				return null;
			}
		});
	}
}
