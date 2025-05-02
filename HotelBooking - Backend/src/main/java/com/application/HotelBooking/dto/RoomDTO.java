package com.application.HotelBooking.dto;


import com.application.HotelBooking.entity.Booking;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomDTO {
    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private String photoURL;
    private String roomDescription;
    private List<BookingDTO> bookings = new ArrayList<>();
}
