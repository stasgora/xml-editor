package dev.sgora.xml_editor.services.pdf;

import dev.sgora.xml_editor.XMLEditor;
import dev.sgora.xml_editor.services.xml.XMLService;
import org.apache.fop.apps.*;

import javax.activation.FileDataSource;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

@Singleton
public class PDFService {
	private XMLService xmlService;
	private static final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

	@Inject
	public PDFService(XMLService xmlService) {
		this.xmlService = xmlService;
	}

	public void generatePDF(File outFile) {
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
}
