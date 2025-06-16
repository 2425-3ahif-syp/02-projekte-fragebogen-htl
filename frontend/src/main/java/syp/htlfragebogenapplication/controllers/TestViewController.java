package syp.htlfragebogenapplication.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import syp.htlfragebogenapplication.database.QuestionRepository;
import syp.htlfragebogenapplication.database.AnswerRepository;
import syp.htlfragebogenapplication.model.Question;
import syp.htlfragebogenapplication.model.Test;
import syp.htlfragebogenapplication.view.TestView;
import syp.htlfragebogenapplication.view.TransitionAnimationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class TestViewController {

    private Label testNameLabel;
    private VBox questionsContainer;
    private Button nextButton;
    private Button backButton;
    private Button cancelButton;
    private Label questionCount;
    private Label timeCount;
    private Label descriptionLabel;

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

    public void setDescriptionLabel(Label descriptionLabel) {
        this.descriptionLabel = descriptionLabel;
    }

    public void initData(Test test) {
        this.test = test;
        testNameLabel.setText(test.getName());
        questionCount.setText("Frage: " + (currentQuestionIndex + 1) + "/" + test.getQuestionCount());
        answerSelections = new String[test.getQuestionCount()];
        descriptionLabel.setText(test.getDescription());
        Arrays.fill(answerSelections, "");

        loadQuestions();
        displayCurrentQuestion();
        startTimer();
    }

    private void loadQuestions() {
        QuestionRepository questionRepository = new QuestionRepository();
        questions = questionRepository.getAllQuestionsFromTest(test.getId());

        if (questions.size() != answerSelections.length) {
            String[] newAnswerSelections = new String[questions.size()];
            for (int i = 0; i < Math.min(answerSelections.length, questions.size()); i++) {
                newAnswerSelections[i] = answerSelections[i];
            }
            for (int i = answerSelections.length; i < questions.size(); i++) {
                newAnswerSelections[i] = "";
            }
            answerSelections = newAnswerSelections;
            questionCount.setText("Frage: " + (currentQuestionIndex + 1) + "/" + questions.size());
        }
    }

    private void displayCurrentQuestion() {
        questionsContainer.getChildren().clear();
        if (questions != null && !questions.isEmpty()) {
            Question currentQuestion = questions.get(currentQuestionIndex);

            // Update button text
            if (currentQuestionIndex == questions.size() - 1) {
                nextButton.setText("Beenden");
                nextButton.setId("submit-btn");
            } else {
                nextButton.setText("Weiter");
                nextButton.getStyleClass().add("nav-btn");
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

                VBox wrapper = new VBox(flow);
                wrapper.setAlignment(Pos.CENTER);
                wrapper.setPrefWidth(Region.USE_COMPUTED_SIZE);
                wrapper.setPrefHeight(Region.USE_COMPUTED_SIZE);
                VBox.setVgrow(wrapper, Priority.ALWAYS);

                answersPane = wrapper;
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
                        String optionText = "Letter".equals(answerTypeName) ? String.valueOf((char) ('a' + i))
                                : String.valueOf(i);

                        RadioButton rb = new RadioButton(optionText);
                        rb.setToggleGroup(toggleGroup);
                        rb.setUserData(optionText);
                        rb.setOnAction(_ -> answerSelections[currentQuestionIndex] = rb.getUserData().toString());

                        if (optionText.equals(answerSelections[currentQuestionIndex])) {
                            rb.setSelected(true);
                        }
                        answersBox.getChildren().add(rb);
                    }

                    answersBox.setAlignment(Pos.CENTER);
                    answersPane = answersBox;
                } else if ("Number Field".equals(answerTypeName)) {
                    TextField numberField = new TextField();
                    numberField.setPromptText("Geben Sie eine Zahl ein");

                    numberField.textProperty().addListener((_, _, newVal) -> {
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

                    numberBox.setAlignment(Pos.CENTER);
                    answersPane = numberBox;
                } else if ("Fraction".equals(answerTypeName)) {
                    TextField numeratorField = new TextField();
                    TextField denominatorField = new TextField();

                    numeratorField.setPromptText("Zähler");
                    denominatorField.setPromptText("Nenner");

                    Separator fractionLine = new Separator();
                    fractionLine.setPrefWidth(100);

                    numeratorField.textProperty().addListener((_, _, newVal) -> {
                        if (!newVal.matches("-?\\d*")) {
                            String corrected = newVal;
                            if (newVal.startsWith("-")) {
                                corrected = "-" + newVal.substring(1).replaceAll("[^\\d]", "");
                            } else {
                                corrected = newVal.replaceAll("[^\\d]", "");
                            }
                            numeratorField.setText(corrected);
                        }
                        updateAnswerForFraction(numeratorField, denominatorField);
                    });

                    denominatorField.textProperty().addListener((_, _, newVal) -> {
                        if (!newVal.matches("\\d*")) {
                            denominatorField.setText(newVal.replaceAll("[^\\d]", ""));
                        }
                        updateAnswerForFraction(numeratorField, denominatorField);
                    });

                    if (answerSelections[currentQuestionIndex] != null
                            && !answerSelections[currentQuestionIndex].isEmpty()) {
                        String[] fractionParts = answerSelections[currentQuestionIndex].split("/");
                        if (fractionParts.length == 2) {
                            numeratorField.setText(fractionParts[0]);
                            denominatorField.setText(fractionParts[1]);
                        }
                    }

                    VBox fractionBox = new VBox(5);
                    fractionBox.setAlignment(Pos.CENTER);
                    fractionBox.setMaxWidth(100);
                    fractionBox.getChildren().addAll(numeratorField, fractionLine, denominatorField);

                    Image infoImage = new Image(
                            Objects.requireNonNull(getClass().getResourceAsStream("/img/general/interrogation.png")));
                    ImageView infoIcon = new ImageView(infoImage);
                    infoIcon.setFitWidth(20);
                    infoIcon.setFitHeight(20);

                    Label iconLabel = new Label("", infoIcon);
                    iconLabel.setContentDisplay(ContentDisplay.CENTER);

                    Tooltip tooltip = new Tooltip("Hinweis: Geben Sie den Zähler und Nenner des Bruchs ein.");
                    tooltip.getStyleClass().add("tip-box");
                    tooltip.setShowDelay(Duration.ZERO);
                    tooltip.setHideDelay(Duration.millis(200));
                    tooltip.setShowDuration(Duration.INDEFINITE);

                    Tooltip.install(iconLabel, tooltip);

                    HBox answersBox = new HBox(15);
                    answersBox.setAlignment(Pos.CENTER_LEFT);
                    answersBox.setPadding(new Insets(10, 0, 0, 0));
                    answersBox.getChildren().addAll(fractionBox, iconLabel);

                    answersBox.setAlignment(Pos.CENTER);
                    answersPane = answersBox;
                } else if ("Set Comma".equals(answerTypeName)) {
                    TextField preDecimalField = new TextField();
                    TextField decimalField = new TextField();
                    Label commaLabel = new Label(",");
                    preDecimalField.setPromptText("Vorkommastelle");
                    decimalField.setPromptText("Nachkommastelle");

                    Image infoImage = new Image(
                            Objects.requireNonNull(getClass().getResourceAsStream("/img/general/interrogation.png")));
                    ImageView infoIcon = new ImageView(infoImage);
                    infoIcon.setFitWidth(20);
                    infoIcon.setFitHeight(20);

                    Label iconLabel = new Label("", infoIcon);
                    iconLabel.setContentDisplay(ContentDisplay.CENTER);

                    Tooltip tooltip = new Tooltip(
                            "Hinweis: \n\n Angabe: \n 0035006300 \n\n Beispiele für Kommasetzung: \n 35 , 0063 \n 0 , 350063 \n 350063 , \n ...");
                    tooltip.getStyleClass().add("tip-box");
                    tooltip.setShowDelay(Duration.ZERO);
                    tooltip.setHideDelay(Duration.millis(200));
                    tooltip.setShowDuration(Duration.INDEFINITE);

                    Tooltip.install(iconLabel, tooltip);

                    preDecimalField.textProperty().addListener((_, _, newVal) -> {
                        if (!newVal.matches("\\d*")) {
                            preDecimalField.setText(newVal.replaceAll("[^\\d]", ""));
                        }
                        updateAnswerForSetComma(preDecimalField, decimalField);
                    });

                    decimalField.textProperty().addListener((_, _, newVal) -> {
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
                    commaBox.setAlignment(Pos.CENTER);
                    commaBox.getChildren().addAll(preDecimalField, commaLabel, decimalField);

                    HBox answersBox = new HBox(15);
                    answersBox.setAlignment(Pos.CENTER);
                    answersBox.setPadding(new Insets(10, 0, 0, 0));
                    answersBox.getChildren().addAll(commaBox, iconLabel);

                    answersPane = answersBox;
                } else {
                    Label lbl = new Label("Antworttyp „" + answerTypeName + "“ noch nicht implementiert.");
                    answersPane = new VBox(lbl);
                }
            }
            questionsContainer.getChildren().add(answersPane);
            questionsContainer.setAlignment(Pos.CENTER);
        }
    }

    private void updateAnswerForSetComma(TextField preDecimalField, TextField decimalField) {
        String pre = preDecimalField.getText().replaceFirst("^0+(?!$)", ""); // Remove leading zeros
        String post = decimalField.getText().replaceFirst("0+$", ""); // Remove trailing zeros

        String answer;
        if (post.isEmpty()) {
            answer = pre; // No decimal part, don't include comma
        } else {
            answer = pre + "," + post;
        }

        answerSelections[currentQuestionIndex] = answer;
    }

    private void updateAnswerForFraction(TextField numeratorField, TextField denominatorField) {
        String numerator = numeratorField.getText().trim();
        String denominator = denominatorField.getText().trim();

        if (!numerator.isEmpty() && !denominator.isEmpty()) {
            if (denominator.equals("0")) {
                return;
            }
            answerSelections[currentQuestionIndex] = numerator + "/" + denominator;
        } else {
            answerSelections[currentQuestionIndex] = "";
        }
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

    private String collectTextAnswer() {
        VBox wrapper = (VBox) questionsContainer.getChildren().getFirst();
        FlowPane flow = (FlowPane) wrapper.getChildren().getFirst();

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
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
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

        Map<Integer, String> correctAnswers = new HashMap<>();
        try {
            AnswerRepository answerRepository = new AnswerRepository();
            correctAnswers = answerRepository.getCorrectAnswersMap();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error loading correct answers: " + e.getMessage());
            return;
        }

        showAnimationTransition(correctAnswers);
    }

    private void showAnimationTransition(Map<Integer, String> correctAnswers) {
        TransitionAnimationView transitionView = new TransitionAnimationView(
                primaryStage, test, questions, answerSelections, correctAnswers, timeSeconds);

        Scene transitionScene = new Scene(
                transitionView, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());

        try {
            transitionScene.getStylesheets().add(getClass()
                    .getResource("/syp/htlfragebogenapplication/Base.css").toExternalForm());
            transitionScene.getStylesheets().add(getClass()
                    .getResource("/syp/htlfragebogenapplication/testview/test-result-view.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS file not found, continuing without styling");
        }

        primaryStage.setScene(transitionScene);
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            showStaticErrorAlert("Could not load test view: " + e.getMessage());
        }
    }

    private static void showStaticErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
