package dev.sgora.xml_editor.services.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.XMLEditor;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.ObjectFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ValidationService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private Binder<Node> binder;
	JAXBElement<AccountStatement> model;
	private DocumentBuilder documentBuilder;
	private Node node;
	private Schema schema;

	@Inject
	private ValidationService() {
		try {
			var factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = factory.newSchema(new StreamSource(XMLEditor.class.getResourceAsStream("/account-statement.xsd")));
		} catch (SAXException e) {
			logger.log(Level.SEVERE, "Schema loading failed", e);
		}
	}

	public AccountStatement loadXML(File xmlFile) throws ValidationException {
		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(xmlFile);
			JAXBContext context = JAXBContext.newInstance(AccountStatement.class);

			binder = context.createBinder();
			binder.setSchema(schema);
			binder.setEventHandler(new ValidationErrorHandler());
			binder.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			model = binder.unmarshal(document, AccountStatement.class);
			node = binder.getXMLNode(model);
			return model.getValue();
		} catch (JAXBException | ParserConfigurationException | SAXException | IOException e) {
			logger.log(Level.SEVERE, "Loading XML failed", e);
			throw new ValidationException("Loading XML failed", e);
		}
	}

	public void validateXML() {
		try {
			node = binder.updateXML(model, node);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(node), new StreamResult(System.out));
		} catch (JAXBException | TransformerException e) {
			logger.log(Level.SEVERE, "Validating XML failed", e);
		}
	}
}
