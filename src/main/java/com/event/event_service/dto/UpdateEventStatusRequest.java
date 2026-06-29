package com.event.event_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateEventStatusRequest
{
    @NotBlank
    @Pattern(regexp = "DRAFT|PUBLISHED|CANCELLED",
            message = "state must be one of: DRAFT, PUBLISHED, CANCELLED")
    private String state;
}
