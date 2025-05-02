package com.application.HotelBooking.controllers;


import com.application.HotelBooking.dto.LoginRequest;
import com.application.HotelBooking.dto.Response;
import com.application.HotelBooking.entity.User;
import com.application.HotelBooking.service.interfac.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody User user){
        Response response = userService.register(user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest user){
        Response response = userService.login(user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
