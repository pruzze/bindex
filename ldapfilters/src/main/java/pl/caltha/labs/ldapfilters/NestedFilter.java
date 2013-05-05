package pl.caltha.labs.ldapfilters;

public class NestedFilter implements Filter {

	final private String attribute;

	final private Filter nested;
	
	public NestedFilter(String attribute, Filter nested) {
		this.attribute = attribute;
		this.nested = nested;
	}

	public String getAttribute() {
		return attribute;
	}
	
	public Filter getNested() {
		return nested;
	}

	public <T> T accept(FilterVisitor<T> visitor, T data) {
		data = nested.accept(visitor, data);
		return visitor.visit(this, data);
	}
	
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append('(').append(attribute).append('=');
		buff.append(nested.toString()).append(')');
		return buff.toString();
	}
}
