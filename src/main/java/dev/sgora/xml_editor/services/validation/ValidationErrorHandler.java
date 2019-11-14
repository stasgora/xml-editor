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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.Field;
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
	private static final Pattern ELEM_NAME_PATTERN = Pattern.compile("element '([a-zA-Z]*)'");
	private static final Pattern ATTR_NAME_PATTERN = Pattern.compile("attribute '([a-zA-Z]*)'");

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
		Matcher matcher = errorMessage.contains("attribute '") ? ATTR_NAME_PATTERN.matcher(errorMessage) : ELEM_NAME_PATTERN.matcher(errorMessage);
		Element invalidElement = getInvalidElement(object, element.get(), matcher, errorMessage);
		if(invalidElement != null) {
			String name = UIElementFactory.transformFieldName(getElementName(invalidElement));
			StringBuffer sb = new StringBuffer();
			matcher.appendReplacement(sb, "element '" + name + "'");
			sb.append(" is not valid");
			if(sb.length() > 100)
				invalidElement.addError("Value is invalid", false);
			else
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
		Field field = ((FieldPosition) element.position).objectField;
		XmlAttribute attribute = field.getAnnotation(XmlAttribute.class);
		if(attribute != null)
			return attribute.name();
		return field.getName();
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

	private void clearErrorMessages(List<? extends Element> elements) {
		for (Element element : elements) {
			element.clearErrors(false);
			if(element instanceof ComplexElement)
				clearErrorMessages(((ComplexElement) element).children);
		}
	}

	public boolean documentHasErrors() {
		return documentHasErrors(model.rootElements);
	}

	private boolean documentHasErrors(List<? extends Element> elements) {
		for (Element element : elements) {
			if(element.hasErrors())
				return true;
			if(element instanceof ComplexElement && documentHasErrors(((ComplexElement) element).children))
				return true;
		}
		return false;
	}
}
