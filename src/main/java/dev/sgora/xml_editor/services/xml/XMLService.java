package dev.sgora.xml_editor.services.xml;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.XMLEditor;
import dev.sgora.xml_editor.element.ComplexElement;
import dev.sgora.xml_editor.element.ValueElement;
import dev.sgora.xml_editor.model.xml.AccountStatement;
import dev.sgora.xml_editor.model.xml.ObjectFactory;
import dev.sgora.xml_editor.services.ui.element.EmptyModelFactory;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class XMLService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private Binder<Node> binder;
	JAXBElement<AccountStatement> model;
	private DocumentBuilder documentBuilder;
	private Node node;
	private Schema schema;
	public ValidationErrorHandler errorHandler;

	@Inject
	private XMLService(ValidationErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		try {
			var factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = factory.newSchema(new StreamSource(XMLEditor.class.getResourceAsStream("/account-statement.xsd")));
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (SAXException | ParserConfigurationException e) {
			logger.log(Level.SEVERE, "Schema loading failed", e);
		}
		ValueElement.onModelUpdated = this::validateXML;
		ComplexElement.onElementsChanged = this::validateXML;
	}

	public AccountStatement loadXML(InputStream xmlFile) throws ValidationException {
		try {
			var model = loadXML(documentBuilder.parse(xmlFile));
			xmlFile.close();
			return model;
		} catch (JAXBException | SAXException | IOException e) {
			logger.log(Level.SEVERE, "Loading XML failed", e);
			throw new ValidationException("Loading XML failed", e);
		}
	}

	public AccountStatement loadXML(File xmlFile) throws ValidationException {
		try {
			return loadXML(documentBuilder.parse(xmlFile));
		} catch (JAXBException | SAXException | IOException e) {
			logger.log(Level.SEVERE, "Loading XML failed", e);
			throw new ValidationException("Loading XML failed", e);
		}
	}

	private AccountStatement loadXML(Document document) throws JAXBException {
		createBinder();
		model = binder.unmarshal(document, AccountStatement.class);
		node = binder.getXMLNode(model);
		validateXML();
		return model.getValue();
	}

	public AccountStatement createEmptyXML() {
		createBinder();
		model = new ObjectFactory().createAccountStatement(EmptyModelFactory.createEmptyModel(AccountStatement.class, null));
		try {
			node = documentBuilder.newDocument();
			binder.marshal(model, node);
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, "Marshaling XML failed", e);
		}
		return model.getValue();
	}

	public void validateXML() {
		try {
			errorHandler.clearErrorMessages();
			node = binder.updateXML(model, node);
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, "Validating XML failed", e);
		}
	}

	public ByteArrayOutputStream serializeXML() {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			transformer.transform(new DOMSource(node), new StreamResult(output));
			return output;
		} catch (TransformerException e) {
			logger.log(Level.SEVERE, "Serializing XML failed", e);
			return null;
		}
	}

	private void createBinder() {
		try {
			JAXBContext context = JAXBContext.newInstance(AccountStatement.class);

			binder = context.createBinder();
			binder.setSchema(schema);
			binder.setEventHandler(errorHandler);
			binder.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, "Creating binder failed", e);
		}
	}
}
