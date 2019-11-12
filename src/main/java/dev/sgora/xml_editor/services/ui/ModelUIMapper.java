package dev.sgora.xml_editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.element.ComplexElement;
import dev.sgora.xml_editor.element.position.FieldPosition;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.ErrorUtil;
import dev.sgora.xml_editor.element.enums.ElementLayout;
import dev.sgora.xml_editor.element.enums.ElementTitleType;
import dev.sgora.xml_editor.services.ui.element.EmptyModelFactory;
import dev.sgora.xml_editor.services.ui.element.UIElementFactory;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ModelUIMapper {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private static final String VALUE_SET_ERROR = "Setting model value failed";

	private final Model<AccountStatement> model;

	public final Map<Object, Node> modelUiMap = new HashMap<>();
	public final Map<Node, ContextMenu> elementErrorMap = new HashMap<>();

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
				ComplexElement element = new ComplexElement(rootFields[i].get(model.getValue()), new FieldPosition(rootFields[i], model.getValue()), true);
				root.getChildren().add(element.uiElement);
			}
		} catch (IllegalAccessException e) {
			logger.log(Level.WARNING, "Mapping model failed", e);
		}
	}

	private void registerElement(Object model, Node element) {
		modelUiMap.put(model, element);
		elementErrorMap.put(element, UIElementFactory.createFieldErrorList(element));
	}

	private void clearElements() {
		infoRoot.getChildren().clear();
		historyRoot.getChildren().clear();
	}
}
