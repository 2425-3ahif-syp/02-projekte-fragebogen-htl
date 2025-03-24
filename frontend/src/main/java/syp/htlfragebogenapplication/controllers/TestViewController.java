package syp.htlfragebogenapplication.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import syp.htlfragebogenapplication.model.Test;

import java.io.IOException;
public class TestViewController {

    @FXML
    private Label testNameLabel;

    @FXML
    private Label testDescriptionLabel;

    @FXML
    private VBox questionsContainer;

    private static Stage primaryStage;

    private Test test;

    public void initData(Test test) {
        this.test = test;
        testNameLabel.setText(test.getName());
        testDescriptionLabel.setText(test.getDescription());

        // TODO: Load questions from the database
        loadQuestions();
    }

    private void loadQuestions() {
        // TODO: Implement question loading from database
        // This will depend on your Question model and database structure
    }

    @FXML
    private void onSubmitButtonClicked() {
        // TODO: Implement test submission logic
    }

    public static void show(Stage stage, Test test) {
        try {
            primaryStage = stage;
            FXMLLoader loader = new FXMLLoader(TestViewController.class.getResource("/syp/htlfragebogenapplication/TestView.fxml"));
            Parent root = loader.load();

            TestViewController controller = loader.getController();
            controller.initData(test);

            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            try {
                scene.getStylesheets().add(TestViewController.class.getResource("/syp/htlfragebogenapplication/TestView.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS file not found, continuing without styling");
            }

            stage.setTitle("Test: " + test.getName());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Could not load test view: " + e.getMessage());
        }
    }

    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void onBackButtonClicked(ActionEvent actionEvent) {
        MainViewController.show(primaryStage);
    }
}