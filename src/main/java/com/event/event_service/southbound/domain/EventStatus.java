package com.event.event_service.southbound.domain;

import lombok.Getter;

@Getter
public enum EventStatus
{
    DRAFT("DRAFT"),
    PUBLISHED("PUBLISHED"),
    CANCELLED("CANCELLED");

    private final String code;

    EventStatus(String code)
    {
        this.code = code;
    }
}
