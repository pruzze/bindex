package pl.caltha.labs.ldapfilters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class LexerTest {

	private FilterParser start(String input) {
		return new FilterParser(new StringReader(input));
	}

	@Test
	public void testEquals() throws IOException {
		FilterParser l = start("(attr=value)");
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
		FilterParser l = start("(attr=value*)");
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
		FilterParser l = start("(attr=*value)");
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
		FilterParser l = start("(attr~=value)");
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
		FilterParser l = start("(attr>=1.0.0)");
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
		FilterParser l = start("(attr<=1.0.0)");
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
		FilterParser l = start("(attr=*)");
		assertEquals(FilterParser.YYSIMPLE, l.yylex());
		assertEquals(FilterParser.YYATTRNAME, l.yylex());
		assertEquals("attr", l.yytext());
		assertEquals(FilterParser.YYPRES, l.yylex());
		assertEquals("=*", l.yytext());
		assertEquals(FilterParser.YYDONE, l.yylex());
	}

	@Test
	public void testEscapes() throws IOException {
		FilterParser l = start("(attr=\\(va\\\\lue\\))");
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
		FilterParser l = start("(&(attr1=val1)(attr2>=1))");
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
		FilterParser l = start("(|(attr1<=1 2)(attr2>=1=2))");
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
		FilterParser l = start("(&(version>=1.1)(!(version<=2)))");

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
		FilterParser l = start("(a:Long=1)");

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
		FilterParser l = start("(a:Double=1)");

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
		FilterParser l = start("(a:Version=1.0.0)");

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
		FilterParser l = start("(a:List<String>=a,b,c)");

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
		FilterParser l = start("(a:List<Long>=1,2,3)");

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
		FilterParser l = start("(a:List<Double>=1.0,1.5)");

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
		FilterParser l = start("(a:List<Version>=1.0.0,1.2.0)");

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
		FilterParser l = start("(osgi.wiring.package=(osgi.wiring.package=osgi.wiring.framework))");
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
		FilterParser l = start("(osgi.wiring.package=(&(osgi.wiring.package=osgi.wiring.framework)(version:Version>=1.6.0)))");
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
		FilterParser l = start("(osgi.resource.capability=(&(osgi.wiring.package=(&(osgi.wiring.package=javax.mail)(version:Version>=1.4.0)(version:Version<=2.0.0)))(osgi.identity=(license=http://www.opensource.org/licenses/EPL-1.0))))");
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

	private void expectException(String filter, String message) {
		try {
			FilterParser l = start(filter);
			int yystate;
			do {
				yystate = l.yylex();
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
	}
}
