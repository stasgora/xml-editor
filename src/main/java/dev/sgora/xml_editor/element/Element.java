package dev.sgora.xml_editor.element;

import dev.sgora.xml_editor.element.position.ElementPosition;
import javafx.collections.ListChangeListener;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.logging.Logger;

public abstract class Element<E extends Node, MV> {
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String FIELD_ERROR_CLASS = "error-field";

	public final Class modelType;
	protected final ElementPosition<MV> position;

	public E uiElement;
	private ContextMenu errorList;
	private final MV value;

	public Element(MV value, ElementPosition<MV> position) {
		this.value = value;
		this.position = position;
		this.modelType = value.getClass();
	}

	protected void init() {
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
