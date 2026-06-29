package com.event.event_service.southbound.mapper;

import com.event.event_service.dto.EventRequest;
import com.event.event_service.dto.EventResponse;
import com.event.event_service.southbound.domain.Event;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper
{

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", constant = "DRAFT")
    @Mapping(target = "initialAvailability", source = "availability")
    @Mapping(target = "currentAvailability", source = "availability")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Event toEntity(EventRequest request);

    @Mapping(target = "state", expression = "java(event.getState().getCode())")
    EventResponse toResponse(Event event);

    List<EventResponse> toResponse(List<Event> events);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "initialAvailability", source = "availability")
    @Mapping(target = "currentAvailability", source = "availability")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(EventRequest request, @MappingTarget Event event);
}
