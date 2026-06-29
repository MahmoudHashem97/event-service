package com.event.event_service.southbound.repository;

import com.event.event_service.southbound.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>
{
}
