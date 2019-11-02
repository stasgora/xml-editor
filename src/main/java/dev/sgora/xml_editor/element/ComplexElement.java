package dev.sgora.xml_editor.element;

import dev.sgora.xml_editor.element.enums.ElementLayout;
import dev.sgora.xml_editor.element.enums.ElementTitleType;
import dev.sgora.xml_editor.services.ui.element.UIElementFactory;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ComplexElement<M> extends Element<Pane, M> {
	public final List<Element> children = new ArrayList<>();

	private final ElementLayout layout;
	private boolean root;
	private boolean labelChildren = true;

	public ComplexElement(Object modelObject, Field objectField, M value, ElementLayout layout) {
		super(modelObject, objectField, value);
		this.layout = layout;
	}

	public ComplexElement asRoot() {
		root = true;
		return this;
	}

	public ComplexElement withoutChildLabels() {
		labelChildren = false;
		return this;
	}

	@Override
	protected Pane createUIElement(M value) {
		Class modelType = objectField.getType();
		Pane elementContainer = layout == ElementLayout.VERTICAL ? UIElementFactory.createAlignedVBox(Pos.TOP_LEFT, 5) : new HBox(10);
		elementContainer.setPadding(new Insets(10, 0, 10, 0));
		ObservableList<Node> children = elementContainer.getChildren();
		if(root) {
			children.add(UIElementFactory.createElementTitle(modelType.getSimpleName(), ElementTitleType.ROOT));
			if(layout == ElementLayout.VERTICAL)
				((VBox) elementContainer).setAlignment(Pos.TOP_CENTER);
		}
		//registerElement(element, elementContainer);
		try {
			Object modelObject = objectField.get(modelParentObject);
			for (Field field : modelType.getDeclaredFields()) {
				field.setAccessible(true);
				Object fieldValue = field.get(modelObject);

				this.children.add(mapElement(modelObject, field, fieldValue, layout));
				Node childUIElement = this.children.get(this.children.size() - 1).uiElement;
				if(!labelChildren || fieldValue instanceof List) {
					children.add(childUIElement);
					continue;
				}
				Label label = UIElementFactory.createElementTitle(field.getName(), ElementTitleType.SUB);
				if(layout == ElementLayout.VERTICAL)
					children.addAll(label, childUIElement);
				else
					children.add(UIElementFactory.createAlignedVBox(Pos.TOP_CENTER, 5, label, childUIElement));
			}
		} catch (IllegalAccessException e) {
			logger.log(Level.WARNING, "Resolving field value failed", e);
		}
		return elementContainer;
	}

	protected static Element mapElement(Object parentObject, Field elementField, Object element, ElementLayout layout) {
		if(element instanceof String)
			return new StringElement(parentObject, elementField, (String) element);
		if(element instanceof Number)
			return new NumericElement(parentObject, elementField, (Number) element);
		if(element instanceof XMLGregorianCalendar)
			return new DateElement(parentObject, elementField, (XMLGregorianCalendar) element);
		if (element.getClass().isEnum())
			return new EnumElement(parentObject, elementField, element);
		if (element instanceof List)
			return mapListElement(element);
		return new ComplexElement(parentObject, elementField, element, layout);
	}
}
