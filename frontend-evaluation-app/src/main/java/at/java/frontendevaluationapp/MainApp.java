package at.java.frontendevaluationapp;

import at.java.frontendevaluationapp.controller.MainViewController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Application.launch;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("🚀 Starting JavaFX Application...");

        MainViewController.show(stage);
    }

    @Override
    public void stop() {
        System.out.println("🔄 Shutting down application...");

        System.out.println("✅ Application exited.");
    }

    public static void main(String[] args) {
        launch();
    }
}
