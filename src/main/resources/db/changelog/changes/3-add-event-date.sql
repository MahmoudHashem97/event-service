--liquibase formatted sql

--changeset mahmoud:003-add-event-date
ALTER TABLE event
    ADD COLUMN event_date DATE NOT NULL DEFAULT CURRENT_DATE;

--rollback ALTER TABLE event DROP COLUMN event_date;
