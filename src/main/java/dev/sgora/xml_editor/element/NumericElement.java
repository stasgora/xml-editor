package dev.sgora.xml_editor.element;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class NumericElement<N extends Number> extends TextFieldElement<N> {
	private final Class<N> numberType;

	public NumericElement(Object modelObject, Field objectField, N value, Class<N> numberType) {
		super(modelObject, objectField, value);
		this.numberType = numberType;
	}

	@Override
	protected N convertElementToModelValue(String value) throws ValueConversionError {
		try {
			return numberType.cast(objectField.getType().getConstructor(String.class).newInstance(value));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new ValueConversionError("Not a valid number", e);
		}
	}

	@Override
	protected String convertModelToElementValue(N value) {
		return value.toString();
	}
}
