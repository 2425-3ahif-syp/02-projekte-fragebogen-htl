package syp.htlfragebogenapplication.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import syp.htlfragebogenapplication.view.TestView;
import syp.htlfragebogenapplication.database.QuestionRepository;
import syp.htlfragebogenapplication.model.Question;
import syp.htlfragebogenapplication.model.Test;

import java.util.Arrays;
import java.util.List;

public class TestViewController {

    private Label testNameLabel;
    private VBox questionsContainer;
    private Button nextButton;
    private Button backButton;
    private Button cancelButton;
    private Label questionCount;
    private Label timeCount;

    private static Stage primaryStage;

    private Test test;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private Timeline timeline;
    private int timeSeconds = 0;

    private String[] answerSelections;

    // Setters for the TestView to inject components
    public void setTestNameLabel(Label testNameLabel) {
        this.testNameLabel = testNameLabel;
    }

    public void setQuestionsContainer(VBox questionsContainer) {
        this.questionsContainer = questionsContainer;
    }

    public void setNextButton(Button nextButton) {
        this.nextButton = nextButton;
    }

    public void setBackButton(Button backButton) {
        this.backButton = backButton;
    }

    public void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }

    public void setQuestionCount(Label questionCount) {
        this.questionCount = questionCount;
    }

    public void setTimeCount(Label timeCount) {
        this.timeCount = timeCount;
    }

    public void initData(Test test) {
        this.test = test;
        testNameLabel.setText(test.getName());
        questionCount.setText("Frage: " + (currentQuestionIndex + 1) + "/" + test.getQuestionCount());
        answerSelections = new String[test.getQuestionCount()];
        Arrays.fill(answerSelections, "");

        loadQuestions();
        displayCurrentQuestion();
        startTimer();
    }

    private void loadQuestions() {
        QuestionRepository questionRepository = new QuestionRepository();
        questions = questionRepository.getAllQuestionsFromTest(test.getId());
    }

    private void displayCurrentQuestion() {
        questionsContainer.getChildren().clear();
        if (questions != null && !questions.isEmpty()) {
            Question currentQuestion = questions.get(currentQuestionIndex);

            // Update button text
            if (currentQuestionIndex == questions.size() - 1) {
                nextButton.setText("Beenden");
            } else {
                nextButton.setText("Weiter");
            }

            // Show image
            String imagePath = getClass().getResource(currentQuestion.getImagePath()).toExternalForm();
            ImageView imageView = new ImageView(new Image(imagePath));
            imageView.setFitWidth(800);
            imageView.setPreserveRatio(true);
            questionsContainer.getChildren().add(imageView);

            // Show progress
            questionCount.setText("Frage: " + (currentQuestionIndex + 1) + "/" + questions.size());

            // Prepare Horizontal Box for answer Buttons
            HBox answersBox = new HBox(15);
            answersBox.setAlignment(Pos.CENTER_LEFT);
            answersBox.setPadding(new Insets(10, 0, 0, 0));

            // Prepare answer options
            int possibleAnswerCount = currentQuestion.getPossibleAnswerCount();
            String answerTypeName       = currentQuestion.getAnswerType().getName();
            ToggleGroup toggleGroup     = new ToggleGroup();

            // LETTER‐type: a, b, c, …
            if ("Letter".equals(answerTypeName)) {
                for (int i = 0; i < possibleAnswerCount; i++) {
                    String optionText = String.valueOf((char) ('a' + i));
                    RadioButton rb = new RadioButton(optionText);
                    rb.setToggleGroup(toggleGroup);
                    rb.setUserData(optionText);
                    rb.setOnAction(e -> answerSelections[currentQuestionIndex] = rb.getUserData().toString());
                    questionsContainer.getChildren().add(rb);

                    if (optionText.equals(answerSelections[currentQuestionIndex])) {
                        rb.setSelected(true);
                    }
                    answersBox.getChildren().add(rb);
                }
              // NUMBER‐type: 0, 1, 2, …
            } else if ("Number".equals(answerTypeName)) {
                for (int i = 0; i < possibleAnswerCount; i++) {
                    String optionText = String.valueOf(i);
                    RadioButton rb = new RadioButton(optionText);
                    rb.setToggleGroup(toggleGroup);
                    rb.setUserData(optionText);
                    rb.setOnAction(e -> answerSelections[currentQuestionIndex] =  rb.getUserData().toString());
                    questionsContainer.getChildren().add(rb);

                    if (optionText.equals(answerSelections[currentQuestionIndex])) {
                        rb.setSelected(true);
                    }
                    answersBox.getChildren().add(rb);
                }

            } else {
                // TODO: handle Text, Fraction, Number Field, Set Comma, etc.
            }
            questionsContainer.getChildren().add(answersBox);
        }
    }


    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeSeconds++;
            int minutes = timeSeconds / 60;
            int seconds = timeSeconds % 60;
            timeCount.setText(String.format("Zeit: %02d:%02d", minutes, seconds));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void onNextButtonClicked() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            displayCurrentQuestion();
        } else {
            // We are on the last question, show the results
            finishTest();
        }
    }

    public void onBackButtonClicked() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayCurrentQuestion();
        }
    }

    private void finishTest() {
        if (timeline != null) {
            timeline.stop();
        }

        // Show test results view
        TestResultViewController.show(primaryStage, test, questions, answerSelections, timeSeconds);
    }

    public void onCancelButtonClicked() {
        MainViewController.show(primaryStage);
    }

    public static void show(Stage stage, Test test) {
        try {
            primaryStage = stage;
            TestViewController controller = new TestViewController();
            TestView testView = new TestView(controller);

            controller.initData(test);

            Scene scene = new Scene(testView, stage.getScene().getWidth(), stage.getScene().getHeight());
            try {
                scene.getStylesheets().add(MainViewController.class
                        .getResource("/syp/htlfragebogenapplication/Base.css").toExternalForm());
                scene.getStylesheets().add(TestViewController.class
                        .getResource("/syp/htlfragebogenapplication/testview/test-view.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS file not found, continuing without styling");
            }

            stage.setTitle("Test: " + test.getName());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Could not load test view: " + e.getMessage());
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
