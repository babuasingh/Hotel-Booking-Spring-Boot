package com.application.HotelBooking.service;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
//import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${CLOUDINARY_NAME}") String cloudinaryName ,
                             @Value("${CLOUDINARY_API_KEY}") String apiKey,
                             @Value("${CLOUDINARY_API_SECRET}") String cloudinaryApiSecret
                             ) {
//        Dotenv dotenv = Dotenv.load();

//        Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
//        System.out.println("The Cloudinary name is "+cloudinary.config.cloudName);
//        System.out.println("The Cloudinary api key is "+cloudinary.config.apiKey);
//        System.out.println("The Cloudinary api secret is "+cloudinary.config.apiSecret);

//        System.out.println("The Cloudinary name is "+ cloudinaryName);
//        System.out.println("The Cloudinary api key is "+apiKey);
//        System.out.println("The Cloudinary api secret is "+cloudinaryApiSecret);


//        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
//                "cloud_name", dotenv.get("CLOUDINARY_NAME"),
//                "api_key", dotenv.get("CLOUDINARY_API_KEY"),
//                "api_secret", dotenv.get("CLOUDINARY_API_SECRET")
//        ));

                this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudinaryName,
                "api_key", apiKey,
                "api_secret", cloudinaryApiSecret
        ));
    }

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url"); // Returns the URL of the uploaded image
    }
}
