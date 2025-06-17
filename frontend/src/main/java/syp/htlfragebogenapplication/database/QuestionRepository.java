package syp.htlfragebogenapplication.database;

import syp.htlfragebogenapplication.model.AnswerType;
import syp.htlfragebogenapplication.model.Question;
import syp.htlfragebogenapplication.model.Test;

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuestionRepository {
    private Connection connection;

    public QuestionRepository() {
        Database.getInstance();
        connection = Database.getConnection();
    }

    public List<Question> getAllQuestions() {
        var testRepository = new TestRepository();
        List<Test> tests = testRepository.getAllTests();

        // Create a map of testId to Test object for quick lookup
        Map<Integer, Test> testMap = tests.stream()
                .collect(Collectors.toMap(Test::getId, Function.identity()));

        List<Question> questionList = new ArrayList<>();

        String sql = """
            SELECT q.id, q.test_id, q.answer_type_id, q.image_path, q.possible_answer_count, q.num_in_test,
                   a.description AS answer_type_name
            FROM question q
            JOIN AnswerType a ON q.answer_type_id = a.id
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int testId = rs.getInt("test_id");

                Test matchingTest = testMap.get(testId);
                if (matchingTest == null) {
                    System.err.println("Warning: No test found for test_id: " + testId);
                    continue;
                }

                AnswerType answerType = new AnswerType(
                        rs.getInt("answer_type_id"),
                        rs.getString("answer_type_name")
                );

                Question question = new Question(
                        rs.getInt("id"),
                        matchingTest,
                        rs.getInt("answer_type_id"),
                        rs.getString("image_path"),
                        rs.getInt("possible_answer_count"),
                        rs.getInt("num_in_test"),
                        answerType
                );

                questionList.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questionList.stream()
                .sorted(Comparator.comparingInt(Question::getNumInTest))
                .toList();
    }


    public List<Question> getAllQuestionsFromTest(int testId) {
        var testRepository = new TestRepository();
        List<Test> tests = testRepository.getAllTests();

        // Find the test with the matching ID
        Test matchingTest = tests.stream()
                .filter(t -> t.getId() == testId)
                .findFirst()
                .orElse(null); // Or throw an exception if test must exist

        if (matchingTest == null) {
            System.err.println("No test found with ID: " + testId);
            return Collections.emptyList();
        }

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

                Question question = new Question(
                        rs.getInt("id"),
                        matchingTest,  // use the correct Test object
                        rs.getInt("answer_type_id"),
                        rs.getString("image_path"),
                        rs.getInt("possible_answer_count"),
                        rs.getInt("num_in_test"),
                        answerType
                );

                questionList.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questionList.stream()
                .sorted(Comparator.comparingInt(Question::getNumInTest))
                .toList();
    }
}
