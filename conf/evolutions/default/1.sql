# Background-check schema

# --- !Ups


CREATE TABLE probes (
    id UUID NOT NULL PRIMARY KEY,
    request_id VARCHAR(255) NOT NULL,
    request JSONB NOT NULL,
    response JSONB,
    callback_url TEXT,
    executed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE provider_requests (
    id UUID NOT NULL PRIMARY KEY,
    probe_id UUID NOT NULL REFERENCES probes (id),
    provider_name VARCHAR(255) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    request JSONB,
    response JSONB,
    processed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_probes_request_id ON probes (request_id);

CREATE INDEX idx_provider_requests_provider_id ON provider_requests (provider_id);

# --- !Downs

DROP TABLE provider_requests CASCADE;
DROP TABLE probes CASCADE;

