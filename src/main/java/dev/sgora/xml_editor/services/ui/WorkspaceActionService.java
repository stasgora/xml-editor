package dev.sgora.xml_editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.model.FileType;
import dev.sgora.xml_editor.model.xml.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.pdf.PDFService;
import dev.sgora.xml_editor.services.ui.dialog.DialogService;
import dev.sgora.xml_editor.services.ui.dialog.FileChooserAction;
import dev.sgora.xml_editor.services.webdata.WebDataService;
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
	private PDFService pdfService;

	private static final FileChooser.ExtensionFilter XML_FILTER = new FileChooser.ExtensionFilter("XML Documents", "*.xml");
	private static final FileChooser.ExtensionFilter PDF_FILTER = new FileChooser.ExtensionFilter("PDF Documents", "*.pdf");
	private static final String OPEN_DOC_TITLE = "Open Document";

	@Inject
	private WorkspaceActionService(DialogService dialogService, Model<AccountStatement> model, XMLService XMLService, PDFService pdfService) {
		this.dialogService = dialogService;
		this.model = model;
		this.XMLService = XMLService;
		this.pdfService = pdfService;
	}

	@Override
	public void openDocumentAction() {
		File location = dialogService.showFileChooser(FileChooserAction.OPEN_DIALOG, OPEN_DOC_TITLE, XML_FILTER);
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
		model.set(XMLService.createEmptyXML(), null, FileType.LOCAL);
		model.notifyListeners();
	}

	@Override
	public void saveDocumentAction() {
		File location = getSaveFile("Save Document", XML_FILTER);
		if(location == null)
			return;
		try(FileOutputStream output = new FileOutputStream(location)) {
			output.write(XMLService.serializeXML().toByteArray());
			model.move(location.getName(), FileType.LOCAL);
			model.notifyListeners();
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
	public void exportDocumentAction() {
		File location = getSaveFile("Export Document", PDF_FILTER);
		if(location == null)
			return;
		pdfService.generatePDF(location);
	}

	private File getSaveFile(String title, FileChooser.ExtensionFilter filter) {
		if(XMLService.errorHandler.documentHasErrors()) {
			dialogService.showErrorDialog("Save Document", "Saving failed", "There are errors remaining");
			return null;
		}
		return dialogService.showFileChooser(FileChooserAction.SAVE_DIALOG, title, filter);
	}

	@Override
	public void validateDocumentAction() {
		XMLService.validateXML();
	}
}
