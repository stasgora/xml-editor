package dev.sgora.xml_editor.services.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.services.ui.ModelUIMapper;
import javafx.scene.control.MenuItem;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ValidationErrorHandler implements ValidationEventHandler {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private ModelUIMapper modelUIMapper;

	@Inject
	public ValidationErrorHandler(ModelUIMapper modelUIMapper) {
		this.modelUIMapper = modelUIMapper;
	}

	@Override
	public boolean handleEvent(ValidationEvent validationEvent) {
		if(validationEvent == null || validationEvent.getLocator() == null) {
			logger.log(Level.WARNING, "Validation event is null");
			return true;
		}
		ValidationEventLocator locator = validationEvent.getLocator();
		addErrorMessage(validationEvent.getMessage(), locator.getObject());
		return true;
	}

	private void addErrorMessage(String message, Object object) {
		if(!modelUIMapper.modelUiMap.containsKey(object)) {
			logger.log(Level.WARNING, "Invalid model object is not present in mapper maps");
			return;
		}
		modelUIMapper.elementErrorMap.get(modelUIMapper.modelUiMap.get(object)).getItems().add(new MenuItem(message));
	}

	public void clearErrorMessages() {
		modelUIMapper.elementErrorMap.forEach((node, list) -> list.getItems().clear());
	}
}
