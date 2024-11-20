package com.weplayWeb.spring.services;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class ImageService {
	
	@Autowired
    private Cloudinary cloudinary;
    
    public String uploadImage(File imageFile) {
        try {
            Map uploadResult = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap());
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }
}
