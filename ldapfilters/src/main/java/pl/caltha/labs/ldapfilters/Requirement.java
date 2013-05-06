package pl.caltha.labs.ldapfilters;

import java.util.Map;

public class Requirement implements Filter {

	private NestedFilter filter;
	
	private Map<String, String> properties;

	Requirement(String namespace, Filter filter, Map<String, String> properties) {
		this.filter = new NestedFilter(namespace, filter);
		this.properties = properties;
		for (String key : properties.keySet()) {
			if (key.equalsIgnoreCase("filter"))
				throw new IllegalArgumentException("ambiguous filter");
		}
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append(filter.getAttribute());
		buff.append(";filter:=\"");
		buff.append(filter.getFilter().toString());
		buff.append("\"");
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			buff.append(';').append(entry.getKey());
			buff.append(":=").append(entry.getValue());
		}
		return buff.toString();
	}

	public <T> T accept(FilterVisitor<T> visitor, T data) {
		data = filter.accept(visitor, data);
		return visitor.visit(this, data);
	}
}
