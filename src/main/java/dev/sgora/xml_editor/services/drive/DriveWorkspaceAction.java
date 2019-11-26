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
import javafx.scene.control.TextInputDialog;

import java.util.stream.Collectors;

@Singleton
public class DriveWorkspaceAction {
	private static final String OPEN_DOC_TITLE = "Drive Document";

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
		try {
			String name = model.getFileName();
			while (name == null || name.isEmpty())
				name = dialogService.showTextDialog("Enter file name");
			if(!name.endsWith(".xml"))
				name += ".xml";
			var file = driveService.saveFile(name, XMLService.serializeXML());
			driveLoadMenu.getItems().add(newDriveEntry(file));
		} catch (DriveException e) {
			dialogService.showErrorDialog(OPEN_DOC_TITLE, "Saving document failed", "No document will be saved");
		}
	}

	public void refreshDriveFiles() {
		try {
			var files = driveService.getFileList();
			driveLoadMenu.getItems().setAll(files.stream().map(this::newDriveEntry).collect(Collectors.toList()));
		} catch (DriveException e) {
			dialogService.showErrorDialog(OPEN_DOC_TITLE, "Acquiring documents failed", "No documents available to open");
		}
	}

	private void onDocumentOpen(MenuItem item) {
		try {
			model.set(XMLService.loadXML(driveService.openFile((String) item.getUserData())), item.getText(), FileType.DRIVE);
			model.notifyListeners();
		} catch (ValidationException | DriveException e) {
			dialogService.showErrorDialog(OPEN_DOC_TITLE, "Opening document failed", "No document will be loaded");
		}
	}

	private MenuItem newDriveEntry(File file) {
		MenuItem item = new MenuItem(file.getName());
		item.setUserData(file.getId());
		item.setOnAction(event -> onDocumentOpen(item));
		return item;
	}
}
