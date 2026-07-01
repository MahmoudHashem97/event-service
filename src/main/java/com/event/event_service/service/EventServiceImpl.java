package com.event.event_service.service;

import com.event.event_service.dto.EventRequest;
import com.event.event_service.dto.EventReservationRequest;
import com.event.event_service.dto.EventReservationResponse;
import com.event.event_service.dto.EventResponse;
import com.event.event_service.exception.BusinessException;
import com.event.event_service.southbound.client.BookingClient;
import com.event.event_service.southbound.domain.Event;
import com.event.event_service.southbound.domain.EventStatus;
import com.event.event_service.southbound.mapper.EventMapper;
import com.event.event_service.southbound.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService
{

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final BookingClient bookingClient;

    @Override
    @Transactional
    public EventResponse create(EventRequest request)
    {
        Event event = eventMapper.toEntity(request);
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Override
    public List<EventResponse> findAll()
    {
        return eventMapper.toResponse(eventRepository.findAll());
    }

    @Override
    public EventResponse findById(Long id)
    {
        return eventMapper.toResponse(getEvent(id));
    }

    @Override
    @Transactional
    public EventResponse update(Long id, EventRequest request)
    {
        Event event = getEvent(id);
        int bookedSeats = Math.max(0, event.getInitialAvailability() - event.getCurrentAvailability());

        if (request.getAvailability() < bookedSeats)
        {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_EVENT_AVAILABILITY",
                    "Event availability cannot be lower than already booked seats");
        }

        eventMapper.updateEntity(request, event);
        event.setCurrentAvailability(request.getAvailability() - bookedSeats);
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional
    public EventResponse updateStatus(Long id, String state)
    {
        Event event = getEvent(id);
        EventStatus nextState = EventStatus.valueOf(state);

        if (!canMoveTo(event.getState(), nextState))
        {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "INVALID_EVENT_STATUS",
                    "Event cannot move from " + event.getState().getCode() + " to " + nextState.getCode()
            );
        }

        if (nextState == EventStatus.CANCELLED
                && !event.getCurrentAvailability().equals(event.getInitialAvailability()))
        {
            bookingClient.deleteByEventId(event.getId());
        }

        event.setState(nextState);
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional
    public EventReservationResponse reserveSeats(EventReservationRequest request)
    {
        Event event = getEvent(request.getEventId());

        int remainingAvailability = event.getCurrentAvailability() - request.getNoOfSeats();

        if (!isAvailableForReservation(event, remainingAvailability))
        {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "EVENT_NOT_AVAILABLE",
                    "event not available right now");
        }

        event.setCurrentAvailability(remainingAvailability);
        eventRepository.save(event);

        return eventMapper.toReservationResponse(event);
    }

    private Event getEvent(Long id)
    {
        return eventRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "EVENT_NOT_FOUND", "Event not found"));
    }

    private boolean canMoveTo(EventStatus current, EventStatus next)
    {
        if (current == next)
        {
            return true;
        }

        return (current == EventStatus.DRAFT && next == EventStatus.PUBLISHED)
                || (current == EventStatus.PUBLISHED && next == EventStatus.CANCELLED);
    }

    private boolean isAvailableForReservation(Event event, Integer remainingAvailability)
    {
        return event.getState() == EventStatus.PUBLISHED
                && remainingAvailability >= 0;
    }
}
