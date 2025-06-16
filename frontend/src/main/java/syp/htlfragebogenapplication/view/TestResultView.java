package syp.htlfragebogenapplication.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import syp.htlfragebogenapplication.controllers.TestResultViewController;

public class TestResultView extends BorderPane {

    private final Label testNameLabel;
    private final VBox contentContainer;
    private final Button backToMainButton;
    private final Button reviewQuestionsButton;
    private final Button backToResultsButton;
    private final TestResultViewController controller;
    private final BorderPane scoreContainer;
    private final VBox questionReviewContainer;
    private final TextField searchField;
    private final CheckBox showWrongOnlyCheckBox;
    private final Button downloadButton;
    private final ScrollPane scrollPane;

    public TestResultView(TestResultViewController controller) {
        this.controller = controller;

        // Scrollable content container
        contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(20));
        contentContainer.setAlignment(Pos.TOP_CENTER);
        contentContainer.setFillWidth(true);
        contentContainer.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        contentContainer.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        contentContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        contentContainer.getStyleClass().add("content");

        // Top Section
        testNameLabel = new Label();
        testNameLabel.getStyleClass().add("result-title-label");

        Separator separator = new Separator();
        VBox topBox = new VBox(10, testNameLabel, separator);
        topBox.setPadding(new Insets(20));
        contentContainer.getChildren().add(topBox);

        // Score Container
        scoreContainer = new BorderPane();
        scoreContainer.setPadding(new Insets(10));
        scoreContainer.getStyleClass().add("score-container");
        scoreContainer.setMaxWidth(600);
        contentContainer.getChildren().add(scoreContainer);

        // Search and filter controls
        HBox searchFilterBox = new HBox(15);
        searchFilterBox.setPadding(new Insets(10, 20, 10, 20));
        searchFilterBox.setAlignment(Pos.CENTER_LEFT);
        searchFilterBox.getStyleClass().add("search-filter-box");

        searchField = new TextField();
        searchField.setPromptText("Frage suchen");
        searchField.setPrefWidth(300);
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        showWrongOnlyCheckBox = new CheckBox("Nur falsche Antworten zeigen");
        searchFilterBox.getChildren().addAll(searchField, showWrongOnlyCheckBox);
        contentContainer.getChildren().add(searchFilterBox);

        // Questions Section
        questionReviewContainer = new VBox(20);
        questionReviewContainer.setPadding(new Insets(20, 40, 20, 40));
        questionReviewContainer.setMinWidth(Region.USE_COMPUTED_SIZE);
        questionReviewContainer.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(questionReviewContainer, Priority.ALWAYS);
        questionReviewContainer.setAlignment(Pos.CENTER);
        contentContainer.getChildren().add(questionReviewContainer);

        // Make the entire content scrollable
        scrollPane = new ScrollPane(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVisible(true);
        scrollPane.getStyleClass().add("question-scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        this.setCenter(scrollPane);

        // Bottom Section
        backToMainButton = new Button("Zurück zur Startseite");
        backToMainButton.getStyleClass().add("main-btn");

        downloadButton = new Button("Download Ergebnis");
        downloadButton.getStyleClass().add("main-btn");

        reviewQuestionsButton = new Button("Antworten anschauen");
        reviewQuestionsButton.getStyleClass().add("review-btn");

        backToResultsButton = new Button("Zurück zu Ergebnissen");
        backToResultsButton.getStyleClass().add("nav-btn");
        backToResultsButton.setVisible(false);

        HBox buttonRow = new HBox(10, downloadButton, backToMainButton);
        buttonRow.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottomBox = new HBox(10, spacer, backToResultsButton, reviewQuestionsButton, buttonRow);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.getStyleClass().add("fixed-bottom-bar");

        StackPane bottomPane = new StackPane(bottomBox);
        bottomPane.getStyleClass().add("fixed-bottom-pane");
        this.setBottom(bottomPane);

        // Hide toggle buttons as they're not needed
        reviewQuestionsButton.setVisible(false);
        backToResultsButton.setVisible(false);

        connectController();
    }

    private void connectController() {
        backToMainButton.setOnAction(e -> controller.onBackToMainButtonClicked());
        downloadButton.setOnAction(e -> {
            try {
                controller.onDownloadButtonClicked();
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> controller.onSearchTextChanged(newValue));
        showWrongOnlyCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> controller.onShowWrongOnlyChanged(newValue));

        controller.setTestNameLabel(testNameLabel);
        controller.setContentContainer(contentContainer);
        controller.setScoreContainer(scoreContainer);
        controller.setQuestionReviewContainer(questionReviewContainer);
        controller.setQuestionScrollPane(scrollPane);
        controller.setSearchField(searchField);
        controller.setShowWrongOnlyCheckBox(showWrongOnlyCheckBox);
        controller.setBackToMainButton(backToMainButton);
        controller.setDownloadButton(downloadButton);
        controller.setReviewQuestionsButton(reviewQuestionsButton);
        controller.setBackToResultsButton(backToResultsButton);
    }
}
