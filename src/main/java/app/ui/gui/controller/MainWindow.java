package app.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.junit.FixMethodOrder;

import java.util.ArrayList;
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

    private Label[] lbLayerSizes;
    private Pane[] paneOutLayerArrows;

    @FXML
    public void initialize() {
        lbLayerSizes = new Label[] {lbLayer0, lbLayer1, lbLayer2};
        paneOutLayerArrows = new Pane[] {paneOut0, paneOut1, paneOut2};
        lbLayerSizes[0].setText("3 Nodes");
        paneOut2.setVisible(true);
    }

}
