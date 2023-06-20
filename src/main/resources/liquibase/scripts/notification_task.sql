--liquibase formatted sql

--changeset EkaterinaPetro:1
CREATE TABLE notification_task (
    id SERIAL PRIMARY KEY,
    chat_id INTEGER,
    message_text TEXT,
    date_time TIMESTAMP
);