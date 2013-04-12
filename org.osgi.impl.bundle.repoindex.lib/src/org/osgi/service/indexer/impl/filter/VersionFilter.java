package org.osgi.service.indexer.impl.filter;

import org.osgi.framework.Version;

public class VersionFilter extends SimpleFilter {

	private Version value;

	VersionFilter(String attribute, Operator operator, Version value) {
		super(attribute, operator);
		this.value = value;
	}

	public Version value() {
		return value;
	}
}
