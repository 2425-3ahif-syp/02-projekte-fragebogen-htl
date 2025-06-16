package syp.htlfragebogenapplication.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import syp.htlfragebogenapplication.utils.FractionUtils;
import syp.htlfragebogenapplication.view.TestResultView;
import syp.htlfragebogenapplication.model.Question;
import syp.htlfragebogenapplication.model.Test;
import syp.htlfragebogenapplication.database.AnswerRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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
    private Button downloadButton;

    private static Stage primaryStage;

    private Test test;
    private List<Question> questions;
    private String[] userAnswers;
    private Map<Integer, String> correctAnswers;
    private Map<Integer, int[]> wordScores = new HashMap<>(); // [correctWords, wrongWords, totalWords]
    private int score;
    private int percentageSum;
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

    public void setDownloadButton(Button downloadButton) {
        this.downloadButton = downloadButton;
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
        testNameLabel.setText("📊 Testergebnisse: " + test.getName());

        // Build the enhanced score box (more compact)
        scoreContainer.getChildren().clear();
        VBox resultsBox = new VBox(10);
        resultsBox.setAlignment(Pos.CENTER);

        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        String answerTypeName = questions.getFirst().getAnswerType().getName();

        int percentage = Math.round((float) percentageSum / totalQuestions);
        boolean isPassed = "Text".equals(answerTypeName) ? percentage >= 95 : percentage >= 70;

        Label statusLabel = new Label(isPassed ? "BESTANDEN" : "NICHT BESTANDEN");
        statusLabel.getStyleClass().add("status-label");
        statusLabel.getStyleClass().add(isPassed ? "status-passed" : "status-failed");

        // Score display with better formatting (more compact)
        HBox scoreRow = new HBox(8);
        scoreRow.setAlignment(Pos.CENTER);

        Label scoreLabel = new Label(String.valueOf(score));
        scoreLabel.setTextFill(isPassed ? Color.web("#00af50") : Color.web("#d07474"));
        scoreLabel.getStyleClass().add("score-number");

        Label totalLabel = new Label(String.valueOf(totalQuestions));

        totalLabel.getStyleClass().add("score-total");

        Label pointsLabel = new Label("Punkte");
        pointsLabel.getStyleClass().add("score-unit");

        Label separatorLabel = new Label("/");
        separatorLabel.getStyleClass().add("score-separator");

        if ("Text".equals(answerTypeName)) {
            int totalCorrectWords = wordScores.values().stream().mapToInt(arr -> arr[0]).sum();
            scoreLabel = new Label(String.valueOf(totalCorrectWords));
            int totalWords = wordScores.values().stream().mapToInt(arr -> arr[2]).sum();
            totalLabel = new Label(String.valueOf(totalWords));
            pointsLabel = new Label("Wörter");
        }

        scoreRow.getChildren().addAll(scoreLabel, separatorLabel, totalLabel);

        // Percentage with progress bar style (more compact)
        VBox percentageBox = new VBox(6);
        percentageBox.setAlignment(Pos.CENTER);

        Label percentageLabel = new Label(percentage + "%");
        percentageLabel.setTextFill(isPassed ? Color.web("#00af50") : Color.web("#d07474"));
        percentageLabel.getStyleClass().add("percentage-label");


        // Create a progress bar visual (smaller)
        HBox progressBar = new HBox();
        progressBar.getStyleClass().add("progress-bar-container");
        progressBar.setPrefWidth(250);
        progressBar.setPrefHeight(10);

        Region progressFill = new Region();
        progressFill.getStyleClass().add("progress-bar-fill");
        progressFill.getStyleClass().add(isPassed ? "progress-passed" : "progress-failed");
        progressFill.setPrefWidth(250 * percentage / 100);
        progressFill.setPrefHeight(10);

        progressBar.getChildren().add(progressFill);
        percentageBox.getChildren().addAll(percentageLabel, progressBar);

        // Time display with icon (smaller)
        HBox timeBox = new HBox(6);
        timeBox.setAlignment(Pos.CENTER);

        Label timeIcon = new Label("⏱️");

        Label timeLabel = new Label("Dauer: " + formattedTime);
        timeBox.getStyleClass().add("time-box");

        timeBox.getChildren().addAll(timeIcon, timeLabel);

        // Add spacing elements (smaller)
        Region spacer1 = new Region();
        spacer1.setPrefHeight(5);

        Region spacer2 = new Region();
        spacer2.setPrefHeight(8);

        Region spacer3 = new Region();
        spacer3.setPrefHeight(5);

        resultsBox.getChildren().addAll(
                statusLabel,
                spacer1,
                scoreRow,
                pointsLabel,
                spacer2,
                percentageBox,
                spacer3,
                timeBox);

        scoreContainer.setCenter(resultsBox);

        // Prepare to show question reviews
        questionReviewContainer.getChildren().clear();
        searchField.setVisible(true);
        showWrongOnlyCheckBox.setVisible(true);

        questionScrollPane.setVisible(true);
        scoreContainer.setVisible(true);
        reviewQuestionsButton.setVisible(false);
        backToResultsButton.setVisible(false);

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String type = q.getAnswerType().getName();
            String correctValue = correctAnswers.get(q.getId());
            String givenValue = userAnswers[i];

            VBox questionCard = new VBox(10);
            questionCard.getStyleClass().add("question-card");
            questionCard.setMaxWidth(700);
            questionCard.setMinWidth(700);

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label num = new Label("Frage " + (i + 1) + ":");
            num.setFont(Font.font("System", FontWeight.BOLD, 16));
            header.getChildren().add(num);

            HBox answerBox = new HBox(20);
            answerBox.setAlignment(Pos.CENTER_LEFT);
            answerBox.getStyleClass().add("answer-box");

            if ("Text".equals(type)) {
                String[] originalTokens = loadTextResource(q.getImagePath()).split(" ");
                String[] correctTokens = correctValue.split(" ");
                String[] givenTokens = givenValue.split(" ");
                FlowPane textFlow = new FlowPane();
                textFlow.getStyleClass().add("text-flow");
                textFlow.setMinWidth(650);
                textFlow.setMaxWidth(650);
                textFlow.setHgap(5);
                textFlow.setVgap(5);
                textFlow.setPadding(new Insets(10, 0, 0, 0));
                for (int j = 0; j < originalTokens.length; j++) {
                    String orig = originalTokens[j];
                    String corr = j < correctTokens.length ? correctTokens[j] : "";
                    String given = j < givenTokens.length ? givenTokens[j] : "";
                    Label wordLabel = new Label(given);
                    if (given.equals(orig) && given.equals(corr)) {
                        wordLabel.getStyleClass().add("text-black");
                    } else if (given.equals(corr)) {
                        wordLabel.getStyleClass().add("text-green");
                    } else {
                        wordLabel.getStyleClass().add("text-red");
                    }
                    textFlow.getChildren().add(wordLabel);
                }
                int[] scores = wordScores.get(q.getNumInTest());

                boolean isCorrect = givenValue != null
                        && correctValue != null
                        && givenValue.equals(correctValue);

                Label resultLabel = new Label(isCorrect ? "Richtig" : "Falsch");
                resultLabel.getStyleClass().add(isCorrect ? "passed-label" : "failed-label");
                header.getChildren().add(resultLabel);

                Label resultText = new Label(
                        String.format("Wörter: %d/%d korrekt, %d/%d falsch (%.1f%%)",
                                scores[0], scores[2], scores[1], scores[2], scores[0] * 100.0 / scores[2]));

                resultText.getStyleClass().add("user-answer-label");
                answerBox.getChildren().add(resultText);

                questionCard.getChildren().addAll(header, textFlow, answerBox);
            } else if ("Fraction".equals(type)) {
                boolean isExactlyEqual = givenValue != null
                        && correctValue != null
                        && givenValue.equals(correctValue);

                boolean isMathEqual = false;
                if (!isExactlyEqual && givenValue != null && correctValue != null) {
                    try {
                        isMathEqual = FractionUtils.areEquivalent(givenValue, correctValue);
                    } catch (Exception e) {
                        isMathEqual = false;
                    }
                }

                boolean isCorrect = isExactlyEqual || isMathEqual;

                Label resultLabel = new Label(isCorrect ? "Richtig" : "Falsch");
                resultLabel.getStyleClass().add(isCorrect ? "passed-label" : "failed-label");
                header.getChildren().add(resultLabel);

                if (isMathEqual && !isExactlyEqual) {
                    Label noteLabel = new Label("(Mathematisch äquivalent)");
                    noteLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
                    noteLabel.getStyleClass().add("note-label");
                    header.getChildren().add(noteLabel);
                }

                String imagePath = getClass()
                        .getResource(q.getImagePath())
                        .toExternalForm();
                ImageView imageView = new ImageView(new Image(imagePath));
                imageView.setFitWidth(650);
                imageView.setPreserveRatio(true);

                String userAnswerText = (givenValue == null || givenValue.isEmpty())
                        ? "Keine Antwort"
                        : givenValue;

                String correctAnswerText = (correctValue == null || correctValue.isEmpty())
                        ? "–"
                        : correctValue;

                if (isMathEqual && !isExactlyEqual) {
                    try {
                        int[] userFraction = FractionUtils.parseFraction(givenValue);
                        String simplifiedUserFraction = FractionUtils.formatSimplified(
                                userFraction[0], userFraction[1]);

                        if (!simplifiedUserFraction.equals(givenValue)) {
                            userAnswerText += " (vereinfacht: " + simplifiedUserFraction + ")";
                        }
                    } catch (Exception e) {
                    }
                }

                Label userAnswerLabel = new Label("Deine Antwort: " + userAnswerText);
                Label correctAnswerLabel = new Label("Richtige Antwort: " + correctAnswerText);
                userAnswerLabel.getStyleClass().add("user-answer-label");
                correctAnswerLabel.getStyleClass().add("correct-answer-label");

                answerBox.getChildren().addAll(userAnswerLabel, correctAnswerLabel);

                questionCard.getChildren().addAll(header, imageView, answerBox);
            } else {
                boolean isCorrect = givenValue != null
                        && correctValue != null
                        && givenValue.equals(correctValue);

                Label resultLabel = new Label(isCorrect ? "Richtig" : "Falsch");
                resultLabel.getStyleClass().add(isCorrect ? "passed-label" : "failed-label");
                header.getChildren().add(resultLabel);

                // Image
                String imagePath = getClass()
                        .getResource(q.getImagePath())
                        .toExternalForm();
                ImageView imageView = new ImageView(new Image(imagePath));
                imageView.setFitWidth(650);
                imageView.setPreserveRatio(true);

                String userAnswerText = (givenValue == null || givenValue.isEmpty())
                        ? "Keine Antwort"
                        : givenValue;

                String correctAnswerText = (correctValue == null || correctValue.isEmpty())
                        ? "–"
                        : correctValue;

                Label userAnswerLabel = new Label("Deine Antwort: " + userAnswerText);
                Label correctAnswerLabel = new Label("Richtige Antwort: " + correctAnswerText);
                userAnswerLabel.getStyleClass().add("user-answer-label");
                correctAnswerLabel.getStyleClass().add("correct-answer-label");

                answerBox.getChildren().addAll(userAnswerLabel, correctAnswerLabel);

                questionCard.getChildren().addAll(header, imageView, answerBox);
            }
            questionReviewContainer.getChildren().add(questionCard);
        }
        questionScrollPane.setFitToWidth(true);
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
        // For text questions, load file content as the correct answer string
        for (Question q : questions) {
            if ("Text".equals(q.getAnswerType().getName())) {
                String path = correctAnswers.get(q.getId());
                String text = loadTextResource(path);
                correctAnswers.put(q.getId(), text);
            }
        }
    }

    private void calculateScore() {
        score = 0;
        percentageSum = 0;
        Arrays.stream(questions.toArray(new Question[0]))
                .forEach(q -> {
                    String correctValue = correctAnswers.get(q.getId());
                    String givenValue = userAnswers[q.getNumInTest() - 1];
                    if ("Text".equals(q.getAnswerType().getName())) {
                        String[] correctTokens = correctValue.split(" ");
                        String[] givenTokens = (givenValue != null ? givenValue : "").split(" ");
                        int total = correctTokens.length;
                        int correctCount = 0;
                        for (int i = 0; i < total; i++) {
                            if (i < givenTokens.length && givenTokens[i].equals(correctTokens[i])) {
                                correctCount++;
                            }
                        }
                        int wrongCount = total - correctCount;
                        wordScores.put(q.getNumInTest(), new int[] { correctCount, wrongCount, total });
                        double pct = (correctCount * 100.0) / total;
                        if (pct >= 95.0) {
                            score++;
                        }
                        percentageSum += pct;
                    } else if ("Fraction".equals(q.getAnswerType().getName())) {
                        boolean isCorrect = false;

                        if (givenValue != null && correctValue != null) {
                            if (givenValue.equals(correctValue)) {
                                isCorrect = true;
                            } else {
                                try {
                                    isCorrect = FractionUtils.areEquivalent(givenValue, correctValue);
                                } catch (Exception e) {
                                    isCorrect = false;
                                }
                            }
                        }

                        if (isCorrect) {
                            score++;
                            percentageSum += 100.0;
                        }
                    } else {
                        if (givenValue != null && givenValue.equals(correctValue)) {
                            score++;
                            percentageSum += 100.0;
                        }
                    }
                });
    }

    private String loadTextResource(String resourcePath) {
        try (InputStream in = getClass().getResourceAsStream(resourcePath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(" "));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void onBackToMainButtonClicked() {
        MainViewController.show(primaryStage);
    }

    public void onDownloadButtonClicked() throws JsonProcessingException {
        // Prompt for student name
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Name eingeben");
        nameDialog.setHeaderText("Bitte geben Sie Ihren Namen ein");
        nameDialog.setContentText("Name:");

        Optional<String> nameResult = nameDialog.showAndWait();
        if (nameResult.isEmpty() || nameResult.get().trim().isEmpty()) {
            showErrorAlert("Sie müssen einen Namen eingeben, um den Download zu starten.");
            return;
        }

        String studentName = nameResult.get().trim();

        // Create result structure
        Map<String, Object> result = new HashMap<>();
        result.put("studentName", studentName); // Add student name here
        result.put("testId", test.getId());
        result.put("testName", test.getName());

        Map<String, Object> scoreMap = new HashMap<>();
        scoreMap.put("obtained", score);
        scoreMap.put("max", totalQuestions);
        scoreMap.put("percentage", percentageSum / (double) totalQuestions);
        result.put("score", scoreMap);

        List<Map<String, String>> questionList = questions.stream().map(q -> {
            Map<String, String> qMap = new HashMap<>();
            qMap.put("questionId", String.valueOf(q.getNumInTest()));
            qMap.put("givenAnswer", userAnswers[q.getNumInTest() - 1]);
            qMap.put("correctAnswer", correctAnswers.get(q.getId()));
            return qMap;
        }).collect(Collectors.toList());

        result.put("questions", questionList);

        // Serialize to JSON
        String json = new com.fasterxml.jackson.databind.ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(result);

        // Save file dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Testergebnisse speichern");
        fileChooser.setInitialFileName("test_results.json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json);
            } catch (IOException e) {
                showErrorAlert("Fehler beim Speichern: " + e.getMessage());
            }
        }
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
