package ru.sibsutis.profile.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.sibsutis.profile.core.model.Owner;

import java.util.Optional;

public interface OwnerRepository extends MongoRepository<Owner, String> {
    Optional<Owner> findByVkUserId(Long vkUserId);
    boolean existsByVkUserId(Long vkUserId);
    void deleteByVkUserId(Long vkUserId);
}