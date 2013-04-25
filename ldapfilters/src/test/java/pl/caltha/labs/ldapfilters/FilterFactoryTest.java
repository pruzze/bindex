package pl.caltha.labs.ldapfilters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static pl.caltha.labs.ldapfilters.FilterFactory.and;
import static pl.caltha.labs.ldapfilters.FilterFactory.filter;
import static pl.caltha.labs.ldapfilters.FilterFactory.not;
import static pl.caltha.labs.ldapfilters.Operator.APPROX;
import static pl.caltha.labs.ldapfilters.Operator.EQUAL;
import static pl.caltha.labs.ldapfilters.Operator.GREATER_EQ;
import static pl.caltha.labs.ldapfilters.Operator.LESS_EQ;
import static pl.caltha.labs.ldapfilters.Operator.PRESENT;
import static pl.caltha.labs.ldapfilters.Operator.SUBSTRING;

import org.junit.Test;

public class FilterFactoryTest {	
	@Test
	public void testSimple() {
		Filter filter = filter("a", EQUAL, "val");
		assertEquals("(a=val)", filter.toString());
	}
	
	@Test
	public void testAnd() {
		Filter filter = and(filter("a", EQUAL, "val"), filter("b", APPROX, "bzz"));
		assertEquals("(&(a=val)(b~=bzz))", filter.toString());
	}
	
	@Test
	public void testOr() {
		Filter filter = and(filter("a", EQUAL, "val"), filter("b", PRESENT));
		assertEquals("(&(a=val)(b=*))", filter.toString());
	}
	
	@Test
	public void testNot() {
		Filter filter = and(filter("a", GREATER_EQ, "1"), not(filter("a", LESS_EQ, "3")));
		assertEquals("(&(a>=1)(!(a<=3)))", filter.toString());
	}
	
	@Test
	public void testSubstring() {
		Filter filter = filter("a", SUBSTRING, "*val");
		assertEquals("(a=*val)", filter.toString());
	}	
	
	@Test
	public void testMissingValue() {
		try {
			filter("a", EQUAL);
			fail("should have thrown an exception");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}
}
