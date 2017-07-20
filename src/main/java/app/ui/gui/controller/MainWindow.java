package app.ui.gui.controller;

import app.ui.text.TextInterpreter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class MainWindow {

    //private static final ButtonType CONFIRM_BTN = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

    private TextInterpreter interpreter;

    @FXML
    public Label lbLayer0;
    @FXML
    public Label lbLayer1;
    @FXML
    public Label lbLayer2;

    @FXML
    public Pane paneOut0;
    @FXML
    public Pane paneOut1;
    @FXML
    public Pane paneOut2;

    @FXML
    public HBox paneModelLayers;
    @FXML
    public VBox paneNoModelLoaded;


    @FXML
    private List<Label> lbLayerSizes;
    @FXML
    private List<Pane> paneOutLayerArrows;

    @FXML
    private void initialize() {
        interpreter = new TextInterpreter();
    }

    @FXML
    private void notImplemented() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Not implemented");
        alert.setContentText("Not implemented yet");

        alert.showAndWait();
    }

    @FXML
    private void test() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setHeaderText("0");

        // dont do this, overrides buttons
        //VBox content = new VBox();
        //for (int i = 0; i < 5; i++)
        //    content.getChildren().add(new Label("label " + i));

        //DialogPane pane = new DialogPane();
        //pane.setContent(content);

        //alert.setDialogPane(pane);
        alert.setContentText("1\n2\n3");
        Optional<ButtonType> result = alert.showAndWait();

        System.out.println("result.isPresent() = " + result.isPresent());
        System.out.println("result.get() == ButtonType.OK = " + (result.get() == ButtonType.OK));
    }

    @FXML
    private void handleCreateModel() {
    }

    private static Dialog createDialog(String title, Pane content) {
        Dialog dialog = new Dialog();
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(content);
        return dialog;
    }

    private void setModelLoaded(boolean loaded) {
        paneModelLayers.setVisible(loaded);
        paneNoModelLoaded.setVisible(!loaded);
    }

}
