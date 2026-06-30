package com.event.event_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EventReservationResponse
{
    private Long eventId;
    private String eventName;
    private LocalDate eventDate;
}
