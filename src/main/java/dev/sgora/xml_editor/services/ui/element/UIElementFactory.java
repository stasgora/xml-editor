package dev.sgora.xml_editor.services.ui.element;

import dev.sgora.xml_editor.element.enums.ElementTitleType;
import dev.sgora.xml_editor.element.enums.EnumField;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UIElementFactory {
	private static final String FIELD_ERROR_CLASS = "error-field";

	public static Label createElementTitle(String title, ElementTitleType type) {
		var label = new Label(transformFieldName(title));
		label.getStyleClass().add(type.styleClass);
		return label;
	}

	public static String transformFieldName(String fieldName) {
		fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		return fieldName.replaceAll("(.)([A-Z])", "$1 $2");
	}


	public static VBox createAlignedVBox(Pos alignment, int spacing, Node... nodes) {
		VBox box = new VBox(spacing, nodes);
		box.setAlignment(alignment);
		return box;
	}
	
	public static ContextMenu createFieldErrorList(Node node) {
		ContextMenu errorList = new ContextMenu();
		errorList.setAutoHide(false);
		errorList.getItems().addListener((ListChangeListener.Change<? extends MenuItem> c) -> {
			if(!c.getList().isEmpty() && !node.getStyleClass().contains(FIELD_ERROR_CLASS))
				node.getStyleClass().add(FIELD_ERROR_CLASS);
			else if(c.getList().isEmpty() && node.getStyleClass().contains(FIELD_ERROR_CLASS))
				node.getStyleClass().remove(FIELD_ERROR_CLASS);
		});
		node.hoverProperty().addListener(((observable, oldVal, newVal) -> {
			if(newVal)
				errorList.show(node, Side.RIGHT, 10, 0);
			else
				errorList.hide();
		}));
		return errorList;
	}
}
