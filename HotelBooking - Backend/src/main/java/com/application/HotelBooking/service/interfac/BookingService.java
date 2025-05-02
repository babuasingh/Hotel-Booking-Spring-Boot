package com.application.HotelBooking.service.interfac;

import com.application.HotelBooking.dto.Response;
import com.application.HotelBooking.entity.Booking;

public interface BookingService {
    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBooking(Long bookingId);
}
