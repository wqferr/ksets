package app.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

public class MainWindow {

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
    public void initialize() {
        paneNoModelLoaded.setVisible(false);
        paneModelLayers.setVisible(true);
        lbLayerSizes.get(0).setText("3 Nodes");
        paneOutLayerArrows.get(0).setVisible(true);
    }

}
