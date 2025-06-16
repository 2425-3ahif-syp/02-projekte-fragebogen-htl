package at.java.frontendevluationapp.controller;


import at.java.frontendevluationapp.model.Score;
import at.java.frontendevluationapp.model.TestResult;
import at.java.frontendevluationapp.view.ClassOverviewView;
import at.java.frontendevluationapp.view.NavbarView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

public class ClassOverviewController {

    private ComboBox<Integer> testIdComboBox;
    private Label averagePointsLabel;
    private Label averagePercentageLabel;
    private Label highestScoreLabel;
    private Label lowestScoreLabel;
    private TableView<StudentEntry> studentTable;

    private static Stage primaryStage;
    private static List<TestResult> allResults = new ArrayList<>();

    public static void setTestResults(List<TestResult> results) {
        allResults = results;
    }

    public static List<TestResult> getUploadedResults() {
        return allResults;
    }

    public static void show(Stage stage) {
        primaryStage = stage;
        ClassOverviewController controller = new ClassOverviewController();
        ClassOverviewView view = new ClassOverviewView(controller);

        NavbarViewController navbarController = new NavbarViewController(stage);
        NavbarView navbar = new NavbarView(navbarController);

        VBox rootLayout = new VBox();
        rootLayout.getChildren().addAll(navbar, view);
        VBox.setVgrow(view, Priority.ALWAYS);

        Scene scene = new Scene(rootLayout, 1500, 800);
        scene.getStylesheets().add(ClassOverviewController.class.getResource("/style/Base.css").toExternalForm());

        stage.setTitle("Klassenauswertung");
        stage.setScene(scene);
        stage.show();
    }

    public void onTestSelected(Integer testId) {
        List<TestResult> resultsForTest = allResults.stream()
                .filter(r -> r.getTestId() == testId)
                .collect(Collectors.toList());

        if (resultsForTest.isEmpty()) {
            studentTable.setItems(FXCollections.observableArrayList());
            averagePointsLabel.setText(ClassOverviewView.padTo60("Durchschnittliche Punkte: -"));
            averagePercentageLabel.setText("Durchschnittliche Prozentzahl: -");
            highestScoreLabel.setText(ClassOverviewView.padTo60("Höchster Wert: -"));
            lowestScoreLabel.setText("Niedrigster Wert: -");
            return;
        }

        Optional<TestResult> best = resultsForTest.stream()
                .max(Comparator.comparingDouble(r -> r.getScore().getPercentage()));

        Optional<TestResult> worst = resultsForTest.stream()
                .min(Comparator.comparingDouble(r -> r.getScore().getPercentage()));

        if (best.isPresent()) {
            Score s = best.get().getScore();
            highestScoreLabel.setText(ClassOverviewView.padTo60(String.format("Höchster Wert: %d / %d (%.1f%%)",
                    s.getObtained(), s.getMax(), s.getPercentage())));
        }

        if (worst.isPresent()) {
            Score s = worst.get().getScore();
            lowestScoreLabel.setText(String.format("Niedrigster Wert: %d / %d (%.1f%%)",
                    s.getObtained(), s.getMax(), s.getPercentage()));
        }

        int totalMax = resultsForTest.getFirst().getScore().getMax();
        double avgPoints = resultsForTest.stream().mapToInt(r -> r.getScore().getObtained()).average().orElse(0);
        double avgPercent = resultsForTest.stream().mapToDouble(r -> r.getScore().getPercentage()).average().orElse(0);

        averagePointsLabel.setText(ClassOverviewView.padTo60(String.format("Durchschnittliche Punkte: %.2f / %d", avgPoints, totalMax)));
        averagePercentageLabel.setText(String.format("Durchschnittliche Prozentzahl: %.2f%%", avgPercent));

        List<StudentEntry> entries = resultsForTest.stream()
                .map(r -> new StudentEntry(
                        r.getStudentName(),
                        r.getScore().getObtained(),
                        r.getScore().getMax(),
                        r.getScore().getPercentage()))
                .sorted(Comparator.comparingDouble(StudentEntry::getPercentage).reversed())
                .collect(Collectors.toList());

        studentTable.setItems(FXCollections.observableArrayList(entries));
    }

    public static class StudentEntry {
        private final String name;
        private final int score;
        private final int max;
        private final double percentage;

        public StudentEntry(String name, int score, int max, double percentage) {
            this.name = name;
            this.score = score;
            this.max = max;
            this.percentage = percentage;
        }

        public String getStudentName() {
            return name;
        }

        public double getPercentage() {
            return percentage;
        }

        public SimpleStringProperty studentNameProperty() {
            return new SimpleStringProperty(name);
        }

        public SimpleStringProperty scoreDisplayProperty() {
            return new SimpleStringProperty(score + " / " + max);
        }

        public SimpleStringProperty percentageDisplayProperty() {
            return new SimpleStringProperty(String.format("%.1f%%", percentage));
        }
    }

    public void setTestIdComboBox(ComboBox<Integer> comboBox) {
        this.testIdComboBox = comboBox;

        // populate dropdown with unique test IDs
        Set<Integer> uniqueTestIds = allResults.stream()
                .map(TestResult::getTestId)
                .collect(Collectors.toCollection(TreeSet::new));

        comboBox.setItems(FXCollections.observableArrayList(uniqueTestIds));
    }

    public void setAveragePointsLabel(Label label) {
        this.averagePointsLabel = label;
    }

    public void setAveragePercentageLabel(Label label) {
        this.averagePercentageLabel = label;
    }

    public void setStudentTable(TableView<StudentEntry> table) {
        this.studentTable = table;
    }

    public void setHighestScoreLabel(Label label) {
        this.highestScoreLabel = label;
    }

    public void setLowestScoreLabel(Label label) {
        this.lowestScoreLabel = label;
    }

}
