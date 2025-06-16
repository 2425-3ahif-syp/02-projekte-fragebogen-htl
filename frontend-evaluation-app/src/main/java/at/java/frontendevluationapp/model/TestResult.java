package at.java.frontendevluationapp.model;

import java.util.List;

public class TestResult {
    private String studentName;
    private int testId;
    private String testName;
    private Score score;
    private List<QuestionResult> questions;
    private String fileName;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public List<QuestionResult> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResult> questions) {
        this.questions = questions;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
