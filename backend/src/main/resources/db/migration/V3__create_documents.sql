CREATE TABLE document_types (
    document_type_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO document_types (name) VALUES
    ('Budget Report'), ('Memo'), ('Policy'), ('Legal Filing'), ('Correspondence');

CREATE TABLE reference_counters (
    department_id BIGINT NOT NULL,
    ref_year SMALLINT NOT NULL,
    last_number INT NOT NULL,
    PRIMARY KEY (department_id, ref_year),
    CONSTRAINT fk_refcounter_department FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

CREATE TABLE documents (
    document_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    reference_no VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(500) NOT NULL,
    document_type_id BIGINT NOT NULL,
    remarks TEXT,
    origin_department_id BIGINT NOT NULL,
    created_by_user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    completed_at DATETIME(6),
    CONSTRAINT fk_documents_type FOREIGN KEY (document_type_id) REFERENCES document_types(document_type_id),
    CONSTRAINT fk_documents_origin FOREIGN KEY (origin_department_id) REFERENCES departments(department_id),
    CONSTRAINT fk_documents_creator FOREIGN KEY (created_by_user_id) REFERENCES users(user_id),
    INDEX idx_documents_status (status),
    INDEX idx_documents_origin (origin_department_id),
    INDEX idx_documents_updated (updated_at)
);