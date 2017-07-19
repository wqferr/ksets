package app.ui.gui.controller;

import app.ui.text.TextInterpreter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class MainWindow {

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
    private void notImplemented(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Not implemented");
        alert.setContentText("Not implemented yet");

        alert.showAndWait();
    }

    @FXML
    private void test(ActionEvent event) {
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

    private void setModelLoaded(boolean loaded) {
        paneModelLayers.setVisible(loaded);
        paneNoModelLoaded.setVisible(!loaded);
    }

}
