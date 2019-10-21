package dev.sgora.xml_editor;

import dev.sgora.xml_editor.model.AccountStatement;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	void init() {
		try {
			var unmarshaller = JAXBContext.newInstance(AccountStatement.class).createUnmarshaller();
			unmarshaller.setEventHandler(new ValidationErrorHandler());
			var factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			unmarshaller.setSchema(factory.newSchema(new StreamSource(Main.class.getResourceAsStream("/xml/account-statement.xsd"))));
			Object model = unmarshaller.unmarshal(new StreamSource(Main.class.getResourceAsStream("/xml/account-statement-1.xml")));
		} catch (JAXBException | SAXException e) {
			logger.log(Level.SEVERE, "Validation exception", e);
		}
	}
}
