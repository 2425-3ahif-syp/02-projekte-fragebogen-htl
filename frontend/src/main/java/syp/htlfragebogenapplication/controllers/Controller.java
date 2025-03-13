package syp.htlfragebogenapplication.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class Controller {

    public Controller() {
    }
    @FXML
    private Button myButton;

    @FXML
    public void handleButtonClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Button Clicked");
        alert.setHeaderText(null);
        alert.setContentText("You clicked the button!");
        alert.showAndWait();
    }
}