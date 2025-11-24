-- V1__create_initial_test_table.sql
-- Initial setup - Create a test table to verify Flyway is working

CREATE TABLE IF NOT EXISTS flyway_test (
    id SERIAL PRIMARY KEY,
    message VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO flyway_test (message) VALUES ('Flyway is working!');
