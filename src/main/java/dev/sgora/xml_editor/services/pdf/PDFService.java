package dev.sgora.xml_editor.services.pdf;

import dev.sgora.xml_editor.services.webdata.WebDataService;
import dev.sgora.xml_editor.services.xml.XMLService;
import org.apache.fop.apps.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.text.Normalizer;

@Singleton
public class PDFService {
	private XMLService xmlService;
	private WebDataService webDataService;
	private static final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

	@Inject
	public PDFService(XMLService xmlService, WebDataService webDataService) {
		this.xmlService = xmlService;
		this.webDataService = webDataService;
	}

	public void generatePDF(File outFile) {
		updateBalances();
		validateStreet("bank/address");
		validateStreet("client/address");

		StreamSource xmlSource = new StreamSource(new ByteArrayInputStream(xmlService.serializeXML().toByteArray()));
		FOUserAgent userAgent = fopFactory.newFOUserAgent();
		try (OutputStream out = new FileOutputStream(outFile)) {
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, out);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(new File("pdf.xsl")));//new StreamSource(XMLEditor.class.getResourceAsStream("/pdf.xsl")));

			transformer.transform(xmlSource, new SAXResult(fop.getDefaultHandler()));
		} catch (IOException | TransformerException | FOPException e) {
			e.printStackTrace();
		}
	}

	private void validateStreet(String xmlPath) {
		var street = webDataService.requestStreet(xmlService.query(xmlPath + "/postCode").getTextContent());
		street = street.replace('ł', 'l');
		street = street.replace('ń', 'n');
		street = street.replace('ą', 'a');
		street = street.replace('ę', 'e');
		if(street != null && !street.isEmpty())
			xmlService.query(xmlPath + "/street").setTextContent(street);
	}

	private void updateBalances() {
		var history = xmlService.query("transactionHistory");
		var transactions = history.getChildNodes();

		var period = xmlService.query("period");
		double balance = Double.parseDouble(period.getAttributes().getNamedItem("startBalance").getTextContent());
		for (int i = 0; i < transactions.getLength(); i++) {
			balance += Double.parseDouble(transactions.item(i).getLastChild().getTextContent());
			transactions.item(i).appendChild(xmlService.createElement("balanceAfer", String.valueOf(balance)));
		}
		period.appendChild(xmlService.createElement("endBalance", String.valueOf(balance)));
	}
}
