package org.osgi.service.indexer.impl.filter;

public class SimpleFilter {
	private String attribute;

	private Operator operator;

	SimpleFilter(String attribute, Operator operator) {
		this.attribute = attribute;
		this.operator = operator;
	}
	
	public String attribute() {
		return attribute;
	}

	public Operator operator() {
		return operator;
	}

	public <T> T visit(FilterVisitor<T> visitor, T data) {
		return visitor.visit(this, data);
	}
}
