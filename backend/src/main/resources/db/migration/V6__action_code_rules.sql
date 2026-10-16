ALTER TABLE action_codes
    ADD COLUMN file_rule VARCHAR(10) NOT NULL DEFAULT 'NONE',
    ADD COLUMN file_kind VARCHAR(20),
    ADD COLUMN instruction_required BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE action_codes SET file_rule = 'OPTIONAL', file_kind = 'SIGNED'      WHERE code = 'FS/A';
UPDATE action_codes SET file_rule = 'OPTIONAL', file_kind = 'EDITED'      WHERE code = 'FYAA';
UPDATE action_codes SET file_rule = 'REQUIRED', file_kind = 'DRAFT_REPLY' WHERE code = 'FDREP';
UPDATE action_codes SET file_rule = 'OPTIONAL', file_kind = 'ATTACHMENT', instruction_required = TRUE
    WHERE code = 'OTHR';

-- The file a department attaches isn't always a signed copy any more.
ALTER TABLE route_steps RENAME COLUMN signed_version_id TO result_version_id;

-- Fast lookup for the duplicate check.
CREATE INDEX idx_versions_sha256 ON document_versions (sha256, size_bytes);