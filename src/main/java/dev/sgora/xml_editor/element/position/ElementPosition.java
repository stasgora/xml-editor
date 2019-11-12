package dev.sgora.xml_editor.element.position;

import javafx.scene.Node;

public interface ElementPosition<V> {
	void setModelValue(V value, Node uiElement) throws IllegalAccessException;
}
