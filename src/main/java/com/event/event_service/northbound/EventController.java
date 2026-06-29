package com.event.event_service.northbound;

import com.event.event_service.dto.EventRequest;
import com.event.event_service.dto.EventResponse;
import com.event.event_service.dto.UpdateEventStatusRequest;
import com.event.event_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController implements EventApi
{
    private final EventService eventService;

    @Override
    public EventResponse create(EventRequest request)
    {
        return eventService.create(request);
    }

    @Override
    public List<EventResponse> findAll()
    {
        return eventService.findAll();
    }

    @Override
    public EventResponse findById(Long id)
    {
        return eventService.findById(id);
    }

    @Override
    public EventResponse update(Long id, EventRequest request)
    {
        return eventService.update(id, request);
    }

    @Override
    public EventResponse updateStatus(Long id, UpdateEventStatusRequest request)
    {
        return eventService.updateStatus(id, request.getState());
    }
}
