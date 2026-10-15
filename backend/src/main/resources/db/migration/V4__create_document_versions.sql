CREATE TABLE document_versions (
    version_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    version_no INT NOT NULL,
    kind VARCHAR(20) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    sha256 CHAR(64) NOT NULL,
    size_bytes BIGINT NOT NULL,
    uploaded_by_user_id BIGINT NOT NULL,
    uploaded_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_versions_document FOREIGN KEY (document_id) REFERENCES documents(document_id),
    CONSTRAINT fk_versions_user FOREIGN KEY (uploaded_by_user_id) REFERENCES users(user_id),
    CONSTRAINT uq_version_doc_no UNIQUE (document_id, version_no)
);