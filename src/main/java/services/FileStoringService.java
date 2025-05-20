package main.java.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import main.java.builders.StoredFileBuilder;
import main.java.repositories.FileRepository;

@Service
public class FileStoringService {
	@Autowired
	private final FileRepository fileRepository;
	
	public FileStoringService(FileRepository fileRepository)
	{
		this.fileRepository = fileRepository;
	};
	
	public String storeFile(MultipartFile file) throws IOException
	{
		String hashCode = DigestUtils.sha256Hex(file.getBytes());
		fileRepository.findByHashCode(hashCode).ifPresentOrElse(
				existingFile -> {
					existingFile.setCounter(existingFile.getCounter() + 1);
					fileRepository.save(existingFile);
				},
				() -> {
					try {
						fileRepository.save(StoredFileBuilder.buildFromMultipartFile(file, hashCode));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				);
		return hashCode;
	}
	public String getContent(String hashCode) throws IOException {
	    fileRepository.findByHashCode(hashCode)
	        .orElseThrow(() -> new FileNotFoundException("File with hash " + hashCode + " not found"));
	    
	    Path filePath = Paths.get("uploads/", hashCode);
	    return Files.readString(filePath);
	    
	}
}
