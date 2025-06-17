package syp.htlfragebogenapplication.model;

import javafx.beans.property.*;

public class Answer {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty answerText = new SimpleStringProperty();

    private Question question;

    public Answer(int id, Question question, String answerText) {
        this.id.set(id);
        this.question = question;
        this.answerText.set(answerText);
    }

    public int getId() {
        return id.get();
    }

    public int getQuestionId() {
        return question.getId();
    }

    public String getAnswerText() {
        return answerText.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty answerTextProperty() {
        return answerText;
    }
}
