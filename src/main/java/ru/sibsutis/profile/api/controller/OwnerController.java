package ru.sibsutis.profile.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sibsutis.profile.api.dto.OwnerRequest;
import ru.sibsutis.profile.api.dto.OwnerResponse;
import ru.sibsutis.profile.core.service.OwnerService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/profile/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    // POST /profile/owners
    @PostMapping
    public ResponseEntity<OwnerResponse> createOwner(@RequestBody OwnerRequest request) {
        OwnerResponse response = ownerService.createOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /profile/owners/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponse> getOwnerById(@PathVariable String id) {
        OwnerResponse response = ownerService.getOwnerById(id);
        return ResponseEntity.ok(response);
    }

    // GET /profile/owners
    @GetMapping
    public ResponseEntity<List<OwnerResponse>> getAllOwners() {
        List<OwnerResponse> owners = ownerService.getAllOwners();
        return ResponseEntity.ok(owners);
    }

    // PUT /profile/owners/{id}
    @PutMapping("/{id}")
    public ResponseEntity<OwnerResponse> updateOwner(
            @PathVariable String id,
            @RequestBody OwnerRequest request) {
        OwnerResponse response = ownerService.updateOwner(id, request);
        return ResponseEntity.ok(response);
    }

    // DELETE /profile/owners/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwner(@PathVariable String id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/link-vk")
    public ResponseEntity<OwnerResponse> linkVk(@PathVariable String id,
                                                @RequestBody String token) {
        OwnerResponse response = ownerService.linkVk(id, token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-vk-id/{vkUserId}")
    public ResponseEntity<OwnerResponse> getOwnerByVkUserId(@PathVariable Long vkUserId) {
        OwnerResponse response = ownerService.getOwnerByVkUserId(vkUserId);
        return ResponseEntity.ok(response);
    }

}