package pl.caltha.labs.ldapfilters;

public class VoidFilter extends SimpleFilter<Void> {
	
	VoidFilter(String attribute, Operator operator) {
		super(attribute, operator, null);
	}
	
	@Override
	public Void getValue() {
		throw new UnsupportedOperationException();
	}

	@Override
	public AttributeType getAttributeType() {
		throw new UnsupportedOperationException();
	}
	
	protected void appendType(StringBuilder buff) {
	}
	
	public <V> V accept(FilterVisitor<V> visitor, V data) {
		return visitor.visit(this, data);
	}
}
