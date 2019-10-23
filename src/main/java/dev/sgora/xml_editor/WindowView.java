package dev.sgora.xml_editor;

import com.google.inject.Inject;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.ui.WorkspaceActionService;
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

	private Model<AccountStatement> model;
	private WorkspaceActionService workspaceAction;
	private Stage window;

	@Inject
	void init(Stage window, Parent root, WorkspaceActionService workspaceAction, Model<AccountStatement> model) {
		this.model = model;
		this.workspaceAction = workspaceAction;
		this.window = window;
		//validationService.validateXML(new File("xml/account-statement-1.xml"));

		Scene scene = new Scene(root, 800, 500);
		scene.getStylesheets().add(XMLEditor.class.getResource("/styles.css").toExternalForm());
		window.setScene(scene);

		bindEvents();
	}

	private void bindEvents() {
		newMenuItem.setOnAction(event -> workspaceAction.newDocumentAction());
		openMenuItem.setOnAction(event -> workspaceAction.openDocumentAction());
		saveMenuItem.setOnAction(event -> workspaceAction.saveDocumentAction());

		model.addListener(() -> {
			String windowName = "XML Editor";
			if(model.getFile() != null) {
				String fileName = model.getFile().getName();
				windowName = fileName.substring(0, fileName.length() - 4) + " - " + windowName;
			}
			window.setTitle(windowName);
		});

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
