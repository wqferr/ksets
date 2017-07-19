package app.ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AppK3 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private FXMLLoader createLoader(String path) {
        return new FXMLLoader(getClass().getResource(path));
    }

    private Pane load(String path) throws IOException {
        return FXMLLoader.load(getClass().getResource(path));
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hi");
        Pane pane;
        try {
            pane = load("view/MainWindow.fxml");
        } catch (IOException e) {
            System.err.println("deu ruim");
            return;
        }

        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
