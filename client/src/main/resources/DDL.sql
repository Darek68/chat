



CREATE TABLE homework.students (
                                   "student-id" int4 NOT NULL,
                                   "name" varchar NOT NULL,
                                   surname varchar NOT NULL,
                                   patronymic varchar NULL,
                                   CONSTRAINT students_pk PRIMARY KEY ("student-id")
);
CREATE UNIQUE INDEX students_name_idx ON homework.students USING btree (name, surname, patronymic);
CREATE UNIQUE INDEX students_student_id_idx ON homework.students USING btree ("student-id");



CREATE TABLE homework.tests (
                                "test-id" int4 NOT NULL,
                                "name" varchar NOT NULL,
                                "desc" varchar NULL,
                                CONSTRAINT tests_pk PRIMARY KEY ("test-id")
);
CREATE UNIQUE INDEX tests_name_idx ON homework.tests USING btree (name);
CREATE UNIQUE INDEX tests_test_id_idx ON homework.tests USING btree ("test-id");



CREATE TABLE homework.questions (
                                    "quest-id" int4 NOT NULL,
                                    "test-id" int4 NOT NULL,
                                    question varchar NOT NULL,
                                    CONSTRAINT questions_pk PRIMARY KEY ("quest-id", "test-id")
);
CREATE UNIQUE INDEX questions_quest_id_idx ON homework.questions USING btree ("quest-id", "test-id");
ALTER TABLE homework.questions ADD CONSTRAINT questions_tests_fk FOREIGN KEY ("test-id") REFERENCES homework.tests("test-id");



CREATE TABLE homework.answers (
                                  "test-id" int4 NOT NULL,
                                  "quest-id" int4 NOT NULL,
                                  "ans-id" int4 NOT NULL,
                                  answer varchar NOT NULL,
                                  CONSTRAINT answers_pk PRIMARY KEY ("test-id", "quest-id", "ans-id")
);
CREATE UNIQUE INDEX answers_test_it_idx ON homework.answers USING btree ("test-id", "quest-id", "ans-id");
-- homework.answers внешние включи
ALTER TABLE homework.answers ADD CONSTRAINT answers_questions_fk FOREIGN KEY ("quest-id","test-id") REFERENCES homework.questions("quest-id","test-id");
ALTER TABLE homework.answers ADD CONSTRAINT answers_tests_fk FOREIGN KEY ("test-id") REFERENCES homework.tests("test-id");



CREATE TABLE homework.passed_tests (
                                       "pstest-id" int4 NOT NULL,
                                       "student-id" int4 NOT NULL,
                                       "date" date NOT NULL,
                                       "test-id" int4 NOT NULL,
                                       CONSTRAINT passed_tests_pk PRIMARY KEY ("pstest-id")
);
CREATE UNIQUE INDEX passed_tests_pstest_id_idx ON homework.passed_tests USING btree ("pstest-id");
CREATE UNIQUE INDEX passed_tests_student_id_idx ON homework.passed_tests USING btree ("student-id", "test-id", date);
-- homework.passed_tests внешние включи
ALTER TABLE homework.passed_tests ADD CONSTRAINT passed_tests_students_fk FOREIGN KEY ("student-id") REFERENCES homework.students("student-id");
ALTER TABLE homework.passed_tests ADD CONSTRAINT passed_tests_tests_fk FOREIGN KEY ("test-id") REFERENCES homework.tests("test-id");



CREATE TABLE homework.answers (
                                  "test-id" int4 NOT NULL,
                                  "quest-id" int4 NOT NULL,
                                  "ans-id" int4 NOT NULL,
                                  answer varchar NOT NULL,
                                  CONSTRAINT answers_pk PRIMARY KEY ("test-id", "quest-id", "ans-id")
);
CREATE UNIQUE INDEX answers_test_it_idx ON homework.answers USING btree ("test-id", "quest-id", "ans-id");
-- homework.answers внешние включи
ALTER TABLE homework.answers ADD CONSTRAINT answers_questions_fk FOREIGN KEY ("quest-id","test-id") REFERENCES homework.questions("quest-id","test-id");
ALTER TABLE homework.answers ADD CONSTRAINT answers_tests_fk FOREIGN KEY ("test-id") REFERENCES homework.tests("test-id");



CREATE TABLE homework.select_answers (
                                         "pstest-id" int4 NOT NULL,
                                         "quest-id" int4 NOT NULL,
                                         "ans-id" int4 NULL,
                                         CONSTRAINT select_answers_pk PRIMARY KEY ("pstest-id", "quest-id")
);
CREATE UNIQUE INDEX select_answers_pstest_id_idx ON homework.select_answers USING btree ("pstest-id", "quest-id");
-- homework.select_answers внешние включи
ALTER TABLE homework.select_answers ADD CONSTRAINT select_answers_passed_tests_fk FOREIGN KEY ("pstest-id") REFERENCES homework.passed_tests("pstest-id");

-- Поле select_answers.quest-id нельзя связать внешним ключом с answers.quest-id т.к. выйти на конкретный тест надо через
-- pstest-id ->  passed_tests.pstest-id и оттуда получить test-id. Имея test-id и quest-id можем вывести вопрос questions.answer
-- и  получить список ответов (для анализа результата). Это усложняет логику но исключает избыточность данных.
-- Возможно добавить поле - признак верного ответа в таблицу answers, для автоматизации проверки.