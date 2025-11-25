--liquibase formatted sql

--changeset brayan:01-create-initial-tables

-- 1. PERMISSIONS
CREATE TABLE permissions (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

-- 2. ROLES
CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- 3. ROLE_PERMISSIONS (Many-to-Many)
CREATE TABLE role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

-- 4. USERS
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(255) UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP
);

-- 5. USER_ROLES (Many-to-Many)
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 6. USER_PERMISSIONS (Many-to-Many)
CREATE TABLE user_permissions (
    user_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (user_id, permission_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

-- 7. REFRESH TOKENS
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    token TEXT NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    expiry TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 8. INSERT DEFAULT DATA (Optional but recommended)
INSERT INTO roles (id, name) VALUES ('d8a0c2c1-2f08-4e12-b054-054523955677', 'USER');
INSERT INTO roles (id, name) VALUES ('e2d0f9a2-4a02-4b33-a021-932131234981', 'ADMIN');