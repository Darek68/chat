



CREATE TABLE homework.students (
                                   student_id int4 NOT NULL,
                                   first_name varchar NOT NULL,
                                   last_name varchar NOT NULL,
                                   patronymic_name varchar NULL
);
CREATE UNIQUE INDEX students_name_idx ON homework.students  (name, surname, patronymic);
CREATE UNIQUE INDEX students_student_id_idx ON homework.students  (student_id);



CREATE TABLE homework.tests (
                                test_id int4 NOT NULL,
                                test_name varchar NOT NULL,
                                "desc" varchar NULL
);
CREATE UNIQUE INDEX tests_name_idx ON homework.tests  (name);
CREATE UNIQUE INDEX tests_test_id_idx ON homework.tests  (test_id);



CREATE TABLE homework.questions (
                                    quest_id int4 NOT NULL,
                                    test_id int4 NOT NULL,
                                    question varchar NOT NULL
);
CREATE UNIQUE INDEX questions_quest_id_idx ON homework.questions  (quest_id, test_id);
ALTER TABLE homework.questions ADD CONSTRAINT questions_tests_fk FOREIGN KEY (test_id) REFERENCES homework.tests(test_id);



CREATE TABLE homework.answers (
                                  test_id int4 NOT NULL,
                                  quest_id int4 NOT NULL,
                                  answer_id int4 NOT NULL,
                                  answer varchar NOT NULL
);
CREATE UNIQUE INDEX answers_test_it_idx ON homework.answers  (test_id, quest_id, answer_id);
-- homework.answers внешние включи
ALTER TABLE homework.answers ADD CONSTRAINT answers_questions_fk FOREIGN KEY (quest_id,test_id) REFERENCES homework.questions(quest_id,test_id);
ALTER TABLE homework.answers ADD CONSTRAINT answers_tests_fk FOREIGN KEY (test_id) REFERENCES homework.tests(test_id);



CREATE TABLE homework.passed_tests (
                                       passed_test_id int4 NOT NULL,
                                       student_id int4 NOT NULL,
                                       date date NOT NULL,
                                       test_id int4 NOT NULL
);
CREATE UNIQUE INDEX passed_tests_passed_test_id_idx ON homework.passed_tests  (passed_test_id);
CREATE UNIQUE INDEX passed_tests_student_id_idx ON homework.passed_tests  (student_id, test_id, date);
-- homework.passed_tests внешние включи
ALTER TABLE homework.passed_tests ADD CONSTRAINT passed_tests_students_fk FOREIGN KEY (student_id) REFERENCES homework.students(student_id);
ALTER TABLE homework.passed_tests ADD CONSTRAINT passed_tests_tests_fk FOREIGN KEY (test_id) REFERENCES homework.tests(test_id);


CREATE TABLE homework.select_answers (
                                         passed_test_id int4 NOT NULL,
                                         quest_id int4 NOT NULL,
                                         answer_id int4 NULL
);
CREATE UNIQUE INDEX select_answers_passed_test_id_idx ON homework.select_answers (passed_test_id, quest_id);
-- homework.select_answers внешние включи
ALTER TABLE homework.select_answers ADD CONSTRAINT select_answers_passed_tests_fk FOREIGN KEY (passed_test_id) REFERENCES homework.passed_tests(passed_test_id);

-- Поле select_answers.quest_id нельзя связать внешним ключом с answers.quest_id т.к. выйти на конкретный тест надо через
-- passed_test_id ->  passed_tests.passed_test_id и оттуда получить test_id. Имея test_id и quest_id можем вывести вопрос questions.answer
-- и  получить список ответов (для анализа результата). Это усложняет логику но исключает избыточность данных.
-- Возможно добавить поле - признак верного ответа в таблицу answers, для автоматизации проверки.