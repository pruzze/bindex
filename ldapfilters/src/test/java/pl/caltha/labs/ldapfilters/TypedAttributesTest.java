package pl.caltha.labs.ldapfilters;

import static org.junit.Assert.assertEquals;
import static pl.caltha.labs.ldapfilters.FilterFactory.filter;
import static pl.caltha.labs.ldapfilters.Operator.EQUAL;
import static pl.caltha.labs.ldapfilters.SimpleFilter.newFilter;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Version;

public class TypedAttributesTest {

	@Test
	public void testStringAttribute() {
		Filter f = filter("a", EQUAL, "v");
		assertEquals("(a=v)", f.toString());
	}

	@Test
	public void testLongAttribute() {
		Filter f = filter("a", EQUAL, 1l);
		assertEquals("(a:Long=1)", f.toString());
	}

	@Test
	public void testDoubleAttribute() {
		Filter f = filter("a", EQUAL, 1.5d);
		assertEquals("(a:Double=1.5)", f.toString());
	}

	@Test
	public void testVersionttribute() {
		Filter f = filter("a", EQUAL, new Version(1, 2, 3, "qual"));
		assertEquals("(a:Version=1.2.3.qual)", f.toString());
	}

	@Test
	public void testStringListAttribute() {
		Filter f = filter("a", EQUAL, AttributeType.STRING, "a", "\\b", ",c");
		assertEquals("(a:List<String>=\"a,\\\\b,\\,c\")", f.toString());
	}

	@Test
	public void testLongListAttribute() {
		Filter f = filter("a", EQUAL, AttributeType.LONG, 1l, 2l, 3l);
		assertEquals("(a:List<Long>=\"1,2,3\")", f.toString());
	}

	@Test
	public void testDoubleListAttribute() {
		Filter f = filter("a", EQUAL, AttributeType.DOUBLE, 1.0d, 1.0e15d);
		assertEquals("(a:List<Double>=\"1.0,1.0E15\")", f.toString());
	}

	@Test
	public void testVersionListAttribute() {
		Filter f = filter("a", EQUAL, AttributeType.VERSION, new Version(1, 0,
				0), new Version(1, 2, 0));
		assertEquals("(a:List<Version>=\"1.0.0,1.2.0\")", f.toString());
	}

	@Test
	public void testInvalidListListAttribute() {
		try {
			filter("a", EQUAL, AttributeType.LIST, Collections.emptyList());
			Assert.fail("should have thrown an exception");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	// parsing

	@Test
	public void testParseStringAttribute() {
		Filter f = newFilter("a", null, EQUAL, "v");
		assertEquals("(a=v)", f.toString());
	}

	@Test
	public void testParseLongAttribute() {
		Filter f = newFilter("a", "Long", EQUAL, "1");
		assertEquals("(a:Long=1)", f.toString());
	}

	@Test
	public void testParseDoubleAttribute() {
		Filter f = newFilter("a", "Double", EQUAL, "1.5");
		assertEquals("(a:Double=1.5)", f.toString());
	}

	@Test
	public void testParseVersionttribute() {
		Filter f = newFilter("a", "Version", EQUAL, "1.2.3.qual");
		assertEquals("(a:Version=1.2.3.qual)", f.toString());
	}

	@Test
	public void testParsetringListAttribute() {
		Filter f = newFilter("a", "List<String>", EQUAL, "\"a,\\\\b,\\,c\"");
		assertEquals("(a:List<String>=\"a,\\\\b,\\,c\")", f.toString());
	}

	@Test
	public void testParseLongListAttribute() {
		Filter f = newFilter("a", "List<Long>", EQUAL, "\"1,2,3\"");
		assertEquals("(a:List<Long>=\"1,2,3\")", f.toString());
	}

	@Test
	public void testParseDoubleListAttribute() {
		Filter f = newFilter("a", "List<Double>", EQUAL, "\"1.0,1.0E15\"");
		assertEquals("(a:List<Double>=\"1.0,1.0E15\")", f.toString());
	}

	@Test
	public void testParseVersionListAttribute() {
		Filter f = newFilter("a", "List<Version>", EQUAL, "\"1.0.0,1.2.0\"");
		assertEquals("(a:List<Version>=\"1.0.0,1.2.0\")", f.toString());
	}

	@Test
	public void testParseInvalidListListAttribute() {
		try {
			newFilter("a", "List<List>", EQUAL, "\"\"");
			Assert.fail("should have thrown an exception");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}
}
