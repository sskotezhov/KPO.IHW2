package main.java.builders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

import main.java.models.StoredFile;

public class StoredFileBuilder {
	private static final String UPLOAD_DIR = "uploads/";
	
	
    public static StoredFile buildFromMultipartFile(MultipartFile file, String hashCode) throws IOException
    {
    	Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(hashCode);
        
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        StoredFile newFile = new StoredFile();
        newFile.setHashCode(hashCode);
        newFile.setName(file.getOriginalFilename());
        newFile.setLocation("/uploads/" + file.getOriginalFilename());
        newFile.setCounter(1);
        return newFile;
    }
    
}
