package com.event.event_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponse
{
    private Long id;
    private String name;
    private String description;
    private String state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
