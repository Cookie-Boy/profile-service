package ru.sibsutis.profile.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.sibsutis.profile.core.model.Owner;

import java.util.Optional;

public interface OwnerRepository extends MongoRepository<Owner, String> {
    Optional<Owner> findByTgChatId(String tgChatId);
    boolean existsByTgChatId(String tgChatId);
    void deleteByTgChatId(String tgChatId);
}