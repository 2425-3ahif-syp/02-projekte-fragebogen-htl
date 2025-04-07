package syp.htlfragebogenapplication.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Question {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty testId = new SimpleIntegerProperty();
    private final IntegerProperty answerTypeId = new SimpleIntegerProperty();
    private final StringProperty imagePath = new SimpleStringProperty();
    private final IntegerProperty possibleAnswerCount = new SimpleIntegerProperty();
    private final IntegerProperty numInTest = new SimpleIntegerProperty();

    private AnswerType answerType;

    public Question(int id, int testId, int answerTypeId, String imagePath, int possibleAnswerCount, int numInTest, AnswerType answerType) {
        this.id.set(id);
        this.testId.set(testId);
        this.answerTypeId.set(answerTypeId);
        this.imagePath.set(imagePath);
        this.possibleAnswerCount.set(possibleAnswerCount);
        this.numInTest.set(numInTest);
        this.answerType = answerType;
    }

    public int getId() { return id.get(); }
    public int getTestId() { return testId.get(); }
    public int getAnswerTypeId() { return answerTypeId.get(); }
    public String getImagePath() { return imagePath.get(); }
    public int getPossibleAnswerCount() { return possibleAnswerCount.get(); }
    public int getNumInTest() { return numInTest.get(); }

    public IntegerProperty idProperty() { return id; }
    public IntegerProperty testIdProperty() { return testId; }
    public IntegerProperty answerTypeIdProperty() { return answerTypeId; }
    public StringProperty imagePathProperty() { return imagePath; }
    public IntegerProperty possibleAnswerCountProperty() { return possibleAnswerCount; }
    public IntegerProperty numInTestProperty() { return numInTest; }
}
