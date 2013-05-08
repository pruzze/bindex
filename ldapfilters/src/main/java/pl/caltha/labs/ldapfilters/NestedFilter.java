package pl.caltha.labs.ldapfilters;

public class NestedFilter extends CompoundFilter {

	final private String attribute;
	
	public NestedFilter(String attribute) {
		super();
		this.attribute = attribute;
	}
	
	public NestedFilter(String attribute, Filter filter) {
		super(filter);
		this.attribute = attribute;
	}

	public String getAttribute() {
		return attribute;
	}
	
	public Filter getFilter() {
		return getTerms().get(0);
	}

	@Override
	void addTerm(Filter term) {
		if (getTerms().isEmpty())
			super.addTerm(term);
		else
			throw new IllegalStateException("already contains a term");
	}
	
	public <T> T accept(FilterVisitor<T> visitor, T data) {
		data = getFilter().accept(visitor, data);
		return visitor.visit(this, data);
	}
	
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append('(').append(attribute).append('=');
		buff.append(getFilter().toString()).append(')');
		return buff.toString();
	}
}
