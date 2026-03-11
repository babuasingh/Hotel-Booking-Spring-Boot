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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

@Service
public class BookingServiceImpl implements BookingService {


    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired

    private BookingRepo bookingRepo;

    private static final Logger logger = LoggerFactory.getLogger("BookingServiceImpl.class");


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response response = new Response();
        Lock lock = null;
        try {
            BookingRequestValidation(bookingRequest);

            User user = userRepo.findById(userId).orElseThrow(() -> new OurException("User doesn't exist"));
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room doesn't exist"));

            lock = room.getLock();
            lock.lock();
            // Critical section starts here
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
            // Critical section ends here

            logger.info("User with ID: {} successfully booked Room with ID: {} for dates {} to {}. Booking Confirmation Code: {}",
                    userId, roomId, bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingConfirmationCode);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            logger.info("Booking failed for User ID: {} and Room ID: {}. Reason: {}", userId, roomId, e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving the room " + e.getMessage());
            logger.info("Booking failed for User ID: {} and Room ID: {}. Reason: {}", userId, roomId, e.getMessage());
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }

        return response;
    }

    @Tool(name = "saveBooking", description = "Used to book a room for a user. It checks for room availability and saves the booking if the room is available.")
    public Response saveBookingForUserToolCalling(
            @ToolParam(required = true, description = "The room id of the room to be booked") Long roomId
            , @ToolParam(required = true, description = "The User Id of the user trying to book the room") Long userId
            , @ToolParam(required = true, description = "Check in date for the booking") LocalDate checkInDate
            , @ToolParam(required = true, description = "Check out date for the booking") LocalDate checkOutDate
            , @ToolParam(required = true, description = "An integer denoting the number of adults in the booking") Integer no_of_adults,
            @ToolParam(required = true, description = "An integer denoting the number of child in the booking") Integer no_of_childs
    ) {
        try {
            logger.info("Received booking request from tool for User ID: {} and Room ID: {} for dates {} to {} with {} adults and {} children",
                    userId, roomId, checkInDate, checkOutDate, no_of_adults, no_of_childs);

            Booking bookingRequest = new Booking();
            bookingRequest.setCheckInDate(checkInDate);
            bookingRequest.setCheckOutDate(checkOutDate);
            bookingRequest.setNo_of_adults(no_of_adults);
            bookingRequest.setNo_of_childs(no_of_childs);
            return saveBooking(roomId, userId, bookingRequest);
        } catch (Exception e) {
            logger.error("Error in saveBookingForUserToolCalling: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void BookingRequestValidation(Booking bookingRequest) throws OurException {
        if (bookingRequest.getCheckInDate().isAfter(bookingRequest.getCheckOutDate())) {
            throw new IllegalArgumentException("Check in Date must be before checkout Date");
        }
        if (bookingRequest.getCheckInDate().isEqual(bookingRequest.getCheckOutDate())) {
            throw new IllegalArgumentException("CheckIn and CheckOut Date must be different");
        }
        if (bookingRequest.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("CheckIn Date cannot be in Past");
        }
    }


    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckInDate())
                );
    }


    @Override
    @Tool(name = "findBookingByConfirmationCode", description = "Fetches a booking details based on the provided confirmation code .")
    public Response findBookingByConfirmationCode(@ToolParam(required = true, description = "The booking confirmation code of the booking ") String confirmationCode) {
        Response response = new Response();
        try {
            Booking booking = bookingRepo.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new OurException("No Booking Found"));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true);
            response.setStatusCode(200);
            response.setMessage("Successfull");
            response.setBooking(bookingDTO);
            logger.info("Fetched booking successfully with Confirmation Code: {}", confirmationCode);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            logger.error("Error fetching booking with Confirmation Code {}: {}", confirmationCode, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Fetching the booking " + e.getMessage());
            logger.error("Error fetching booking with Confirmation Code {}: {}", confirmationCode, e.getMessage(), e);
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
            logger.info("Fetched all bookings successfully, total bookings: {}", bookingDTOList.size());

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            logger.error("Error fetching all bookings: {}", e.getMessage(), e);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Fetching all bookings " + e.getMessage());
            logger.error("Error fetching all bookings: {}", e.getMessage(), e);
        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();
        try {
            bookingRepo.findById(bookingId).orElseThrow(() -> new OurException("No Booking Found"));
            bookingRepo.deleteById(bookingId);
            response.setStatusCode(200);
            response.setMessage("Successfull");
            logger.info("Cancelled booking successfully with ID: {}", bookingId);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            logger.error("Error cancelling booking with ID {}: {}", bookingId, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error cancelling the booking" + e.getMessage());
            logger.error("Error cancelling booking with ID {}: {}", bookingId, e.getMessage(), e);
        }
        return response;
    }

    @Tool(name = "getTodayDate", description = "Returns today's date in ISO format (YYYY-MM-DD).")
    public String getTodayDate() {
        logger.info("Tool getTodayDate called, returning today's date.");
        return LocalDate.now().toString();
    }
}
