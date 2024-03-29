package dev.sgora.xml_editor;

import com.google.inject.Inject;
import dev.sgora.xml_editor.model.xml.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.drive.DriveWorkspaceAction;
import dev.sgora.xml_editor.services.ui.ModelUIMapper;
import dev.sgora.xml_editor.services.ui.WorkspaceAction;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class WindowView {
	public MenuItem newMenuItem;
	public MenuItem openMenuItem;
	public MenuItem saveMenuItem;
	public MenuItem exportMenuItem;
	public MenuItem closeMenuItem;
	public MenuItem quitMenuItem;

	public MenuItem validateMenuItem;

	public Pane infoRoot;
	public Pane historyRoot;

	public Menu driveLoadMenu;
	public MenuItem driveSaveMenuItem;

	private Parent root;
	private Model<AccountStatement> model;
	private WorkspaceAction workspaceAction;
	private Stage window;

	@Inject
	void init(Stage window, Parent root, WorkspaceAction workspaceAction, DriveWorkspaceAction driveWorkspaceAction, Model<AccountStatement> model, ModelUIMapper uiMapper) {
		this.root = root;
		this.model = model;
		this.workspaceAction = workspaceAction;
		this.window = window;

		driveWorkspaceAction.init(driveLoadMenu, driveSaveMenuItem);
		uiMapper.init(infoRoot, historyRoot);

		setScene();
		bindEvents();
	}

	private void bindEvents() {
		newMenuItem.setOnAction(event -> workspaceAction.newDocumentAction());
		openMenuItem.setOnAction(event -> workspaceAction.openDocumentAction());
		saveMenuItem.setOnAction(event -> workspaceAction.saveDocumentAction());
		closeMenuItem.setOnAction(event -> workspaceAction.closeDocumentAction());
		exportMenuItem.setOnAction(event -> workspaceAction.exportDocumentAction());
		quitMenuItem.setOnAction(event -> Platform.exit());

		validateMenuItem.setOnAction(event -> workspaceAction.validateDocumentAction());

		model.addListener(() -> {
			String windowName = "";
			if(model.getFileName() != null) {
				String fileName = model.getFileName();
				windowName = fileName.substring(0, fileName.length() - 4) + " - ";
			} else if(model.getValue() != null)
				windowName = "Untitled - ";
			if(!windowName.isEmpty())
				windowName = model.getFileType().name + ": " + windowName;
			window.setTitle(windowName + "XML Editor");
		});
	}

	private void setScene() {
		Scene scene = new Scene(root, 1000, 600);
		scene.getStylesheets().add(XMLEditor.class.getResource("/styles.css").toExternalForm());
		window.setScene(scene);
	}
}
