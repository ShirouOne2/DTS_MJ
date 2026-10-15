ALTER TABLE action_codes ADD COLUMN is_advisory BOOLEAN NOT NULL DEFAULT FALSE;
UPDATE action_codes SET is_advisory = TRUE WHERE code = 'FYI/F';

CREATE TABLE routes (
    route_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    round_no INT NOT NULL,
    route_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    version_id BIGINT NOT NULL,
    submitted_by_user_id BIGINT NOT NULL,
    remarks VARCHAR(1000),
    started_at DATETIME(6) NOT NULL,
    ended_at DATETIME(6),
    CONSTRAINT fk_routes_document FOREIGN KEY (document_id) REFERENCES documents(document_id),
    CONSTRAINT fk_routes_version FOREIGN KEY (version_id) REFERENCES document_versions(version_id),
    CONSTRAINT fk_routes_user FOREIGN KEY (submitted_by_user_id) REFERENCES users(user_id),
    CONSTRAINT uq_route_doc_round UNIQUE (document_id, round_no)
);

CREATE TABLE route_steps (
    step_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    route_id BIGINT NOT NULL,
    step_order INT NOT NULL,
    department_id BIGINT NOT NULL,
    action_code VARCHAR(10) NOT NULL,
    instruction VARCHAR(500),
    due_date DATE,
    status VARCHAR(20) NOT NULL,
    decision VARCHAR(20),
    activated_at DATETIME(6),
    received_by_user_id BIGINT,
    received_at DATETIME(6),
    decided_by_user_id BIGINT,
    decided_at DATETIME(6),
    decision_remarks VARCHAR(1000),
    signed_version_id BIGINT,
    CONSTRAINT fk_steps_route FOREIGN KEY (route_id) REFERENCES routes(route_id),
    CONSTRAINT fk_steps_department FOREIGN KEY (department_id) REFERENCES departments(department_id),
    CONSTRAINT fk_steps_action FOREIGN KEY (action_code) REFERENCES action_codes(code),
    CONSTRAINT fk_steps_received_by FOREIGN KEY (received_by_user_id) REFERENCES users(user_id),
    CONSTRAINT fk_steps_decided_by FOREIGN KEY (decided_by_user_id) REFERENCES users(user_id),
    CONSTRAINT fk_steps_signed_version FOREIGN KEY (signed_version_id) REFERENCES document_versions(version_id),
    INDEX idx_steps_department_status (department_id, status),
    INDEX idx_steps_route (route_id)
);