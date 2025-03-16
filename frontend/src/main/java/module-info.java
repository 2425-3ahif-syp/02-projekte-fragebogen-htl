module syp.htlfragebogenapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.h2database;


    exports syp.htlfragebogenapplication.controllers to javafx.fxml;


    exports syp.htlfragebogenapplication;
    exports syp.htlfragebogenapplication.database;


    opens syp.htlfragebogenapplication.controllers to javafx.fxml;
}