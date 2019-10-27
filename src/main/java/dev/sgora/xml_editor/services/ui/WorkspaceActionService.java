package dev.sgora.xml_editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.ui.dialog.DialogService;
import dev.sgora.xml_editor.services.ui.dialog.FileChooserAction;
import dev.sgora.xml_editor.services.ui.element.EmptyModelFactory;
import dev.sgora.xml_editor.services.ui.element.UIElementFactory;
import dev.sgora.xml_editor.services.validation.ValidationException;
import dev.sgora.xml_editor.services.validation.ValidationService;
import javafx.stage.FileChooser;

import java.io.File;

@Singleton
public class WorkspaceActionService implements WorkspaceAction {
	private DialogService dialogService;
	private final Model<AccountStatement> model;
	private ValidationService validationService;

	private static final FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML Documents", "*.xml");
	private static final String OPEN_DOC_TITLE = "Open Document";

	@Inject
	private WorkspaceActionService(DialogService dialogService, Model<AccountStatement> model, ValidationService validationService) {
		this.dialogService = dialogService;
		this.model = model;
		this.validationService = validationService;
	}

	@Override
	public void openDocumentAction() {
		File location = dialogService.showFileChooser(FileChooserAction.OPEN_DIALOG, OPEN_DOC_TITLE, filter);
		if(location == null)
			return;
		try {
			model.setValue(validationService.loadXML(location));
			model.setFile(location);
			model.notifyListeners();
		} catch (ValidationException e) {
			dialogService.showErrorDialog(OPEN_DOC_TITLE, "Parsing document failed", "No document will be loaded");
		}
	}

	@Override
	public void newDocumentAction() {
		model.setValue(EmptyModelFactory.createEmptyModel(AccountStatement.class, null));
		model.notifyListeners();
	}

	@Override
	public void saveDocumentAction() {
		File location = dialogService.showFileChooser(FileChooserAction.SAVE_DIALOG, "Save Document", filter);
		if(location == null)
			return;
	}

	@Override
	public void closeDocumentAction() {
		model.setValue(null);
		model.setFile(null);
		model.notifyListeners();
	}
}
