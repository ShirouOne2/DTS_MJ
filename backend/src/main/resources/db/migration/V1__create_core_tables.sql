CREATE TABLE departments (
    department_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE users (
    user_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    department_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_users_department FOREIGN KEY (department_id) REFERENCES departments(department_id),
    INDEX idx_users_role (role),
    INDEX idx_users_is_active (is_active)
);

CREATE TABLE action_codes (
    code VARCHAR(10) NOT NULL PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    requires_signature BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO action_codes (code, label, requires_signature) VALUES
    ('FYAA',  'For Your Appropriate Action', FALSE),
    ('FCOM',  'For Comment', FALSE),
    ('FCON',  'For Consideration', FALSE),
    ('FS/A',  'For Signature/Approval', TRUE),
    ('FYI/F', 'For Information/File', FALSE),
    ('FDREP', 'For Drafting of Reply', FALSE),
    ('FMON',  'For Monitoring', FALSE),
    ('OTHR',  'Others', FALSE);