package pl.caltha.labs.ldapfilters;

public class Or extends CompoundFilter {

	Or() {
		super();
	}

	Or(Filter... initTerms) {
		super(initTerms);
	}

	public <V> V accept(FilterVisitor<V> visitor, V data) {
		for(Filter term : getTerms()) {
			data = term.accept(visitor, data);
		}
		return visitor.visit(this, data);
	}

	@Override
	public String toString() {
		return toString('|');
	}
		
}
