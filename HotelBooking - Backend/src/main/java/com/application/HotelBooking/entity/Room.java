package com.application.HotelBooking.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    // This lock is used to synchronize access to the room when multiple users are trying to book it at the same time.
    @Transient
    private Lock lock;

    public Room(){
        lock=new ReentrantLock();
    }


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
