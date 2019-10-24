package dev.sgora.xml_editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

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
				mapElement(rootFields[i].get(model.getValue()), i < Math.ceil(rootFieldsCount / 2d) ? infoRoot : historyRoot);
			}
		} catch (IllegalAccessException | NoSuchFieldException e) {
			logger.log(Level.WARNING, "Mapping model failed", e);
		}
	}

	private void mapElement(Object element, Pane parent) throws IllegalAccessException, NoSuchFieldException {
		Class modelType = element.getClass();
		VBox box = new VBox(5, UIElementFactory.createSectionTitle(modelType.getSimpleName().replaceAll("(.)([A-Z])", "$1 $2")));
		box.setPadding(new Insets(5, 5, 5, 5));
		for (Field field : modelType.getDeclaredFields()) {
			field.setAccessible(true);
			Object fieldVal = field.get(element);
			if(fieldVal instanceof String)
				box.getChildren().add(UIElementFactory.createTextField((String) fieldVal));
			else if (field.getType().isEnum())
				box.getChildren().add(UIElementFactory.createComboBox(field.getType(), fieldVal));
			else if (fieldVal instanceof List)
				box.getChildren().add(UIElementFactory.createMultiTextField((List) fieldVal));
			else
				mapElement(fieldVal, box);
		}
		parent.getChildren().add(box);
	}
}
