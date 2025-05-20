package main.java.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import main.java.models.StoredAnalyse;


public interface AnalyseRepository extends JpaRepository<StoredAnalyse, String>{
	 Optional<StoredAnalyse> findByHashCode(String hashCode);
}

