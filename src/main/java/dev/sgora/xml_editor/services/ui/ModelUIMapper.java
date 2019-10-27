package dev.sgora.xml_editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.ui.element.ElementLayout;
import dev.sgora.xml_editor.services.ui.element.ElementTitleType;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ModelUIMapper {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private final Model<AccountStatement> model;

	private Pane infoRoot;
	private Pane historyRoot;
	private int rootFieldsCount;

	@Inject
	private ModelUIMapper(Model<AccountStatement> model) {
		this.model = model;
		model.addListener(this::mapModelToUI);
	}

	public void init(Pane infoRoot, Pane historyRoot) {
		this.infoRoot = infoRoot;
		this.historyRoot = historyRoot;
	}

	private void mapModelToUI() {
		clearElements();
		if(model.getValue() == null)
			return;
		Class modelType = model.getValue().getClass();
		Field[] rootFields = modelType.getDeclaredFields();
		rootFieldsCount = rootFields.length;
		try {
			for (int i = 0; i < rootFields.length; i++) {
				rootFields[i].setAccessible(true);
				Pane root = i < Math.ceil(rootFieldsCount / 2d) ? infoRoot : historyRoot;
				root.getChildren().add(mapComplexElement(rootFields[i].get(model.getValue()), ElementLayout.VERTICAL, true, true));
			}
		} catch (IllegalAccessException | NoSuchFieldException e) {
			logger.log(Level.WARNING, "Mapping model failed", e);
		}
	}

	private Node mapComplexElement(Object element, ElementLayout layout, boolean addLabel, boolean root) throws IllegalAccessException, NoSuchFieldException {
		Class modelType = element.getClass();
		Pane elementContainer = layout == ElementLayout.VERTICAL ? UIElementFactory.createAlignedVBox(Pos.TOP_LEFT, 5) : new HBox(10);
		elementContainer.setPadding(new Insets(10, 0, 10, 0));
		ObservableList<Node> children = elementContainer.getChildren();
		if(root) {
			children.add(UIElementFactory.createElementTitle(modelType.getSimpleName(), ElementTitleType.ROOT));
			if(layout == ElementLayout.VERTICAL)
				((VBox) elementContainer).setAlignment(Pos.TOP_CENTER);
		}

		for (Field field : modelType.getDeclaredFields()) {
			field.setAccessible(true);
			Object value = field.get(element);
			Node child = mapElement(value, ElementLayout.VERTICAL, addLabel);
			if(!addLabel || value instanceof List) {
				children.add(child);
				continue;
			}
			Label label = UIElementFactory.createElementTitle(field.getName(), ElementTitleType.SUB);
			if(layout == ElementLayout.VERTICAL)
				children.addAll(label, child);
			else
				children.add(UIElementFactory.createAlignedVBox(Pos.TOP_CENTER, 5, label, child));
		}
		return elementContainer;
	}

	private Node mapElement(Object element, ElementLayout layout, boolean addLabel) throws NoSuchFieldException, IllegalAccessException {
		if(element instanceof String)
			return UIElementFactory.createTextField((String) element);
		if(element instanceof Number)
			return UIElementFactory.createTextField(element.toString());
		if(element instanceof XMLGregorianCalendar)
			return UIElementFactory.createDateField((XMLGregorianCalendar) element);
		if (element.getClass().isEnum())
			return UIElementFactory.createComboBox(element.getClass(), element);
		if (element instanceof List) {
			VBox listContainer = UIElementFactory.createAlignedVBox(Pos.TOP_CENTER, 5);
			List list = (List) element;
			for (int i = 0; i < list.size(); i++) {
				Object listElement = list.get(i);
				listContainer.getChildren().add(UIElementFactory.wrapFieldAsListElement(mapElement(listElement, ElementLayout.HORIZONTAL, i == 0), () -> {
					try {
						return mapElement(UIElementFactory.createEmptyModel(listElement.getClass(), null), ElementLayout.HORIZONTAL, false);
					} catch (IllegalAccessException | NoSuchFieldException e) {
						logger.log(Level.WARNING, "Mapping model failed", e);
						return null;
					}
				}, listContainer));
			}
			return listContainer;
		}
		return mapComplexElement(element, layout, addLabel, false);
	}

	private void clearElements() {
		infoRoot.getChildren().clear();
		historyRoot.getChildren().clear();
	}
}
