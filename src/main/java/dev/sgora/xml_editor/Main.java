package dev.sgora.xml_editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
        Parent root = loader.load();

        stage.setTitle("XML Editor");
        stage.show();
        stage.requestFocus();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
