package syp.htlfragebogenapplication.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import syp.htlfragebogenapplication.database.QuestionRepository;
import syp.htlfragebogenapplication.model.Question;
import syp.htlfragebogenapplication.model.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestViewController {

    @FXML
    private Label testNameLabel;

    @FXML
    private VBox questionsContainer;

    @FXML
    private Button nextButton;

    @FXML
    private Button backButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label questionCount;

    @FXML
    private Label timeCount;

    private static Stage primaryStage;

    private Test test;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private Timeline timeline;
    private int timeSeconds = 0;

    private int[] answerSelections;


    public void initData(Test test) {
        this.test = test;
        testNameLabel.setText(test.getName());
        questionCount.setText("Frage: " + (currentQuestionIndex + 1) + "/" + test.getQuestionCount());
        answerSelections = new int[test.getQuestionCount()];
        Arrays.fill(answerSelections, -1);


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
            if(currentQuestionIndex == questions.size() -1)
            {
                nextButton.setText("Beenden");
            }
            else
            {
                nextButton.setText("Weiter");
            }
            String imagePath = getClass().getResource(currentQuestion.getImagePath()).toExternalForm();
            Image image = new Image(imagePath);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(800);
            imageView.setPreserveRatio(true);
            questionsContainer.getChildren().add(imageView);

            questionCount.setText("Frage: " + (currentQuestionIndex + 1) + "/" + questions.size());

            int possibleAnswerCount = currentQuestion.getPossibleAnswerCount();
            String answerTypeName = currentQuestion.getAnswerType().getName();

            ToggleGroup toggleGroup = new ToggleGroup();
            for (int i = 0; i < possibleAnswerCount; i++) {
                String optionText = "Option " + (i + 1);
                if ("Letter".equals(answerTypeName)) {
                    optionText = String.valueOf((char) ('a' + i));
                }
                RadioButton radioButton = new RadioButton(optionText);
                radioButton.setToggleGroup(toggleGroup);
                final int selectedIndex = i;
                radioButton.setOnAction(e -> answerSelections[currentQuestionIndex] = selectedIndex);
                questionsContainer.getChildren().add(radioButton);

                if (answerSelections[currentQuestionIndex] != -1 && answerSelections[currentQuestionIndex] == i) {
                    radioButton.setSelected(true);
                }
            }
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

    @FXML
    private void onNextButtonClicked() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            displayCurrentQuestion();
        }
    }

    @FXML
    private void onBackButtonClicked() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayCurrentQuestion();
        }
    }

    @FXML
    private void onCancelButtonClicked() {
        MainViewController.show(primaryStage);
    }

    public static void show(Stage stage, Test test) {
        try {
            primaryStage = stage;
            FXMLLoader loader = new FXMLLoader(TestViewController.class.getResource("/syp/htlfragebogenapplication/testview/test-view.fxml"));
            Parent root = loader.load();

            TestViewController controller = loader.getController();
            controller.initData(test);

            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            try {
                scene.getStylesheets().add(MainViewController.class.getResource("/syp/htlfragebogenapplication/Base.css").toExternalForm());
                scene.getStylesheets().add(TestViewController.class.getResource("/syp/htlfragebogenapplication/testview/test-view.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS file not found, continuing without styling");
            }

            stage.setTitle("Test: " + test.getName());
            stage.setScene(scene);
        } catch (IOException e) {
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
