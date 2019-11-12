package dev.sgora.xml_editor.services.ui.element;

import dev.sgora.xml_editor.element.enums.ElementTitleType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class UIElementFactory {
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
}
