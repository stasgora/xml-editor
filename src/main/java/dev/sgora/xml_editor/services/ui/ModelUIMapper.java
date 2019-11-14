package dev.sgora.xml_editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.element.ComplexElement;
import dev.sgora.xml_editor.element.position.FieldPosition;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import javafx.scene.layout.Pane;

import java.lang.reflect.Field;
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
		ComplexElement.registerElement = model::addElement;
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
				ComplexElement element = new ComplexElement(rootFields[i].get(model.getValue()), new FieldPosition(rootFields[i], model.getValue()), true);
				root.getChildren().add(element.uiElement);
			}
		} catch (IllegalAccessException e) {
			logger.log(Level.WARNING, "Mapping model failed", e);
		}
	}

	private void clearElements() {
		infoRoot.getChildren().clear();
		historyRoot.getChildren().clear();
	}
}
