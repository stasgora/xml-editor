package dev.sgora.xml_editor.element;

import java.lang.reflect.Field;

public class StringElement extends TextFieldElement<String> {

	public StringElement(Object modelObject, Field objectField, String value) {
		super(modelObject, objectField, value);
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
