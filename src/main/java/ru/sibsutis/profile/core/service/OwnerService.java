package ru.sibsutis.profile.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import ru.sibsutis.profile.api.dto.OwnerRequest;
import ru.sibsutis.profile.api.dto.OwnerResponse;
import ru.sibsutis.profile.api.exception.DuplicateResourceException;
import ru.sibsutis.profile.api.exception.ResourceNotFoundException;
import ru.sibsutis.profile.core.model.Owner;
import ru.sibsutis.profile.core.repository.OwnerRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final StringRedisTemplate redisTemplate;

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

    public List<OwnerResponse> findOwnersByQuery(String query) {
        log.info("Searching owners by query: {}", query);

        if (query == null || query.length() < 2) {
            return Collections.emptyList();
        }

        String searchPattern = query.toLowerCase();

        List<Owner> owners = ownerRepository.findAll().stream()
                .filter(owner ->
                                owner.getEmail().toLowerCase().contains(searchPattern) ||
                                owner.getPhone().toLowerCase().contains(searchPattern) ||
                                owner.getFirstName().toLowerCase().contains(searchPattern) ||
                                owner.getLastName().toLowerCase().contains(searchPattern)
                )
                .toList();

        log.info("Found {} owners matching query: {}", owners.size(), query);

        return owners.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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

    public OwnerResponse linkVk(String ownerId, String token) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));

        String vkIdStr = redisTemplate.opsForValue().get("link-token:" + token);
        if (vkIdStr == null) {
            throw new ResourceNotFoundException("Token for authorization not found in Redis: " + token);
        }

        Long vkId = Long.parseLong(vkIdStr);
        owner.setVkUserId(vkId);
        Owner updatedOwner = ownerRepository.save(owner);
        return mapToResponse(updatedOwner);
    }

    private Owner mapToEntity(OwnerRequest request) {
        Owner owner = new Owner();
        owner.setId(request.getId());
        owner.setVkUserId(request.getVkUserId());
        owner.setEmail(request.getEmail());
        owner.setFirstName(request.getFirstName());
        owner.setLastName(request.getLastName());
        owner.setPhone(request.getPhone());
        return owner;
    }

    private OwnerResponse mapToResponse(Owner owner) {
        log.info("Mapping owner email: {}", owner.getEmail());
        return OwnerResponse.builder()
                .id(owner.getId())
                .vkUserId(owner.getVkUserId())
                .email(owner.getEmail())
                .firstName(owner.getFirstName())
                .lastName(owner.getLastName())
                .phone(owner.getPhone())
                .build();
    }
}