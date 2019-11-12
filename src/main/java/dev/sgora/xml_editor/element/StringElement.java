package dev.sgora.xml_editor.element;

import dev.sgora.xml_editor.element.position.ElementPosition;

public class StringElement extends TextFieldElement<String> {
	public StringElement(String value, ElementPosition<String> position) {
		super(value, position);
		init();
	}

	@Override
	protected String convertElementToModelValue(String value) {
		return value;
	}

	@Override
	protected String convertModelToElementValue(String value) {
		return value;
	}

}
