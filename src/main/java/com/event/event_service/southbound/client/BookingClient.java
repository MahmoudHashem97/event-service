package com.event.event_service.southbound.client;

import com.event.event_service.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
public class BookingClient
{

    private final RestClient bookingServiceRestClient;

    public void deleteByEventId(Long eventId)
    {
        try
        {
            bookingServiceRestClient
                    .delete()
                    .uri("/api/v1/bookings/events/{eventId}", eventId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception)
        {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "BOOKING_DELETE_FAILED",
                    "failed to delete bookings for event"
            );
        }
    }
}
