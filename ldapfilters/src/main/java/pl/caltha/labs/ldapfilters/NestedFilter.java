package pl.caltha.labs.ldapfilters;

public class NestedFilter implements Filter {

	final private String attribute;

	private Filter nested;
	
	public NestedFilter(String attribute, Filter nested) {
		this.attribute = attribute;
		this.nested = nested;
	}
	
	public NestedFilter(String attribute) {
		this.attribute = attribute;
	}

	public String getAttribute() {
		return attribute;
	}
	
	public Filter getNested() {
		return nested;
	}

	public void setNested(Filter nested) {
		this.nested = nested;
	}

	public <T> T accept(FilterVisitor<T> visitor, T data) {
		return visitor.visit(this, data);
	}
}
