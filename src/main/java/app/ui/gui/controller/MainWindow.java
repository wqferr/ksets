package app.ui.gui.controller;

import app.ui.text.TextInterpreter;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class MainWindow {

    public Application application;

    public Button btnCreate;

    private TextInterpreter interpreter;
    private Stage stage;

    private FileChooser modelChooser;
    private FileChooser dataChooser;

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
    private List<Button> btnKsetDependent;

    @FXML
    private void initialize() {
        interpreter = new TextInterpreter();

        Platform.runLater(btnCreate::requestFocus);
        setModelLoaded(false);

        modelChooser = new FileChooser();
        modelChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        modelChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("KIII models (.k3)", "*.k3")
        );

        dataChooser = new FileChooser();
        dataChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        dataChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV File (*.csv)", "*.csv")
        );
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
    private void showHelp() {
        application.getHostServices().showDocument("reference.pdf");
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
    }

    @FXML
    private void handleCreateModel() {
        GridPane pane = new GridPane();
        List<TextField> txtLayerSizes = new ArrayList<>();

        pane.setVgap(10);
        pane.setHgap(10);
        for (int i = 0; i < 3; i++) {
            txtLayerSizes.add(new TextField());
            pane.addRow(i, new Label(format("Layer %d size:", i)), txtLayerSizes.get(i));
        }

        pane.add(new Separator(Orientation.HORIZONTAL), 0, 3, 2, 1);

        pane.addRow(4, new Label("Input layer:"), new Label("0"));

        ChoiceBox choiceBox = new ChoiceBox<>(
                FXCollections.observableArrayList("0", "1", "2")
        );
        choiceBox.setValue("2");

        pane.addRow(
                5,
                new Label("Output layer:"),
                choiceBox
        );

        Platform.runLater(txtLayerSizes.get(0)::requestFocus);
        Dialog<Boolean> dialog = createDialog("Create model", pane);

        boolean done = false;

        do { // Repeat until either canceled or a valid input is given
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
                        interpreter.execute(cmd); // "new" command
                        interpreter.execute(format("set output_layer %s", choiceBox.getValue())); // set output layer
                        done = true;
                        updateModelDisplay();
                        showMessage("Success", "Model successfully created");
                    } catch (NoSuchElementException | IllegalArgumentException exc) {
                        showErrorDialog(
                                "Error creating model",
                                exc.getMessage(),
                                "This error was unexpected. Please open an issue at the GitHub repository"
                        );
                    }
                } else {
                    showErrorDialog("Invalid layer size", "All layers must have positive integers as sizes");
                }
            } else {
                done = true;
            }
        } while (!done);
    }

    @FXML
    private void handleSaveModel() {
        if (!checkKset())
            return;
        modelChooser.setTitle("Save model");
        File file = modelChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                interpreter.execute("save " + file.getAbsolutePath());
                showMessage("Success", "Model saved successfully");
            } catch (IllegalArgumentException exc) {
                showErrorDialog(
                        "Error saving model to file " + file.getAbsolutePath(),
                        "Could not write to file " + file.getAbsolutePath(),
                        exc.getMessage()
                );
            }
        }
    }

    @FXML
    private void handleLoadModel() {
        modelChooser.setTitle("Load model");
        File file = modelChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                interpreter.execute("load " + file.getAbsolutePath());
                updateModelDisplay();
                showMessage("Success", "Model loaded successfully");
            } catch (IllegalArgumentException exc) {
                showErrorDialog(
                        "Error loading model from file " + file.getAbsolutePath(),
                        "Could not load model from file " + file.getAbsolutePath(),
                        exc.getMessage()
                );
            }
        }
    }

    @FXML
    private void handleTrain() {
        if (!checkKset())
            return;

        dataChooser.setTitle("Open training dataset");
        File dataFile = dataChooser.showOpenDialog(stage);
        if (dataFile == null || !dataFile.exists())
            return;

        GridPane pane = new GridPane();
        pane.setHgap(20);
        List<CheckBox> cbkTrainLayer = new ArrayList<>();
        List<TextField> txtLearningRates = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            cbkTrainLayer.add(new CheckBox(format("Train layer %d", i)));
            txtLearningRates.add(new TextField(String.valueOf(interpreter.kset.getLearningRate(i))));
            Separator sep = new Separator(Orientation.VERTICAL);

            pane.addRow(i,
                    cbkTrainLayer.get(i),
                    sep,
                    new Label(format("Learning rate for layer %d:", i)),
                    txtLearningRates.get(i)
            );
        }

        Dialog<Boolean> dialog = createDialog("Layerwise training", pane);
        boolean doTrain = dialog.showAndWait().orElse(false);
        if (!doTrain)
            return;

        String cmd = cbkTrainLayer.stream()
                .map(CheckBox::isSelected)
                .map(String::valueOf)
                .reduce(
                        "set layer_training",
                        (s, s2) -> String.join(" ", s, s2)
        );

        try {
            interpreter.execute(cmd);
        } catch (IllegalArgumentException exc) {
            showErrorDialog(exc.getMessage());
            return;
        }

        for (int i = 0; i < 3; i++) {
            String rate = txtLearningRates.get(i).getText();
            if (!rate.isEmpty() && checkFloat(rate, f -> f > 0)) {
                // field not blank and valid learning rate
                try {
                    interpreter.execute(format("set learning_rate %d %s", i, rate));
                } catch (IllegalArgumentException exc) {
                    showErrorDialog(exc.getMessage());
                }
            }
        }

        try {
            interpreter.execute("train " + dataFile.getPath());
        } catch (IllegalArgumentException exc) {
            showErrorDialog(exc.getMessage());
            return;
        }

        showMessage("Training finished", "Training process completed");
    }

    @FXML
    private void handleSimulate() {
        if (!checkKset())
            return;

        dataChooser.setTitle("Select input dataset");
        File inputFile = dataChooser.showOpenDialog(stage);
        if (inputFile == null || !inputFile.exists())
            return;

        dataChooser.setTitle("Select output data file");
        File outputFile = dataChooser.showSaveDialog(stage);
        if (outputFile == null)
            return;

        try {
            interpreter.execute(format("run %s %s", inputFile.getPath(), outputFile.getPath()));
        } catch (IllegalArgumentException exc) {
            showErrorDialog(exc.getMessage());
            return;
        }

        showMessage("Simulation finished", "Simulation process completed");
    }

    private boolean checkKset() {
        return interpreter.kset != null;
    }

    private static void showMessage(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(content);
        alert.showAndWait();
    }

    private static void showErrorDialog(String content) {
        showErrorDialog("Error", content);
    }

    private static void showErrorDialog(String title, String header) {
        showErrorDialog(title, header, null);
    }

    private static void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        if (content != null)
            alert.setContentText(content);
        alert.showAndWait();
    }

    private static boolean checkInt(String str, Predicate<Integer> predicate) {
        if (str == null)
            return false;
        try {
            int i = Integer.parseInt(str);
            return predicate.test(i);
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    private static boolean checkFloat(String str, Predicate<Float> predicate) {
        if (str == null)
            return false;
        try {
            float f = Float.parseFloat(str);
            return predicate.test(f);
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

        btnKsetDependent.forEach(button -> button.setDisable(!loaded));
    }

    private void updateModelDisplay() {
        if (interpreter.kset == null) {
            setModelLoaded(false);
        } else {
            setModelLoaded(true);
            for (int i = 0; i < 3; i++) {
                lbLayerSizes.get(i).setText(interpreter.kset.getLayer(i).getSize() + " Nodes");
                paneOutLayerArrows.get(i).setVisible(false);
            }
            paneOutLayerArrows.get(interpreter.kset.getOutputLayer()).setVisible(true);
        }
    }

    public void setStage(Stage s) {
        stage = s;
    }

}
