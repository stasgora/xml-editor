package dev.sgora.xml_editor.element.position;

import javafx.scene.Node;

import java.lang.reflect.Field;

public class FieldPosition<V> implements ElementPosition<V> {
	public final Field objectField;
	public final Object modelParentObject;

	public FieldPosition(Field objectField, Object modelParentObject) {
		this.objectField = objectField;
		this.modelParentObject = modelParentObject;
		objectField.setAccessible(true);
	}

	@Override
	public void setModelValue(V value, Node uiElement) throws IllegalAccessException {
		objectField.set(modelParentObject, value);
	}
}
