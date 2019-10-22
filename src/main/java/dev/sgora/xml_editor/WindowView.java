package dev.sgora.xml_editor;

import dev.sgora.xml_editor.services.WorkspaceActionService;
import dev.sgora.xml_editor.services.ui.DialogService;
import dev.sgora.xml_editor.services.validation.ValidationService;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WindowView {
	public VBox mainView;

	public MenuItem newMenuItem;
	public MenuItem openMenuItem;
	public MenuItem saveMenuItem;
	public MenuItem exportMenuItem;

	private WorkspaceActionService workspaceAction;

	void init(Stage window) {
		workspaceAction = new WorkspaceActionService(window);
		//validationService.validateXML(new File("xml/account-statement-1.xml"));

		newMenuItem.setOnAction(event -> workspaceAction.newDocumentAction());
		openMenuItem.setOnAction(event -> workspaceAction.openDocumentAction());
		saveMenuItem.setOnAction(event -> workspaceAction.saveDocumentAction());
	}
}
