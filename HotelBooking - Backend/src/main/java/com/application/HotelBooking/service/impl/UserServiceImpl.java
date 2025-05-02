package com.application.HotelBooking.service.impl;


import com.application.HotelBooking.dto.BookingDTO;
import com.application.HotelBooking.dto.LoginRequest;
import com.application.HotelBooking.dto.Response;
import com.application.HotelBooking.dto.UserDTO;
import com.application.HotelBooking.entity.Booking;
import com.application.HotelBooking.entity.User;
import com.application.HotelBooking.exception.OurException;
import com.application.HotelBooking.repo.UserRepo;
import com.application.HotelBooking.service.interfac.UserService;
import com.application.HotelBooking.utils.JWTUtils;
import com.application.HotelBooking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Override
    public Response register(User user) {
        Response response= new Response();
        try{
            if(userRepo.existsByEmail(user.getEmail())){
                throw new OurException("User with this email already exists");
            }

            if(user.getRole()==null || user.getRole().isBlank()){
                user.setRole("USER");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepo.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);            response.setStatusCode(200);
            response.setUser(userDTO);

        }catch (OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Failed to Created User "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
        Response response=new Response();
        try{
            var user = userRepo.findByEmail(loginRequest.getEmail()).orElseThrow(()->new OurException("User with email doesn't exist"));
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
            String token = jwtUtils.generateToken(user);

            response.setStatusCode(200);
            response.setMessage("login Successful");
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 days");

        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Problem occured during user login "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllUsers() {
        Response response = new Response();
        try {
            List<User> userList = userRepo.findAll();
            List<UserDTO> userDTOS = Utils.mapUserListEntityToUserListDTO(userList);
            response.setStatusCode(200);
            response.setMessage("Fetched All users");
            response.setUserList(userDTOS);
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Problem occured during user fetching all users "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
        Response response = new Response();
        try {
            User user = userRepo.findById(Long.valueOf(userId)).orElseThrow(()->new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);
            response.setStatusCode(200);
            response.setMessage("Fetched All users");
            response.setUser(userDTO);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Problem fetching user booking history "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteUser(String userId) {
        Response response = new Response();
        try {
            userRepo.findById(Long.valueOf(userId)).orElseThrow(()->new OurException("User Not Found"));
            userRepo.deleteById(Long.valueOf(userId));
            response.setStatusCode(200);
            response.setMessage("User Deleted");
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Problem occured during deleting the user "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {
        Response response = new Response();
        try {
            User user = userRepo.findById(Long.valueOf(userId)).orElseThrow(()->new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDTO);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Problem fetching the user "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {
        Response response = new Response();
        try {
            User user = userRepo.findByEmail(email).orElseThrow(()->new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDTO);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Problem fetching user Information "+e.getMessage());
        }
        return response;
    }
}
