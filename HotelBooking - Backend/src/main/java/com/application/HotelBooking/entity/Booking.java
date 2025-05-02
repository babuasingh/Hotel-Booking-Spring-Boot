package com.application.HotelBooking.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "check in date required")
    private LocalDate checkInDate;
    @Future(message = "check out date required and must be in the future")
    private LocalDate checkOutDate;

    @Min(value = 1 , message = "No of Adults must not be less than 1")
    private int no_of_adults;

    @Min(value = 0 , message = "Number of children must not be less than 0")
    private int no_of_childs;

    private int total_guest;

    private String bookingConfirmationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id")
    private Room room;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user;


    public void calculateTotalNoOfGuests(){
        this.total_guest = this.no_of_adults+this.no_of_childs;
    }

    public void setNo_of_adults(int no_of_adults) {
        this.no_of_adults = no_of_adults;
        calculateTotalNoOfGuests();
    }

    public void setNo_of_childs(int no_of_childs) {
        this.no_of_childs = no_of_childs;
        calculateTotalNoOfGuests();
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingConfirmationCode='" + bookingConfirmationCode + '\'' +
                ", total_guest=" + total_guest +
                ", no_of_childs=" + no_of_childs +
                ", no_of_adults=" + no_of_adults +
                ", checkOutDate=" + checkOutDate +
                ", checkInDate=" + checkInDate +
                ", id=" + id +
                '}';
    }
}
