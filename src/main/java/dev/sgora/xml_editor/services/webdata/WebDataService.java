package dev.sgora.xml_editor.services.webdata;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

@Singleton
public class WebDataService {
	private static final String URL = "https://znajdzkodpocztowy.pl/szukaj/mk-";

	@Inject
	public WebDataService() {
	}

	public String requestStreet(String code) throws IOException {
		Document document = Jsoup.parse(new URL(URL + code),10000);
		var element = document.selectFirst(".tab_tresc > div > p:nth-of-type(2) a");
		return element != null ? element.text() : "";
	}
}
