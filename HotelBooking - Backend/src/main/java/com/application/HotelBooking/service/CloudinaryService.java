package com.application.HotelBooking.service;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
//import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    public CloudinaryService(@Value("${CLOUDINARY_NAME}") String cloudinaryName,
                             @Value("${CLOUDINARY_API_KEY}") String apiKey,
                             @Value("${CLOUDINARY_API_SECRET}") String cloudinaryApiSecret
    ) {
        try {
            this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudinaryName,
                    "api_key", apiKey,
                    "api_secret", cloudinaryApiSecret
            ));
        } catch (Exception e) {
            logger.error("Error initializing Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public String uploadImage(MultipartFile file) throws IOException {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            logger.info("Image uploaded successfully to Cloudinary with URL: {}", uploadResult.get("secure_url"));
            return (String) uploadResult.get("secure_url"); // Returns the URL of the uploaded image
        } catch (Exception e) {
            logger.error("Error uploading image to Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
