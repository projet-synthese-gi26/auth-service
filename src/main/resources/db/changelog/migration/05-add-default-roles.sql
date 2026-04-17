--liquibase formatted sql

--changeset brayan:05-add-default-roles
INSERT INTO roles (id, name) VALUES ('336f15c1-f458-4587-af7c-9befcd5826fb', 'DRIVER');
INSERT INTO roles (id, name) VALUES ('f8141049-1af8-416f-a8c8-3f9cc4c364b3', 'CLIENT');
