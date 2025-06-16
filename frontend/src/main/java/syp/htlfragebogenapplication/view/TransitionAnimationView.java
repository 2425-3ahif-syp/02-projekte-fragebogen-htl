package syp.htlfragebogenapplication.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import syp.htlfragebogenapplication.controllers.TestResultViewController;
import syp.htlfragebogenapplication.model.Question;
import syp.htlfragebogenapplication.model.Test;

import java.util.List;
import java.util.Map;

public class TransitionAnimationView extends StackPane {

    private final Test test;
    private final List<Question> questions;
    private final String[] userAnswers;
    private final Map<Integer, String> correctAnswers;
    private final int timeSeconds;
    private final Stage stage;

    public TransitionAnimationView(Stage stage, Test test, List<Question> questions, String[] userAnswers,
            Map<Integer, String> correctAnswers, int timeSeconds) {
        this.stage = stage;
        this.test = test;
        this.questions = questions;
        this.userAnswers = userAnswers;
        this.correctAnswers = correctAnswers;
        this.timeSeconds = timeSeconds;

        setupView();
    }

    private void setupView() {
        // Set up background - styles are in CSS
        this.setPadding(new Insets(20));
        this.getStyleClass().addAll("root", "transition-animation-view");

        // Create centered container for animation elements
        VBox container = new VBox(40);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(800);
        // Add title
        Label titleLabel = new Label("Test abgeschlossen!");
        titleLabel.getStyleClass().add("title-label");
        // Add subtitle with test name
        Label subtitleLabel = new Label(test.getName());
        subtitleLabel.getStyleClass().add("subtitle-label");

        // Create progress animation container
        VBox animationContainer = new VBox(20);
        animationContainer.setAlignment(Pos.CENTER);

        // Create the top bar (correct answers)
        VBox correctBarBox = new VBox(5);
        correctBarBox.setAlignment(Pos.CENTER_LEFT);
        Label correctLabel = new Label("Richtige Antworten:");
        correctLabel.getStyleClass().add("bar-label");
        HBox correctBar = new HBox();
        correctBar.setMinHeight(30);
        correctBar.setMaxHeight(30);
        correctBar.setPrefWidth(700);
        correctBar.setMinWidth(700);
        correctBar.setMaxWidth(700);
        correctBar.getStyleClass().add("animated-bar");
        correctBar.setSpacing(0); // Ensure no spacing between sections
        HBox.setHgrow(correctBar, Priority.ALWAYS); // Make the bar grow to fill space

        correctBarBox.getChildren().addAll(correctLabel, correctBar);

        // Create the bottom bar (user answers)
        VBox userBarBox = new VBox(5);
        userBarBox.setAlignment(Pos.CENTER_LEFT);
        Label userLabel = new Label("Deine Antworten:");
        userLabel.getStyleClass().add("bar-label");
        HBox userBar = new HBox();
        userBar.setMinHeight(30);
        userBar.setMaxHeight(30);
        userBar.setPrefWidth(700);
        userBar.setMinWidth(700);
        userBar.setMaxWidth(700);
        userBar.getStyleClass().add("animated-bar");
        userBar.setSpacing(0);
        HBox.setHgrow(userBar, Priority.ALWAYS);

        userBarBox.getChildren().addAll(userLabel, userBar);
        HBox scoreBox = new HBox(10);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.getStyleClass().add("score-box");

        Label scoreLabel = new Label("0");
        scoreLabel.getStyleClass().add("score-label");

        Label slashLabel = new Label(" / ");
        slashLabel.getStyleClass().add("subtitle-label");

        Label totalLabel = new Label(String.valueOf(questions.size()));
        totalLabel.getStyleClass().add("subtitle-label");

        scoreBox.getChildren().addAll(scoreLabel, slashLabel, totalLabel);

        // Add all components to the main container
        animationContainer.getChildren().addAll(correctBarBox, userBarBox, scoreBox);
        container.getChildren().addAll(titleLabel, subtitleLabel, animationContainer);

        // Add container to main view
        this.getChildren().add(container);

        // After setting up the view, start the animation
        startAnimation(correctBar, userBar, scoreLabel);
    }

    private void startAnimation(HBox correctBar, HBox userBar, Label scoreLabel) {
        Timeline timeline = TestResultViewController.createAndAnimateProgressBars(
                correctBar,
                userBar,
                questions,
                userAnswers,
                correctAnswers,
                scoreLabel,
                5.0);

        KeyFrame finalKeyFrame = new KeyFrame(
                Duration.seconds(6.0), // Extra time for the final transition
                _ -> showTestResultsScreen());

        timeline.getKeyFrames().add(finalKeyFrame);
        timeline.play();
    }

    private void showTestResultsScreen() {
        TestResultViewController.show(stage, test, questions, userAnswers, timeSeconds);
    }
}
