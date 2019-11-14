package dev.sgora.xml_editor.element;

import dev.sgora.xml_editor.element.position.ElementPosition;
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
	public ValueElement(MV value, ElementPosition<MV> position) {
		super(value, position);
	}

	protected abstract MV convertElementToModelValue(EV value) throws ValueConversionError;
	protected abstract EV convertModelToElementValue(MV value);

	protected void updateModelValue(EV value) {
		try {
			MV modelValue = convertElementToModelValue(value);
			position.setModelValue(modelValue, uiElement);
			clearErrors(true);
		} catch (IllegalAccessException e) {
			logger.log(Level.SEVERE, "Setting model value failed", e);
		} catch (ValueConversionError e) {
			addError(e.getMessage(), true);
		}
	}
}
