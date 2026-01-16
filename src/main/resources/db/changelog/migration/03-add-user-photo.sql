--liquibase formatted sql

--changeset brayan:03-add-user-photo-columns

ALTER TABLE users 
ADD COLUMN IF NOT EXISTS photo_id UUID,
ADD COLUMN IF NOT EXISTS photo_uri VARCHAR(512);