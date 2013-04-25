package pl.caltha.labs.ldapfilters;

public class OrFilter extends CompoundFilter {

	OrFilter() {
		super();
	}

	OrFilter(Filter... initTerms) {
		super(initTerms);
	}

	public <V> V accept(FilterVisitor<V> visitor, V data) {
		for (Filter term : getTerms()) {
			data = term.accept(visitor, data);
		}
		return visitor.visit(this, data);
	}

	@Override
	public String toString() {
		return toString('|');
	}

}
