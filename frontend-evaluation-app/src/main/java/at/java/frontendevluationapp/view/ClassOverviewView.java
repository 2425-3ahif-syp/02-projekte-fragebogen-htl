package at.java.frontendevluationapp.view;

import at.java.frontendevluationapp.controller.ClassOverviewController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class ClassOverviewView extends BorderPane {

    private final ComboBox<Integer> testIdComboBox;
    private final Label averagePointsLabel;
    private final Label averagePercentageLabel;
    private final TableView<ClassOverviewController.StudentEntry> studentTable;
    private final VBox contentBox;
    private final Label highestScoreLabel;
    private final Label lowestScoreLabel;


    private final ClassOverviewController controller;

    public ClassOverviewView(ClassOverviewController controller) {
        this.controller = controller;

        testIdComboBox = new ComboBox<>();
        testIdComboBox.setPromptText("Test auswählen...");
        testIdComboBox.setPrefWidth(250);

        GridPane statGrid = new GridPane();
        statGrid.setHgap(40);
        statGrid.setVgap(5);
        statGrid.setAlignment(Pos.CENTER_LEFT);
        statGrid.setPadding(new Insets(10));

        averagePointsLabel = new Label(padTo60("Durchschnittliche Punkte: -"));
        averagePercentageLabel = new Label("Durchschnittliche Prozentzahl: -");
        highestScoreLabel = new Label(padTo60("Höchster Wert: -"));
        lowestScoreLabel = new Label("Niedrigster Wert: -");

        statGrid.add(averagePointsLabel, 0, 0);
        statGrid.add(averagePercentageLabel, 1, 0);
        statGrid.add(highestScoreLabel, 0, 1);
        statGrid.add(lowestScoreLabel, 1, 1);
        VBox statBox = new VBox(5,
                statGrid
        );
        statBox.setAlignment(Pos.CENTER_LEFT);

        HBox topControls = new HBox(30, testIdComboBox, statBox);
        topControls.setPadding(new Insets(20));
        topControls.setAlignment(Pos.CENTER_LEFT);

        studentTable = new TableView<>();
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ClassOverviewController.StudentEntry, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> data.getValue().studentNameProperty());

        TableColumn<ClassOverviewController.StudentEntry, String> scoreCol = new TableColumn<>("Punkte");
        scoreCol.setCellValueFactory(data -> data.getValue().scoreDisplayProperty());

        TableColumn<ClassOverviewController.StudentEntry, String> percentCol = new TableColumn<>("Prozent");
        percentCol.setCellValueFactory(data -> data.getValue().percentageDisplayProperty());

        studentTable.getColumns().addAll(nameCol, scoreCol, percentCol);

        contentBox = new VBox(10, topControls, studentTable);
        contentBox.setPadding(new Insets(20));

        this.setCenter(contentBox);

        connectController();
    }

    private void connectController() {
        testIdComboBox.setOnAction(e -> controller.onTestSelected(testIdComboBox.getValue()));
        controller.setTestIdComboBox(testIdComboBox);
        controller.setAveragePointsLabel(averagePointsLabel);
        controller.setAveragePercentageLabel(averagePercentageLabel);
        controller.setStudentTable(studentTable);
        controller.setHighestScoreLabel(highestScoreLabel);
        controller.setLowestScoreLabel(lowestScoreLabel);

    }

    public static String padTo60(String text) {
        return String.format("%-60s", text);
    }

}
