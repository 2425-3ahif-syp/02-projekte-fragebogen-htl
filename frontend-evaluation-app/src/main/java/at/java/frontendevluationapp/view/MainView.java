package at.java.frontendevluationapp.view;

import at.java.frontendevluationapp.controller.MainViewController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainView extends BorderPane {

    private final VBox contentContainer;
    private final Label titleLabel;
    private final Button uploadFilesButton;
    private final Label uploadStatusLabel;
    private final VBox uploadedFilesList;
    private final ScrollPane scrollPane;

    public MainView() {

        // Main container with padding and spacing
        contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(30));
        contentContainer.setAlignment(Pos.TOP_CENTER);
        contentContainer.getStyleClass().add("upload-container");

        // Title label
        titleLabel = new Label("JSON-Testdateien hochladen");
        titleLabel.getStyleClass().add("upload-title-label");

        // Upload button
        uploadFilesButton = new Button("Dateien auswählen");
        uploadFilesButton.getStyleClass().add("main-btn");

        // Upload status label
        uploadStatusLabel = new Label("Noch keine Dateien hochgeladen.");
        uploadStatusLabel.getStyleClass().add("upload-status-label");

        // Uploaded files display (placeholder container)
        uploadedFilesList = new VBox(10);
        uploadedFilesList.setPadding(new Insets(10));
        uploadedFilesList.setAlignment(Pos.TOP_LEFT);
        uploadedFilesList.getStyleClass().add("uploaded-files-list");

        // Scrollable pane for uploaded files
        scrollPane = new ScrollPane(uploadedFilesList);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("uploaded-files-scroll");

        // Assemble content
        contentContainer.getChildren().addAll(titleLabel, uploadFilesButton, uploadStatusLabel, scrollPane);
        this.setCenter(contentContainer);
    }

    // Getters for controller access
    public Button getUploadFilesButton() {
        return uploadFilesButton;
    }

    public Label getUploadStatusLabel() {
        return uploadStatusLabel;
    }

    public VBox getUploadedFilesList() {
        return uploadedFilesList;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }
}