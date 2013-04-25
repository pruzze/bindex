package pl.caltha.labs.ldapfilters;

public class Not extends CompoundFilter {

	Not() {
		super();
	}

	Not(Filter term) {
		super(term);
	}

	@Override
	void addTerm(Filter term) {
		if (getTerms().isEmpty())
			super.addTerm(term);
		else
			throw new IllegalStateException("already contains a term");
	}
		
	public <V> V accept(FilterVisitor<V> visitor, V data) {
		for(Filter term : getTerms()) {
			data = term.accept(visitor, data);
		}
		return visitor.visit(this, data);
	}

	@Override
	public String toString() {
		return toString('!');
	}
}
