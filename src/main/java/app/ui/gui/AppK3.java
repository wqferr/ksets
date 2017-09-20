package app.ui.gui;

import app.ui.gui.controller.MainWindow;
import app.ui.text.TextInterpreter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

public class AppK3 extends Application {

    private static void textInputLoop() {
        TextInterpreter ti = new TextInterpreter();
        String line;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while ((line = in.readLine()) != null) {
                try {
                    ti.execute(line);
                } catch (IllegalArgumentException e) {
                    System.out.printf("Error: %s\n", e.getMessage());
                } catch (NoSuchElementException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from stdin");
        }
    }

    public static void main(String[] args) {
        boolean text = false;
        for (String arg : args)
            if ("-t".equals(arg))
                text = true;

        if (text) {
            textInputLoop();
        } else {
            launch(args);
        }
    }

    private FXMLLoader createLoader(String path) {
        return new FXMLLoader(getClass().getResource(path));
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
            controller.application = this;
        } catch (Exception e) {
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
