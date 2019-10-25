package dev.sgora.xml_editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
		Class modelType = model.getValue().getClass();
		Field[] rootFields = modelType.getDeclaredFields();
		rootFieldsCount = rootFields.length;
		try {
			for (int i = 0; i < rootFields.length; i++) {
				rootFields[i].setAccessible(true);
				Pane root = i < Math.ceil(rootFieldsCount / 2d) ? infoRoot : historyRoot;
				root.getChildren().add(mapComplexElement(rootFields[i].get(model.getValue())));
			}
		} catch (IllegalAccessException | NoSuchFieldException e) {
			logger.log(Level.WARNING, "Mapping model failed", e);
		}
	}

	private Node mapComplexElement(Object element) throws IllegalAccessException, NoSuchFieldException {
		Class modelType = element.getClass();
		VBox box = new VBox(5);
		box.setPadding(new Insets(0, 5, 5, 0));
		ObservableList<Node> children = box.getChildren();
		children.add(UIElementFactory.createSectionTitle(modelType.getSimpleName().replaceAll("(.)([A-Z])", "$1 $2")));

		for (Field field : modelType.getDeclaredFields()) {
			field.setAccessible(true);
			Object fieldVal = field.get(element);
			children.add(mapElement(fieldVal));
		}
		return box;
	}

	private Node mapElement(Object fieldVal) throws NoSuchFieldException, IllegalAccessException {
		if(fieldVal instanceof String)
			return UIElementFactory.createTextField((String) fieldVal);
		else if(fieldVal instanceof Number)
			return UIElementFactory.createTextField(fieldVal.toString());
		else if(fieldVal instanceof XMLGregorianCalendar)
			return UIElementFactory.createDateField((XMLGregorianCalendar) fieldVal);
		else if (fieldVal.getClass().isEnum())
			return UIElementFactory.createComboBox(fieldVal.getClass(), fieldVal);
		else if (fieldVal instanceof List) {
			VBox container = new VBox(5);
			for (Object listElement : (List) fieldVal)
				container.getChildren().add(UIElementFactory.wrapFieldAsListElement(mapElement(listElement), container));
			return container;
		} else
			return mapComplexElement(fieldVal);
	}
}
