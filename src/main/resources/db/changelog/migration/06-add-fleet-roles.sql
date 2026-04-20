--liquibase formatted sql

--changeset brayan:06-add-fleet-roles
INSERT INTO roles (id, name) VALUES ('716e25c1-e458-4587-af7c-9befcd5826fa', 'FLEET_ADMIN');
INSERT INTO roles (id, name) VALUES ('a8141049-2af8-416f-a8c8-3f9cc4c364b3', 'FLEET_MANAGER');