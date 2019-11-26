package dev.sgora.xml_editor.services.drive;

import com.google.api.services.drive.model.File;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.model.FileType;
import dev.sgora.xml_editor.model.xml.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.ui.dialog.DialogService;
import dev.sgora.xml_editor.services.xml.ValidationException;
import dev.sgora.xml_editor.services.xml.XMLService;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.stream.Collectors;

@Singleton
public class DriveWorkspaceAction {
	private static final String DIALOG_TITLE = "Drive Document";

	private DialogService dialogService;
	private DriveService driveService;
	private dev.sgora.xml_editor.services.xml.XMLService XMLService;
	private Model<AccountStatement> model;
	private Menu driveLoadMenu;
	private MenuItem driveSaveMenuItem;

	@Inject
	public DriveWorkspaceAction(DialogService dialogService, DriveService driveService, XMLService XMLService, Model<AccountStatement> model) {
		this.dialogService = dialogService;
		this.driveService = driveService;
		this.XMLService = XMLService;
		this.model = model;
	}

	public void init(Menu driveLoadMenu, MenuItem driveSaveMenuItem) {
		this.driveLoadMenu = driveLoadMenu;
		this.driveSaveMenuItem = driveSaveMenuItem;

		refreshDriveFiles();
		model.addListener(() -> driveSaveMenuItem.setDisable(model.getValue() == null));
		driveSaveMenuItem.setOnAction(event -> saveDriveDocument());
	}

	private void saveDriveDocument() {
		if(XMLService.errorHandler.documentHasErrors()) {
			dialogService.showErrorDialog(DIALOG_TITLE, "Saving failed", "There are errors remaining");
			return;
		}
		try {
			String name = model.getFileName();
			do
				name = dialogService.showTextDialog("Enter file name", name);
			while (name == null || name.isEmpty());
			if(!name.endsWith(".xml"))
				name += ".xml";
			var file = driveService.saveFile(name, XMLService.serializeXML());
			driveLoadMenu.getItems().add(newDriveEntry(file));
			model.move(name, FileType.DRIVE);
			model.notifyListeners();
		} catch (DriveException e) {
			dialogService.showErrorDialog(DIALOG_TITLE, "Saving document failed", "No document will be saved");
		}
	}

	public void refreshDriveFiles() {
		try {
			var files = driveService.getFileList();
			driveLoadMenu.getItems().setAll(files.stream().map(this::newDriveEntry).collect(Collectors.toList()));
		} catch (DriveException e) {
			dialogService.showErrorDialog(DIALOG_TITLE, "Acquiring documents failed", "No documents available to open");
		}
	}

	private void onDocumentOpen(MenuItem item) {
		try {
			model.set(XMLService.loadXML(driveService.openFile((String) item.getUserData())), item.getText(), FileType.DRIVE);
			model.notifyListeners();
		} catch (ValidationException | DriveException e) {
			dialogService.showErrorDialog(DIALOG_TITLE, "Opening document failed", "No document will be loaded");
		}
		refreshDriveFiles();
	}

	private MenuItem newDriveEntry(File file) {
		MenuItem item = new MenuItem(file.getName());
		item.setUserData(file.getId());
		item.setOnAction(event -> onDocumentOpen(item));
		return item;
	}
}
