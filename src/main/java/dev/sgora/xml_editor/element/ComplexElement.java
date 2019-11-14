package dev.sgora.xml_editor.element;

import dev.sgora.xml_editor.element.enums.ElementLayout;
import dev.sgora.xml_editor.element.enums.ElementTitleType;
import dev.sgora.xml_editor.element.position.ElementPosition;
import dev.sgora.xml_editor.element.position.FieldPosition;
import dev.sgora.xml_editor.services.ui.element.EmptyModelFactory;
import dev.sgora.xml_editor.services.ui.element.UIElementFactory;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ComplexElement<M> extends Element<Pane, M> {
	public final List<Element> children = new ArrayList<>();

	private final ElementLayout layout;
	public final boolean root;
	private final boolean labelChildren;

	public static Consumer<ComplexElement> registerElement;

	public ComplexElement(M value, ElementPosition<M> position, boolean isRoot, boolean labelChildren) {
		super(value, position);
		layout = position instanceof FieldPosition ? ElementLayout.VERTICAL : ElementLayout.HORIZONTAL;
		root = isRoot;
		this.labelChildren = labelChildren;
		init();
		registerElement.accept(this);
	}

	public ComplexElement(M value, ElementPosition<M> position) {
		this(value, position, false, true);
	}

	public ComplexElement(M value, ElementPosition<M> position, boolean root) {
		this(value, position, root, true);
	}

	@Override
	protected Pane createUIElement(M modelObject) {
		Pane elementContainer = layout == ElementLayout.VERTICAL ? UIElementFactory.createAlignedVBox(Pos.TOP_LEFT, 5) : new HBox(10);
		elementContainer.setPadding(new Insets(10, 0, 10, 0));
		ObservableList<Node> children = elementContainer.getChildren();
		if(root) {
			children.add(UIElementFactory.createElementTitle(modelType.getSimpleName(), ElementTitleType.ROOT));
			if(layout == ElementLayout.VERTICAL)
				((VBox) elementContainer).setAlignment(Pos.TOP_CENTER);
		}
		try {
			for (Field field : modelType.getDeclaredFields()) {
				field.setAccessible(true);
				Object fieldValue = field.get(modelObject);

				Node childUIElement;
				if(fieldValue != null) {
					Element element = mapElement(fieldValue, new FieldPosition(field, modelObject));
					this.children.add(element);
					childUIElement = element.uiElement;
				} else {
					Button addButton = new Button("+");
					addButton.setTooltip(new Tooltip("Add"));
					addButton.setOnAction(event -> {
						Object model = EmptyModelFactory.createEmptyModel(field.getType(), null);
						try {
							field.set(modelObject, model);
						} catch (IllegalAccessException e) {
							logger.log(Level.WARNING, "Setting field value failed", e);
						}
						Element newElem = mapElement(model, position);
						this.children.add(newElem);
						int index = children.indexOf(addButton);
						children.remove(index);
						children.remove(index - 1);
						handleNewElement(fieldValue, field, newElem.uiElement, children);
					});
					childUIElement = addButton;
				}
				handleNewElement(fieldValue, field, childUIElement, children);
			}
		} catch (IllegalAccessException e) {
			logger.log(Level.WARNING, "Resolving field value failed", e);
		}
		return elementContainer;
	}

	private void handleNewElement(Object fieldValue, Field field, Node childUIElement, ObservableList<Node> children) {
		if(!labelChildren || fieldValue instanceof List) {
			children.add(childUIElement);
			return;
		}
		Label label = UIElementFactory.createElementTitle(field.getName(), ElementTitleType.SUB);
		if(layout == ElementLayout.VERTICAL)
			children.addAll(label, childUIElement);
		else
			children.add(UIElementFactory.createAlignedVBox(Pos.TOP_CENTER, 5, label, childUIElement));
	}

	protected static Element mapElement(Object element, ElementPosition position) {
		if(element instanceof String)
			return new StringElement((String) element, position);
		if(element instanceof Number)
			return new NumericElement((Number) element, position);
		if(element instanceof XMLGregorianCalendar)
			return new DateElement((XMLGregorianCalendar) element, position);
		if (element.getClass().isEnum())
			return new EnumElement(element, position);
		if (element instanceof List)
			return new ListElement((List) element, position);
		return new ComplexElement(element, position);
	}
}
