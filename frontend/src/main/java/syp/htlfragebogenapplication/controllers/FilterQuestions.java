package syp.htlfragebogenapplication.controllers;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import syp.htlfragebogenapplication.model.Question;
import syp.htlfragebogenapplication.utils.FractionUtils;

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
            if (!(node instanceof VBox))
                continue;

            VBox questionCard = (VBox) node;
            Question question = (i < questions.size()) ? questions.get(i) : null;
            if (question == null) {
                questionCard.setVisible(false);
                questionCard.setManaged(false);
                continue;
            }
            String givenValue = userAnswers[i];
            String correctValue = correctAnswers.get(question.getId());
            boolean isCorrect;

            if ("Fraction".equals(question.getAnswerType().getName())) {
                isCorrect = givenValue != null && correctValue != null &&
                        (givenValue.equals(correctValue) ||
                                FractionUtils.areEquivalent(givenValue, correctValue));
            } else {
                isCorrect = givenValue != null
                        && correctValue != null
                        && givenValue.equals(correctValue);
            }

            if (showWrongOnly && isCorrect) {
                questionCard.setVisible(false);
                questionCard.setManaged(false);
                continue;
            }

            boolean matchesSearch = lowerSearch.isEmpty();

            if (!matchesSearch) {
                String questionLabel = ("Frage " + (i + 1)).toLowerCase();
                String userAnswerText = (givenValue == null || givenValue.isEmpty())
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

            boolean shouldShow = matchesSearch;
            questionCard.setVisible(shouldShow);
            questionCard.setManaged(shouldShow);
        }
    }
}
