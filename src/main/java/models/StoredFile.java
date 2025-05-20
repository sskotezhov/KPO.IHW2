package main.java.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "filestoring")// filestoring(name text, hashcode varchar(64) primary key, location text, counter integer not null);
public class StoredFile {
	

    private String name;
    @Id
    @Column(name = "hashcode", nullable = false, unique = true, length = 64)
    private String hashCode;
    private String location; 
    private Integer counter;
}