package dev.sgora.xml_editor.element;

import dev.sgora.xml_editor.element.enums.ElementLayout;
import dev.sgora.xml_editor.services.ErrorUtil;
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

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ListElement<M> extends ComplexElement<M> {
	private static final String MULTI_TEXT_FIELD_CLASS = "xml-multi-field";

	public ListElement(Object modelObject, Field objectField, M value, ElementLayout layout) {
		super(modelObject, objectField, value, layout);
	}

	@Override
	protected Pane createUIElement(M value) {
		VBox listContainer = UIElementFactory.createAlignedVBox(Pos.TOP_CENTER, 5);
		List list = (List) value;
		ObservableList<Node> children = listContainer.getChildren();
		try {
			Object modelObject = objectField.get(modelParentObject);
		} catch (IllegalAccessException e) {
			logger.log(Level.WARNING, "Resolving field value failed", e);
		}
		for (int i = 0; i < list.size(); i++) {
			Object listElement = list.get(i);
			BiConsumer<Node, Object> listValueSetter = (uiElem, val) -> list.set(findChildIndex(uiElem), val);
			Function<Object, Node> modelToElement = ErrorUtil.wrap((ErrorUtil.UnsafeFunction<Object, Node>)
					model -> mapElement(model, listValueSetter, ElementLayout.HORIZONTAL), "Mapping model failed");
			Element childElement = mapElement(listElement, listValueSetter, ElementLayout.HORIZONTAL);
			children.add(wrapFieldAsListElement(childElement, modelToElement, list::add, list::remove, listContainer));
		}
		return listContainer;
	}

	private HBox wrapFieldAsListElement(Object listElement, Function<Object, Node> modelToElement,
	                                                  BiConsumer<Integer, Object> onElementAdded, IntConsumer onElementRemoved, VBox container) {
		HBox layout = new HBox(5);
		layout.setAlignment(Pos.CENTER_LEFT);
		layout.getStyleClass().add(MULTI_TEXT_FIELD_CLASS);
		Button removeField = new Button("-");
		removeField.setOnAction(event -> {
			if(container.getChildren().size() > 1) {
				onElementRemoved.accept(container.getChildren().indexOf(layout));
				container.getChildren().remove(layout);
			}
		});
		removeField.setTooltip(new Tooltip("Remove item"));
		Button addField = new Button("+");
		addField.setTooltip(new Tooltip("Add item after"));
		addField.setOnAction(event -> {
			int index = container.getChildren().indexOf(layout) + 1;
			Object model = EmptyModelFactory.createEmptyModel(listElement.getClass(), null);
			HBox element = wrapFieldAsListElement(modelToElement.apply(model), modelToElement, onElementAdded, onElementRemoved, container);
			onElementAdded.accept(index, model);
			container.getChildren().add(index, element);
		});
		layout.getChildren().addAll(field, removeField, addField);
		return layout;
	}

	private int findChildIndex(Node node) {
		ObservableList<Node> children = uiElement.getChildren();
		for (int j = 0; j < this.children.size(); j++) {
			Node child = children.get(j);
			if (((HBox) child).getChildren().get(0) == node)
				return j;
		}
		return -1;
	}
}
