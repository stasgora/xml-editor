package dev.sgora.xml_editor.services.drive;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.Arrays;
import java.util.stream.Collectors;

@Singleton
public class DriveWorkspaceAction {
	private DriveService driveService;
	private Menu driveLoadMenu;

	@Inject
	public DriveWorkspaceAction(DriveService driveService) {
		this.driveService = driveService;
	}

	public void refreshDriveFiles() {
		String[] files = driveService.getFileList();
		driveLoadMenu.getItems().setAll(Arrays.stream(files).map(MenuItem::new).collect(Collectors.toList()));
	}

	public void init(Menu driveLoadMenu) {
		this.driveLoadMenu = driveLoadMenu;
	}
}
