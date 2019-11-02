package dev.sgora.xml_editor.element;

import javafx.scene.Node;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * @param <E> UI element type
 * @param <EV> UI elements value type
 * @param <MV> Model value type
 */
public abstract class ValueElement<E extends Node, EV, MV> extends Element<E, MV> {

	private Consumer<MV> setModelValue;

	public ValueElement(Object modelObject, Field objectField, MV value) {
		super(modelObject, objectField, value);
	}

	public void withCustomSetFunction(Consumer<MV> setModelValue) {
		this.setModelValue = setModelValue;
	}

	protected abstract MV convertElementToModelValue(EV value) throws ValueConversionError;
	protected abstract EV convertModelToElementValue(MV value);

	protected void updateModelValue(EV value) {
		try {
			MV modelValue = convertElementToModelValue(value);
			if (setModelValue == null)
				objectField.set(modelParentObject, modelValue);
			else
				setModelValue.accept(modelValue);
		} catch (IllegalAccessException e) {
			logger.log(Level.SEVERE, "Setting model value failed", e);
		} catch (ValueConversionError e) {
			addError(e.getMessage());
		}
	}
}
