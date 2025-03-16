package syp.htlfragebogenapplication.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static Database instance;

    private  static final  String URL = "jdbc:h2:tcp://localhost:9092/./questionnaireDb";
    private  final static String USER = "sa";
    private  final static String PASSWORD = "";

    private static Connection connection;

    private Database() {
        try {
            connection = createConnection();
            initialize();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static Database getInstance() {
        if(instance == null) {
            synchronized (Database.class) {
                if(instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = createConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void initialize() {
        // TODO

    }

    public void closeConnection() {
        if(connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
