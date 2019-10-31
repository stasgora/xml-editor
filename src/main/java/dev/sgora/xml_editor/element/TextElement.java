package dev.sgora.xml_editor.element;

import javafx.scene.control.TextField;

import java.lang.reflect.Field;

public class TextElement extends Element<TextField, String> {
	private static final String TEXT_FIELD_CLASS = "xml-field";

	public TextElement(Object modelObject, Field objectField, String value) {
		super(modelObject, objectField, value);
	}

	@Override
	protected TextField createUiElement(String value) {
		TextField field = new TextField(value);
		field.getStyleClass().add(TEXT_FIELD_CLASS);
		field.textProperty().addListener((observable, oldVal, newVal) -> updateModelValue(newVal));
		return field;
	}

	@Override
	public void updateModelValue(String value) {
		super.updateModelValue(value);
	}
}
