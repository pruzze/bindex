package pl.caltha.labs.ldapfilters;

public class And extends CompoundFilter {
	
	And() {
		super();
	}

	And(Filter... initTerms) {
		super(initTerms);
	}

	@Override
	public String toString() {
		return toString('&');
	}
}
