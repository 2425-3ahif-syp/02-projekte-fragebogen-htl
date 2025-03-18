package syp.htlfragebogenapplication.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class Controller {

    public GridPane testGrid;

    public void initialize() {
        System.out.println("🚀 Initializing Controller...");
        System.out.println(testGrid);
    }
    public Controller() {
    }
    @FXML
    private Button myButton;


}