CREATE TABLE student_answers
(
    id          UUID   NOT NULL,
    student_id  BIGINT NOT NULL,
    question_id UUID   NOT NULL,
    judgement   TEXT,
    CONSTRAINT pk_student_answers PRIMARY KEY (id)
);

ALTER TABLE student_answers
    ADD CONSTRAINT FK_STUDENT_ANSWERS_ASSIGNMENT_QUESTIONS FOREIGN KEY (question_id) REFERENCES assignment_questions (id);

CREATE INDEX idx_student_answers_question_id ON student_answers (question_id);

ALTER TABLE student_answers
    ADD CONSTRAINT FK_STUDENT_ANSWERS_STUDENTS FOREIGN KEY (student_id) REFERENCES users (id);

CREATE INDEX idx_student_answers_student_id ON student_answers (student_id);