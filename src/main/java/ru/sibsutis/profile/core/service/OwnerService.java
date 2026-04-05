package ru.sibsutis.profile.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sibsutis.profile.api.dto.OwnerRequest;
import ru.sibsutis.profile.api.dto.OwnerResponse;
import ru.sibsutis.profile.api.exception.DuplicateResourceException;
import ru.sibsutis.profile.api.exception.ResourceNotFoundException;
import ru.sibsutis.profile.core.model.Owner;
import ru.sibsutis.profile.core.model.Pet;
import ru.sibsutis.profile.core.repository.OwnerRepository;
import ru.sibsutis.profile.core.repository.PetRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerResponse createOwner(OwnerRequest request) {
        log.info("Creating new owner with id: {}", request.getId());

        if (ownerRepository.existsById(request.getId())) {
            throw new DuplicateResourceException("Owner with id " + request.getId() + " already exists");
        }

        if (request.getTgChatId() != null && ownerRepository.existsByTgChatId(request.getTgChatId())) {
            throw new DuplicateResourceException("Owner with tgChatId " + request.getTgChatId() + " already exists");
        }

        Owner owner = mapToEntity(request);
        Owner savedOwner = ownerRepository.save(owner);

        log.info("Owner created successfully with id: {}", savedOwner.getId());
        return mapToResponse(savedOwner);
    }

    public OwnerResponse getOwnerById(String id) {
        log.info("Fetching owner by id: {}", id);

        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));

        return mapToResponse(owner);
    }

    public OwnerResponse getOwnerByTgChatId(String tgChatId) {
        log.info("Fetching owner by tgChatId: {}", tgChatId);

        Owner owner = ownerRepository.findByTgChatId(tgChatId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with tgChatId: " + tgChatId));

        return mapToResponse(owner);
    }

    public List<OwnerResponse> getAllOwners() {
        log.info("Fetching all owners");

        return ownerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OwnerResponse updateOwner(String id, OwnerRequest request) {
        log.info("Updating owner with id: {}", id);

        Owner existingOwner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));

        // Проверяем уникальность tgChatId при изменении
        if (request.getTgChatId() != null && !request.getTgChatId().equals(existingOwner.getTgChatId())) {
            if (ownerRepository.existsByTgChatId(request.getTgChatId())) {
                throw new DuplicateResourceException("Owner with tgChatId " + request.getTgChatId() + " already exists");
            }
            existingOwner.setTgChatId(request.getTgChatId());
        }

        if (request.getFirstName() != null) {
            existingOwner.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            existingOwner.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            existingOwner.setPhone(request.getPhone());
        }

        Owner updatedOwner = ownerRepository.save(existingOwner);
        log.info("Owner updated successfully with id: {}", updatedOwner.getId());

        return mapToResponse(updatedOwner);
    }

    public void deleteOwner(String id) {
        log.info("Deleting owner with id: {}", id);

        if (!ownerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Owner not found with id: " + id);
        }

        ownerRepository.deleteById(id);
        log.info("Owner deleted successfully with id: {}", id);
    }

    public void deleteOwnerByTgChatId(String tgChatId) {
        log.info("Deleting owner by tgChatId: {}", tgChatId);

        if (!ownerRepository.existsByTgChatId(tgChatId)) {
            throw new ResourceNotFoundException("Owner not found with tgChatId: " + tgChatId);
        }

        ownerRepository.deleteByTgChatId(tgChatId);
        log.info("Owner deleted successfully with tgChatId: {}", tgChatId);
    }

    public boolean existsById(String id) {
        return ownerRepository.existsById(id);
    }

    public String getTgChatIdByOwnerId(String ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
        return owner.getTgChatId();
    }

    public String getTgChatIdByPetId(String petId, PetRepository petRepository) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        Owner owner = ownerRepository.findById(pet.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + pet.getOwnerId()));

        return owner.getTgChatId();
    }

    private Owner mapToEntity(OwnerRequest request) {
        Owner owner = new Owner();
        owner.setId(request.getId());
        owner.setTgChatId(request.getTgChatId());
        owner.setFirstName(request.getFirstName());
        owner.setLastName(request.getLastName());
        owner.setPhone(request.getPhone());
        return owner;
    }

    private OwnerResponse mapToResponse(Owner owner) {
        return OwnerResponse.builder()
                .id(owner.getId())
                .tgChatId(owner.getTgChatId())
                .firstName(owner.getFirstName())
                .lastName(owner.getLastName())
                .phone(owner.getPhone())
                .build();
    }
}