package dev.sgora.xml_editor.services.ui.dialog;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

@Singleton
public class DialogService {
	private final Stage window;

	private EventHandler<KeyEvent> pressOnEnter = event -> {
		if (KeyCode.ENTER.equals(event.getCode()) && event.getTarget() instanceof Button) {
			((Button) event.getTarget()).fire();
		}
	};

	@Inject
	private DialogService(Stage window) {
		this.window = window;
	}

	public File showFileChooser(FileChooserAction action, String title, FileChooser.ExtensionFilter extensionFilter) {
		FileChooser projectFileChooser = new FileChooser();
		projectFileChooser.setTitle(title);
		projectFileChooser.getExtensionFilters().addAll(extensionFilter, new FileChooser.ExtensionFilter("All Files", "*.*"));
		if (action == FileChooserAction.SAVE_DIALOG) {
			return projectFileChooser.showSaveDialog(window);
		} else if (action == FileChooserAction.OPEN_DIALOG) {
			return projectFileChooser.showOpenDialog(window);
		}
		return null;
	}

	public String showTextDialog(String title, String value) {
		var dialog = new TextInputDialog(value);
		dialog.setHeaderText(title);
		return dialog.showAndWait().orElse(null);
	}

	public Optional<ButtonType> showErrorDialog(String title, String header, String content) {
		return showDialog(Alert.AlertType.ERROR, title, header, content, null);
	}

	private Optional<ButtonType> showDialog(Alert.AlertType type, String title, String header, String content, ButtonType[] buttons) {
		Alert dialog;
		if (buttons != null) {
			dialog = new Alert(type, content, buttons);
		} else {
			dialog = new Alert(type, content);
		}
		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getButtonTypes().stream().map(dialogPane::lookupButton).forEach(button -> button.addEventHandler(KeyEvent.KEY_PRESSED, pressOnEnter));
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		return dialog.showAndWait();
	}
}
