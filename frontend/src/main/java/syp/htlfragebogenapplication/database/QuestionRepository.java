package syp.htlfragebogenapplication.database;

import syp.htlfragebogenapplication.model.AnswerType;
import syp.htlfragebogenapplication.model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QuestionRepository {
    private Connection connection;

    public QuestionRepository() {
        Database.getInstance();
        connection = Database.getConnection();
    }

    public List<Question> getAllQuestionsFromTest(int testId) {
        List<Question> questionList = new ArrayList<>();
        String sql = """
                SELECT q.id, q.test_id, q.answer_type_id, q.image_path, q.possible_answer_count, q.num_in_test,
                       a.description AS answer_type_name
                FROM question q
                JOIN AnswerType a ON q.answer_type_id = a.id
                WHERE q.test_id = ?
                """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, testId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AnswerType answerType = new AnswerType(
                        rs.getInt("answer_type_id"),
                        rs.getString("answer_type_name")
                );
                questionList.add(new Question(
                        rs.getInt("id"),
                        rs.getInt("test_id"),
                        rs.getInt("answer_type_id"),
                        rs.getString("image_path"),
                        rs.getInt("possible_answer_count"),
                        rs.getInt("num_in_test"),
                        answerType
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questionList.stream()
                .sorted(Comparator.comparingInt(Question::getNumInTest))
                .toList();    }
}
