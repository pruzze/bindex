package pl.caltha.labs.ldapfilters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static pl.caltha.labs.ldapfilters.FilterFactory.and;
import static pl.caltha.labs.ldapfilters.FilterFactory.filter;
import static pl.caltha.labs.ldapfilters.FilterFactory.not;
import static pl.caltha.labs.ldapfilters.FilterFactory.requirement;
import static pl.caltha.labs.ldapfilters.Operator.APPROX;
import static pl.caltha.labs.ldapfilters.Operator.EQUAL;
import static pl.caltha.labs.ldapfilters.Operator.GREATER_EQ;
import static pl.caltha.labs.ldapfilters.Operator.LESS_EQ;
import static pl.caltha.labs.ldapfilters.Operator.PRESENT;
import static pl.caltha.labs.ldapfilters.Operator.SUBSTRING;

import java.util.Collections;

import org.junit.Test;
import org.osgi.framework.Version;

public class FactoryTest {
	@Test
	public void testSimple() {
		Filter filter = filter("a", EQUAL, "val");
		assertEquals("(a=val)", filter.toString());
	}

	@Test
	public void testAnd() {
		Filter filter = and(filter("a", EQUAL, "val"),
				filter("b", APPROX, "bzz"));
		assertEquals("(&(a=val)(b~=bzz))", filter.toString());
	}

	@Test
	public void testOr() {
		Filter filter = and(filter("a", EQUAL, "val"), filter("b", PRESENT));
		assertEquals("(&(a=val)(b=*))", filter.toString());
	}

	@Test
	public void testNot() {
		Filter filter = and(filter("a", GREATER_EQ, "1"),
				not(filter("a", LESS_EQ, "3")));
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

	@Test
	public void testNested1() {
		Filter filter = filter("osgi.wiring.package",
				filter("osgi.wiring.package", EQUAL, "osgi.wiring.framework"));
		assertEquals(
				"(osgi.wiring.package=(osgi.wiring.package=osgi.wiring.framework))",
				filter.toString());
	}

	@Test
	public void testNested2() {
		Filter filter = filter(
				"osgi.wiring.package",
				and(filter("osgi.wiring.package", EQUAL,
						"osgi.wiring.framework"),
						filter("version", GREATER_EQ, new Version(1, 6, 0))));
		assertEquals(
				"(osgi.wiring.package=(&(osgi.wiring.package=osgi.wiring.framework)(version:Version>=1.6.0)))",
				filter.toString());
	}

	@Test
	public void testNested3() {
		Filter filter = filter(
				"osgi.resource.capability",
				and(filter(
						"osgi.wiring.package",
						and(filter("osgi.wiring.package", EQUAL, "javax.mail"),
								filter("version", GREATER_EQ, new Version(1, 4,
										0)),
								filter("version", LESS_EQ, new Version(2, 0, 0)))),
						filter("osgi.identity",
								filter("license", EQUAL,
										"http://www.opensource.org/licenses/EPL-1.0"))));
		assertEquals(
				"(osgi.resource.capability=(&(osgi.wiring.package=(&(osgi.wiring.package=javax.mail)(version:Version>=1.4.0)(version:Version<=2.0.0)))(osgi.identity=(license=http://www.opensource.org/licenses/EPL-1.0))))",
				filter.toString());
	}

	@Test
	public void testOptionalRequirement() {
		Filter filter = requirement(
				"osgi.wiring.package",
				and(filter("osgi.wiring.package", EQUAL, "org.osgi.framework"),
						filter("version", GREATER_EQ, new Version(1, 6, 0))),
				Collections.singletonMap("resolution", "optional"));
		assertEquals(
				"osgi.wiring.package;filter:=\"(&(osgi.wiring.package=org.osgi.framework)(version:Version>=1.6.0))\";resolution:=optional",
				filter.toString());
	}

	@Test
	public void testMultipleRequirement() {
		Filter filter = requirement(
				"osgi.wiring.package",
				and(filter("osgi.wiring.package", EQUAL, "org.osgi.framework"),
						filter("version", GREATER_EQ, new Version(1, 6, 0))),
				Collections.singletonMap("cardinality", "multiple"));
		assertEquals(
				"osgi.wiring.package;filter:=\"(&(osgi.wiring.package=org.osgi.framework)(version:Version>=1.6.0))\";cardinality:=multiple",
				filter.toString());
	}

	@Test
	public void testRequirement() {
		Filter filter = requirement(
				"osgi.wiring.package",
				and(filter("osgi.wiring.package", EQUAL, "org.osgi.framework"),
						filter("version", GREATER_EQ, new Version(1, 6, 0))));
		assertEquals(
				"osgi.wiring.package;filter:=\"(&(osgi.wiring.package=org.osgi.framework)(version:Version>=1.6.0))\"",
				filter.toString());
	}

	// RFC 187
	@Test
	public void testComplexRequirement() {
		Filter filter = requirement(
				"osgi.resource.capability",
				and(filter(
						"osgi.wiring.package",
						and(filter("osgi.wiring.package", EQUAL, "javax.mail"),
								filter("version", GREATER_EQ, new Version(1, 4,
										0)),
								filter("version", LESS_EQ, new Version(2, 0, 0)))),
						filter("osgi.identity",
								filter("license", EQUAL,
										"http://www.opensource.org/licenses/EPL-1.0"))));
		assertEquals(
				"osgi.resource.capability;filter:=\"(&(osgi.wiring.package=(&(osgi.wiring.package=javax.mail)(version:Version>=1.4.0)(version:Version<=2.0.0)))(osgi.identity=(license=http://www.opensource.org/licenses/EPL-1.0)))\"",
				filter.toString());
	}

	@Test
	public void testDuplicatedFilter() {
		try {
			requirement(
					"osgi.wiring.package",
					and(filter("osgi.wiring.package", EQUAL,
							"org.osgi.framework"),
							filter("version", GREATER_EQ, new Version(1, 6, 0))),
					Collections
							.singletonMap("filter",
									"(&(osgi.wiring.package=org.osgi.framework)(version>=1.6.0))"));
			fail("should have thrown an exception");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}
}
