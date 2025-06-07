package syp.htlfragebogenapplication.controllers;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import syp.htlfragebogenapplication.model.Question;

import java.util.List;
import java.util.Map;

public class FilterQuestions {

    /**
     * Helper method to filter questions in the TestResultViewController
     */
    public static void filter(VBox questionReviewContainer,
            List<Question> questions,
            int[] userAnswers,
            Map<Integer, Integer> correctAnswers,
            String searchText,
            boolean showWrongOnly) {

        // Hide questions that don't match the search or filter criteria
        for (int i = 0; i < questionReviewContainer.getChildren().size(); i++) {
            Node node = questionReviewContainer.getChildren().get(i);
            if (node instanceof VBox) {
                VBox questionCard = (VBox) node;
                boolean isMatch = true;

                // Reference to the actual question
                Question currentQuestion = i < questions.size() ? questions.get(i) : null;
                if (currentQuestion == null)
                    continue;

                // Get if answer was correct
                boolean isAnswerCorrect = userAnswers[i] == correctAnswers.get(currentQuestion.getId());

                // Filter by wrong answers if checkbox is selected
                if (showWrongOnly && isAnswerCorrect) {
                    isMatch = false;
                }

                // Filter by search text
                if (searchText != null && !searchText.isEmpty()) {
                    String questionText = "Frage " + (i + 1);
                    String userAnswerText = userAnswers[i] == -1 ? "Keine Antwort"
                            : String.valueOf((char) ('a' + userAnswers[i]));
                    String correctAnswerText = String
                            .valueOf((char) ('a' + correctAnswers.get(currentQuestion.getId())));

                    // Check if search text matches with question number, user answer, or correct
                    // answer
                    if (!questionText.toLowerCase().contains(searchText.toLowerCase()) &&
                            !userAnswerText.toLowerCase().contains(searchText.toLowerCase()) &&
                            !correctAnswerText.toLowerCase().contains(searchText.toLowerCase())) {
                        isMatch = false;
                    }
                }

                // Set the visibility based on the match result
                questionCard.setVisible(isMatch);
                questionCard.setManaged(isMatch); // This prevents empty spaces for hidden questions
            }
        }
    }
}
