package com.application.HotelBooking.dto;

import com.application.HotelBooking.entity.Room;
import com.application.HotelBooking.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDTO {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private int no_of_adults;

    private int no_of_childs;

    private int total_guest;

    private String bookingConfirmationCode;

    private RoomDTO room;


    private UserDTO user;
}
