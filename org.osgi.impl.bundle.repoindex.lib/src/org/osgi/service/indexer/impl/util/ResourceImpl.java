package org.osgi.service.indexer.impl.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class ResourceImpl implements Resource {

	private final Map<String, List<Requirement>> reqMap = new LinkedHashMap<String, List<Requirement>>();

	private final Map<String, List<Capability>> capMap = new LinkedHashMap<String, List<Capability>>();

	public ResourceImpl(List<Requirement> reqs, List<Capability> caps) {
		for (Requirement req : reqs) {
			getRequirements(req.getNamespace()).add(new RequirementImpl(this, req));
		}
		for (Capability cap : caps) {
			getCapabilities(cap.getNamespace()).add(new CapabilityImpl(this, cap));
		}
	}

	public List<Requirement> getRequirements(String namespace) {
		List<Requirement> reqs;
		if (namespace == null) {
			reqs = new LinkedList<Requirement>();
			for (List<Requirement> nsReqs : reqMap.values())
				reqs.addAll(nsReqs);
		} else {
			reqs = reqMap.get(namespace);
			if (reqs == null) {
				reqs = new LinkedList<Requirement>();
				reqMap.put(namespace, reqs);
			}
		}
		return reqs;
	}

	public List<Capability> getCapabilities(String namespace) {
		List<Capability> caps;
		if (namespace == null) {
			caps = new LinkedList<Capability>();
			for (List<Capability> nsCaps : capMap.values())
				caps.addAll(nsCaps);
		} else {
			caps = capMap.get(namespace);
			if (caps == null) {
				caps = new LinkedList<Capability>();
				capMap.put(namespace, caps);
			}
		}
		return caps;
	}
}
