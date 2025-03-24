package syp.htlfragebogenapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import syp.htlfragebogenapplication.controllers.MainViewController;
import syp.htlfragebogenapplication.database.Database;
import syp.htlfragebogenapplication.database.H2Server;
import syp.htlfragebogenapplication.database.TestRepository;


import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("🚀 Starting JavaFX Application...");
        H2Server.start();
        Database.getInstance();

        TestRepository testRepository = new TestRepository();
        System.out.println(testRepository.getAllTests());
        System.out.println(testRepository.getTestById(6));

        // Use the static show method
        MainViewController.show(stage);
    }

    @Override
    public void stop() {
        System.out.println("🔄 Shutting down application...");

        Database.getInstance().closeConnection();

        H2Server.stop();

        System.out.println("✅ Application exited.");
    }

    public static void main(String[] args) {
        launch();
    }
}