# 🏨 Hotel Booking Application With Spring AI features 

A full-stack Hotel Booking application that allows users to browse, book, and manage hotel reservations seamlessly. Built with **Spring Boot** for the backend and **React** for the frontend, the application also incorporates secure authentication, image handling, and persistent data storage.

---

## ✨ Features

### 🔐 User Features
- Register and login securely using JWT-based authentication.
- Browse available hotels with images and descriptions.
- Book hotels with selected dates , room Types and manage bookings.
- View booking history and cancel reservations.

---

### ⚙️ Admin Features
- Add, update, or remove hotel listings.
- View all bookings and manage rooms.
- Upload hotel room properties using Cloudinary integration.

---
### ⚙️ AI Features
- Spring AI integration to bring AI-powered capabilities into the application
- A conversational assistant using ChatClient
- Tool Calling so the AI can interact with backend services and fetch hotel/room-related information
- RAG (Retrieval-Augmented Generation) to retrieve relevant information and provide contextual responses related to hotel amenities , policies etc.



## 🧰 Tech Stack

### Backend (Java)
- **Spring Boot** – Main backend framework
- **Spring AI** – for RAG and Tool Calling
- **Spring Security** – Authentication & authorization
- **JWT (JSON Web Token)** – Secure token-based login system
- **JPA & Hibernate** – ORM for database interactions
- **MySQL** – Relational database to persist user and booking 
data
- **PG Vector** – Used for RAG alongside docker
- **Cloudinary** – Image upload and management service

### Frontend (JavaScript)
- **React** – Component-based UI
- **Axios** – API communication
- **React Router** – Client-side routing

---

### Demo
currently this doesn't cover the spring AI part .


https://github.com/user-attachments/assets/e6a770ba-41a1-4377-ad51-12d05bd92a4b


---


## 🛠️ Installation & Setup

### Backend (Spring Boot)

1. Clone the repository:
    ```bash
    git clone https://github.com/babuasingh/Hotel-Booking-Spring-Boot.git
    cd hotel-booking-app/backend
    ```

2. Set up `application.properties` at src/main/resources/application.properties (if not present add it manually):
    ```properties
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
    spring.datasource.username=your_mysql_username
    spring.datasource.password=your_mysql_password

    #cloudinary credentials
    CLOUDINARY_URL= your_cloudinary_url
    CLOUDINARY_NAME = cloud_name
    CLOUDINARY_API_KEY =  cloud_api_key
    CLOUDINARY_API_SECRET = cloud_secret_key

    #jwt secret key
    secretkey = your_JWT_secretkey

    #frontend-url
    frontendUrl = ${FRONTEND_URL}


    #logging configuration
    logging.level.root=INFO
    logging.file.name=backendlogs/hotelbooking.log
    logging.logback.rollingpolicy.max-file-size=10MB
    logging.logback.rollingpolicy.max-history=3
    logging.logback.rollingpolicy.total-size-cap=20MB
    logging.logback.rollingpolicy.file-name-pattern=backendlogs/    hotelbooking-%d{yyyy-MM-dd}.%i.log.gz


    #open-AI configurations
    spring.ai.openai.api-key=${OPENAI_API_KEY}

    #pgvector configurations

    app.pgvector.datasource.url=jdbc:postgresql://localhost:5432/   TestDB
    app.pgvector.datasource.username=user
    app.pgvector.datasource.password=password
    app.pgvector.datasource.driver-class-name=org.postgresql.Driver

    app.spring.ai.vectorstore.pgvector.initialize-schema=true
    app.spring.ai.vectorstore.pgvector.schema-name=vector_search
    app.spring.ai.vectorstore.pgvector.table-name=vector_store
    #spring.main.allow-bean-definition-overriding=true

    spring.autoconfigure.exclude = org.springframework.ai.    vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration



    

    ```
    if you are using Postgre SQL adjust the url and driver class name accordingly
   

---

### Frontend (React)

1. Navigate to the frontend folder:
    ```bash
    cd ../frontend
    ```

2. Install dependencies:
    ```bash
    npm install
    ```

3. Run the frontend app:
    ```bash
    npm start
    ```

---

# API Documentation

