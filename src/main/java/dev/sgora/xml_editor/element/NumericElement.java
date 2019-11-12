package dev.sgora.xml_editor.element;

import dev.sgora.xml_editor.element.position.ElementPosition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class NumericElement extends TextFieldElement<Number> {
	public NumericElement(Number value, ElementPosition<Number> position) {
		super(value, position);
		init();
	}

	@Override
	protected Number convertElementToModelValue(String value) throws ValueConversionError {
		try {
			return (Number) modelType.cast(modelType.getConstructor(String.class).newInstance(value));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new ValueConversionError("Not a valid number", e);
		}
	}

	@Override
	protected String convertModelToElementValue(Number value) {
		return value.toString();
	}
}
