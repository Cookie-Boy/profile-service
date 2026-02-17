package ru.sibsutis.profile.core.model;

import lombok.Data;
import java.util.List;

@Data
public class MedicalRecord {
    private List<Vaccination> vaccinations;
    private List<String> allergies;
    private List<String> chronicDiseases;

    @Data
    public static class Vaccination {
        private String name;
        private String date;
        private String nextDueDate;
    }
}