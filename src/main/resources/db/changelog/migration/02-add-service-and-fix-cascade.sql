--liquibase formatted sql

--changeset brayan:02-add-service-and-fix-permissions-cascade

-- 1. Add Service Column to Users
ALTER TABLE users ADD COLUMN service VARCHAR(50);

-- 2. FIX CASCADE FOR ROLE_PERMISSIONS
-- First, find and drop the existing constraint (Name might vary, so we drop by column logic if possible, 
-- but usually in standard SQL we need the name. Liquibase/Hibernate usually names it standardly).
-- We assume standard constraint names or force drop/recreate. 
-- In PostgreSQL, we can try dropping the FK on permission_id.

ALTER TABLE role_permissions DROP CONSTRAINT IF EXISTS role_permissions_permission_id_fkey;

ALTER TABLE role_permissions 
ADD CONSTRAINT role_permissions_permission_id_fkey 
FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE;

-- 3. FIX CASCADE FOR USER_PERMISSIONS
ALTER TABLE user_permissions DROP CONSTRAINT IF EXISTS user_permissions_permission_id_fkey;

ALTER TABLE user_permissions 
ADD CONSTRAINT user_permissions_permission_id_fkey 
FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE;