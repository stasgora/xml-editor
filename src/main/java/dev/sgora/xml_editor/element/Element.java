package dev.sgora.xml_editor.element;

import javafx.scene.Node;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Element<E extends Node, V> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	public final Object modelObject;
	public final Field objectField;
	public final E uiElement;

	public Element(Object modelObject, Field objectField, V value) {
		this.modelObject = modelObject;
		this.objectField = objectField;
		uiElement = createUiElement(value);
		objectField.setAccessible(true);
	}

	protected abstract E createUiElement(V value);

	protected void updateModelValue(V value) {
		try {
			objectField.set(modelObject, value);
		} catch (IllegalAccessException e) {
			logger.log(Level.SEVERE, "Setting model value failed", e);
		}
	}
}
