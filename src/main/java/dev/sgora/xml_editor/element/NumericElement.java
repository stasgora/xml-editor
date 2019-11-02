package dev.sgora.xml_editor.element;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class NumericElement extends TextFieldElement<Number> {
	private final Class<? extends Number> numberType;

	public NumericElement(Object modelObject, Field objectField, Number value) {
		super(modelObject, objectField, value);
		this.numberType = value.getClass();
	}

	@Override
	protected Number convertElementToModelValue(String value) throws ValueConversionError {
		try {
			return numberType.cast(objectField.getType().getConstructor(String.class).newInstance(value));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new ValueConversionError("Not a valid number", e);
		}
	}

	@Override
	protected String convertModelToElementValue(Number value) {
		return value.toString();
	}
}
