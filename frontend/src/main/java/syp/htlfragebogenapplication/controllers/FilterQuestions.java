package syp.htlfragebogenapplication.controllers;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import syp.htlfragebogenapplication.model.Question;

import java.util.List;
import java.util.Map;

public class FilterQuestions {

    public static void filter(VBox questionReviewContainer,
                              List<Question> questions,
                              String[] userAnswers,
                              Map<Integer, String> correctAnswers,
                              String searchText,
                              boolean showWrongOnly) {

        String lowerSearch = (searchText == null) ? "" : searchText.trim().toLowerCase();

        for (int i = 0; i < questionReviewContainer.getChildren().size(); i++) {
            Node node = questionReviewContainer.getChildren().get(i);
            if (!(node instanceof VBox)) continue;

            VBox questionCard = (VBox) node;
            Question question = (i < questions.size()) ? questions.get(i) : null;
            if (question == null) {
                // nothing to show
                questionCard.setVisible(false);
                questionCard.setManaged(false);
                continue;
            }

            // Determine correctness via String.equals
            String givenValue   = userAnswers[i];
            String correctValue = correctAnswers.get(question.getId());
            boolean isCorrect = givenValue != null
                    && correctValue != null
                    && givenValue.equals(correctValue);

            // Filter out correct when "wrong only" is checked
            if (showWrongOnly && isCorrect) {
                questionCard.setVisible(false);
                questionCard.setManaged(false);
                continue;
            }

            // Filter by searchText (if any)
            boolean matchesSearch = lowerSearch.isEmpty();

            if (!matchesSearch) {
                // build the three fields to search in
                String questionLabel     = ("Frage " + (i + 1)).toLowerCase();
                String userAnswerText    = (givenValue == null || givenValue.isEmpty())
                        ? "keine antwort"
                        : givenValue.toLowerCase();
                String correctAnswerText = (correctValue == null || correctValue.isEmpty())
                        ? ""
                        : correctValue.toLowerCase();

                if (questionLabel.contains(lowerSearch)
                        || userAnswerText.contains(lowerSearch)
                        || correctAnswerText.contains(lowerSearch)) {
                    matchesSearch = true;
                }
            }

            // Show or hide based on combined tests
            boolean shouldShow = matchesSearch;
            questionCard.setVisible(shouldShow);
            questionCard.setManaged(shouldShow);
        }
    }
}
