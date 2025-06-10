package syp.htlfragebogenapplication.controllers;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import syp.htlfragebogenapplication.view.TestResultView;
import syp.htlfragebogenapplication.model.Question;
import syp.htlfragebogenapplication.model.Test;
import syp.htlfragebogenapplication.database.AnswerRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestResultViewController {
    private Label testNameLabel;
    private VBox contentContainer;
    private BorderPane scoreContainer;
    private VBox questionReviewContainer;
    private ScrollPane questionScrollPane;
    private TextField searchField;
    private CheckBox showWrongOnlyCheckBox;
    private Button backToMainButton;
    private Button reviewQuestionsButton;
    private Button backToResultsButton;

    private static Stage primaryStage;

    private Test test;
    private List<Question> questions;
    private String[] userAnswers;
    private Map<Integer, String> correctAnswers;
    private int score;
    private int totalQuestions;
    private int timeSeconds;

    // Setters for the TestResultView to inject components
    public void setTestNameLabel(Label testNameLabel) {
        this.testNameLabel = testNameLabel;
    }

    public void setContentContainer(VBox contentContainer) {
        this.contentContainer = contentContainer;
    }

    public void setScoreContainer(BorderPane scoreContainer) {
        this.scoreContainer = scoreContainer;
    }

    public void setQuestionReviewContainer(VBox questionReviewContainer) {
        this.questionReviewContainer = questionReviewContainer;
    }

    public void setBackToMainButton(Button backToMainButton) {
        this.backToMainButton = backToMainButton;
    }

    public void setReviewQuestionsButton(Button reviewQuestionsButton) {
        this.reviewQuestionsButton = reviewQuestionsButton;
    }

    public void setBackToResultsButton(Button backToResultsButton) {
        this.backToResultsButton = backToResultsButton;
    }

    public void setQuestionScrollPane(ScrollPane questionScrollPane) {
        this.questionScrollPane = questionScrollPane;
    }

    public void setSearchField(TextField searchField) {
        this.searchField = searchField;
    }

    public void setShowWrongOnlyCheckBox(CheckBox showWrongOnlyCheckBox) {
        this.showWrongOnlyCheckBox = showWrongOnlyCheckBox;
    }

    public void onSearchTextChanged(String searchText) {
        filterQuestions(searchText, showWrongOnlyCheckBox.isSelected());
    }

    public void onShowWrongOnlyChanged(Boolean showWrongOnly) {
        filterQuestions(searchField.getText(), showWrongOnly);
    }

    private void filterQuestions(String searchText, boolean showWrongOnly) {
        // Use the helper class to filter questions
        FilterQuestions.filter(
                questionReviewContainer,
                questions,
                userAnswers,
                correctAnswers,
                searchText,
                showWrongOnly);
    }

    public void initData(Test test, List<Question> questions, String[] userAnswers, int timeSeconds) {
        this.test = test;
        this.questions = questions;
        this.userAnswers = userAnswers;
        this.timeSeconds = timeSeconds;
        this.totalQuestions = questions.size();
        loadCorrectAnswers();
        calculateScore();
        displayResultsAndQuestions();
    }

    private void displayResultsAndQuestions() {
        // Set test name
        testNameLabel.setText("Resultate " + test.getName());

        // Build the score box
        scoreContainer.getChildren().clear();
        VBox resultsBox = new VBox(15);
        resultsBox.setAlignment(Pos.CENTER);

        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        int percentage = (score * 100) / totalQuestions;
        boolean isPassed = percentage >= 50;

        Label scoreLabel = new Label(score + "/" + totalQuestions + " Punkten");
        scoreLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        scoreLabel.getStyleClass().add(isPassed ? "passed-label" : "failed-label");
        scoreLabel.setTextFill(isPassed ? Color.web("#00af50") : Color.web("#d07474"));

        Label percentageLabel = new Label(
                percentage + "% - " + (isPassed ? "bestanden" : "nicht bestanden")
        );
        percentageLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        percentageLabel.getStyleClass().add(isPassed ? "passed-label" : "failed-label");
        percentageLabel.setTextFill(isPassed ? Color.web("#00af50") : Color.web("#d07474"));

        Label timeLabel = new Label("Dauer: " + formattedTime);

        resultsBox.getChildren().addAll(scoreLabel, percentageLabel, timeLabel);
        scoreContainer.setCenter(resultsBox);

        // Prepare to show question reviews
        questionReviewContainer.getChildren().clear();
        searchField.setVisible(true);
        showWrongOnlyCheckBox.setVisible(true);

        for (Node node : contentContainer.getChildren()) {
            if (node instanceof HBox && ((HBox) node).getChildren().contains(searchField)) {
                node.setVisible(true);
                node.setManaged(true);
                break;
            }
        }

        questionScrollPane.setVisible(true);
        scoreContainer.setVisible(true);
        reviewQuestionsButton.setVisible(false);
        backToResultsButton.setVisible(false);

        // For each question, show a card
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String answerTypeName = question.getAnswerType().getName();

            // Load the two strings
            String correctValue = correctAnswers.get(question.getId());
            String givenValue   = userAnswers[i];

            // Compare safely
            boolean isCorrect = givenValue != null
                    && correctValue != null
                    && givenValue.equals(correctValue);

            // Card container
            VBox questionCard = new VBox(10);
            questionCard.getStyleClass().add("question-card");
            questionCard.setMaxWidth(700);

            // Header: Frage X + Richtig/Falsch
            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label questionNumber = new Label("Frage " + (i + 1) + ":");
            questionNumber.setFont(Font.font("System", FontWeight.BOLD, 16));
            Label resultLabel = new Label(isCorrect ? "Richtig" : "Falsch");
            resultLabel.getStyleClass().add(isCorrect ? "passed-label" : "failed-label");
            header.getChildren().addAll(questionNumber, resultLabel);

            // Image
            String imagePath = getClass()
                    .getResource(question.getImagePath())
                    .toExternalForm();
            ImageView imageView = new ImageView(new Image(imagePath));
            imageView.setFitWidth(650);
            imageView.setPreserveRatio(true);

            // Answers display
            HBox answerBox = new HBox(20);
            answerBox.setAlignment(Pos.CENTER_LEFT);
            answerBox.getStyleClass().add("answer-box");

            String userAnswerText = (givenValue == null || givenValue.isEmpty())
                    ? "Keine Antwort"
                    : givenValue;

            String correctAnswerText = (correctValue == null || correctValue.isEmpty())
                    ? "–"
                    : correctValue;

            Label userAnswerLabel    = new Label("Deine Antwort: " + userAnswerText);
            Label correctAnswerLabel = new Label("Richtige Antwort: " + correctAnswerText);
            userAnswerLabel.getStyleClass().add("user-answer-label");
            correctAnswerLabel.getStyleClass().add("correct-answer-label");

            answerBox.getChildren().addAll(userAnswerLabel, correctAnswerLabel);

            questionCard.getChildren().addAll(header, imageView, answerBox);
            questionReviewContainer.getChildren().add(questionCard);
        }
        // Set the visibility
        questionReviewContainer.setVisible(true);
        questionScrollPane.setVisible(true);
        scoreContainer.setVisible(true);
        reviewQuestionsButton.setVisible(false);
        backToResultsButton.setVisible(false);
    }


    private void loadCorrectAnswers() {
        AnswerRepository answerRepository = new AnswerRepository();
        correctAnswers = answerRepository.getCorrectAnswersMap();
    }

    private int getLetterIndex(String letter) {
        return letter.charAt(0) - 'a';
    }

    private void calculateScore() {
        score = 0;
        for (int i = 0; i < questions.size(); i++) {
            String correctValue = correctAnswers.get(questions.get(i).getId());
            String givenValue   = userAnswers[i];
            if (givenValue != null && givenValue.equals(correctValue)) {
                score++;
            }
        }
    }

    public void onBackToMainButtonClicked() {
        MainViewController.show(primaryStage);
    }

    public static void show(Stage stage, Test test, List<Question> questions, String[] userAnswers, int timeSeconds) {
        try {
            primaryStage = stage;
            TestResultViewController controller = new TestResultViewController();
            TestResultView resultView = new TestResultView(controller);

            controller.initData(test, questions, userAnswers, timeSeconds);

            Scene scene = new Scene(resultView, stage.getScene().getWidth(), stage.getScene().getHeight());
            try {
                scene.getStylesheets().add(MainViewController.class
                        .getResource("/syp/htlfragebogenapplication/Base.css").toExternalForm());
                scene.getStylesheets().add(TestResultViewController.class
                        .getResource("/syp/htlfragebogenapplication/testview/test-result-view.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS file not found, continuing without styling");
            }

            stage.setTitle("Testergebnisse: " + test.getName());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Could not load test results view: " + e.getMessage());
        }
    }

    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
