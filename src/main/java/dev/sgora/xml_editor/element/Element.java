package dev.sgora.xml_editor.element;

import dev.sgora.observetree.Observable;
import dev.sgora.xml_editor.element.position.ElementPosition;
import javafx.collections.ListChangeListener;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class Element<E extends Node, MV> extends Observable {
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String FIELD_ERROR_CLASS = "error-field";

	public final Class modelType;
	public final MV modelValue;
	public E uiElement;

	public final ElementPosition<MV> position;
	private ContextMenu errorList;

	public Element(MV modelValue, ElementPosition<MV> position) {
		this.modelValue = modelValue;
		this.position = position;
		this.modelType = modelValue.getClass();
	}

	protected void init() {
		uiElement = createUIElement(modelValue);
		errorList = createFieldErrorList(uiElement);
	}

	protected abstract E createUIElement(MV value);

	public void addError(String error, boolean manualError) {
		clearErrors(true); // Keep one error at a time for now
		MenuItem errorItem = new MenuItem(error);
		errorItem.setUserData(manualError);
		errorList.getItems().add(errorItem);
	}

	public boolean hasErrors() {
		return errorList.getItems().size() > 0;
	}

	public void clearErrors(boolean clearManual) {
		if(clearManual)
			errorList.getItems().clear();
		else
			errorList.getItems().removeAll(errorList.getItems().stream().filter(error -> !(boolean) error.getUserData()).collect(Collectors.toList()));
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
