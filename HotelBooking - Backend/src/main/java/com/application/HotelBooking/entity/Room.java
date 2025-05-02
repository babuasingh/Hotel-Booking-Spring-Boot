package com.application.HotelBooking.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomType;
    private BigDecimal roomPrice;
    private String photoURL;
    private String roomDescription;


    @OneToMany(mappedBy = "room" ,fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();


    @Override
    public String toString() {
        return "Room{" +
                "roomDescription='" + roomDescription + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", roomPrice=" + roomPrice +
                ", roomType='" + roomType + '\'' +
                ", id=" + id +
                '}';
    }
}
