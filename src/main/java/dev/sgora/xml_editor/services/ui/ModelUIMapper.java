package dev.sgora.xml_editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.ErrorUtil;
import dev.sgora.xml_editor.element.ElementLayout;
import dev.sgora.xml_editor.element.ElementTitleType;
import dev.sgora.xml_editor.services.ui.element.EmptyModelFactory;
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
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ModelUIMapper {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private static final String VALUE_SET_ERROR = "Setting model value failed";

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
		} catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
			logger.log(Level.WARNING, "Mapping model failed", e);
		}
	}

	private Node mapComplexElement(Object element, ElementLayout layout, boolean addLabel, boolean root)
			throws IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException, InstantiationException {
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
			Node child = mapElement(value, ErrorUtil.wrap((uiElem, val) -> field.set(element, val), VALUE_SET_ERROR), ElementLayout.VERTICAL, addLabel);
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

	private Node mapElement(Object element, BiConsumer<Node, Object> setValue, ElementLayout layout, boolean addLabel)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
		if(element instanceof String)
			return UIElementFactory.createTextField((String) element, setValue);
		if(element instanceof Number) {
			BiConsumer<Node, Object> valueSetter = ErrorUtil.wrap((node, val) -> setValue.accept(node, element.getClass().getConstructor(String.class).newInstance(val)), VALUE_SET_ERROR);
			return UIElementFactory.createTextField(element.toString(), valueSetter);
		}
		if(element instanceof XMLGregorianCalendar)
			return UIElementFactory.createDateField((XMLGregorianCalendar) element, setValue);
		if (element.getClass().isEnum())
			return UIElementFactory.createComboBox(element.getClass(), element, setValue);
		if (element instanceof List)
			return mapListElement(element);
		return mapComplexElement(element, layout, addLabel, false);
	}

	private VBox mapListElement(Object element) throws NoSuchFieldException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException, InstantiationException {
		VBox listContainer = UIElementFactory.createAlignedVBox(Pos.TOP_CENTER, 5);
		List list = (List) element;
		ObservableList<Node> children = listContainer.getChildren();
		for (int i = 0; i < list.size(); i++) {
			Object listElement = list.get(i);
			Function<Node, Integer> indexFinder = node -> {
				for (int j = 0; j < children.size(); j++) {
					Node child = children.get(j);
					if (((HBox) child).getChildren().get(0) == node)
						return j;
				}
				return -1;
			};
			BiConsumer<Node, Object> listValueSetter = (uiElem, val) -> list.set(indexFinder.apply(uiElem), val);
			Supplier<Node> listElementSupplier = ErrorUtil.wrap(() -> mapElement(EmptyModelFactory.createEmptyModel(listElement.getClass(),
					null), listValueSetter, ElementLayout.HORIZONTAL, false), "Mapping model failed");
			children.add(UIElementFactory.wrapFieldAsListElement(
					mapElement(listElement, listValueSetter, ElementLayout.HORIZONTAL, i == 0), listElementSupplier, listContainer));
		}
		return listContainer;
	}

	private void clearElements() {
		infoRoot.getChildren().clear();
		historyRoot.getChildren().clear();
	}
}
