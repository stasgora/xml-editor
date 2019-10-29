package dev.sgora.xml_editor.services.validation;

import org.w3c.dom.Node;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ValidationErrorHandler implements ValidationEventHandler {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public boolean handleEvent(ValidationEvent validationEvent) {
		if(validationEvent == null || validationEvent.getLocator() == null) {
			logger.log(Level.WARNING, "Validation event is null");
			return true;
		}
		ValidationEventLocator locator = validationEvent.getLocator();
		logger.log(Level.WARNING, validationEvent.getMessage());
		Object object = locator.getObject();
		return true;
	}
}
