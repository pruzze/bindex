package pl.caltha.labs.ldapfilters;

public class NestedFilter implements Filter {

	final private String attribute;

	final private Filter filter;
	
	public NestedFilter(String attribute, Filter filter) {
		this.attribute = attribute;
		this.filter = filter;
	}

	public String getAttribute() {
		return attribute;
	}
	
	public Filter getFilter() {
		return filter;
	}

	public <T> T accept(FilterVisitor<T> visitor, T data) {
		data = filter.accept(visitor, data);
		return visitor.visit(this, data);
	}
	
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append('(').append(attribute).append('=');
		buff.append(filter.toString()).append(')');
		return buff.toString();
	}
}
