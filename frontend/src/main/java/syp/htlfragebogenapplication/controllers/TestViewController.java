package syp.htlfragebogenapplication.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import syp.htlfragebogenapplication.database.QuestionRepository;
import syp.htlfragebogenapplication.model.Question;
import syp.htlfragebogenapplication.model.Test;
import syp.htlfragebogenapplication.view.TestView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

            // Show progress
            questionCount.setText("Frage: " + (currentQuestionIndex + 1) + "/" + questions.size());

            String answerTypeName = currentQuestion.getAnswerType().getName();
            Pane answersPane;

            if ("Text".equals(answerTypeName)) {
                String text = loadTextResource(currentQuestion.getImagePath());
                String[] tokens = text.split(" ");

                FlowPane flow = new FlowPane();
                flow.setHgap(5);
                flow.setVgap(5);
                flow.setPadding(new Insets(10, 0, 0, 0));
                flow.prefWrapLengthProperty().bind(questionsContainer.widthProperty().subtract(40));

                for (String token : tokens) {
                    String word = token;
                    String punct = "";

                    if (word.endsWith(",") || word.endsWith(".")) {
                        punct = word.substring(word.length() - 1);
                        word = word.substring(0, word.length() - 1);
                    }

                    TextField tf = new TextField(word);
                    tf.setPrefColumnCount(Math.max(word.length(), 1));
                    HBox cell = new HBox(2, tf);

                    if (!punct.isEmpty()) {
                        Label lbl = new Label(punct);
                        cell.getChildren().add(lbl);
                    }
                    flow.getChildren().add(cell);

                }
                answersPane = flow;
            } else {
                String imagePath = getClass().getResource(currentQuestion.getImagePath()).toExternalForm();
                ImageView imageView = new ImageView(new Image(imagePath));
                imageView.setFitWidth(800);
                imageView.setPreserveRatio(true);
                questionsContainer.getChildren().add(imageView);
                if ("Letter".equals(answerTypeName) || "Number".equals(answerTypeName)) {

                    HBox answersBox = new HBox(15);
                    answersBox.setAlignment(Pos.CENTER_LEFT);
                    answersBox.setPadding(new Insets(10, 0, 0, 0));

                    int possibleAnswerCount = currentQuestion.getPossibleAnswerCount();
                    ToggleGroup toggleGroup = new ToggleGroup();

                    for (int i = 0; i < possibleAnswerCount; i++) {
                        String optionText = "Letter".equals(answerTypeName) ? String.valueOf((char) ('a' + i)) : String.valueOf(i);

                        RadioButton rb = new RadioButton(optionText);
                        rb.setToggleGroup(toggleGroup);
                        rb.setUserData(optionText);
                        rb.setOnAction(e -> answerSelections[currentQuestionIndex] = rb.getUserData().toString());

                        if (optionText.equals(answerSelections[currentQuestionIndex])) {
                            rb.setSelected(true);
                        }
                        answersBox.getChildren().add(rb);
                    }

                    answersPane = answersBox;
                } else if ("Number Field".equals(answerTypeName)) {
                    TextField numberField = new TextField();
                    numberField.setPromptText("Geben Sie eine Zahl ein");


                    numberField.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.matches("\\d*")) {
                            numberField.setText(newVal.replaceAll("[^\\d]", ""));
                        }
                        answerSelections[currentQuestionIndex] = numberField.getText(); // update answer
                    });


                    if (answerSelections[currentQuestionIndex] != null) {
                        numberField.setText(answerSelections[currentQuestionIndex]);
                    }

                    HBox numberBox = new HBox(15);
                    numberBox.setAlignment(Pos.CENTER_LEFT);
                    numberBox.setPadding(new Insets(10, 0, 0, 0));
                    numberBox.getChildren().add(numberField);

                    answersPane = numberBox;
                } else if ("Set Comma".equals(answerTypeName)) {
                    TextField preDecimalField = new TextField();
                    TextField decimalField = new TextField();
                    Label commaLabel = new Label(",");
                    preDecimalField.setPromptText("Vorkommastelle");
                    decimalField.setPromptText("Nachkommastelle");

                    Label tipsLabel = new Label("Tipps für Antworteingabe dieses Tests: \n\n Angabe: \n 0035006300 \n\n kann werden zu: \n 35 , 0063 \n 0 , 350063 \n 350063 , ");
                    tipsLabel.setPadding(new Insets(20, 0, 0, 0));

                    preDecimalField.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.matches("\\d*")) {
                            preDecimalField.setText(newVal.replaceAll("[^\\d]", ""));
                        }
                        updateAnswerForSetComma(preDecimalField, decimalField);
                    });

                    decimalField.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.matches("\\d*")) {
                            decimalField.setText(newVal.replaceAll("[^\\d]", ""));
                        }
                        updateAnswerForSetComma(preDecimalField, decimalField);
                    });


                    if (answerSelections[currentQuestionIndex] != "") {
                        // if user only enters 1 number, we still have to present it
                        String modifiedString = " " + answerSelections[currentQuestionIndex] + " ";
                        String[] setAnswer = modifiedString.split(",");
                        preDecimalField.setText(setAnswer[0]);
                        decimalField.setText(setAnswer[1]);
                    }

                    HBox commaBox = new HBox(15);
                    commaBox.setAlignment(Pos.CENTER_LEFT);
                    commaBox.setPadding(new Insets(10, 0, 0, 0));
                    commaBox.getChildren().addAll(preDecimalField, commaLabel, decimalField);

                    VBox answersBox = new VBox(15);
                    answersBox.setAlignment(Pos.CENTER_LEFT);
                    answersBox.setPadding(new Insets(10, 0, 0, 0));
                    answersBox.getChildren().addAll(commaBox, tipsLabel);

                    answersPane = answersBox;
                } else {
                    Label lbl = new Label("Antworttyp „" + answerTypeName + "“ noch nicht implementiert.");
                    answersPane = new VBox(lbl);
                }
            }
            questionsContainer.getChildren().add(answersPane);
        }
    }

    private void updateAnswerForSetComma(TextField preDecimalField, TextField decimalField) {
        String pre = preDecimalField.getText().replaceFirst("^0+(?!$)", "");  // Remove leading zeros
        String post = decimalField.getText().replaceFirst("0+$", "");         // Remove trailing zeros

        String answer;
        if (post.isEmpty()) {
            answer = pre; // No decimal part, don't include comma
        } else {
            answer = pre + "," + post;
        }

        answerSelections[currentQuestionIndex] = answer;
    }

    private String loadTextResource(String resourcePath) {
        try (InputStream in = getClass().getResourceAsStream(resourcePath); BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(" "));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String collectTextAnswer() {
        FlowPane flow = (FlowPane) questionsContainer.getChildren().getFirst();

        return flow.getChildren().stream().filter(node -> node instanceof HBox).map(node -> {
            HBox cell = (HBox) node;
            // the first Child is always a TextField
            String word = ((TextField) cell.getChildren().get(0)).getText();
            // if a Label is present, it contains punctuation
            if (cell.getChildren().size() > 1 && cell.getChildren().get(1) instanceof Label) {
                word += ((Label) cell.getChildren().get(1)).getText();
            }
            return word;
        }).collect(Collectors.joining(" "));
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
        Question currentQuestion = questions.get(currentQuestionIndex);
        String answerTypeName = currentQuestion.getAnswerType().getName();

        if ("Text".equals(answerTypeName)) {
            answerSelections[currentQuestionIndex] = collectTextAnswer();
        }

        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            displayCurrentQuestion();
        } else {
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
                scene.getStylesheets().add(MainViewController.class.getResource("/syp/htlfragebogenapplication/Base.css").toExternalForm());
                scene.getStylesheets().add(TestViewController.class.getResource("/syp/htlfragebogenapplication/testview/test-view.css").toExternalForm());
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
