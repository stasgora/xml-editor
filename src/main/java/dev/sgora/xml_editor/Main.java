package dev.sgora.xml_editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);

        WindowView windowView = loader.getController();
        windowView.init();

        stage.setTitle("XML Editor");
        stage.show();
        stage.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
