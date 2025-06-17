package syp.htlfragebogenapplication.model;

import javafx.beans.property.*;

public class Question {
    private final IntegerProperty id = new SimpleIntegerProperty();

    private final IntegerProperty answerTypeId = new SimpleIntegerProperty();
    private final StringProperty imagePath = new SimpleStringProperty();
    private final IntegerProperty possibleAnswerCount = new SimpleIntegerProperty();
    private final IntegerProperty numInTest = new SimpleIntegerProperty();

    private AnswerType answerType;
    private final Test test;

    public Question(int id, Test test, int answerTypeId, String imagePath, int possibleAnswerCount, int numInTest, AnswerType answerType) {
        this.id.set(id);
        this.test = test;
        this.answerTypeId.set(answerTypeId);
        this.imagePath.set(imagePath);
        this.possibleAnswerCount.set(possibleAnswerCount);
        this.numInTest.set(numInTest);
        this.answerType = answerType;
    }

    public int getId() { return id.get(); }
    public int getTestId() { return test.getId(); }
    public int getAnswerTypeId() { return answerTypeId.get(); }
    public String getImagePath() { return imagePath.get(); }
    public int getPossibleAnswerCount() { return possibleAnswerCount.get(); }
    public int getNumInTest() { return numInTest.get(); }
    public AnswerType getAnswerType() { return answerType; }

    public IntegerProperty idProperty() { return id; }
    public IntegerProperty answerTypeIdProperty() { return answerTypeId; }
    public StringProperty imagePathProperty() { return imagePath; }
    public IntegerProperty possibleAnswerCountProperty() { return possibleAnswerCount; }
    public IntegerProperty numInTestProperty() { return numInTest; }
}
