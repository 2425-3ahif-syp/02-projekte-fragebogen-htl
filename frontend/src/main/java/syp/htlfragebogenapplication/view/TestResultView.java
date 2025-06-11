package syp.htlfragebogenapplication.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import syp.htlfragebogenapplication.controllers.TestResultViewController;

public class TestResultView extends BorderPane {

    private final Label testNameLabel;
    private final VBox contentContainer;
    private final Button backToMainButton;
    private final Button reviewQuestionsButton;
    private final Button backToResultsButton;
    private final TestResultViewController controller;
    private final BorderPane scoreContainer;
    private final ScrollPane questionScrollPane;
    private final VBox questionReviewContainer;
    private final TextField searchField;
    private final CheckBox showWrongOnlyCheckBox;

    public TestResultView(TestResultViewController controller) {
        this.controller = controller;

        // Top Section
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(20));

        testNameLabel = new Label();
        testNameLabel.getStyleClass().add("result-title-label");

        Separator separator = new Separator();

        topBox.getChildren().addAll(testNameLabel, separator);

        // Center Section - Content Container
        contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(20));
        contentContainer.setAlignment(Pos.CENTER); // Score container for displaying results
        scoreContainer = new BorderPane();
        scoreContainer.setPadding(new Insets(20));
        scoreContainer.getStyleClass().add("score-container");
        scoreContainer.setVisible(true);
        scoreContainer.setMaxWidth(700);

        // Search and filter controls
        HBox searchFilterBox = new HBox(15);
        searchFilterBox.setPadding(new Insets(10, 20, 10, 20));
        searchFilterBox.setAlignment(Pos.CENTER_LEFT);
        searchFilterBox.getStyleClass().add("search-filter-box");

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Frage suchen");
        searchField.setPrefWidth(300);
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // Toggle for wrong answers only
        showWrongOnlyCheckBox = new CheckBox("Nur falsche Antworten zeigen");

        searchFilterBox.getChildren().addAll(searchField, showWrongOnlyCheckBox);
        searchFilterBox.setVisible(true);

        // Question review container (in a ScrollPane for scrollability)
        questionReviewContainer = new VBox(20);
        questionReviewContainer.setPadding(new Insets(20));
        questionReviewContainer.setVisible(true);

        // Wrap the question container in a scroll pane
        questionScrollPane = new ScrollPane(questionReviewContainer);
        questionScrollPane.setFitToWidth(true);
        questionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        questionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        questionScrollPane.setVisible(true);
        questionScrollPane.getStyleClass().add("question-scroll-pane");

        contentContainer.getChildren().addAll(scoreContainer, searchFilterBox, questionScrollPane); // Bottom Section -
                                                                                                    // fixed position
        HBox bottomBox = new HBox(10);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.getStyleClass().add("fixed-bottom-bar");
        // Create a white background effect and drop shadow for the bottom bar
        StackPane bottomPane = new StackPane();
        bottomPane.getStyleClass().add("fixed-bottom-pane");

        backToMainButton = new Button("Zurück zur Startseite");
        backToMainButton.getStyleClass().add("main-btn");

        reviewQuestionsButton = new Button("Antworten anschauen");
        reviewQuestionsButton.getStyleClass().add("review-btn");

        backToResultsButton = new Button("Zurück zu Ergebnissen");
        backToResultsButton.getStyleClass().add("nav-btn");
        backToResultsButton.setVisible(false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottomBox.getChildren().addAll(spacer, backToResultsButton, reviewQuestionsButton, backToMainButton);
        bottomPane.getChildren().add(bottomBox);
        this.setTop(topBox);
        this.setCenter(contentContainer);
        this.setBottom(bottomPane);

        this.setPrefHeight(600.0);
        this.setPrefWidth(800.0);

        // Hide toggle buttons as they're not needed
        reviewQuestionsButton.setVisible(false);
        backToResultsButton.setVisible(false);

        connectController();
    }

    private void connectController() {
        backToMainButton.setOnAction(e -> controller.onBackToMainButtonClicked());
        // Remove reviewQuestionsButton and backToResultsButton actions since they are
        // not needed anymore
        // Remove their visibility toggling as well
        // Connect search field and filter checkbox
        searchField.textProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            @SuppressWarnings("unused")
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                controller.onSearchTextChanged(newValue);
            }
        });
        showWrongOnlyCheckBox.selectedProperty().addListener(new javafx.beans.value.ChangeListener<Boolean>() {
            @SuppressWarnings("unused")
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends Boolean> observable,
                    Boolean oldValue, Boolean newValue) {
                controller.onShowWrongOnlyChanged(newValue);
            }
        });
        controller.setTestNameLabel(testNameLabel);
        controller.setContentContainer(contentContainer);
        controller.setScoreContainer(scoreContainer);
        controller.setQuestionReviewContainer(questionReviewContainer);
        controller.setQuestionScrollPane(questionScrollPane);
        controller.setSearchField(searchField);
        controller.setShowWrongOnlyCheckBox(showWrongOnlyCheckBox);
        controller.setBackToMainButton(backToMainButton);
        controller.setReviewQuestionsButton(reviewQuestionsButton);
        controller.setBackToResultsButton(backToResultsButton);
    } // Getters

    public Label getTestNameLabel() {
        return testNameLabel;
    }

    public VBox getContentContainer() {
        return contentContainer;
    }

    public BorderPane getScoreContainer() {
        return scoreContainer;
    }

    public VBox getQuestionReviewContainer() {
        return questionReviewContainer;
    }

    public ScrollPane getQuestionScrollPane() {
        return questionScrollPane;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public CheckBox getShowWrongOnlyCheckBox() {
        return showWrongOnlyCheckBox;
    }

    public Button getBackToMainButton() {
        return backToMainButton;
    }

    public Button getReviewQuestionsButton() {
        return reviewQuestionsButton;
    }

    public Button getBackToResultsButton() {
        return backToResultsButton;
    }
}
