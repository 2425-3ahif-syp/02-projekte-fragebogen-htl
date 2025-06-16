module at.java.frontendevaluationapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens at.java.frontendevaluationapp to javafx.fxml;
    exports at.java.frontendevaluationapp;
}