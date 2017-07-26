package app.ui.gui;

import app.ui.gui.controller.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

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
        primaryStage.setTitle("Freeman's KIII");
        FXMLLoader loader;
        Pane pane;
        MainWindow controller;

        try {
            loader = createLoader("view/MainWindow.fxml");
            pane = loader.load();
            controller = (MainWindow) loader.getController();
        } catch (Exception e) {
            //e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("There was an error trying to load the application");
            alert.setContentText("\"" + e.getMessage() + "\"");
            alert.showAndWait();
            primaryStage.close();
            return;
        }

        controller.setStage(primaryStage);
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
