package org.osgi.service.indexer.impl.filter;

public class Not extends CompoundFilter {
	@Override
	void addTerm(Filter term) {
		if (terms().isEmpty())
			super.addTerm(term);
		else
			throw new IllegalStateException("already contains a term");
	}
}
