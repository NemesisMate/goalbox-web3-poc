CREATE TABLE IF NOT EXISTS goalbox_metadata(
    id uuid DEFAULT gen_random_uuid (),
    name VARCHAR(24) NOT NULL,
    owner_address VARCHAR(42) NOT NULL,
    contract_address VARCHAR(42) NOT NULL,
    goal NUMERIC NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name, owner_address)
);