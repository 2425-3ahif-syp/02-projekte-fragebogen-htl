package syp.htlfragebogenapplication.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Test {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty questionCount = new SimpleIntegerProperty();

    public Test(int id, String name, String description, int questionCount) {
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
        this.questionCount.set(questionCount);
    }


    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getDescription() { return description.get(); }
    public int getQuestionCount() { return questionCount.get(); }

    // Property getters (for binding)
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public IntegerProperty questionCountProperty() { return questionCount; }

    @Override
    public String toString() {
        return "Test{id=" + getId() + ", name='" + getName() + "', description='" + getDescription() + "', questionCount=" + getQuestionCount() + "}";
    }
}
