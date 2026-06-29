package com.event.event_service.dto.pagination;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationQueryDTO
{

    @PositiveOrZero(message = "page must be greater than or equal to 0")
    private Integer page = 0;

    @Positive(message = "size must be greater than 0")
    private Integer size = 10;
}
