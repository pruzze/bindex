package org.osgi.service.indexer.impl.types;

import org.osgi.service.indexer.impl.Schema;
import org.osgi.service.indexer.impl.util.Tag;

public class RFC112TypedAttribute {
	
	private String name;
	private Type type;
	private Object value;
	private boolean isResourceAttr;

	private RFC112TypedAttribute(String name, Type type, Object value, boolean isResourceAttr) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.isResourceAttr = isResourceAttr;
	}

	public static RFC112TypedAttribute createResourceAttribute(String name, Object value) {
		return new RFC112TypedAttribute(name, typeOf(value), value, true);
	}

	public static RFC112TypedAttribute createCapabilityAttribute(String name, Object value) {
		return new RFC112TypedAttribute(name, typeOf(value), value, false);
	}

	public Tag toXML() {
		Tag tag;
		if (isResourceAttr) {
			tag = new Tag(name);
			tag.addContent(type.convertToString(value));
		} else {
			tag = new Tag("p");
			tag.addAttribute("n", name);
			if (type.isList() || type.getType() != ScalarType.String) {
				tag.addAttribute("t", type.toString());
			}			
			tag.addAttribute("v", type.convertToString(value));
		}
		return tag;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static Type typeOf(Object value) {
		final Type r5type = Type.typeOf(value);
		return new Type(r5type.getType(), r5type.isList()) {
			@Override
			public String toString() {
				return isList() ? "set" : getType().name().toLowerCase();
			}
		};
	}
}
