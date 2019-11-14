package dev.sgora.xml_editor.element;

import dev.sgora.xml_editor.element.position.ElementPosition;
import dev.sgora.xml_editor.element.position.ListPosition;
import dev.sgora.xml_editor.services.ui.element.EmptyModelFactory;
import dev.sgora.xml_editor.services.ui.element.UIElementFactory;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ListElement<M extends List> extends ComplexElement<M> {
	private static final String MULTI_TEXT_FIELD_CLASS = "xml-multi-field";
	public static Runnable onListElementsChanged;

	public ListElement(M value, ElementPosition position) {
		super(value, position);
	}

	@Override
	protected Pane createUIElement(M list) {
		VBox listContainer = UIElementFactory.createAlignedVBox(Pos.TOP_CENTER, 5);
		ObservableList<Node> children = listContainer.getChildren();
		for (int i = 0; i < list.size(); i++) {
			Object listElement = list.get(i);
			Element childElement = mapElement(listElement, new ListPosition(list, listContainer));
			children.add(wrapFieldAsListElement(childElement));
		}
		return listContainer;
	}

	private HBox wrapFieldAsListElement(Element listElement) {
		HBox layout = new HBox(5);
		layout.setAlignment(Pos.CENTER_LEFT);
		layout.getStyleClass().add(MULTI_TEXT_FIELD_CLASS);
		Button removeField = new Button("-");
		ListPosition position = (ListPosition) listElement.position;
		removeField.setOnAction(event -> {
			if(position.listElement.getChildren().size() > 1) {
				position.removeElement(layout);
				onListElementsChanged.run();
			}
		});
		removeField.setTooltip(new Tooltip("Remove item"));
		Button addField = new Button("+");
		addField.setTooltip(new Tooltip("Add item after"));
		addField.setOnAction(event -> {
			int index = position.listElement.getChildren().indexOf(layout) + 1;
			Object model = EmptyModelFactory.createEmptyModel(listElement.modelType, null);
			HBox element = wrapFieldAsListElement(mapElement(model, position));
			position.addElement(index, model, element);
			onListElementsChanged.run();
		});
		layout.getChildren().addAll(listElement.uiElement, removeField, addField);
		return layout;
	}
}
