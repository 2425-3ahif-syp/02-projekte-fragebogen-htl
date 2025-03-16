package syp.htlfragebogenapplication.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    public static void initialize() {
        Connection conn = Database.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null. Initialization aborted.");
            return;
        }

        executeSQLFile(conn, "/sql/init.sql");  // Drops & creates tables
        importCSVData(conn);                    // Reads CSV files & inserts data
        System.out.println("Database initialization complete.");
    }

    private static void executeSQLFile(Connection conn, String filePath) {
        try (Statement stmt = conn.createStatement()) {
            InputStream inputStream = DatabaseInitializer.class.getResourceAsStream(filePath);
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + filePath);
            }

            String sql = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));

            for (String statement : sql.split(";")) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement.trim());
                }
            }
            System.out.println("Executed SQL script: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void importCSVData(Connection conn) {
        importCSV(conn, "/csv/test.csv", "INSERT INTO Test (id, name, description, question_count) VALUES (?, ?, ?, ?)");
        importCSV(conn, "/csv/answer_type.csv", "INSERT INTO AnswerType (id, description) VALUES (?, ?)");
        importCSV(conn, "/csv/question.csv", "INSERT INTO Question (id, test_id, answer_type_id, image_path, possible_answer_count) VALUES (?, ?, ?, ?, ?)");
        importCSV(conn, "/csv/answer.csv", "INSERT INTO Answer (id, question_id, answer_text) VALUES (?, ?, ?)");
    }

    private static void importCSV(Connection conn, String filePath, String sql) {
        try (InputStream inputStream = DatabaseInitializer.class.getResourceAsStream(filePath)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                long insertedRows = reader.lines().skip(1).filter(line -> !line.trim().isEmpty()).map(line -> {
                    try {
                        String[] values = line.split(",");
                        for (int i = 0; i < values.length; i++) {
                            pstmt.setString(i + 1, values[i].trim());
                        }
                        return pstmt.executeUpdate();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                }).filter(rows -> rows > 0).count();

                System.out.println("Imported " + insertedRows + " rows from: " + filePath);            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
