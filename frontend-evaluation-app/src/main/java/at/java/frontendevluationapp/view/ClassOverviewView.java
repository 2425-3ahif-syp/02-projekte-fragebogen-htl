package at.java.frontendevluationapp.view;

import at.java.frontendevluationapp.controller.ClassOverviewController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class ClassOverviewView extends BorderPane {

    private final ComboBox<Integer> testIdComboBox;
    private final Label averagePointsLabel;
    private final Label averagePercentageLabel;
    private final TableView<ClassOverviewController.StudentEntry> studentTable;
    private final VBox contentBox;

    private final ClassOverviewController controller;

    public ClassOverviewView(ClassOverviewController controller) {
        this.controller = controller;

        testIdComboBox = new ComboBox<>();
        testIdComboBox.setPromptText("Test auswählen...");
        testIdComboBox.setPrefWidth(250);

        averagePointsLabel = new Label("Durchschnittliche Punkte: -");
        averagePercentageLabel = new Label("Durchschnittliche Prozentzahl: -");

        HBox topControls = new HBox(20, testIdComboBox, averagePointsLabel, averagePercentageLabel);
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
    }
}
