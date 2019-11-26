package dev.sgora.xml_editor.services.webdata;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class WebDataService {
	private static final String URL = "https://time.is/";

	@Inject
	public WebDataService() {
		try {
			System.out.println(requestDate());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String requestDate() throws IOException {
		Document document = Jsoup.parse(new URL(URL),10000);
		List<String> dateParts = new ArrayList<>(Arrays.asList(document.selectFirst("#dd").ownText().split(",")));
		dateParts.remove(dateParts.size() - 1);
		return document.selectFirst("#clock").text() + " " + String.join(",", dateParts);
	}
}
