package dev.sgora.xml_editor;

import dev.sgora.xml_editor.services.WorkspaceActionService;
import dev.sgora.xml_editor.services.ui.DialogService;
import dev.sgora.xml_editor.services.validation.ValidationService;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WindowView {
	public VBox mainView;

	public MenuItem newMenuItem;
	public MenuItem openMenuItem;
	public MenuItem saveMenuItem;
	public MenuItem exportMenuItem;

	public TextField testField;

	private WorkspaceActionService workspaceAction;

	void init(Stage window, Parent root) {
		workspaceAction = new WorkspaceActionService(window);
		//validationService.validateXML(new File("xml/account-statement-1.xml"));

		Scene scene = new Scene(root, 800, 500);
		scene.getStylesheets().add(XMLEditor.class.getResource("/styles.css").toExternalForm());
		window.setScene(scene);

		newMenuItem.setOnAction(event -> workspaceAction.newDocumentAction());
		openMenuItem.setOnAction(event -> workspaceAction.openDocumentAction());
		saveMenuItem.setOnAction(event -> workspaceAction.saveDocumentAction());

		ContextMenu validationMessage = new ContextMenu();
		validationMessage.setAutoHide(false);
		validationMessage.getItems().add(new MenuItem("Validation error"));
		testField.hoverProperty().addListener(((observable, oldVal, newVal) -> {
			if(newVal)
				validationMessage.show(testField, Side.RIGHT, 10, 0);
			else
				validationMessage.hide();
		}));
	}
}
