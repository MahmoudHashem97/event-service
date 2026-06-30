package com.event.event_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EventReservationRequest
{
    @NotNull
    @Positive
    private Integer noOfSeats;

    @NotNull
    private Long eventId;
}
