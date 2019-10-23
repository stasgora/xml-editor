package dev.sgora.xml_editor.services;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.sgora.xml_editor.model.AccountStatement;
import dev.sgora.xml_editor.model.Model;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class WindowModule extends AbstractModule {
	private final Stage window;
	private final Parent root;
	private final Model<AccountStatement> model = new Model<>();

	public WindowModule(Stage window, Parent root) {
		this.window = window;
		this.root = root;
	}

	@Provides
	@Singleton
	Model<AccountStatement> getModel() {
		return model;
	}

	@Provides
	@Singleton
	Parent root() {
		return root;
	}

	@Provides
	@Singleton
	Stage getWindow() {
		return window;
	}
}
