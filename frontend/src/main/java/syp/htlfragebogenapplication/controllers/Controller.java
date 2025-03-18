package syp.htlfragebogenapplication.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import syp.htlfragebogenapplication.database.Database;
import syp.htlfragebogenapplication.database.TestRepository;
import syp.htlfragebogenapplication.model.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



public class Controller {
    private Connection connection = Database.getInstance().getConnection();
    public GridPane testGrid;

    public void initialize() {
        System.out.println("🚀 Initializing Controller...");

        List<Test> testList = new TestRepository().getAllTests();

        for (Test test : testList) {

        }


    }
    public Controller() {
    }
    @FXML
    private Button myButton;


}