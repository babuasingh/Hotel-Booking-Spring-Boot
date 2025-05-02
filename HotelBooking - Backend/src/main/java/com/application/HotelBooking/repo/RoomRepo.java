package com.application.HotelBooking.repo;

import com.application.HotelBooking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface RoomRepo extends JpaRepository<Room,Long> {

    @Query("select distinct r.roomType from Room r")
    List<String> findDistinctRoomTypes();

    @Query("select r from Room r where r.roomType Like %:roomType and r.id not in (select bk.id from Booking bk where ((bk.checkInDate <= :checkInDate) and (bk.checkOutDate >= :checkInDate)) or ((bk.checkInDate <= :checkOutDate) and (bk.checkOutDate >= :checkOutDate)) or ((bk.checkInDate >= :checkInDate) and (bk.checkOutDate <= :checkOutDate)) )")
    List<Room> findAvailableRoomsByDatesAndTypes(LocalDate checkInDate , LocalDate checkOutDate , String roomType);


    @Query("select r from Room r where r.id not in (select b.id from Booking b)")
    List<Room> getAllAvailableRooms();

}
