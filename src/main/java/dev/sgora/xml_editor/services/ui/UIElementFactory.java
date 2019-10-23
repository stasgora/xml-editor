package dev.sgora.xml_editor.services.ui;

import com.google.inject.Singleton;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

@Singleton
public class UIElementFactory {
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
