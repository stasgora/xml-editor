package dev.sgora.xml_editor.element;

public enum ElementTitleType {
	ROOT("xml-title"),
	SUB("xml-subtitle");

	public final String styleClass;

	ElementTitleType(String styleClass) {
		this.styleClass = styleClass;
	}
}
