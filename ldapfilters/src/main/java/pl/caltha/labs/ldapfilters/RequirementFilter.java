package pl.caltha.labs.ldapfilters;

import java.util.Map;

/**
 * Require-Capability: namespace;filter:="(attr-filter)" represented as
 * (namespace=(attr-filter))
 * 
 * @author rafal
 * 
 */
public class RequirementFilter extends SimpleFilter<Filter> {

	private Map<String, String> properties;

	RequirementFilter(String namespace, Filter value,
			Map<String, String> properties) {
		super(namespace, Operator.MATCHES, value);
		this.properties = properties;
		for (String key : properties.keySet()) {
			if (key.equalsIgnoreCase("filter"))
				throw new IllegalArgumentException("ambiguous filter");
		}
	}

	@Override
	public AttributeType getAttributeType() {
		return AttributeType.REQUIREMENT;
	}

	public <T> T accept(FilterVisitor<T> visitor, T data) {
		data = getValue().accept(visitor, data);
		data = visitor.visit(this, data);
		return data;
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append("(").append(getAttribute());
		buff.append(";filter:=");
		buff.append(getValue().toString());
		for(Map.Entry<String,String> entry : properties.entrySet()) {
			buff.append(';').append(entry.getKey());
			buff.append(":=").append(entry.getValue());
		}		
		buff.append(")");
		return buff.toString();
	}
}
