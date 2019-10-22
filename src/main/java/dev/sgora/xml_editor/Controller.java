package dev.sgora.xml_editor;

import dev.sgora.xml_editor.services.validation.ValidationService;

public class Controller {
	ValidationService validationService;

	void init() {
		validationService = new ValidationService();
		validationService.validateXML();
	}
}