This document provides comprehensive details about the Hotel Booking API endpoints, their functionality, and request parameters.

## Table of Contents

- [Authentication](#authentication)
  - [Register](#register)
  - [Login](#login)
- [Users](#users)
  - [Get All Users](#get-all-users)
  - [Get User by ID](#get-user-by-id)
  - [Delete User](#delete-user)
  - [Get Logged-in User Profile](#get-logged-in-user-profile)
  - [Get User Booking History](#get-user-booking-history)
- [Rooms](#rooms)
  - [Add New Room](#add-new-room)
  - [Get All Rooms](#get-all-rooms)
  - [Get Room Types](#get-room-types)
  - [Get Room by ID](#get-room-by-id)
  - [Get All Available Rooms](#get-all-available-rooms)
  - [Get Available Rooms by Date and Type](#get-available-rooms-by-date-and-type)
  - [Update Room](#update-room)
  - [Delete Room](#delete-room)
- [Bookings](#bookings)
  - [Book a Room](#book-a-room)
  - [Get All Bookings](#get-all-bookings)
  - [Get Booking by Confirmation Code](#get-booking-by-confirmation-code)
  - [Cancel Booking](#cancel-booking)
- [Chatbot](#chatbot)
  - [Chat with chatclient](#book-a-room)

## Authentication

### Register

Registers a new user in the system.

- **URL**: `/auth/register`
- **Method**: `POST`
- **Auth Required**: No
- **Permissions**: None

**Request Body**:
```json
{
    "email": "user@example.com",
    "name": "username",
    "phoneNumber" : 123456789,
    "password" : "yourpassword"
}
```

### Login

Authenticates a user and returns access token.

- **URL**: `/auth/login`
- **Method**: `POST`
- **Auth Required**: No
- **Permissions**: None

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "securepassword"
}
```

## Users

### Get All Users

Returns a list of all users. Limited to admin users only.

- **URL**: `/users/all`
- **Method**: `GET`
- **Auth Required**: Yes
- **Permissions**: ADMIN

### Get User by ID

Returns a specific user by their ID.

- **URL**: `/users/get-by-id/{userId}`
- **Method**: `GET`
- **Auth Required**: Yes
- **Permissions**: Any authenticated user

**Path Parameters**:
- `userId` - The ID of the user to retrieve

### Delete User

Deletes a user from the system. Limited to admin users only.

- **URL**: `/users/delete/{userId}`
- **Method**: `DELETE`
- **Auth Required**: Yes
- **Permissions**: ADMIN

**Path Parameters**:
- `userId` - The ID of the user to delete

### Get Logged-in User Profile

Returns the profile information of the currently logged-in user.

- **URL**: `/users/get-logged-in-profile-info`
- **Method**: `GET`
- **Auth Required**: Yes
- **Permissions**: Any authenticated user

### Get User Booking History

Returns all bookings associated with a specific user.

- **URL**: `/users/get-user-bookings/{userId}`
- **Method**: `GET`
- **Auth Required**: Yes
- **Permissions**: Any authenticated user

**Path Parameters**:
- `userId` - The ID of the user whose booking history is requested

## Rooms

### Add New Room

Adds a new room to the hotel inventory. Limited to admin users only.

- **URL**: `/rooms/add`
- **Method**: `POST`
- **Auth Required**: Yes
- **Permissions**: ADMIN
- **Content-Type**: `multipart/form-data`

**Form Parameters**:
- `photo` - Image file of the room
- `roomType` - Type of the room (e.g., "Single", "Double", "Suite")
- `roomPrice` - Price per night for the room
- `roomDescription` - Detailed description of the room

### Get All Rooms

Returns a list of all rooms in the hotel.

- **URL**: `/rooms/all`
- **Method**: `GET`
- **Auth Required**: No
- **Permissions**: None

### Get Room Types

Returns a list of all available room types.

- **URL**: `/rooms/types`
- **Method**: `GET`
- **Auth Required**: No
- **Permissions**: None

### Get Room by ID

Returns details of a specific room.

- **URL**: `/rooms/room-by-id/{roomId}`
- **Method**: `GET`
- **Auth Required**: No
- **Permissions**: None

**Path Parameters**:
- `roomId` - The ID of the room to retrieve

### Get All Available Rooms

Returns a list of all rooms that are currently available.

- **URL**: `/rooms/all-available-rooms`
- **Method**: `GET`
- **Auth Required**: No
- **Permissions**: None

### Get Available Rooms by Date and Type

Returns a list of rooms available for booking within a specific date range and of a specific type.

- **URL**: `/rooms/available-rooms-by-date-and-type`
- **Method**: `GET`
- **Auth Required**: No
- **Permissions**: None

**Query Parameters**:
- `checkInDate` - Date of check-in (format: ISO Date, e.g., "2023-12-01")
- `checkOutDate` - Date of check-out (format: ISO Date, e.g., "2023-12-05")
- `roomType` - Type of room requested

### Update Room

Updates the details of a specific room. Limited to admin users only.

- **URL**: `/rooms/update/{roomId}`
- **Method**: `PUT`
- **Auth Required**: Yes
- **Permissions**: ADMIN
- **Content-Type**: `multipart/form-data`

**Path Parameters**:
- `roomId` - The ID of the room to update

**Form Parameters**:
- `photo` (optional) - New image file of the room
- `roomType` (optional) - Updated type of the room
- `roomPrice` (optional) - Updated price per night for the room
- `roomDescription` (optional) - Updated description of the room

### Delete Room

Deletes a room from the hotel inventory. Limited to admin users only.

- **URL**: `/rooms/delete/{roomId}`
- **Method**: `DELETE`
- **Auth Required**: Yes
- **Permissions**: ADMIN

**Path Parameters**:
- `roomId` - The ID of the room to delete

## Bookings

### Book a Room

Creates a new booking for a specified room and user.

- **URL**: `/bookings/book-room/{roomId}/{userId}`
- **Method**: `POST`
- **Auth Required**: Yes
- **Permissions**: ADMIN or USER

**Path Parameters**:
- `roomId` - The ID of the room to book
- `userId` - The ID of the user making the booking

**Request Body**:
```json
{
  "checkInDate": "2023-12-01",
  "checkOutDate": "2023-12-05"
}
```

### Get All Bookings

Returns a list of all bookings. Limited to admin users only.

- **URL**: `/bookings/all`
- **Method**: `GET`
- **Auth Required**: Yes
- **Permissions**: ADMIN

### Get Booking by Confirmation Code

Returns details of a specific booking by its confirmation code.

- **URL**: `/bookings/get-by-confirmation-code/{confirmationCode}`
- **Method**: `GET`
- **Auth Required**: Yes
- **Permissions**: Any authenticated user

**Path Parameters**:
- `confirmationCode` - The unique confirmation code of the booking

### Cancel Booking

Cancels an existing booking.

- **URL**: `/bookings/cancel/{bookingId}`
- **Method**: `DELETE`
- **Auth Required**: Yes
- **Permissions**: ADMIN or USER

**Path Parameters**:
- `bookingId` - The ID of the booking to cancel

### Chatbot

Talk with the chatclient

- **URL**: `/ai/chat?q="your query"`
- **Auth Required**: No (You will be asked to login only if you want to proceed booking via the chatclient .)
- **Permissions**: ADMIN or USER

## Response Structure

All API endpoints return responses with a consistent structure:

```json
{
  "statusCode": 200, // HTTP status code
  "message": "Operation successful", // Human-readable message
  "data": {} // Response data, can be an object, array, or null
}
```

## Error Handling

When an error occurs, the API will return an appropriate HTTP status code along with a descriptive message:

```json
{
  "statusCode": 400, // or 401, 403, 404, 500, etc.
  "message": "Error description",
  "data": null
}
```

Common error codes:
- `400` - Bad Request (invalid input)
- `401` - Unauthorized (not authenticated)
- `403` - Forbidden (not authorized)
- `404` - Not Found (resource doesn't exist)
- `500` - Internal Server Error

## 🧑‍💻 Author

**Siddharth Singh**  
🔗 [LinkedIn](https://www.linkedin.com/in/siddharth-singh-00a3a6232)  
📧 siddharthsingh02901@gmail.com


