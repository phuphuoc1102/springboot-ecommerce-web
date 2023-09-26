package com.ecommerce.library.ultils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class ProductImageUpload {
    private final String UPLOAD_FOLDER = "D:\\SpringBoot-workspace\\MyPetshop\\Admin\\src\\main\\resources\\static\\img\\image-product";

    public boolean uploadImage(MultipartFile imageProduct   ){
        boolean isUploaded = false;
        try {
            Files.copy(imageProduct.getInputStream(), Paths.get(UPLOAD_FOLDER+ File.separator,
                    imageProduct.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
            isUploaded = true;
        }catch (Exception e){
            e.printStackTrace();

        }
        return isUploaded;

    }
    public boolean checkExist(MultipartFile multipartFile){
        boolean isExist = false;
        try {
            File file = new File(UPLOAD_FOLDER +"\\" + multipartFile.getOriginalFilename());
            isExist = file.exists();
        }catch (Exception e){
            e.printStackTrace();
        }
        return isExist;
    }
}
