package dev.sgora.xml_editor.element;

import dev.sgora.xml_editor.element.position.ElementPosition;
import javafx.scene.control.TextField;

import java.lang.reflect.Field;

public abstract class TextFieldElement<MV> extends ValueElement<TextField, String, MV> {
	private static final String TEXT_FIELD_CLASS = "xml-field";

	public TextFieldElement(MV value, ElementPosition<MV> position) {
		super(value, position);
	}

	@Override
	protected TextField createUIElement(MV value) {
		TextField field = new TextField(convertModelToElementValue(value));
		field.getStyleClass().add(TEXT_FIELD_CLASS);
		field.textProperty().addListener((observable, oldVal, newVal) -> updateModelValue(newVal));
		return field;
	}
}
