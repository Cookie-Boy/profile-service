package ru.sibsutis.profile.core.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pets")
@Data
public class Pet {
    @Id
    private String id;

    private String species;
    private Integer age;
    private String breed;
    private String chipNumber;

    private MedicalRecord medicalRecord;

    @Indexed(unique = true)
    private String qrCode;
}
