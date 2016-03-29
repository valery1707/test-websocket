CREATE TABLE account (
		id       IDENTITY     NOT NULL CONSTRAINT pk$account PRIMARY KEY,
		email    VARCHAR(256) NOT NULL CONSTRAINT uq$account$email UNIQUE,
		password VARCHAR(32)  NOT NULL
);
COMMENT ON TABLE account IS 'Account list';

CREATE TABLE token (
		id         IDENTITY  NOT NULL CONSTRAINT pk$token PRIMARY KEY,
		account_id INT8      NOT NULL CONSTRAINT fk$token$account_id REFERENCES account,
		token      UUID      NOT NULL,
		expiration TIMESTAMP NOT NULL
);

INSERT INTO account (email, password) VALUES
		('fpi@bk.ru', '123123'),
		('admin', '1q2w3e4r');

INSERT INTO token (account_id, token, expiration) VALUES
		((SELECT id FROM account WHERE email = 'fpi@bk.ru'), '00000000-0000-0000-0000-000000000000', NOW());
