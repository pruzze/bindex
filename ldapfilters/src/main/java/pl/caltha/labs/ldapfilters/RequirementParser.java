package pl.caltha.labs.ldapfilters;

import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;

public class RequirementParser extends FilterParser {
	public RequirementParser(Reader input, boolean extendedFilters)
			throws ParseException {
		super(input, extendedFilters ? 1 : 0);
		requirements = new LinkedList<Requirement>();
		yybegin(YYREQUIREMENTS);
	}

	public static Requirements parseRequirements(String input,
			boolean extendedFilters) throws ParseException {
		RequirementParser parser = new RequirementParser(
				new StringReader(input), extendedFilters);
		parser.parseAll();
		Requirements reqs = new Requirements(parser.requirements);
		for(Requirement req : parser.requirements) {
			req.setParent(reqs);
		}
		return reqs;
	}
}
