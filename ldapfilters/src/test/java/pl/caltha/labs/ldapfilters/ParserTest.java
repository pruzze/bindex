package pl.caltha.labs.ldapfilters;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ParserTest {
	
	@Test
	public void testEqual() {
		Filter f = FilterParser.parse("(a=v)");
		assertEquals("(a=v)", f.toString());
	}
	
	@Test
	public void testSubstr1() {
		Filter f = FilterParser.parse("(a=v*)");
		assertEquals("(a=v*)", f.toString());
		assertEquals(Operator.SUBSTRING, ((SimpleFilter<?>)f).getOperator());
	}

	@Test
	public void testSubstr2() {
		Filter f = FilterParser.parse("(a=*v)");
		assertEquals("(a=*v)", f.toString());
		assertEquals(Operator.SUBSTRING, ((SimpleFilter<?>)f).getOperator());
	}
	
	@Test
	public void testApprox() {
		Filter f = FilterParser.parse("(a~=v)");
		assertEquals("(a~=v)", f.toString());
	}

	@Test
	public void testGreaterEq() {
		Filter f = FilterParser.parse("(a>=1)");
		assertEquals("(a>=1)", f.toString());
	}
	
	@Test
	public void testLessEq() {
		Filter f = FilterParser.parse("(a<=2)");
		assertEquals("(a<=2)", f.toString());
	}
	
	@Test
	public void testPresent() {
		Filter f = FilterParser.parse("(a=*)");
		assertEquals("(a=*)", f.toString());
		assertEquals(Operator.PRESENT, ((SimpleFilter<?>)f).getOperator());
	}

	@Test
	public void testEscapes() {
		Filter f = FilterParser.parse("(attr=\\(va\\\\lue\\))");
		assertEquals("(va\\lue)", ((StringFilter)f).getValue());
		assertEquals("(attr=\\(va\\\\lue\\))", f.toString());
	}
	
	@Test
	public void testAnd() {
		Filter f = FilterParser.parse("(&(a=v)(b=x))");
		assertEquals("(&(a=v)(b=x))", f.toString());
	}

	@Test
	public void testOr() {
		Filter f = FilterParser.parse("(|(a=v)(b=x))");
		assertEquals("(|(a=v)(b=x))", f.toString());
	}

	@Test
	public void testNot() {
		Filter f = FilterParser.parse("(!(a=v))");
		assertEquals("(!(a=v))", f.toString());
	}
	
	@Test
	public void testComplex() {
		Filter f = FilterParser.parse("(&(a=v)(|(b=x)(c=z))(!(d=q)))");
		assertEquals("(&(a=v)(|(b=x)(c=z))(!(d=q)))", f.toString());
	}
	
	@Test
	public void testLongAttribute() {
		Filter f = FilterParser.parse("(a:Long=1)");
		assertEquals("(a:Long=1)", f.toString());
	}

	@Test
	public void testExplicitStringAttribute() {
		Filter f = FilterParser.parse("(a:String=x)");
		assertEquals("(a=x)", f.toString());
	}
	
	@Test
	public void testDoubleAttribute() {
		Filter f = FilterParser.parse("(a:Double=1.0)");
		assertEquals("(a:Double=1.0)", f.toString());
	}
	
	@Test
	public void testVersionAttribute() {
		Filter f = FilterParser.parse("(a:Version=1.0.0)");
		assertEquals("(a:Version=1.0.0)", f.toString());
	}	
	
	@Test
	public void testNested1() throws Exception {
		Filter f = FilterParser.parse("(osgi.wiring.package=(osgi.wiring.package=osgi.wiring.framework))", 1);
		assertEquals("(osgi.wiring.package=(osgi.wiring.package=osgi.wiring.framework))", f.toString());
	}
	
	@Test
	public void testNested2() throws Exception {
		Filter f = FilterParser.parse("(osgi.wiring.package=(&(osgi.wiring.package=osgi.wiring.framework)(version:Version>=1.6.0)))", 1);
		assertEquals("(osgi.wiring.package=(&(osgi.wiring.package=osgi.wiring.framework)(version:Version>=1.6.0)))", f.toString());
	}
	
	@Test
	public void testNested3() throws Exception {
		Filter f = FilterParser.parse("(osgi.resource.capability=(&(osgi.wiring.package=(&(osgi.wiring.package=javax.mail)(version:Version>=1.4.0)(version:Version<=2.0.0)))(osgi.identity=(license=http://www.opensource.org/licenses/EPL-1.0))))", 2);
		assertEquals("(osgi.resource.capability=(&(osgi.wiring.package=(&(osgi.wiring.package=javax.mail)(version:Version>=1.4.0)(version:Version<=2.0.0)))(osgi.identity=(license=http://www.opensource.org/licenses/EPL-1.0))))", f.toString());
	}
	
	@Test
	public void testRequirements1() {
		Requirements r = RequirementParser.parseRequirements("test.1", false);
		assertEquals("test.1", r.toString());
	}
	
	@Test
	public void testRequirements2() {
		Requirements r = RequirementParser.parseRequirements("test.1,\ntest.2", false);
		assertEquals("test.1,test.2", r.toString());
	}
	
	@Test
	public void testRequirementDirectives1() {
		Requirements r = RequirementParser.parseRequirements(" test.1;a:=b", false);
		assertEquals("test.1;a:=b", r.toString());
	}
	
	@Test
	public void testRequirementDirectives2() {
		Requirements r = RequirementParser.parseRequirements("test.1; a := b; e := f", false);
		assertEquals("test.1;a:=b;e:=f", r.toString());
	}
	
	@Test
	public void testRequirementDirectives3() {
		Requirements r = RequirementParser.parseRequirements("test.1; a:=b, test.2; e:=f ", false);
		assertEquals("test.1;a:=b,test.2;e:=f", r.toString());
	}
	
	@Test
	public void testRequirementFilter1() {
		Requirements r = RequirementParser.parseRequirements("test.1; filter:=\"(a=b)\"", false);
		assertEquals("test.1;filter:=\"(a=b)\"", r.toString());
	}
	
	@Test
	public void testRequirementFilter2() {
		Requirements r = RequirementParser.parseRequirements("test.1; d1:=v1; filter:=\"(a=b)\"; d2:=v2", false);
		assertEquals("test.1;filter:=\"(a=b)\";d1:=v1;d2:=v2", r.toString());
	}
	
	@Test
	public void testRequirementFilter3() {
		Requirements r = RequirementParser.parseRequirements("test.1; filter:=\"(a=b)\", test.2; filter:=\"(c=d)\"", false);
		assertEquals("test.1;filter:=\"(a=b)\",test.2;filter:=\"(c=d)\"", r.toString());
	}

	@Test
	public void testRequirementNestedFilter1() {
		Requirements r = RequirementParser.parseRequirements("test.1; filter:=\"(&(a=(a=b)))\"", true);
		assertEquals("test.1;filter:=\"(&(a=(a=b)))\"", r.toString());
	}
}
