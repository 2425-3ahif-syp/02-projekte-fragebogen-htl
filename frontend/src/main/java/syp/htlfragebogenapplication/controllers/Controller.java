package syp.htlfragebogenapplication.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
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
        int rowIndex = 0;
        int columnIndex = 0;

        List<Test> testList = new TestRepository().getAllTests();

        for (Test test : testList) {
            VBox vbox = new VBox();
            vbox.setStyle("-fx-border-color: #404040; -fx-border-width: 2; -fx-border-radius: 15; -fx-padding: 15;");
            vbox.setMaxHeight(200);
            vbox.setMaxWidth(400);
            VBox.setMargin(vbox, new Insets(10));


            Label label = new Label(test.getName());
            label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
            label.setWrapText(true);
            label.setMaxWidth(Double.MAX_VALUE);
            VBox.setMargin(label, new Insets(0, 0, 10, 0));


            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS); // This is the key line


            Button button = new Button("Test durchführen");
            button.setAlignment(Pos.BASELINE_CENTER);
            button.setId(String.valueOf(test.getId()));
            button.setMaxWidth(Double.MAX_VALUE);
            button.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #d0d0d0; -fx-border-radius: 5; -fx-cursor: hand; -fx-font-size: 14;");
            button.setPadding(new Insets(10, 15, 10, 15));

            vbox.getChildren().addAll(label, spacer, button);


            GridPane.setConstraints(vbox, columnIndex, rowIndex);

            testGrid.getChildren().add(vbox);

            GridPane.setRowIndex(vbox, rowIndex);
            GridPane.setColumnIndex(vbox, columnIndex);

            if (columnIndex == 4) {
                columnIndex = 0;
                rowIndex++;
            } else {
                columnIndex++;
            }

        }

        testGrid.requestLayout();
    }

    public Controller() {
    }

    @FXML
    private Button myButton;

}