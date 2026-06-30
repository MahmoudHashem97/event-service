package com.event.event_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EventRequest
{
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private LocalDate eventDate;

    @NotNull
    @PositiveOrZero
    private Integer availability;
}
