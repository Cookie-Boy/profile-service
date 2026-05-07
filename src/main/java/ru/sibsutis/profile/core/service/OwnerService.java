package ru.sibsutis.profile.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sibsutis.profile.api.dto.OwnerRequest;
import ru.sibsutis.profile.api.dto.OwnerResponse;
import ru.sibsutis.profile.api.exception.DuplicateResourceException;
import ru.sibsutis.profile.api.exception.ResourceNotFoundException;
import ru.sibsutis.profile.core.model.Owner;
import ru.sibsutis.profile.core.repository.OwnerRepository;

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

        if (request.getVkUserId() != null && ownerRepository.existsByVkUserId(request.getVkUserId())) {
            throw new DuplicateResourceException("Owner with VkUserId " + request.getVkUserId() + " already exists");
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

    public OwnerResponse getOwnerByVkUserId(Long vkUserId) {
        log.info("Fetching owner by vkUserId: {}", vkUserId);

        Owner owner = ownerRepository.findByVkUserId(vkUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with vkUserId: " + vkUserId));

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

        // Проверяем уникальность VkUserId при изменении
        if (request.getVkUserId() != null && !request.getVkUserId().equals(existingOwner.getVkUserId())) {
            if (ownerRepository.existsByVkUserId(request.getVkUserId())) {
                throw new DuplicateResourceException("Owner with VkUserId " + request.getVkUserId() + " already exists");
            }
            existingOwner.setVkUserId(request.getVkUserId());
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

    private Owner mapToEntity(OwnerRequest request) {
        Owner owner = new Owner();
        owner.setId(request.getId());
        owner.setVkUserId(request.getVkUserId());
        owner.setFirstName(request.getFirstName());
        owner.setLastName(request.getLastName());
        owner.setPhone(request.getPhone());
        return owner;
    }

    private OwnerResponse mapToResponse(Owner owner) {
        return OwnerResponse.builder()
                .id(owner.getId())
                .vkUserId(owner.getVkUserId())
                .firstName(owner.getFirstName())
                .lastName(owner.getLastName())
                .phone(owner.getPhone())
                .build();
    }
}