package app.ui.gui.controller;

import app.ui.text.TextInterpreter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        GridPane pane = new GridPane();
        List<TextField> txtLayerSizes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            txtLayerSizes.add(new TextField());
            pane.addRow(i, new Label(String.format("Layer %d size:", i)), txtLayerSizes.get(i));
        }
        Platform.runLater(txtLayerSizes.get(0)::requestFocus);
        Dialog<Boolean> dialog = createDialog("title", pane);

        // Check if user pressed OK
        boolean doCreate = dialog.showAndWait().orElse(false); // Default to canceling
        if (doCreate) {
            List<String> args = txtLayerSizes.stream()
                    .map(textField -> textField.getText())
                    .collect(Collectors.toList());
            // If every arg is a positive integer
            if (args.stream().allMatch(str -> checkInt(str, i -> i > 0))) {
                // Concatenate args with "new"
                String cmd = args.stream().reduce(
                        "new",
                        (s, s2) -> String.join(" ", s, s2)
                );
                try {
                    interpreter.execute(cmd);
                    // TODO show OK dialog
                } catch (NoSuchElementException | IllegalArgumentException exc) {
                    // TODO show error dialog
                }
            } else {
                // TODO show error dialog
            }
        }
    }

    private boolean checkInt(String str, Predicate<Integer> predicate) {
        if (str == null)
            return false;
        try {
            int i = Integer.parseInt(str);
            return predicate.test(i);
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    private Optional<Integer> toNonnegativeInteger(String str) {
        if (str == null)
            return Optional.empty();
        try {
            return Optional.of(Integer.valueOf(str));
        } catch (NumberFormatException exc) {
            return Optional.empty();
        }
    }

    private static <R> Dialog<R> createDialog(String title, Pane content, Callback<ButtonType, R> converter) {
        Dialog dialog = new Dialog();
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(converter);
        return dialog;
    }

    private static Dialog<Boolean> createDialog(String title, Pane content) {
        return createDialog(title, content, (btnType) -> btnType == ButtonType.OK);
    }

    private void setModelLoaded(boolean loaded) {
        paneModelLayers.setVisible(loaded);
        paneNoModelLoaded.setVisible(!loaded);
    }

}
