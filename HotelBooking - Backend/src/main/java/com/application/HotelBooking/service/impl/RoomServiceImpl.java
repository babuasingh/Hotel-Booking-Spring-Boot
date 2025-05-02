package com.application.HotelBooking.service.impl;

import com.application.HotelBooking.dto.Response;
import com.application.HotelBooking.dto.RoomDTO;
import com.application.HotelBooking.entity.Room;
import com.application.HotelBooking.exception.OurException;
import com.application.HotelBooking.repo.RoomRepo;
import com.application.HotelBooking.service.CloudinaryService;
import com.application.HotelBooking.service.interfac.RoomService;
import com.application.HotelBooking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();
        try {
            String imageUrl = cloudinaryService.uploadImage(photo);
            Room room=new Room();
            room.setRoomDescription(description);
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setPhotoURL(imageUrl);
            Room savedRoom = roomRepo.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCode(200);
            response.setMessage("Room Added Successfully");
            response.setRoom(roomDTO);
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Saving a room "+e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
       return roomRepo.findDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {
        Response response = new Response();
        try {
            List<Room> roomList= roomRepo.findAll();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Saving a room "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteRoom(Long roomId) {
        Response response = new Response();
        try {
            roomRepo.findById(roomId).orElseThrow(()->new OurException("Room with the id not found"));
            roomRepo.deleteById(roomId);
            response.setStatusCode(200);
            response.setMessage("Successfully Deleted");
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Deleting a room "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice, MultipartFile photo) {
        Response response = new Response();
        try {
            String imageUrl = null;
            if (photo != null && !photo.isEmpty()) {
                imageUrl = cloudinaryService.uploadImage(photo);
            }
            Room room=roomRepo.findById(roomId).orElseThrow(()->new OurException("Room not Found"));

            if (roomType != null) room.setRoomType(roomType);
            if (roomPrice != null) room.setRoomPrice(roomPrice);
            if (description != null) room.setRoomDescription(description);
            if(imageUrl!=null) room.setPhotoURL(imageUrl);

            Room updatedRoom =  roomRepo.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);
            response.setStatusCode(200);
            response.setMessage("Successfull");
            response.setRoom(roomDTO);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Fetching the room"+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getRoomById(Long roomId) {
        Response response = new Response();
        try {
            Room room=roomRepo.findById(roomId).orElseThrow(()->new OurException("Room not Found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);
            response.setStatusCode(200);
            response.setMessage("Successfull");
            response.setRoom(roomDTO);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Fetching the room"+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRoomsByDataAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        Response response = new Response();
        try {
            List<Room> roomList= roomRepo.findAvailableRoomsByDatesAndTypes(checkInDate,checkOutDate,roomType);
            List<RoomDTO> roomDTO = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successfull");
            response.setRoomList(roomDTO);
        }
        catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Fetching the room"+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        Response response = new Response();
        try {
            List<Room> roomList=roomRepo.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successfull");
            response.setRoomList(roomDTOList);
        }
        catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Fetching the room"+e.getMessage());
        }
        return response;
    }
}
