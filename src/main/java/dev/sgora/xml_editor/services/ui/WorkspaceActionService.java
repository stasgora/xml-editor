package dev.sgora.xml_editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.model.FileType;
import dev.sgora.xml_editor.model.xml.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.ui.dialog.DialogService;
import dev.sgora.xml_editor.services.ui.dialog.FileChooserAction;
import dev.sgora.xml_editor.services.xml.ValidationException;
import dev.sgora.xml_editor.services.xml.XMLService;
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
	private final Model<AccountStatement> model;
	private XMLService XMLService;

	private static final FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML Documents", "*.xml");
	private static final String OPEN_DOC_TITLE = "Open Document";

	@Inject
	private WorkspaceActionService(DialogService dialogService, Model<AccountStatement> model, XMLService XMLService) {
		this.dialogService = dialogService;
		this.model = model;
		this.XMLService = XMLService;
	}

	@Override
	public void openDocumentAction() {
		File location = dialogService.showFileChooser(FileChooserAction.OPEN_DIALOG, OPEN_DOC_TITLE, filter);
		if(location == null)
			return;
		try {
			model.set(XMLService.loadXML(location), location.getName(), FileType.LOCAL);
			model.notifyListeners();
		} catch (ValidationException e) {
			dialogService.showErrorDialog(OPEN_DOC_TITLE, "Parsing document failed", "No document will be loaded");
		}
	}

	@Override
	public void newDocumentAction() {
		model.set(XMLService.createEmptyXML(), null, null);
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
		model.set(null, null, null);
		model.notifyListeners();
	}

	@Override
	public void validateDocumentAction() {
		XMLService.validateXML();
	}
}
