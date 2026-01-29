CREATE TABLE assignment_questions
(
    id                 UUID         NOT NULL,
    question_key       VARCHAR(255) NOT NULL,
    question_type      VARCHAR(255) NOT NULL,
    question_image_url TEXT         NOT NULL,
    correct_answer     VARCHAR(255),
    explain_image_url  TEXT,
    page               INTEGER,
    assignment_id      UUID         NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_assignment_questions PRIMARY KEY (id)
);

CREATE TABLE assignments
(
    id          UUID         NOT NULL,
    class_id    BIGINT       NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    deadline    TIMESTAMP WITHOUT TIME ZONE,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_assignments PRIMARY KEY (id)
);

ALTER TABLE assignments
    ADD CONSTRAINT FK_ASSIGNMENT_CLASS FOREIGN KEY (class_id) REFERENCES classes (id);

ALTER TABLE assignment_questions
    ADD CONSTRAINT FK_ASSIGNMENT_QUESTION_ASSIGNMENT FOREIGN KEY (assignment_id) REFERENCES assignments (id);