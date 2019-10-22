package dev.sgora.xml_editor.services;

import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.services.ui.DialogService;
import dev.sgora.xml_editor.services.ui.FileChooserAction;
import dev.sgora.xml_editor.services.validation.ValidationException;
import dev.sgora.xml_editor.services.validation.ValidationService;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class WorkspaceActionService {
	private DialogService dialogService;
	private ValidationService validationService;

	private static final FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML Documents", "*.xml");
	private static final String OPEN_DOC_TITLE = "Open Document";

	public WorkspaceActionService(Stage window) {
		dialogService = new DialogService(window);
		validationService = new ValidationService();
	}

	public void openDocumentAction() {
		File location = dialogService.showFileChooser(FileChooserAction.OPEN_DIALOG, OPEN_DOC_TITLE, filter);
		if(location == null)
			return;
		try {
			AccountStatement document = validationService.loadXML(location);
		} catch (ValidationException e) {
			dialogService.showErrorDialog(OPEN_DOC_TITLE, "Parsing document failed", "No document will be loaded");
		}
	}

	public void newDocumentAction() {
	}

	public void saveDocumentAction() {
		File location = dialogService.showFileChooser(FileChooserAction.SAVE_DIALOG, "Save Document", filter);
		if(location == null)
			return;

	}
}
