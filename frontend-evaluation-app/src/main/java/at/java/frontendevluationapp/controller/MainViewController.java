package at.java.frontendevluationapp.controller;

import at.java.frontendevluationapp.model.TestResult;
import at.java.frontendevluationapp.view.MainView;
import at.java.frontendevluationapp.view.NavbarView;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainViewController {

    private static Stage primaryStage;
    private final Label uploadStatusLabel;
    private final VBox uploadedFilesList;
    private final List<TestResult> uploadedResults;

    private final ObjectMapper mapper;

    private void setUploadedResults(List<TestResult> results) {
        this.uploadedResults.clear();
        this.uploadedResults.addAll(results);
    }

    public MainViewController(MainView view) {
        this.uploadStatusLabel = view.getUploadStatusLabel();
        this.uploadedFilesList = view.getUploadedFilesList();
        this.uploadedResults = ClassOverviewController.getUploadedResults();
        this.mapper = new ObjectMapper();

        // Initialize uploaded files list
        for( TestResult result : uploadedResults) {
            addFileEntry(result.getStudentName(), result.getTestName(), result.getFileName(), result);
        }

        // Link button to action
        view.getUploadFilesButton().setOnAction(e -> handleUpload());
    }

    public void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wähle JSON Testdateien aus");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Dateien", "*.json"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);

        if (selectedFiles == null || selectedFiles.isEmpty()) {
            uploadStatusLabel.setText("Keine Dateien ausgewählt.");
            return;
        }

        int accepted = 0;
        int rejected = 0;

        for (File file : selectedFiles) {
            try {
                TestResult result = mapper.readValue(file, TestResult.class);
                result.setFileName(file.getName());
                uploadedResults.add(result);
                addFileEntry(result.getStudentName(), result.getTestName(), result.getFileName(), result);
                accepted++;
                ClassOverviewController.setTestResults(uploadedResults);
            } catch (Exception ex) {
                ex.printStackTrace(); // or log to UI
                rejected++;
            }
        }

        uploadStatusLabel.setText("Hochgeladen: " + accepted + " | Abgelehnt: " + rejected);
    }

    private void addFileEntry(String studentName, String testName, String fileName, TestResult result) {
        Label nameLabel = new Label(studentName + " - " + testName);
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label fileLabel = new Label("Datei: " + fileName);
        fileLabel.setStyle("-fx-text-fill: #777;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Pushes the remove button to the right

        Button removeButton = new Button("X");
        removeButton.getStyleClass().add("remove-button");
        removeButton.setTooltip(new Tooltip("Datei entfernen"));

        HBox entry = new HBox(10, nameLabel, fileLabel, spacer, removeButton);
        entry.setStyle("-fx-padding: 8; -fx-background-color: #f5f5f5; -fx-border-color: #ddd;");
        entry.setAlignment(Pos.CENTER_LEFT);

        // Remove action
        removeButton.setOnAction(e -> {
            uploadedFilesList.getChildren().remove(entry);
            uploadedResults.remove(result);
            uploadStatusLabel.setText("Datei entfernt: " + fileName);
            ClassOverviewController.setTestResults(uploadedResults);

        });

        uploadedFilesList.getChildren().add(entry);
    }



    public List<TestResult> getUploadedResults() {
        return uploadedResults;
    }

    public static void show(Stage stage) {
        try {
            primaryStage = stage;

            MainView mainView = new MainView();
            MainViewController controller = new MainViewController(mainView);
            NavbarViewController navbarController = new NavbarViewController(stage);
            NavbarView navbar = new NavbarView(navbarController);

            VBox rootLayout = new VBox();
            rootLayout.getChildren().addAll(navbar, mainView);
            VBox.setVgrow(mainView, Priority.ALWAYS); // Let MainView expand

            Scene scene = new Scene(rootLayout, 1500, 800);
            scene.getStylesheets().add(MainViewController.class.getResource("/style/Base.css").toExternalForm());
            scene.getStylesheets().add(MainViewController.class.getResource("/style/MainView.css").toExternalForm());

            stage.setTitle("HTL Fragebogen Evaluation Application");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
