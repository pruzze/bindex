package pl.caltha.labs.ldapfilters;

public class Or extends CompoundFilter {

	Or() {
		super();
	}

	Or(Filter... initTerms) {
		super(initTerms);
	}

	@Override
	public String toString() {
		return toString('|');
	}
}
