package syp.htlfragebogenapplication.controllers;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import syp.htlfragebogenapplication.model.Question;

import java.util.List;
import java.util.Map;

public class FilterQuestions {

    public static void filter(VBox questionReviewContainer,
            List<Question> questions,
            int[] userAnswers,
            Map<Integer, Integer> correctAnswers,
            String searchText,
            boolean showWrongOnly) {

        for (int i = 0; i < questionReviewContainer.getChildren().size(); i++) {
            Node node = questionReviewContainer.getChildren().get(i);
            if (node instanceof VBox) {
                VBox questionCard = (VBox) node;
                boolean isMatch = true;

                Question currentQuestion = i < questions.size() ? questions.get(i) : null;
                if (currentQuestion == null)
                    continue;

                boolean isAnswerCorrect = userAnswers[i] == correctAnswers.get(currentQuestion.getId());

                if (showWrongOnly && isAnswerCorrect) {
                    isMatch = false;
                }

                if (searchText != null && !searchText.isEmpty()) {
                    String questionText = "Frage " + (i + 1);
                    String userAnswerText = userAnswers[i] == -1 ? "Keine Antwort"
                            : String.valueOf((char) ('a' + userAnswers[i]));
                    String correctAnswerText = String
                            .valueOf((char) ('a' + correctAnswers.get(currentQuestion.getId())));

                    if (!questionText.toLowerCase().contains(searchText.toLowerCase()) &&
                            !userAnswerText.toLowerCase().contains(searchText.toLowerCase()) &&
                            !correctAnswerText.toLowerCase().contains(searchText.toLowerCase())) {
                        isMatch = false;
                    }
                }

                questionCard.setVisible(isMatch);
                questionCard.setManaged(isMatch);
            }
        }
    }
}
