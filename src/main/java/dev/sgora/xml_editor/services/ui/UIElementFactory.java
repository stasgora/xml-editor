package dev.sgora.xml_editor.services.ui;

import com.google.inject.Singleton;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class UIElementFactory {
	private static final String TEXT_FIELD_CLASS = "xml-field";

	public static TextField createTextField(String text) {
		TextField field = new TextField(text);
		field.getStyleClass().add(TEXT_FIELD_CLASS);
		return field;
	}

	public static void createMultiTextField() {

	}

	public static ComboBox createComboBox(Class type) throws NoSuchFieldException, IllegalAccessException {
		ComboBox<String> comboBox = new ComboBox<>();
		List<String> values = new ArrayList<>();
		for (Object value : type.getEnumConstants()) {
			Field field = value.getClass().getDeclaredField("value");
			field.setAccessible(true);
			values.add((String) field.get(value));
		}
		comboBox.getItems().addAll(values);
		return comboBox;
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
