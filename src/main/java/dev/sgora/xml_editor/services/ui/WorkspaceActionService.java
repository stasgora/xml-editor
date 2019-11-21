package dev.sgora.xml_editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.drive.DriveWorkspaceAction;
import dev.sgora.xml_editor.services.ui.dialog.DialogService;
import dev.sgora.xml_editor.services.ui.dialog.FileChooserAction;
import dev.sgora.xml_editor.services.validation.ValidationException;
import dev.sgora.xml_editor.services.validation.XMLService;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class WorkspaceActionService implements WorkspaceAction {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private DialogService dialogService;
	private DriveWorkspaceAction driveWorkspaceAction;
	private final Model<AccountStatement> model;
	private XMLService XMLService;

	private static final FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML Documents", "*.xml");
	private static final String OPEN_DOC_TITLE = "Open Document";

	@Inject
	private WorkspaceActionService(DialogService dialogService, DriveWorkspaceAction driveWorkspaceAction, Model<AccountStatement> model, XMLService XMLService) {
		this.dialogService = dialogService;
		this.driveWorkspaceAction = driveWorkspaceAction;
		this.model = model;
		this.XMLService = XMLService;
	}

	@Override
	public void openDocumentAction() {
		File location = dialogService.showFileChooser(FileChooserAction.OPEN_DIALOG, OPEN_DOC_TITLE, filter);
		if(location == null)
			return;
		try {
			model.setValue(XMLService.loadXML(location));
			model.setFile(location);
			model.notifyListeners();
		} catch (ValidationException e) {
			dialogService.showErrorDialog(OPEN_DOC_TITLE, "Parsing document failed", "No document will be loaded");
		}
	}

	@Override
	public void newDocumentAction() {
		model.setValue(XMLService.createEmptyXML());
		model.notifyListeners();
	}

	@Override
	public void saveDocumentAction() {
		if(XMLService.errorHandler.documentHasErrors()) {
			dialogService.showErrorDialog("Save Document", "Saving failed", "There are errors remaining");
			return;
		}
		File location = dialogService.showFileChooser(FileChooserAction.SAVE_DIALOG, "Save Document", filter);
		if(location == null)
			return;
		try(FileOutputStream output = new FileOutputStream(location)) {
			output.write(XMLService.serializeXML().toByteArray());
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Saving XML failed", e);
		}
	}

	@Override
	public void closeDocumentAction() {
		model.setValue(null);
		model.setFile(null);
		model.notifyListeners();
	}

	@Override
	public void validateDocumentAction() {
		XMLService.validateXML();
	}
}
