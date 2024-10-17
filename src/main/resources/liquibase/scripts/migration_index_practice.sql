-- liquibase formatted sql

-- changeset SlavaMarchkov:1
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_catalog.pg_tables t INNER JOIN pg_indexes pi ON t.tablename = pi.tablename WHERE t.tablename = 'students' AND pi.indexname = 'students_name_index'
-- rollback DROP INDEX students_name_index
CREATE INDEX students_name_index ON students (name);

-- changeset SlavaMarchkov:2
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_catalog.pg_tables t INNER JOIN pg_indexes pi ON t.tablename = pi.tablename WHERE t.tablename = 'faculties' AND pi.indexname = 'faculties_name_color_index'
-- rollback DROP INDEX faculties_name_color_index
CREATE INDEX faculties_name_color_index ON faculties (name, color);