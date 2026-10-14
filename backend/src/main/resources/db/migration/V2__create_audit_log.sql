CREATE TABLE audit_logs (
    audit_log_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    actor_username VARCHAR(100),
    action VARCHAR(40) NOT NULL,
    entity_type VARCHAR(40),
    entity_id BIGINT,
    details VARCHAR(1000),
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at DATETIME(6) NOT NULL,
    INDEX idx_audit_created (created_at),
    INDEX idx_audit_actor (actor_username),
    INDEX idx_audit_action (action)
);