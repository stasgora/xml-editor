package dev.sgora.xml_editor;

import com.google.inject.Inject;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.ui.ModelUIMapper;
import dev.sgora.xml_editor.services.ui.WorkspaceAction;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

	public Pane infoRoot;
	public Pane historyRoot;

	private Parent root;
	private Model<AccountStatement> model;
	private WorkspaceAction workspaceAction;
	private Stage window;

	@Inject
	void init(Stage window, Parent root, WorkspaceAction workspaceAction, Model<AccountStatement> model, ModelUIMapper uiMapper) {
		this.root = root;
		this.model = model;
		this.workspaceAction = workspaceAction;
		this.window = window;

		uiMapper.init(infoRoot, historyRoot);
		setScene();
		bindEvents();
	}

	private void bindEvents() {
		newMenuItem.setOnAction(event -> workspaceAction.newDocumentAction());
		openMenuItem.setOnAction(event -> workspaceAction.openDocumentAction());
		saveMenuItem.setOnAction(event -> workspaceAction.saveDocumentAction());
		closeMenuItem.setOnAction(event -> workspaceAction.closeDocumentAction());
		quitMenuItem.setOnAction(event -> Platform.exit());

		model.addListener(() -> {
			String windowName = "XML Editor";
			if(model.getFile() != null) {
				String fileName = model.getFile().getName();
				windowName = fileName.substring(0, fileName.length() - 4) + " - " + windowName;
			} else if(model.getValue() != null)
				windowName = "Untitled - " + windowName;
			window.setTitle(windowName);
		});
	}

	private void setScene() {
		Scene scene = new Scene(root, 800, 500);
		scene.getStylesheets().add(XMLEditor.class.getResource("/styles.css").toExternalForm());
		window.setScene(scene);
	}
}
