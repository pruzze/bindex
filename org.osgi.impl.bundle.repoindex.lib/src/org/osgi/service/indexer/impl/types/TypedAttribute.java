package org.osgi.service.indexer.impl.types;

import org.osgi.service.indexer.impl.Schema;
import org.osgi.service.indexer.impl.util.Tag;

public class TypedAttribute {

	private final String name;
	private final Type type;
	private final Object value;

	public TypedAttribute(String name, Type type, Object value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public static TypedAttribute create(String name, Object value) {
		return new TypedAttribute(name, Type.typeOf(value), value);
	}

	public Tag toXML() {
		Tag tag = new Tag(Schema.R5.ELEM_ATTRIBUTE);
		tag.addAttribute(Schema.R5.ATTR_ATTRIBUTE_NAME, name);

		if (type.isList() || type.getType() != ScalarType.String) {
			tag.addAttribute(Schema.R5.ATTR_ATTRIBUTE_TYPE, type.toString());
		}

		tag.addAttribute(Schema.R5.ATTR_ATTRIBUTE_VALUE, type.convertToString(value));

		return tag;
	}
}
