package dev.sgora.xml_editor.services.ui;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UIElementFactory {
	private static final String TEXT_FIELD_CLASS = "xml-field";
	private static final String MULTI_TEXT_FIELD_CLASS = "xml-multi-field";
	private static final String TITLE_CLASS = "xml-title";

	public static TextField createTextField(String text) {
		TextField field = new TextField(text);
		field.getStyleClass().add(TEXT_FIELD_CLASS);
		return field;
	}

	public static Label createSectionTitle(String title) {
		var label = new Label(title);
		label.getStyleClass().add(TITLE_CLASS);
		return label;
	}

	public static VBox createMultiTextField(List list) {
		VBox container = new VBox(5);
		for (Object element : list)
			container.getChildren().add(addTextFieldToList(element, container));
		return container;
	}

	private static HBox addTextFieldToList(Object value, VBox container) {
		HBox layout = new HBox(5);
		layout.setAlignment(Pos.CENTER_LEFT);
		layout.getStyleClass().add(MULTI_TEXT_FIELD_CLASS);
		TextField textField = createTextField(value.toString());
		Button removeField = new Button("-");
		removeField.setOnAction(event -> container.getChildren().remove(layout));
		Button addField = new Button("+");
		addField.setOnAction(event -> container.getChildren().add(container.getChildren().indexOf(layout) + 1, addTextFieldToList("", container)));
		layout.getChildren().addAll(textField, removeField, addField);
		return layout;
	}

	public static ComboBox createComboBox(Class type, Object value) throws NoSuchFieldException, IllegalAccessException {
		ComboBox<String> comboBox = new ComboBox<>();
		for (Object constant : type.getEnumConstants())
			comboBox.getItems().add(getEnumValue(constant));
		comboBox.setValue(getEnumValue(value));
		return comboBox;
	}

	private static String getEnumValue(Object object) throws NoSuchFieldException, IllegalAccessException {
		Field field = object.getClass().getDeclaredField("value");
		field.setAccessible(true);
		return (String) field.get(object);
	}
	
	public void showFieldError(Node node, String errorMessage) {
		node.getStyleClass().add("error-field");
		ContextMenu validationMessage = new ContextMenu();
		validationMessage.setAutoHide(false);
		validationMessage.getItems().add(new MenuItem(errorMessage));
		node.hoverProperty().addListener(((observable, oldVal, newVal) -> {
			if(newVal)
				validationMessage.show(node, Side.RIGHT, 10, 0);
			else
				validationMessage.hide();
		}));
	}
}
