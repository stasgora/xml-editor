package dev.sgora.xml_editor;

import dev.sgora.xml_editor.services.validation.ValidationService;
import javafx.scene.layout.VBox;

public class WindowView {
	public VBox mainView;

	ValidationService validationService;

	void init() {
		validationService = new ValidationService();
		//validationService.validateXML();
	}
}
