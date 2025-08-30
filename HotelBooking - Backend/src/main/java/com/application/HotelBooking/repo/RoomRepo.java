package com.application.HotelBooking.repo;

import com.application.HotelBooking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {

    @Query("select distinct r.roomType from Room r")
    List<String> findDistinctRoomTypes();

    @Query("""
       select r from Room r
       where r.roomType like %:roomType
       and r.id not in (
           select bk.room.id from Booking bk
           where bk.checkInDate <= :checkOutDate
           and bk.checkOutDate >= :checkInDate
       )
       """)
    List<Room> findAvailableRoomsByDatesAndTypes(
           @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
           @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
           @RequestParam("roomType") String roomType);

    @Query("select r from Room r where r.id not in (select b.room.id from Booking b)")
    List<Room> getAllAvailableRooms();

}
