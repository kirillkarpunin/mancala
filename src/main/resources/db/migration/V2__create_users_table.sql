CREATE TABLE users
(
    id       UUID PRIMARY KEY,
    username VARCHAR(32) NOT NULL,
    password VARCHAR(128) NOT NULL
);
