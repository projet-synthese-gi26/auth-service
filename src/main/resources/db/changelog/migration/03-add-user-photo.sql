--liquibase formatted sql

--changeset brayan:03-add-user-photo-columns

ALTER TABLE users 
ADD COLUMN photo_id UUID,
ADD COLUMN photo_uri VARCHAR(512);