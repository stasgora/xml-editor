package dev.sgora.xml_editor.services.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.element.ComplexElement;
import dev.sgora.xml_editor.element.Element;
import dev.sgora.xml_editor.element.position.FieldPosition;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import dev.sgora.xml_editor.services.Utils;
import dev.sgora.xml_editor.services.ui.ModelUIMapper;
import dev.sgora.xml_editor.services.ui.element.UIElementFactory;
import javafx.scene.control.MenuItem;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Singleton
public class ValidationErrorHandler implements ValidationEventHandler {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private static final Pattern ELEM_NAME_PATTERN = Pattern.compile("'.*'.*'([a-zA-Z]*)'");

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
		String errorMessage = transformMessage(message);
		Matcher matcher = ELEM_NAME_PATTERN.matcher(errorMessage);
		Element invalidElement = getInvalidElement(object, element.get(), matcher, errorMessage);
		if(invalidElement != null) {
			String name = UIElementFactory.transformFieldName(getElementName(invalidElement));
			StringBuffer sb = new StringBuffer();
			matcher.appendReplacement(sb, matcher.group(0).replaceFirst(matcher.group(1), name));
			matcher.appendTail(sb);
			invalidElement.addError(sb.toString(), false);
		}
	}

	private Element getInvalidElement(Object object, ComplexElement element, Matcher matcher, String errorMessage) {
		if(!Utils.isClassInternal(object.getClass()))
			return element;
		if(!matcher.find() || matcher.groupCount() < 1)
			return null;
		Optional<Element> child = ((Stream<Element>) element.children.stream()).filter(elem -> getElementName(elem).equals(matcher.group(1))).findFirst();
		if(child.isEmpty()) {
			logger.log(Level.WARNING, "No child element found with name " + matcher.group(1));
			return null;
		}
		return child.get();
	}

	private String getElementName(Element element) {
		return ((FieldPosition) element.position).objectField.getName();
	}

	private String transformMessage(String message) {
		message = message.replaceFirst(".*: ", "");
		if(message.endsWith("."))
			message = message.substring(0, message.length() - 1);
		return message;
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
