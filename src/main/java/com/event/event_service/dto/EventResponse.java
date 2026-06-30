package com.event.event_service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EventResponse
{
    private Long id;
    private String name;
    private String description;
    private LocalDate eventDate;
    private String state;
    private Integer initialAvailability;
    private Integer currentAvailability;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
