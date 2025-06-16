module at.java.frontendevluationapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    exports at.java.frontendevluationapp.model to com.fasterxml.jackson.databind;
    opens at.java.frontendevluationapp to javafx.fxml;
    exports at.java.frontendevluationapp;
}