module syp.htlfragebogenapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    exports syp.htlfragebogenapplication.controllers to javafx.fxml;


    exports syp.htlfragebogenapplication;


    opens syp.htlfragebogenapplication.controllers to javafx.fxml;
}