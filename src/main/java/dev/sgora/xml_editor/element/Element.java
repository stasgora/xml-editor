package dev.sgora.xml_editor.element;

import javafx.collections.ListChangeListener;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public abstract class Element<E extends Node, MV> {
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String FIELD_ERROR_CLASS = "error-field";

	public final Field objectField;
	public final Object modelParentObject;

	public final E uiElement;
	private final ContextMenu errorList;

	public Element(Object modelParentObject, Field objectField, MV value) {
		this.modelParentObject = modelParentObject;
		this.objectField = objectField;
		objectField.setAccessible(true);
		uiElement = createUIElement(value);
		errorList = createFieldErrorList(uiElement);
	}

	protected abstract E createUIElement(MV value);

	public void addError(String error) {
		errorList.getItems().add(new MenuItem(error));
	}

	public void clearErrors() {
		errorList.getItems().clear();
	}

	private ContextMenu createFieldErrorList(Node node) {
		ContextMenu errorList = new ContextMenu();
		errorList.setAutoHide(false);
		errorList.getItems().addListener((ListChangeListener.Change<? extends MenuItem> c) -> {
			if(!c.getList().isEmpty() && !node.getStyleClass().contains(FIELD_ERROR_CLASS))
				node.getStyleClass().add(FIELD_ERROR_CLASS);
			else if(c.getList().isEmpty() && node.getStyleClass().contains(FIELD_ERROR_CLASS))
				node.getStyleClass().remove(FIELD_ERROR_CLASS);
		});
		node.hoverProperty().addListener(((observable, oldVal, newVal) -> {
			if(newVal)
				errorList.show(node, Side.RIGHT, 10, 0);
			else
				errorList.hide();
		}));
		return errorList;
	}
}
