package syp.htlfragebogenapplication.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Answer {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty questionId = new SimpleIntegerProperty();
    private final StringProperty answerText = new SimpleStringProperty();

    public Answer(int id, int questionId, String answerText) {
        this.id.set(id);
        this.questionId.set(questionId);
        this.answerText.set(answerText);
    }

    public int getId() {
        return id.get();
    }

    public int getQuestionId() {
        return questionId.get();
    }

    public String getAnswerText() {
        return answerText.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public IntegerProperty questionIdProperty() {
        return questionId;
    }

    public StringProperty answerTextProperty() {
        return answerText;
    }
}
