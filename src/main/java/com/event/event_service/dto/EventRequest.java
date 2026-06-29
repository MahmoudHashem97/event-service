package com.event.event_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EventRequest
{
    @NotBlank
    private String name;
    @NotBlank
    private String description;
}
