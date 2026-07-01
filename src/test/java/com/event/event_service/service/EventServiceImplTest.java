package com.event.event_service.service;

import com.event.event_service.dto.EventRequest;
import com.event.event_service.dto.EventReservationRequest;
import com.event.event_service.dto.EventReservationResponse;
import com.event.event_service.dto.EventResponse;
import com.event.event_service.exception.BusinessException;
import com.event.event_service.southbound.domain.Event;
import com.event.event_service.southbound.domain.EventStatus;
import com.event.event_service.southbound.mapper.EventMapper;
import com.event.event_service.southbound.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest
{
    private static final LocalDate EVENT_DATE = LocalDate.of(2026, 7, 15);

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void createShouldReturnCreatedEvent()
    {
        EventRequest request = buildRequest();
        Event event = buildEvent(12L, 37, 37);
        EventResponse response = buildResponse(12L, EventStatus.DRAFT, 37, 37);

        when(eventMapper.toEntity(request)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toResponse(event)).thenReturn(response);

        EventResponse result = eventService.create(request);

        assertEquals(response, result);
        verify(eventMapper).toEntity(request);
        verify(eventRepository).save(event);
        verify(eventMapper).toResponse(event);
    }

    @Test
    void updateShouldReturnUpdatedEvent()
    {
        Long id = 7L;
        EventRequest request = buildRequest();
        Event event = buildEvent(id, 25, 25);
        EventResponse response = buildResponse(id, EventStatus.DRAFT, 18, 18);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        when(eventMapper.toResponse(event)).thenReturn(response);

        EventResponse result = eventService.update(id, request);

        assertEquals(response, result);
        verify(eventRepository).findById(id);
        verify(eventMapper).updateEntity(request, event);
        verify(eventMapper).toResponse(event);
    }

    @Test
    void updateShouldPreserveBookedSeats()
    {
        Long id = 8L;
        EventRequest request = buildRequest();
        request.setAvailability(30);
        Event event = buildEvent(id, 20, 12);
        EventResponse response = buildResponse(id, EventStatus.DRAFT, 30, 22);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        doAnswer(invocation -> {
            EventRequest updateRequest = invocation.getArgument(0);
            Event eventToUpdate = invocation.getArgument(1);
            eventToUpdate.setName(updateRequest.getName());
            eventToUpdate.setDescription(updateRequest.getDescription());
            eventToUpdate.setEventDate(updateRequest.getEventDate());
            eventToUpdate.setInitialAvailability(updateRequest.getAvailability());
            eventToUpdate.setCurrentAvailability(updateRequest.getAvailability());
            return null;
        }).when(eventMapper).updateEntity(request, event);
        when(eventMapper.toResponse(event)).thenReturn(response);

        EventResponse result = eventService.update(id, request);

        assertEquals(30, event.getInitialAvailability());
        assertEquals(22, event.getCurrentAvailability());
        assertEquals(response, result);
        verify(eventRepository).findById(id);
        verify(eventMapper).updateEntity(request, event);
        verify(eventMapper).toResponse(event);
    }

    @Test
    void updateShouldThrowWhenAvailabilityIsLowerThanBookedSeats()
    {
        Long id = 9L;
        EventRequest request = buildRequest();
        request.setAvailability(7);
        Event event = buildEvent(id, 20, 12);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> eventService.update(id, request));

        assertEquals("INVALID_EVENT_AVAILABILITY", exception.getErrorCode());
        assertEquals(20, event.getInitialAvailability());
        assertEquals(12, event.getCurrentAvailability());
        verify(eventRepository).findById(id);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void findAllShouldReturnEvents()
    {
        Event event = buildEvent(3L, 14, 14);
        EventResponse response = buildResponse(3L, EventStatus.DRAFT, 14, 14);
        List<Event> events = List.of(event);
        List<EventResponse> responses = List.of(response);

        when(eventRepository.findAll()).thenReturn(events);
        when(eventMapper.toResponse(events)).thenReturn(responses);

        List<EventResponse> result = eventService.findAll();

        assertEquals(responses, result);
        verify(eventRepository).findAll();
        verify(eventMapper).toResponse(events);
    }

    @Test
    void findByIdShouldReturnEvent()
    {
        Long id = 19L;
        Event event = buildEvent(id, 9, 9);
        EventResponse response = buildResponse(id, EventStatus.DRAFT, 9, 9);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        when(eventMapper.toResponse(event)).thenReturn(response);

        EventResponse result = eventService.findById(id);

        assertEquals(response, result);
        verify(eventRepository).findById(id);
        verify(eventMapper).toResponse(event);
    }

    @Test
    void findByIdShouldThrowWhenEventNotFound()
    {
        Long id = 404L;

        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> eventService.findById(id));

        assertEquals("EVENT_NOT_FOUND", exception.getErrorCode());
        verify(eventRepository).findById(id);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void updateStatusShouldReturnPublishedEvent()
    {
        Long id = 22L;
        Event event = buildEvent(id, 6, 6);
        EventResponse response = buildResponse(id, EventStatus.PUBLISHED, 6, 6);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        when(eventMapper.toResponse(event)).thenReturn(response);

        EventResponse result = eventService.updateStatus(id, "PUBLISHED");

        assertEquals(EventStatus.PUBLISHED, event.getState());
        assertEquals(response, result);
        verify(eventRepository).findById(id);
        verify(eventMapper).toResponse(event);
    }

    @Test
    void updateStatusShouldReturnCancelledDraftEvent()
    {
        Long id = 31L;
        Event event = buildEvent(id, 11, 11);
        EventResponse response = buildResponse(id, EventStatus.CANCELLED, 11, 11);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        when(eventMapper.toResponse(event)).thenReturn(response);

        EventResponse result = eventService.updateStatus(id, "CANCELLED");

        assertEquals(EventStatus.CANCELLED, event.getState());
        assertEquals(response, result);
        verify(eventRepository).findById(id);
        verify(eventMapper).toResponse(event);
    }

    @Test
    void reserveSeatsShouldDecreaseAvailabilityAndReturnReservationResponse()
    {
        Long id = 41L;
        EventReservationRequest request = buildReservationRequest(id, 3);
        Event event = buildEvent(id, 10, 7);
        event.setState(EventStatus.PUBLISHED);
        EventReservationResponse response = buildReservationResponse(event);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toReservationResponse(event)).thenReturn(response);

        EventReservationResponse result = eventService.reserveSeats(request);

        assertEquals(4, event.getCurrentAvailability());
        assertEquals(response, result);
        verify(eventRepository).findById(id);
        verify(eventRepository).save(event);
        verify(eventMapper).toReservationResponse(event);
    }

    @Test
    void reserveSeatsShouldThrowWhenEventNotFound()
    {
        Long id = 404L;
        EventReservationRequest request = buildReservationRequest(id, 1);

        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> eventService.reserveSeats(request));

        assertEquals("EVENT_NOT_FOUND", exception.getErrorCode());
        verify(eventRepository).findById(id);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void reserveSeatsShouldThrowWhenSeatsAreNotAvailable()
    {
        Long id = 42L;
        EventReservationRequest request = buildReservationRequest(id, 8);
        Event event = buildEvent(id, 10, 7);
        event.setState(EventStatus.PUBLISHED);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> eventService.reserveSeats(request));

        assertEquals("EVENT_NOT_AVAILABLE", exception.getErrorCode());
        assertEquals(7, event.getCurrentAvailability());
        verify(eventRepository).findById(id);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void reserveSeatsShouldThrowWhenRemainingAvailabilityIsNotPositive()
    {
        Long id = 44L;
        EventReservationRequest request = buildReservationRequest(id, 8);
        Event event = buildEvent(id, 10, 7);
        event.setState(EventStatus.PUBLISHED);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> eventService.reserveSeats(request));

        assertEquals("EVENT_NOT_AVAILABLE", exception.getErrorCode());
        assertEquals(7, event.getCurrentAvailability());
        verify(eventRepository).findById(id);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void reserveSeatsShouldThrowWhenEventIsNotPublished()
    {
        Long id = 43L;
        EventReservationRequest request = buildReservationRequest(id, 1);
        Event event = buildEvent(id, 10, 7);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> eventService.reserveSeats(request));

        assertEquals("EVENT_NOT_AVAILABLE", exception.getErrorCode());
        assertEquals(7, event.getCurrentAvailability());
        verify(eventRepository).findById(id);
        verify(eventRepository, never()).save(any());
    }

    private EventRequest buildRequest()
    {
        EventRequest request = new EventRequest();
        request.setName("Cairo night");
        request.setDescription("Cairo night");
        request.setEventDate(EVENT_DATE);
        request.setAvailability(300);
        return request;
    }

    private EventReservationRequest buildReservationRequest(Long eventId, Integer noOfSeats)
    {
        EventReservationRequest request = new EventReservationRequest();
        request.setEventId(eventId);
        request.setNoOfSeats(noOfSeats);
        return request;
    }

    private EventReservationResponse buildReservationResponse(Event event)
    {
        EventReservationResponse response = new EventReservationResponse();
        response.setEventId(event.getId());
        response.setEventName(event.getName());
        response.setEventDate(event.getEventDate());
        return response;
    }

    private Event buildEvent(
            Long id,
            Integer initialAvailability,
            Integer currentAvailability
    )
    {
        Event event = new Event();
        event.setId(id);
        event.setName("Cairo night");
        event.setDescription("Cairo night");
        event.setEventDate(EVENT_DATE);
        event.setState(EventStatus.DRAFT);
        event.setInitialAvailability(initialAvailability);
        event.setCurrentAvailability(currentAvailability);
        return event;
    }

    private EventResponse buildResponse(
            Long id,
            EventStatus state,
            Integer initialAvailability,
            Integer currentAvailability
    )
    {
        EventResponse response = new EventResponse();
        response.setId(id);
        response.setName("Cairo night");
        response.setEventDate(EVENT_DATE);
        response.setState(state.getCode());
        response.setInitialAvailability(initialAvailability);
        response.setCurrentAvailability(currentAvailability);
        return response;
    }
}
