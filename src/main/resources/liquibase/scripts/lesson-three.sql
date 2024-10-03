--liquibase formatted sql

--changeset katrin_skt:1
CREATE INDEX student_name_index on student (name);
--changeset katrin_skt:2
CREATE INDEX faculties_nc_idx ON faculties (name, color);