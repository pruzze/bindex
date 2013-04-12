package org.osgi.service.indexer.impl.filter;

public class TextFilter extends SimpleFilter {

	private String value;

	TextFilter(String attribute, Operator operator, String value) {
		super(attribute, operator);
		this.value = value;
	}

	public String value() {
		return value;
	}
}
