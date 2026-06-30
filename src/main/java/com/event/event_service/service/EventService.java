package com.event.event_service.service;

import com.event.event_service.dto.EventRequest;
import com.event.event_service.dto.EventReservationRequest;
import com.event.event_service.dto.EventReservationResponse;
import com.event.event_service.dto.EventResponse;

import java.util.List;

public interface EventService
{
    EventResponse create(EventRequest request);

    List<EventResponse> findAll();

    EventResponse findById(Long id);

    EventResponse update(Long id, EventRequest request);

    EventResponse updateStatus(Long id, String state);

    EventReservationResponse reserveSeats(EventReservationRequest request);
}
