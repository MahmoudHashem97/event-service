--liquibase formatted sql

--changeset mahmoud:002-add-event-availability
ALTER TABLE event
    ADD COLUMN initial_availability INT NOT NULL DEFAULT 0,
    ADD COLUMN current_availability INT NOT NULL DEFAULT 0;

--rollback ALTER TABLE event DROP COLUMN current_availability, DROP COLUMN initial_availability;
