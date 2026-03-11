package com.application.HotelBooking.service.impl;

import com.application.HotelBooking.dto.Response;
import com.application.HotelBooking.dto.RoomDTO;
import com.application.HotelBooking.entity.Room;
import com.application.HotelBooking.exception.OurException;
import com.application.HotelBooking.repo.RoomRepo;
import com.application.HotelBooking.service.CloudinaryService;
import com.application.HotelBooking.service.interfac.RoomService;
import com.application.HotelBooking.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
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

    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();
        try {
            String imageUrl = cloudinaryService.uploadImage(photo);
            Room room = new Room();
            room.setRoomDescription(description);
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setPhotoURL(imageUrl);
            Room savedRoom = roomRepo.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCode(200);
            response.setMessage("Room Added Successfully");
            response.setRoom(roomDTO);
            logger.info("Admin added new room successfully with ID: {}", savedRoom.getId());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Saving a room " + e.getMessage());
            logger.error("Error adding new room: {}", e.getMessage(), e);
        }
        return response;
    }

    @Override
    @Tool(name = "getAllRoomTypes", description = "Fetches all distinct room types available in the hotel")
    public List<String> getAllRoomTypes() {
        List<String> roomTypes;
        try {
            roomTypes = roomRepo.findDistinctRoomTypes();
            logger.info("Fetched all distinct room types successfully, total types: {}", roomTypes.size());
            return roomTypes;
        } catch (Exception e) {
            logger.error("Error fetching distinct room types: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    @Tool(name = "getAllRoomsDetails", description = "Fetches all rooms available in the hotel with their details like room type, price, description")
    public Response getAllRooms() {
        Response response = new Response();
        try {
            List<Room> roomList = roomRepo.findAll();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);
            logger.info("Fetched all rooms successfully, total rooms: {}", roomDTOList.size());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error fetching all the rooms " + e.getMessage());
            logger.error("Error fetching all rooms: {}", e.getMessage(), e);
        }
        return response;
    }

    @Override
    public Response deleteRoom(Long roomId) {
        Response response = new Response();
        try {
            roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room with the id not found"));
            roomRepo.deleteById(roomId);
            response.setStatusCode(200);
            response.setMessage("Successfully Deleted");
            logger.info("Admin deleted room successfully with ID: {}", roomId);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            logger.error("Error deleting room with ID {}: {}", roomId, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Deleting a room " + e.getMessage());
            logger.error("Error deleting room with ID {}: {}", roomId, e.getMessage(), e);
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
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room not Found"));

            if (roomType != null) room.setRoomType(roomType);
            if (roomPrice != null) room.setRoomPrice(roomPrice);
            if (description != null) room.setRoomDescription(description);
            if (imageUrl != null) room.setPhotoURL(imageUrl);

            Room updatedRoom = roomRepo.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);
            response.setStatusCode(200);
            response.setMessage("Successfull");
            response.setRoom(roomDTO);
            logger.info("Admin updated room successfully with ID: {}", roomId);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            logger.error("Error updating room with ID {}: {}", roomId, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Fetching the room" + e.getMessage());
            logger.error("Error updating room with ID {}: {}", roomId, e.getMessage(), e);
        }
        return response;
    }

    @Override
    @Tool(name = "getRoomDetailsById", description = "Fetches details of a specific room by its ID, including room type, price, description")
    public Response getRoomById(@ToolParam(required = true, description = "The id of the room") Long roomId) {
        Response response = new Response();
        try {
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room not Found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);
            response.setStatusCode(200);
            response.setMessage("Successfull");
            response.setRoom(roomDTO);
            logger.info("Fetched room successfully with ID: {}", roomId);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            logger.error("Error fetching room with ID {}: {}", roomId, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Fetching the room" + e.getMessage());
            logger.error("Error fetching room with ID {}: {}", roomId, e.getMessage(), e);
        }
        return response;
    }

    @Override
    @Tool(name = "getAvailableRoomsByDateAndType", description = "Fetches available rooms based on check-in date, check-out date, and room type")
    public Response getAvailableRoomsByDataAndType(
            @ToolParam(required = true, description = "check in date for the booking in DateTimeFormat.ISO.DATE . The most common ISO Date Format yyyy-MM-dd — for example, '2000-10-31'.") LocalDate checkInDate
            , @ToolParam(required = true, description = "check out date for the booking in DateTimeFormat.ISO.DATE . The most common ISO Date Format yyyy-MM-dd — for example, '2000-10-31'.") LocalDate checkOutDate
            , @ToolParam(required = true, description = """
                    The roomType required for booking . Ensure you pass the values in the same manner as given below for the roomtype to get correct response .  
                    1. Standard Room
                    2. Deluxe Suite
                    3. Family Suite
                    4. Single Room
                    5. Executive Room
                    """) String roomType) {
        Response response = new Response();
        try {
            List<Room> roomList = roomRepo.findAvailableRoomsByDatesAndTypes(checkInDate, checkOutDate, roomType);
            List<RoomDTO> roomDTO = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successfull");
            response.setRoomList(roomDTO);
            logger.info("Fetched available rooms successfully for type '{}' between {} and {}, total rooms: {}", roomType, checkInDate, checkOutDate, roomDTO.size());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Fetching the room" + e.getMessage());
            logger.error("Error fetching available rooms for type '{}' between {} and {}: {}", roomType, checkInDate, checkOutDate, e.getMessage(), e);
        }
        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        Response response = new Response();
        try {
            List<Room> roomList = roomRepo.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successfull");
            response.setRoomList(roomDTOList);
            logger.info("Fetched all available rooms successfully, total rooms: {}", roomDTOList.size());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Fetching the room" + e.getMessage());
            logger.error("Error fetching all available rooms: {}", e.getMessage(), e);
        }
        return response;
    }
}
