package at.java.frontendevaluationapp.controllers;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import syp.htlfragebogenapplication.database.TestRepository;
import syp.htlfragebogenapplication.model.Test;
import syp.htlfragebogenapplication.view.MainView;

import java.util.List;

public class MainViewController {
    private GridPane testGrid;
    private TextField searchField;

    private static Stage primaryStage;

    public void initialize() {
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
        noResultsLabel.getStyleClass().add("no-results-label");

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

        HBox questionCountBox = new HBox();
        questionCountBox.setAlignment(Pos.CENTER_LEFT);
        questionCountBox.setSpacing(8);
        questionCountBox.setPadding(new Insets(0, 0, 10, 0));

        Label labelCount = new Label("Fragenanzahl: " + test.getQuestionCount());
        labelCount.getStyleClass().add("question-count-label");

        questionCountBox.getChildren().add(labelCount);

        Button button = new Button("Test durchführen");
        button.setAlignment(Pos.BASELINE_CENTER);
        button.setId(String.valueOf(test.getId()));
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPadding(new Insets(10, 15, 10, 15));
        button.getStyleClass().add("test-start-btn");

        // Add event handler for the button
        button.setOnAction(event -> openTestView(test.getId()));

        vbox.getChildren().addAll(label, spacer, questionCountBox, button);

        return vbox;
    }

    public static void show(Stage stage) {
        try {
            primaryStage = stage;
            MainViewController controller = new MainViewController();
            MainView mainView = new MainView(controller);

            Scene scene = new Scene(mainView, 1500, 800);
            scene.getStylesheets().add(
                    MainViewController.class.getResource("/syp/htlfragebogenapplication/Base.css").toExternalForm());
            scene.getStylesheets().add(MainViewController.class
                    .getResource("/syp/htlfragebogenapplication/mainview/main-view.css").toExternalForm());

            stage.setTitle("HTL Fragebogen Application");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Could not load main view: " + e.getMessage());
        }
    }

    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to open the TestView
    private void openTestView(int testId) {
        Test test = new TestRepository().getTestById(testId);
        TestViewController.show(primaryStage, test);
    }

    // Setters for MainView to inject components
    public void setTestGrid(GridPane testGrid) {
        this.testGrid = testGrid;
    }

    public void setSearchField(TextField searchField) {
        this.searchField = searchField;
    }
}
