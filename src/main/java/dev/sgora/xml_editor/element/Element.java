package dev.sgora.xml_editor.element;

import dev.sgora.xml_editor.services.ui.element.UIElementFactory;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @param <E> UI element type
 * @param <EV> UI elements value type
 * @param <MV> Model value type
 */
public abstract class Element<E extends Node, EV, MV> {
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	public final Field objectField;
	public final Object modelObject;

	public final E uiElement;
	private ContextMenu errorList;

	public Element(Object modelObject, Field objectField, MV value) {
		this.modelObject = modelObject;
		this.objectField = objectField;
		uiElement = createUiElement(value);
		objectField.setAccessible(true);
		errorList = UIElementFactory.createFieldErrorList(uiElement);
	}

	protected abstract E createUiElement(MV value);

	protected abstract MV convertElementToModelValue(EV value) throws ValueConversionError;
	protected abstract EV convertModelToElementValue(MV value);

	protected void updateModelValue(EV value) {
		try {
			objectField.set(modelObject, convertElementToModelValue(value));
		} catch (IllegalAccessException e) {
			logger.log(Level.SEVERE, "Setting model value failed", e);
		} catch (ValueConversionError e) {
			addError(e.getMessage());
		}
	}

	public void addError(String error) {
		errorList.getItems().add(new MenuItem(error));
	}

	public void clearErrors() {
		errorList.getItems().clear();
	}
}
