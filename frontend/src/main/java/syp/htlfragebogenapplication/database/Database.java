package syp.htlfragebogenapplication.database;


import java.sql.Connection;

public class Database {
    private static Database instance;

    private  static final  String URL = "jdbc:h20:tcp://localhost:9092/./contactDb";
    private  final static String USER = "sa";
    private  final static String PASSWORD = "";

    private Connection connection;



}
