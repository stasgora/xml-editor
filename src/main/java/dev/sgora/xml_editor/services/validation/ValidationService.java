package dev.sgora.xml_editor.services.validation;

import dev.sgora.xml_editor.XMLEditor;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.ObjectFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ValidationService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private Schema schema;

	public ValidationService() {
		try {
			var factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = factory.newSchema(new StreamSource(XMLEditor.class.getResourceAsStream("/account-statement.xsd")));
		} catch (SAXException e) {
			logger.log(Level.SEVERE, "Schema loading failed", e);
		}
	}

	public AccountStatement loadXML(File xmlFile) throws ValidationException {
		try {
			var unmarshaller = JAXBContext.newInstance(ObjectFactory.class).createUnmarshaller();
			unmarshaller.setEventHandler(new ValidationErrorHandler());
			unmarshaller.setSchema(schema);
			JAXBElement<AccountStatement> model = (JAXBElement<AccountStatement>) unmarshaller.unmarshal(new StreamSource(xmlFile));
			return model.getValue();
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, "Loading XML failed", e);
			throw new ValidationException("Loading XML failed", e);
		}
	}
}
