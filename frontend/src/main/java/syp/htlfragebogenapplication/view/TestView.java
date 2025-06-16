package syp.htlfragebogenapplication.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import syp.htlfragebogenapplication.controllers.TestViewController;

public class TestView extends BorderPane {

    private final Label testNameLabel;
    private final VBox questionsContainer;
    private final ScrollPane questionsScrollPane;
    private final Button nextButton;
    private final Button backButton;
    private final Button cancelButton;
    private final Label questionCount;
    private final Label timeCount;
    private final TestViewController controller;

    public TestView(TestViewController controller) {
        this.controller = controller;

        // Top Section
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(20));

        testNameLabel = new Label();
        testNameLabel.getStyleClass().add("test-name-label");

        HBox timeBox = new HBox(20);
        timeCount = new Label("Zeit: 00:00");
        questionCount = new Label();
        timeBox.getChildren().addAll(timeCount, questionCount);

        Separator separator = new Separator();

        topBox.getChildren().addAll(testNameLabel, timeBox, separator);

        // Center Section - Questions Container
        questionsContainer = new VBox(20);
        questionsContainer.setPadding(new Insets(50));
        questionsContainer.setMinWidth(Region.USE_COMPUTED_SIZE);
        questionsContainer.setId("questions-container");
        questionsContainer.setAlignment(Pos.CENTER);

        // Center Section Scroll Pane
        questionsScrollPane = new ScrollPane(questionsContainer);
        questionsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        questionsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        questionsScrollPane.setId("question-scroll-pane");
        questionsScrollPane.setFitToWidth(true);


        // Bottom Section
        HBox bottomBox = new HBox(10);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        cancelButton = new Button("Test abbrechen");
        cancelButton.getStyleClass().add("stop-btn");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        backButton = new Button("Zurück");
        backButton.getStyleClass().add("nav-btn");

        nextButton = new Button("Weiter");
        nextButton.getStyleClass().add("nav-btn");

        bottomBox.getChildren().addAll(cancelButton, spacer, backButton, nextButton);

        this.setTop(topBox);
        this.setCenter(questionsScrollPane);
        this.setBottom(bottomBox);

        this.setPrefHeight(600.0);
        this.setPrefWidth(questionsContainer.getPrefWidth());

        connectController();
    }

    private void connectController() {
        cancelButton.setOnAction(event -> controller.onCancelButtonClicked());
        backButton.setOnAction(event -> controller.onBackButtonClicked());
        nextButton.setOnAction(event -> controller.onNextButtonClicked());

        controller.setTestNameLabel(testNameLabel);
        controller.setQuestionsContainer(questionsContainer);
        controller.setNextButton(nextButton);
        controller.setBackButton(backButton);
        controller.setCancelButton(cancelButton);
        controller.setQuestionCount(questionCount);
        controller.setTimeCount(timeCount);
    }

    // Getters
    public Label getTestNameLabel() {
        return testNameLabel;
    }

    public VBox getQuestionsContainer() {
        return questionsContainer;
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Label getQuestionCount() {
        return questionCount;
    }

    public Label getTimeCount() {
        return timeCount;
    }
}
