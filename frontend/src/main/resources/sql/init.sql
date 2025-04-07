-- Drop existing tables (in correct order to avoid FK constraint issues)
DROP TABLE IF EXISTS Answer;
DROP TABLE IF EXISTS Question;
DROP TABLE IF EXISTS AnswerType;
DROP TABLE IF EXISTS Test;

-- Create Test table
CREATE TABLE Test
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(255) NOT NULL,
    description    VARCHAR(255),
    question_count INT CHECK (question_count >= 0)
);

-- Create AnswerType table
CREATE TABLE AnswerType
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255) NOT NULL
);

-- Create Question table
CREATE TABLE Question
(
    id                    INT PRIMARY KEY AUTO_INCREMENT,
    test_id               INT NOT NULL,
    answer_type_id        INT NOT NULL,
    image_path            VARCHAR(255),
    possible_answer_count INT CHECK (possible_answer_count > 0),
    num_in_test           INT NOT NULL,
    FOREIGN KEY (test_id) REFERENCES Test (id) ON DELETE CASCADE,
    FOREIGN KEY (answer_type_id) REFERENCES AnswerType (id) ON DELETE CASCADE
);

-- Create Answer table
CREATE TABLE Answer
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT          NOT NULL,
    answer_text VARCHAR(255) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES Question (id) ON DELETE CASCADE
);
