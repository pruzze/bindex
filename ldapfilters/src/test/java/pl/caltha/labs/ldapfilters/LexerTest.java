package pl.caltha.labs.ldapfilters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class LexerTest {

	private FilterParser startFilter(String input) {
		return startFilter(input, 0);
	}
	
	private FilterParser startFilter(String input, int allowedNestingDepth) {
		return new FilterParser(new StringReader(input), allowedNestingDepth);
	}
	
	private RequirementParser startRequirement(String input) {
		return startRequirement(input, false);
	}
	
	private RequirementParser startRequirement(String input, boolean extendedFilters) {
		return new RequirementParser(new StringReader(input), extendedFilters);
	}

	private void expect(int state, FilterParser p) throws IOException {
		assertEquals(state, p.yylex());
	}
	
	@Test
	public void testEquals() throws IOException {
		FilterParser l = startFilter("(attr=value)");
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("value", l.yytext());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testSubstr1() throws IOException {
		FilterParser l = startFilter("(attr=value*)");
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("value*", l.yytext());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testSubstr2() throws IOException {
		FilterParser l = startFilter("(attr=*value)");
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("*value", l.yytext());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testApprox() throws IOException {
		FilterParser l = startFilter("(attr~=value)");
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("~=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("value", l.yytext());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testGrEq() throws IOException {
		FilterParser l = startFilter("(attr>=1.0.0)");
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals(">=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1.0.0", l.yytext());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testLeEq() throws IOException {
		FilterParser l = startFilter("(attr<=1.0.0)");
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("<=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1.0.0", l.yytext());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testPres() throws IOException {
		FilterParser l = startFilter("(attr=*)");
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr", l.yytext());
		assertEquals(FilterParser.YYPRES, l.yylex());
		assertEquals("=*", l.yytext());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testEscapes() throws IOException {
		FilterParser l = startFilter("(attr=\\(va\\\\lue\\))");
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("\\(va\\\\lue\\)", l.yytext());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testAnd() throws Exception {
		FilterParser l = startFilter("(&(attr1=val1)(attr2>=1))");
		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals('&', l.yycharat(1));

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr1", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("val1", l.yytext());

		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr2", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals(">=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1", l.yytext());

		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testOr() throws Exception {
		FilterParser l = startFilter("(|(attr1<=1 2)(attr2>=1=2))");
		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals('|', l.yycharat(1));

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr1", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("<=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1 2", l.yytext());

		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr2", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals(">=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1=2", l.yytext());

		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testAndNot() throws Exception {
		FilterParser l = startFilter("(&(version>=1.1)(!(version<=2)))");

		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals('&', l.yycharat(1));

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("version", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals(">=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1.1", l.yytext());

		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());

		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals('!', l.yycharat(1));

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("version", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("<=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("2", l.yytext());

		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testLongAttribute() throws Exception {
		FilterParser l = startFilter("(a:Long=1)");

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("a", l.yytext());
		assertEquals(FilterParser.YYATTRTYPE, l.yylex());
		assertEquals("Long", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1", l.yytext());

		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testDoubleAttribute() throws Exception {
		FilterParser l = startFilter("(a:Double=1)");

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("a", l.yytext());
		assertEquals(FilterParser.YYATTRTYPE, l.yylex());
		assertEquals("Double", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1", l.yytext());

		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testVersionAttribute() throws Exception {
		FilterParser l = startFilter("(a:Version=1.0.0)");

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("a", l.yytext());
		assertEquals(FilterParser.YYATTRTYPE, l.yylex());
		assertEquals("Version", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1.0.0", l.yytext());

		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testStringListAttribute() throws Exception {
		FilterParser l = startFilter("(a:List<String>=a,b,c)");

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("a", l.yytext());
		assertEquals(FilterParser.YYATTRELEMTYPE, l.yylex());
		assertEquals("List<", l.yytext());
		assertEquals(FilterParser.YYATTRTYPE, l.yylex());
		assertEquals("String>", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("a,b,c", l.yytext());

		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testLongListAttribute() throws Exception {
		FilterParser l = startFilter("(a:List<Long>=1,2,3)");

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("a", l.yytext());
		assertEquals(FilterParser.YYATTRELEMTYPE, l.yylex());
		assertEquals("List<", l.yytext());
		assertEquals(FilterParser.YYATTRTYPE, l.yylex());
		assertEquals("Long>", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1,2,3", l.yytext());

		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testDoubleListAttribute() throws Exception {
		FilterParser l = startFilter("(a:List<Double>=1.0,1.5)");

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("a", l.yytext());
		assertEquals(FilterParser.YYATTRELEMTYPE, l.yylex());
		assertEquals("List<", l.yytext());
		assertEquals(FilterParser.YYATTRTYPE, l.yylex());
		assertEquals("Double>", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1.0,1.5", l.yytext());

		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testVersionListAttribute() throws Exception {
		FilterParser l = startFilter("(a:List<Version>=1.0.0,1.2.0)");

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("a", l.yytext());
		assertEquals(FilterParser.YYATTRELEMTYPE, l.yylex());
		assertEquals("List<", l.yytext());
		assertEquals(FilterParser.YYATTRTYPE, l.yylex());
		assertEquals("Version>", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1.0.0,1.2.0", l.yytext());

		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testNested1() throws Exception {
		FilterParser l = startFilter("(osgi.wiring.package=(osgi.wiring.package=osgi.wiring.framework))", 1);
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("osgi.wiring.package", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYNESTED, l.yylex());
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("osgi.wiring.package", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("osgi.wiring.framework", l.yytext());
		assertEquals(FilterParser.YYNESTED, l.yylex());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testNested2() throws Exception {
		FilterParser l = startFilter("(osgi.wiring.package=(&(osgi.wiring.package=osgi.wiring.framework)(version:Version>=1.6.0)))", 1);
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("osgi.wiring.package", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYNESTED, l.yylex());

		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals('&', l.yycharat(1));
		
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("osgi.wiring.package", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("osgi.wiring.framework", l.yytext());

		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("version", l.yytext());
		assertEquals(FilterParser.YYATTRTYPE, l.yylex());
		assertEquals("Version", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals(">=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1.6.0", l.yytext());

		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals(FilterParser.YYNESTED, l.yylex());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testNested3() throws Exception {
		FilterParser l = startFilter("(osgi.resource.capability=(&(osgi.wiring.package=(&(osgi.wiring.package=javax.mail)(version:Version>=1.4.0)(version:Version<=2.0.0)))(osgi.identity=(license=http://www.opensource.org/licenses/EPL-1.0))))", 2);
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("osgi.resource.capability", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		
		assertEquals(FilterParser.YYNESTED, l.yylex());
		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals('&', l.yycharat(1));
		
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("osgi.wiring.package", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYNESTED, l.yylex());

		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals('&', l.yycharat(1));
		
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("osgi.wiring.package", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("javax.mail", l.yytext());
		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("version", l.yytext());
		assertEquals(FilterParser.YYATTRTYPE, l.yylex());
		assertEquals("Version", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals(">=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("1.4.0", l.yytext());
		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("version", l.yytext());
		assertEquals(FilterParser.YYATTRTYPE, l.yylex());
		assertEquals("Version", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("<=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("2.0.0", l.yytext());
		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals(FilterParser.YYNESTED, l.yylex());
		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("osgi.identity", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		
		assertEquals(FilterParser.YYNESTED, l.yylex());

		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("license", l.yytext());
		assertEquals(FilterParser.YYOPER, l.yylex());
		assertEquals("=", l.yytext());
		assertEquals(FilterParser.YYVALUE, l.yylex());
		assertEquals("http://www.opensource.org/licenses/EPL-1.0", l.yytext());
		assertEquals(FilterParser.YYNESTED, l.yylex());
		assertEquals(FilterParser.YYCOMPOSITE, l.yylex());
		assertEquals(FilterParser.YYNESTED, l.yylex());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}
	
	@Test
	public void testRequiremens1() throws IOException {
		RequirementParser r = startRequirement("test.1");
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.1", r.yytext().trim());
		expect(FilterParser.YYDONE, r);
	}

	@Test
	public void testRequiremens2() throws IOException {
		RequirementParser r = startRequirement("test.1,\ntest.2");
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.1", r.yytext().trim());
		expect(RequirementParser.YYREQUIREMENTS, r);
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.2", r.yytext().trim());
		expect(RequirementParser.YYDONE, r);
	}

	@Test
	public void testRequirementDirectives1() throws IOException {
		RequirementParser r = startRequirement(" test.1;a:=b");
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.1", r.yytext().trim());
		expect(RequirementParser.YYREQUIREMENT_DIRECTIVE, r);
		expect(RequirementParser.YYDIRECTIVE_NAME, r);
		assertEquals("a:=", r.yytext().trim());
		expect(RequirementParser.YYDIRECTIVE_VALUE, r);
		assertEquals("b", r.yytext().trim());
		expect(FilterParser.YYDONE, r);
	}
	
	@Test
	public void testRequirementDirectives2() throws IOException {
		RequirementParser r = startRequirement("test.1; a := b; e := f");
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.1", r.yytext().trim());
		expect(RequirementParser.YYREQUIREMENT_DIRECTIVE, r);
		expect(RequirementParser.YYDIRECTIVE_NAME, r);
		assertEquals("a :=", r.yytext().trim());
		expect(RequirementParser.YYDIRECTIVE_VALUE, r);
		assertEquals("b", r.yytext().trim());
		expect(RequirementParser.YYDIRECTIVE_NAME, r);
		assertEquals("e :=", r.yytext().trim());
		expect(RequirementParser.YYDIRECTIVE_VALUE, r);
		assertEquals("f", r.yytext().trim());
		expect(FilterParser.YYDONE, r);
	}

	@Test
	public void testRequirementDirectives3() throws IOException {
		RequirementParser r = startRequirement("test.1; a:=b, test.2; e:=f ");
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.1", r.yytext().trim());
		expect(RequirementParser.YYREQUIREMENT_DIRECTIVE, r);
		expect(RequirementParser.YYDIRECTIVE_NAME, r);
		assertEquals("a:=", r.yytext().trim());
		expect(RequirementParser.YYDIRECTIVE_VALUE, r);
		assertEquals("b", r.yytext().trim());
		
		expect(RequirementParser.YYREQUIREMENTS, r);
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.2", r.yytext().trim());
		expect(RequirementParser.YYREQUIREMENT_DIRECTIVE, r);
		expect(RequirementParser.YYDIRECTIVE_NAME, r);
		assertEquals("e:=", r.yytext().trim());
		expect(RequirementParser.YYDIRECTIVE_VALUE, r);
		assertEquals("f", r.yytext().trim());
		expect(FilterParser.YYDONE, r);
	}
	
	@Test
	public void testRequirementFilter1() throws IOException {
		RequirementParser r = startRequirement("test.1; filter:=\"(a=b)\"");
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.1", r.yytext().trim());
		
		expect(RequirementParser.YYREQUIREMENT_DIRECTIVE, r);
		expect(RequirementParser.YYNESTED, r);
		expect(RequirementParser.YYSIMPLE, r);	
		assertEquals(FilterParser.YYATTRNAME, r.yylex());
		assertEquals("a", r.yytext());
		assertEquals(FilterParser.YYOPER, r.yylex());
		assertEquals("=", r.yytext());
		assertEquals(FilterParser.YYVALUE, r.yylex());
		assertEquals("b", r.yytext());
		assertEquals(FilterParser.YYNESTED, r.yylex());
		
		expect(FilterParser.YYDONE, r);
	}

	@Test
	public void testRequirementFilter2() throws IOException {
		RequirementParser r = startRequirement("test.1; d1:=v1; filter:=\"(a=b)\"; d2:=v2");
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.1", r.yytext().trim());

		expect(RequirementParser.YYREQUIREMENT_DIRECTIVE, r);
		expect(RequirementParser.YYDIRECTIVE_NAME, r);
		assertEquals("d1:=", r.yytext().trim());
		expect(RequirementParser.YYDIRECTIVE_VALUE, r);
		assertEquals("v1", r.yytext().trim());

		expect(RequirementParser.YYNESTED, r);
		expect(RequirementParser.YYSIMPLE, r);	
		assertEquals(FilterParser.YYATTRNAME, r.yylex());
		assertEquals("a", r.yytext());
		assertEquals(FilterParser.YYOPER, r.yylex());
		assertEquals("=", r.yytext());
		assertEquals(FilterParser.YYVALUE, r.yylex());
		assertEquals("b", r.yytext());
		assertEquals(FilterParser.YYNESTED, r.yylex());

		expect(RequirementParser.YYDIRECTIVE_NAME, r);
		assertEquals("d2:=", r.yytext().trim());
		expect(RequirementParser.YYDIRECTIVE_VALUE, r);
		assertEquals("v2", r.yytext().trim());
		
		expect(FilterParser.YYDONE, r);
	}
	
	@Test
	public void testRequirementFilter3() throws IOException {
		RequirementParser r = startRequirement("test.1; filter:=\"(a=b)\", test.2; filter:=\"(c=d)\"");
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.1", r.yytext().trim());
		
		expect(RequirementParser.YYREQUIREMENT_DIRECTIVE, r);
		expect(RequirementParser.YYNESTED, r);
		expect(RequirementParser.YYSIMPLE, r);	
		assertEquals(FilterParser.YYATTRNAME, r.yylex());
		assertEquals("a", r.yytext());
		assertEquals(FilterParser.YYOPER, r.yylex());
		assertEquals("=", r.yytext());
		assertEquals(FilterParser.YYVALUE, r.yylex());
		assertEquals("b", r.yytext());
		assertEquals(FilterParser.YYNESTED, r.yylex());

		expect(RequirementParser.YYREQUIREMENTS, r);
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.2", r.yytext().trim());

		expect(RequirementParser.YYREQUIREMENT_DIRECTIVE, r);
		expect(RequirementParser.YYNESTED, r);
		expect(RequirementParser.YYSIMPLE, r);	
		assertEquals(FilterParser.YYATTRNAME, r.yylex());
		assertEquals("c", r.yytext());
		assertEquals(FilterParser.YYOPER, r.yylex());
		assertEquals("=", r.yytext());
		assertEquals(FilterParser.YYVALUE, r.yylex());
		assertEquals("d", r.yytext());
		assertEquals(FilterParser.YYNESTED, r.yylex());
		
		expect(FilterParser.YYDONE, r);
	}
	
	@Test
	public void testRequirementFilter4() throws IOException {
		RequirementParser r = startRequirement("test.1; d1:=v1; filter:=\"(a=b)\", test.2; filter:=\"(c=d)\"; d2:=v2");
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.1", r.yytext().trim());

		expect(RequirementParser.YYREQUIREMENT_DIRECTIVE, r);
		expect(RequirementParser.YYDIRECTIVE_NAME, r);
		assertEquals("d1:=", r.yytext().trim());
		expect(RequirementParser.YYDIRECTIVE_VALUE, r);
		assertEquals("v1", r.yytext().trim());
		
		expect(RequirementParser.YYNESTED, r);
		expect(RequirementParser.YYSIMPLE, r);	
		assertEquals(FilterParser.YYATTRNAME, r.yylex());
		assertEquals("a", r.yytext());
		assertEquals(FilterParser.YYOPER, r.yylex());
		assertEquals("=", r.yytext());
		assertEquals(FilterParser.YYVALUE, r.yylex());
		assertEquals("b", r.yytext());
		assertEquals(FilterParser.YYNESTED, r.yylex());

		expect(RequirementParser.YYREQUIREMENTS, r);
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.2", r.yytext().trim());

		expect(RequirementParser.YYREQUIREMENT_DIRECTIVE, r);
		expect(RequirementParser.YYNESTED, r);
		expect(RequirementParser.YYSIMPLE, r);	
		assertEquals(FilterParser.YYATTRNAME, r.yylex());
		assertEquals("c", r.yytext());
		assertEquals(FilterParser.YYOPER, r.yylex());
		assertEquals("=", r.yytext());
		assertEquals(FilterParser.YYVALUE, r.yylex());
		assertEquals("d", r.yytext());
		assertEquals(FilterParser.YYNESTED, r.yylex());

		expect(RequirementParser.YYDIRECTIVE_NAME, r);
		assertEquals("d2:=", r.yytext().trim());
		expect(RequirementParser.YYDIRECTIVE_VALUE, r);
		assertEquals("v2", r.yytext().trim());
		
		expect(FilterParser.YYDONE, r);
	}
	
	@Test
	public void testRequirementNestedFilter1() throws IOException {
		RequirementParser r = startRequirement("test.1; filter:=\"(a=(a=b))\"", true);
		expect(RequirementParser.YYREQUIREMENT, r);
		assertEquals("test.1", r.yytext().trim());
		
		expect(RequirementParser.YYREQUIREMENT_DIRECTIVE, r);
		expect(RequirementParser.YYNESTED, r);
		assertEquals(FilterParser.YYSIMPLE, r.yylex());
		assertEquals(FilterParser.YYATTRNAME, r.yylex());
		assertEquals("a", r.yytext());
		assertEquals(FilterParser.YYOPER, r.yylex());
		assertEquals("=", r.yytext());
		assertEquals(FilterParser.YYNESTED, r.yylex());
		assertEquals(FilterParser.YYSIMPLE, r.yylex());
		assertEquals(FilterParser.YYATTRNAME, r.yylex());
		assertEquals("a", r.yytext());
		assertEquals(FilterParser.YYOPER, r.yylex());
		assertEquals("=", r.yytext());
		assertEquals(FilterParser.YYVALUE, r.yylex());
		assertEquals("b", r.yytext());
		assertEquals(FilterParser.YYNESTED, r.yylex());
		assertEquals(FilterParser.YYNESTED, r.yylex());
		
		expect(FilterParser.YYDONE, r);
	}
	
	private void expectException(String filter, String message) {
		expectException(filter, 0, message);
	}
	
	private void expectException(String filter, int allowedNestingDepth, String message) {
		try {
			FilterParser l = startFilter(filter, allowedNestingDepth);
			int yystate;
			do {
				yystate = l.yylex();
			} while (yystate != FilterParser.YYEOF);
			fail("should have thrown an exception");
		} catch (Exception e) {
			assertEquals(message, e.getMessage());
		}
	}
	
	private void expectRequirementException(String requirements, String message) {
		expectRequirementException(requirements, false, message);
	}
	
	private void expectRequirementException(String requirements, boolean extendedFilters, String message) {
		try {
			RequirementParser r = startRequirement(requirements, extendedFilters);
			int yystate;
			do {
				yystate = r.yylex();
			} while (yystate != FilterParser.YYEOF);
			fail("should have thrown an exception");
		} catch (Exception e) {
			assertEquals(message, e.getMessage());
		}
	}

	@Test
	public void testInvalidSyntax() {
		expectException("aa=bb", "Illegal character a at position 0");
		expectException("()", "Illegal character ) at position 1");
		expectException("(=)", "Illegal character = at position 1");
		expectException("(aa=bb)&(cc=dd)", "Illegal character & at position 7");
		expectException("(~(aa=bb))", "Illegal character ~ at position 1");
		expectException("(aa<bb=cc)", "Illegal character < at position 3");
		expectException("(aa>bb=cc)", "Illegal character > at position 3");
		expectException("(aa~bb=cc)", "Illegal character ~ at position 3");
		expectException("(aa(bb=cc)", "Illegal character ( at position 3");
		expectException("(aa)bb=cc)", "Illegal character ) at position 3");
		expectException("(aa=b\\b)", "Illegal character \\ at position 5");
		expectException("(aa=b(b)", "Illegal character ( at position 5");
		expectException("(aa=b)b)", "Illegal character b at position 6");
		expectException("(aa=bb)(cc=dd)", "Illegal character ( at position 7");
		expectException("(aa=bb))", "Illegal character ) at position 7");
		// typed attributes
		expectException("(a:=x)", "Illegal character = at position 3");
		expectException("(a:String:=x)", "Illegal character : at position 9");
		expectException("(a:Integer=x)",
				"Invalid attribute type Integer at position 3");
		expectException("(a:List=x)", "Missing element type at position 9");
		expectException("(a:List<=x)", "Illegal character = at position 8");
		expectException("(a:List<>=x)", "Illegal character > at position 8");
		expectException("(a:List<Integer>=x)",
				"Invalid attribute type Integer at position 8");
		expectException("(a:List<List>=x)",
				"Unsupported element type List at position 15");
		// nested filters
		expectException("(a=(b=c)", 1, "Unexpected end of input");
		expectException("(a~=(b=c))", 1, "Illegal operator preceding nested filter at position 4");
		// requirements
		expectRequirementException(",foo", "Illegal character , at position 0");
		expectRequirementException("foo,", "Unexpected end of input");
		expectRequirementException("foo;", "Unexpected end of input");
		expectRequirementException("foo; a", "Illegal character a at position 5");
		expectRequirementException("foo; a:=", "Unexpected end of input");
		expectRequirementException("foo; a=b", "Illegal character a at position 5"); // actually Illegal character = at position 6 would be better
		// requirement filters
		expectRequirementException("foo; filter:=boo", "ambiguous filter");
		expectRequirementException("foo; filter:=\"(a=b)\"; filter:=boo", "ambiguous filter");
		expectRequirementException("foo; filter:=\"(a=b", "Unexpected end of input");
		expectRequirementException("foo; filter:=\"(a=b)", "Unexpected end of input");
		expectRequirementException("foo; filter:=\"(a=(a=b))\"", "Allowed nesting depth of 0 exceeded at position 17");
		expectRequirementException("foo; filter:=\"(a=(a=(a=b)))\"", true, "Allowed nesting depth of 1 exceeded at position 20");
	}
}
