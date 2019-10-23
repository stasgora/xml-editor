package dev.sgora.xml_editor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.sgora.xml_editor.services.WindowModule;
import dev.sgora.xml_editor.services.ui.UIModule;
import dev.sgora.xml_editor.services.validation.ValidationModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class XMLEditor extends Application {
    private Injector injector;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
        Parent root = loader.load();

        injector = Guice.createInjector(new WindowModule(stage, root), new UIModule(), new ValidationModule());

        WindowView windowView = loader.getController();
        injector.injectMembers(windowView);

        stage.setTitle("XML Editor");
        stage.show();
        stage.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
