package com.application.HotelBooking.service.impl;


import com.application.HotelBooking.dto.BookingDTO;
import com.application.HotelBooking.dto.Response;
import com.application.HotelBooking.entity.Booking;
import com.application.HotelBooking.entity.Room;
import com.application.HotelBooking.entity.User;
import com.application.HotelBooking.exception.OurException;
import com.application.HotelBooking.repo.BookingRepo;
import com.application.HotelBooking.repo.RoomRepo;
import com.application.HotelBooking.repo.UserRepo;
import com.application.HotelBooking.service.interfac.BookingService;
import com.application.HotelBooking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BookingRepo bookingRepo;

    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response response = new Response();
        try {
            if(bookingRequest.getCheckInDate().isAfter(bookingRequest.getCheckOutDate())){
                throw new IllegalArgumentException("Check in Date must be before checkout Date");
            }
            if(bookingRequest.getCheckInDate().isEqual(bookingRequest.getCheckOutDate())){
                throw new IllegalArgumentException("CheckIn and CheckOut Date must be different");
            }
            User user = userRepo.findById(userId).orElseThrow(()->new OurException("User doesn't exist"));
            Room room = roomRepo.findById(roomId).orElseThrow(()->new OurException("Room doesn't exist"));


            List<Booking> existingBookings = room.getBookings();

            if (!roomIsAvailable(bookingRequest, existingBookings)) {
                throw new OurException("Room not Available for selected date range");
            }

            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
            Booking savedBooking = bookingRepo.save(bookingRequest);
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTO(savedBooking);
            response.setBookingConfirmationCode(bookingConfirmationCode);
            response.setBooking(bookingDTO);
            response.setMessage("Booking saved Successfully");
            response.setStatusCode(200);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving the room "+e.getMessage());
//            System.out.println("The error is "+e.getMessage());
        }

//        System.out.println("The saved booking is :"+response);
        return response;
    }


    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckInDate())
                );
    }


    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {
        Response response = new Response();
        try {
            Booking booking=bookingRepo.findByBookingConfirmationCode(confirmationCode).orElseThrow(()->new OurException("No Booking Found"));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking,true);
            response.setStatusCode(200);
            response.setMessage("Successfull");
            response.setBooking(bookingDTO);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Fetching the booking "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllBookings() {
        Response response = new Response();
        try {
            List<Booking> bookingList = bookingRepo.findAll();
            List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookingList);
            response.setStatusCode(200);
            response.setMessage("Successfull");
            response.setBookingList(bookingDTOList);

        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Fetching all bookings "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();
        try {
            bookingRepo.findById(bookingId).orElseThrow(()->new OurException("No Booking Found"));
            bookingRepo.deleteById(bookingId);
            response.setStatusCode(200);
            response.setMessage("Successfull");

        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error cancelling the booking"+e.getMessage());
        }
        return response;
    }
}
