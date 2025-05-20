package main.java.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="analysestoring")
public class StoredAnalyse {
	@Id
    @Column(name = "hashcode", nullable = false, unique = true, length = 64)
	private String hashCode;
	private Integer paragraphs,words,letters;
	private String locationimage;
	
}
