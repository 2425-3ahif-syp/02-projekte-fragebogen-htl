package at.java.frontendevaluationapp.controller;

import javafx.stage.Stage;

public class NavbarViewController {

    private final Stage primaryStage;

    public NavbarViewController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void onFileUploadClicked() {
        MainViewController.show(primaryStage);
    }

    public void onOverviewClicked() {
        ClassOverviewController.show(primaryStage);
    }
}