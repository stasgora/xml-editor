package dev.sgora.xml_editor.services.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.element.ComplexElement;
import dev.sgora.xml_editor.element.Element;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.ui.ModelUIMapper;
import javafx.scene.control.MenuItem;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ValidationErrorHandler implements ValidationEventHandler {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private Model<AccountStatement> model;

	@Inject
	public ValidationErrorHandler(Model<AccountStatement> model) {
		this.model = model;
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
		Optional<ComplexElement> element = model.elements.stream().filter(elem -> elem.modelValue == object).findFirst();
		if(element.isEmpty()) {
			logger.log(Level.WARNING, "Invalid model object is not present in mapper maps");
			return;
		}
		element.get().addError(message, false);
	}

	public void clearErrorMessages() {
		clearErrorMessages(model.rootElements);
	}

	public void clearErrorMessages(List<? extends Element> elements) {
		for (Element element : elements) {
			element.clearErrors(false);
			if(element instanceof ComplexElement)
				clearErrorMessages(((ComplexElement) element).children);
		}
	}
}
