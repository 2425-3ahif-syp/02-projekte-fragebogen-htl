package syp.htlfragebogenapplication.database;

import syp.htlfragebogenapplication.model.Answer;
import syp.htlfragebogenapplication.model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnswerRepository {
    private final Connection connection;

    public AnswerRepository() {
        connection = Database.getInstance().getConnection();
    }

    public List<Answer> getAllAnswers() {
        List<Question> questions = new QuestionRepository().getAllQuestions();
        Map<Integer, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        List<Answer> answerList = new ArrayList<>();
        String sql = "SELECT id, question_id, answer_text FROM Answer";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int questionId = rs.getInt("question_id");
                Question question = questionMap.get(questionId);

                if (question == null) {
                    System.err.println("Warning: No question found for question_id: " + questionId);
                    continue;
                }

                answerList.add(new Answer(
                        rs.getInt("id"),
                        question,
                        rs.getString("answer_text")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answerList;
    }


    public Map<Integer, String> getCorrectAnswersMap() {
        Map<Integer, String> correctAnswersMap = new HashMap<>();
        List<Answer> answers = getAllAnswers();

        for (Answer answer : answers) {
            String textAnswer = answer.getAnswerText();
            if (textAnswer != null && !textAnswer.trim().isEmpty()) {
                correctAnswersMap.put(
                        answer.getQuestionId(),
                        textAnswer.trim()
                );
            }
        }

        return correctAnswersMap;
    }

}
