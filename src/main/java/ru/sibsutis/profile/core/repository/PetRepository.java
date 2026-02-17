package ru.sibsutis.profile.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.sibsutis.profile.core.model.Pet;

import java.util.Optional;

public interface PetRepository extends MongoRepository<Pet, String> {
    Optional<Pet> findByQrCode(String qrCode);
    Optional<Pet> findByChipNumber(String chipNumber);
}