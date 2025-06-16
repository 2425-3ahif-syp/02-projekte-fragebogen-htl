module syp.htlfragebogenapplication {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.h2database;
    requires transitive javafx.graphics;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    exports syp.htlfragebogenapplication.controllers;
    exports syp.htlfragebogenapplication.view;
    exports syp.htlfragebogenapplication.model;

    exports syp.htlfragebogenapplication;
    exports syp.htlfragebogenapplication.database;

    opens syp.htlfragebogenapplication.controllers to javafx.fxml;
}