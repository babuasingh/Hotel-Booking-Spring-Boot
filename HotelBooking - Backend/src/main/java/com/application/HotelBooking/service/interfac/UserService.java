package com.application.HotelBooking.service.interfac;

import com.application.HotelBooking.dto.LoginRequest;
import com.application.HotelBooking.dto.Response;
import com.application.HotelBooking.entity.User;

public interface UserService {

    Response register(User user);
    Response login(LoginRequest loginRequest);
    Response getAllUsers();
    Response getUserBookingHistory(String userId);
    Response deleteUser(String userId);
    Response getUserById(String userId);
    Response getMyInfo(String email);

}
