package pl.caltha.labs.ldapfilters;

import java.util.LinkedHashMap;
import java.util.Map;

public class Requirement implements Filter {

	private NestedFilter filter;

	private Map<String, String> directives;

	private String namespace;

	Requirement(String namespace) {
		this.namespace = namespace;
		this.directives = new LinkedHashMap<String, String>();
	}

	void setFilter(NestedFilter filter) {
		if (this.filter == null)
			this.filter = filter;
		else
			throw new IllegalStateException("filter already set");
	}

	void addDirective(String directive, String value) {
		if (directive.equals("filter"))
			throw new IllegalArgumentException("ambiguous filter");
		else
			directives.put(directive, value);
	}
	
	String getNamespace() {
		return namespace;
	}

	Requirement(String namespace, Filter filter, Map<String, String> directives) {
		this.namespace = namespace;
		this.filter = new NestedFilter(namespace, filter);
		this.directives = directives;
		for (String key : directives.keySet()) {
			if (key.equalsIgnoreCase("filter"))
				throw new IllegalArgumentException("ambiguous filter");
		}
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		if(filter != null) {
			buff.append(namespace);
			buff.append(";filter:=\"");
			buff.append(filter.getFilter().toString());
			buff.append("\"");
		} else {
			buff.append(namespace);
		}
		for (Map.Entry<String, String> entry : directives.entrySet()) {
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
