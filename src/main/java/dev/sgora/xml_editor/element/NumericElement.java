package dev.sgora.xml_editor.element;

import javafx.scene.control.TextField;

import java.lang.reflect.Field;

public class NumericElement<N extends Number> extends Element<TextField, N> {

	public NumericElement(Object modelObject, Field objectField, N value) {
		super(modelObject, objectField, value);
	}

	@Override
	protected TextField createUiElement(N value) {
		return null;
	}
}
