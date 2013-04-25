package pl.caltha.labs.ldapfilters;


public class DoubleFilter extends SimpleFilter<Double> {
	DoubleFilter(String attribute, Operator operator, Double value) {
		super(attribute, operator, value);
	}
	
	DoubleFilter(String attribute, Operator operator, String value) {
		super(attribute, operator, Double.parseDouble(value.trim()));
	}

	@Override
	public AttributeType getAttributeType() {		
		return AttributeType.DOUBLE;
	}
	
	protected void appendType(StringBuilder buff) {
		buff.append("Double");
	}
}
