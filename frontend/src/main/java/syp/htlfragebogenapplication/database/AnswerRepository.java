package syp.htlfragebogenapplication.database;

import syp.htlfragebogenapplication.model.Answer;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerRepository {
    private final Connection connection;

    public AnswerRepository() {
        connection = Database.getInstance().getConnection();
    }

    public List<Answer> getAllAnswers() {
        List<Answer> answerList = new ArrayList<>();
        String sql = "SELECT id, question_id, answer_text FROM Answer";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                answerList.add(new Answer(
                        rs.getInt("id"),
                        rs.getInt("question_id"),
                        rs.getString("answer_text")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answerList;
    }

    public Answer getAnswerForQuestion(int questionId) {
        String sql = "SELECT id, question_id, answer_text FROM Answer WHERE question_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Answer(
                        rs.getInt("id"),
                        rs.getInt("question_id"),
                        rs.getString("answer_text"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<Integer, Integer> getCorrectAnswersMap() {
        Map<Integer, Integer> correctAnswersMap = new HashMap<>();
        List<Answer> answers = getAllAnswers();

        for (Answer answer : answers) {
            // Convert letter (a, b, c, etc.) to index (0, 1, 2, etc.)
            String letterAnswer = answer.getAnswerText();
            if (letterAnswer != null && !letterAnswer.isEmpty()) {
                char letter = letterAnswer.toLowerCase().charAt(0);
                int index = letter - 'a';
                correctAnswersMap.put(answer.getQuestionId(), index);
            }
        }

        return correctAnswersMap;
    }
}
