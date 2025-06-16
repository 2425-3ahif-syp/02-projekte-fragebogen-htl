package at.java.frontendevaluationapp;

import javafx.application.Application;
import javafx.stage.Stage;
import syp.htlfragebogenapplication.controllers.MainViewController;
import syp.htlfragebogenapplication.database.Database;
import syp.htlfragebogenapplication.database.H2Server;

import java.io.IOException;

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
