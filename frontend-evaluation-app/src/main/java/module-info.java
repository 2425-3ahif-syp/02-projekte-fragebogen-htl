module at.java.frontendevaluationapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    exports at.java.frontendevaluationapp.model to com.fasterxml.jackson.databind;
    opens at.java.frontendevaluationapp to javafx.fxml;
    exports at.java.frontendevaluationapp;
}