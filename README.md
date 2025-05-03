# 🏨 Hotel Booking Application

A full-stack Hotel Booking application that allows users to browse, book, and manage hotel reservations seamlessly. Built with **Spring Boot** for the backend and **React** for the frontend, the application also incorporates secure authentication, image handling, and persistent data storage.

---

## ✨ Features

### 🔐 User Features
- Register and login securely using JWT-based authentication.
- Browse available hotels with images and descriptions.
- Book hotels with selected dates , room Types and manage bookings.
- View booking history and cancel reservations.

### ⚙️ Admin Features
- Add, update, or remove hotel listings.
- View all bookings and manage rooms.
- Upload hotel room properties using Cloudinary integration.

---

## 🧰 Tech Stack

### Backend (Java)
- **Spring Boot** – Main backend framework
- **Spring Security** – Authentication & authorization
- **JWT (JSON Web Token)** – Secure token-based login system
- **JPA & Hibernate** – ORM for database interactions
- **MySQL** – Relational database to persist user and booking data
- **Cloudinary** – Image upload and management service

### Frontend (JavaScript)
- **React** – Component-based UI
- **Axios** – API communication
- **React Router** – Client-side routing

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

    ```
    if you are using Postgre SQL adjust the url and driver class name accordingly
   
4. Set up `.env` at src/main/resources/.env (if not present add it manually):
    ```properties
    CLOUDINARY_URL= your_cloudinary_url
    CLOUDINARY_NAME = cloud_name
    CLOUDINARY_API_KEY =  cloud_api_key
    CLOUDINARY_API_SECRET = cloud_secret_key
    secretkey = your_JWT_secretkey
    ```

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
##  Demo

> _Add screenshots here of the home page, booking page, and admin panel to showcase your app._

---

## 🧑‍💻 Author

**Siddharth Singh**  
🔗 [LinkedIn](https://www.linkedin.com/in/siddharth-singh-00a3a6232)  
📧 siddharthsingh02901@gmail.com


