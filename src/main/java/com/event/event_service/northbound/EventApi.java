package com.event.event_service.northbound;

import com.event.event_service.dto.EventRequest;
import com.event.event_service.dto.EventResponse;
import com.event.event_service.dto.UpdateEventStatusRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Tag(name = "Event API", description = "API for managing events")
@Validated
@RequestMapping("/api/v1/events")
public interface EventApi
{
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventResponse create(@Valid @RequestBody EventRequest request);

    @GetMapping
    List<EventResponse> findAll();

    @GetMapping("/{id}")
    EventResponse findById(@PathVariable Long id);

    @PutMapping("/{id}")
    EventResponse update(@PathVariable Long id, @Valid @RequestBody EventRequest request);

    @PatchMapping("/{id}/status")
    EventResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateEventStatusRequest request);
}
