package pl.caltha.labs.ldapfilters;

/**
 * Require-Capability: namespace;filter:="(attr-filter)" represented as
 * (namespace=(attr-filter))
 * 
 * @author rafal
 * 
 */
public class RequirementFilter extends SimpleFilter<Filter> {
	private final boolean optional;
	
	private boolean multiple;

	RequirementFilter(String namespace, Filter value, boolean optional, boolean multiple) {
		super(namespace, Operator.EQUAL, value);
		this.optional = optional;
		this.multiple = multiple;
	}

	@Override
	public AttributeType getAttributeType() {
		return AttributeType.REQUIREMENT;
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append("(").append(getAttribute());
		buff.append(";filter:=");
		buff.append(getValue().toString());
		if(optional)
			buff.append(";resolution:=optional");
		if(multiple)
			buff.append(";cardinality:=multiple");
		buff.append(")");
		return buff.toString();
	}
}
