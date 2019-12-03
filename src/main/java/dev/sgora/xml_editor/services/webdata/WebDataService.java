package dev.sgora.xml_editor.services.webdata;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class WebDataService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private static final String URL = "https://znajdzkodpocztowy.pl/szukaj/mk-";

	@Inject
	public WebDataService() {
	}

	public String requestStreet(String code) {
		try {
			Document document = Jsoup.parse(new URL(URL + code),10000);
			var element = document.selectFirst(".tab_tresc > div > p:nth-of-type(2) a");
			return element != null ? element.text() : "";
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Street request failed", e);
			return "";
		}
	}
}
