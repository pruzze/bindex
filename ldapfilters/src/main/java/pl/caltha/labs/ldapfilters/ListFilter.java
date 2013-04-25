package pl.caltha.labs.ldapfilters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Version;

public class ListFilter extends SimpleFilter<List<?>> {

	private static Pattern QUOTED = Pattern.compile("^\"(.*)\"$");

	private static Pattern UNESCAPED_COMMA = Pattern.compile("(?<!\\\\),");

	private final AttributeType elementType;

	ListFilter(String attribute, Operator operator, AttributeType elementType,
			String value) {
		super(attribute, operator, parse(elementType, value));
		this.elementType = elementType;
	}

	<T> ListFilter(String attribute, Operator operator,
			AttributeType elementType, List<T> values) {
		super(attribute, operator, values);
		if (elementType == AttributeType.LIST) {
			throw new IllegalArgumentException("Unsupported element type "
					+ elementType);
		}
		this.elementType = elementType;
	}

	private static List<?> parse(AttributeType elementType, String value) {
		Matcher m = QUOTED.matcher(value);
		if (m.matches())
			value = m.group(1);
		String[] elems = UNESCAPED_COMMA.split(value);
		List<Object> result = new ArrayList<Object>(elems.length);
		for (String elem : elems) {
			result.add(parseElement(elementType,
					elem.trim().replace("\\\\", "\\").replace("\\\"", "\"")
							.replace("\\,", ",")));
		}
		return result;
	}

	private static Object parseElement(AttributeType elementType, String element) {
		switch (elementType) {
		case STRING:
			return element;
		case LONG:
			return Long.parseLong(element);
		case DOUBLE:
			return Double.parseDouble(element);
		case VERSION:
			return new Version(element);
		default:
			throw new IllegalArgumentException("Unsupported element type "
					+ elementType);
		}
	}

	@Override
	public AttributeType getAttributeType() {
		return AttributeType.VERSION;
	}

	public AttributeType getElementType() {
		return elementType;
	}

	public <V> V accept(FilterVisitor<V> visitor, V data) {
		return visitor.visit(this, data);
	}

	@Override
	protected void appendType(StringBuilder buff) {
		buff.append(":List<");
		switch (elementType) {
		case STRING:
			buff.append("String");
			break;
		case LONG:
			buff.append("Long");
			break;
		case DOUBLE:
			buff.append("Double");
			break;
		case VERSION:
			buff.append("Version");
			break;
		default:
			throw new IllegalArgumentException("Unsupported element type "
					+ elementType);
		}
		buff.append(">");
	}

	@Override
	protected void appendValue(StringBuilder buff) {
		buff.append("\"");
		Iterator<?> i = getValue().iterator();
		while (i.hasNext()) {
			Object value = i.next();
			if (value instanceof String) {
				String s = (String) value;
				buff.append(s.replace("\\", "\\\\").replace("\"", "\\\"")
						.replace(",", "\\,"));
			} else
				buff.append(value.toString());
			if (i.hasNext())
				buff.append(',');
		}
		buff.append("\"");
	}
}
