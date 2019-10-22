package dev.sgora.xml_editor.services.validation;

import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.ObjectFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ValidationService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	public void validateXML() {
		try {
			var unmarshaller = JAXBContext.newInstance(ObjectFactory.class).createUnmarshaller();
			unmarshaller.setEventHandler(new ValidationErrorHandler());
			var factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			unmarshaller.setSchema(factory.newSchema(new StreamSource(new File("xml/account-statement.xsd"))));
			JAXBElement<AccountStatement> model = (JAXBElement<AccountStatement>) unmarshaller.unmarshal(new StreamSource(new File("xml/account-statement-1.xml")));
			AccountStatement value = model.getValue();
		} catch (JAXBException | SAXException e) {
			logger.log(Level.SEVERE, "Validation exception", e);
		}
	}
}
