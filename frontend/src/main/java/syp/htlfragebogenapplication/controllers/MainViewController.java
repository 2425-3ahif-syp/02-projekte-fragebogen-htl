package syp.htlfragebogenapplication.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import syp.htlfragebogenapplication.database.Database;
import syp.htlfragebogenapplication.database.TestRepository;
import syp.htlfragebogenapplication.model.Test;

import java.sql.Connection;
import java.util.List;


public class MainViewController {
    private final Connection connection = Database.getInstance().getConnection();

    @FXML
    public GridPane testGrid;

    @FXML
    public TextField searchField;

    public MainViewController() {
    }

    public void initialize() {
        System.out.println("🚀 Initializing Controller...");
        List<Test> testList = new TestRepository().getAllTests();
        displayTests(testList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            performSearch(newValue);
        });

        Platform.runLater(() -> {
            searchField.getParent().requestFocus();
        });
    }

    private void performSearch(String searchTerm) {
        searchTerm = searchTerm.toLowerCase().trim();

        List<Test> allTests = new TestRepository().getAllTests();
        String finalSearchTerm = searchTerm;
        List<Test> filteredTests = allTests.stream()
                .filter(test -> test.getName().toLowerCase().contains(finalSearchTerm) ||
                        test.getDescription().toLowerCase().contains(finalSearchTerm))
                .toList();

        displayTests(filteredTests);
    }

    private void displayTests(List<Test> tests) {
        testGrid.getChildren().clear();

        if (tests.isEmpty()) {
            showNoResultsMessage();
            return;
        }

        int rowIndex = 0;
        int columnIndex = 0;

        for (Test test : tests) {
            VBox vbox = createTestCard(test);

            GridPane.setRowIndex(vbox, rowIndex);
            GridPane.setColumnIndex(vbox, columnIndex);

            testGrid.getChildren().add(vbox);

            if (columnIndex == 4) {
                columnIndex = 0;
                rowIndex++;
            } else {
                columnIndex++;
            }
        }

        testGrid.requestLayout();
    }

    private void showNoResultsMessage() {
        VBox messageBox = new VBox();
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPrefWidth(testGrid.getWidth());
        messageBox.setPrefHeight(300);

        Label noResultsLabel = new Label("Keine Tests gefunden");
        noResultsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #666;");

        messageBox.getChildren().add(noResultsLabel);

        GridPane.setRowIndex(messageBox, 0);
        GridPane.setColumnIndex(messageBox, 0);
        GridPane.setColumnSpan(messageBox, 5);
        GridPane.setRowSpan(messageBox, 3);

        testGrid.getChildren().add(messageBox);
    }

    private VBox createTestCard(Test test) {
        VBox vbox = new VBox();
        vbox.getStyleClass().add("card");
        vbox.setMaxHeight(200);
        vbox.setMaxWidth(400);
        VBox.setMargin(vbox, new Insets(10));

        Label label = new Label(test.getName());
        label.getStyleClass().add("card-label");
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(label, new Insets(0, 0, 10, 0));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button button = new Button("Test durchführen");
        button.setAlignment(Pos.BASELINE_CENTER);
        button.setId(String.valueOf(test.getId()));
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPadding(new Insets(10, 15, 10, 15));

        vbox.getChildren().addAll(label, spacer, button);

        return vbox;
    }
}