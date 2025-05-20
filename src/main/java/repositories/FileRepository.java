package main.java.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import main.java.models.StoredFile;

public interface FileRepository extends JpaRepository<StoredFile, String>{
	 Optional<StoredFile> findByHashCode(String hashCode);
}
