package dev.sgora.xml_editor.services.drive;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.XMLEditor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class DriveService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private static final String USER = "user";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private Drive driveService;

	@Inject
	public DriveService() {
		try(InputStreamReader reader = new InputStreamReader(XMLEditor.class.getResourceAsStream("/credentials.json"))) {
			final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
					Collections.singletonList(DriveScopes.DRIVE)).setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
					.setAccessType("offline").build();
			var credentials = flow.loadCredential(USER);
			if(credentials == null) {
				LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
				credentials = new AuthorizationCodeInstalledApp(flow, receiver).authorize(USER);
			}
			driveService = new Drive.Builder(httpTransport, JSON_FACTORY, credentials).setApplicationName("XMLEditor").build();
		} catch (IOException | GeneralSecurityException e) {
			logger.log(Level.SEVERE, "Opening drive credentials failed", e);
		}
	}

	public void saveFile() throws DriveException {
		try {
			File fileMetadata = new File();
			fileMetadata.setName("account-statement-2.xml");
			FileContent content = new FileContent("text/plain", new java.io.File("xml/account-statement-1.xml"));
			File file = driveService.files().create(fileMetadata, content).setFields("id").execute();
		} catch (IOException e) {
			throw new DriveException("Saving file to Drive failed", e);
		}
	}

	public String[] getFileList() {
		return new String[] {"1", "2"};
	}
}
