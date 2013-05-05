package pl.caltha.labs.ldapfilters;

import java.util.Map;

/**
 * Require-Capability: namespace;filter:="(attr-filter)" represented as
 * (namespace=(attr-filter))
 * 
 * @author rafal
 * 
 */
public class Requirement implements Filter {

	private String namespace;
	
	private Map<String, String> properties;

	private Filter filter;

	Requirement(String namespace, Filter filter, Map<String, String> properties) {
		this.namespace = namespace;
		this.filter = filter;
		this.properties = properties;
		for (String key : properties.keySet()) {
			if (key.equalsIgnoreCase("filter"))
				throw new IllegalArgumentException("ambiguous filter");
		}
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append("(").append(namespace);
		buff.append(";filter:=");
		buff.append(filter.toString());
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			buff.append(';').append(entry.getKey());
			buff.append(":=").append(entry.getValue());
		}
		buff.append(")");
		return buff.toString();
	}

	public <T> T accept(FilterVisitor<T> visitor, T data) {
		data = filter.accept(visitor, data);
		return visitor.visit(this, data);
	}
}
