package syp.htlfragebogenapplication.database;

import syp.htlfragebogenapplication.model.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestRepository {
    private Connection connection;

    public TestRepository() {
        Database.getInstance();
        connection = Database.getConnection();
    }

    public List<Test> getAllTests() {
        List<Test> testList = new ArrayList<>();
        String sql = "SELECT id, name, description, question_count FROM Test";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)
        ) {
            while (rs.next()) {
                testList.add(new Test(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("question_count")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return testList;
    }

    public Test getTestById(int id) {
        String sql = "SELECT id, name, description, question_count FROM Test WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Test(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("question_count")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
