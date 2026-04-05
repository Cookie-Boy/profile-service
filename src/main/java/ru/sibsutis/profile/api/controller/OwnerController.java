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

    // GET /profile/owners/by-tg-chat-id?tgChatId={tgChatId}
    @GetMapping("/by-tg-chat-id")
    public ResponseEntity<OwnerResponse> getOwnerByTgChatId(@RequestParam String tgChatId) {
        OwnerResponse response = ownerService.getOwnerByTgChatId(tgChatId);
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

    // DELETE /profile/owners/by-tg-chat-id?tgChatId={tgChatId}
    @DeleteMapping("/by-tg-chat-id")
    public ResponseEntity<Void> deleteOwnerByTgChatId(@RequestParam String tgChatId) {
        ownerService.deleteOwnerByTgChatId(tgChatId);
        return ResponseEntity.noContent().build();
    }

    // GET /profile/owners/{id}/tg-chat-id
    @GetMapping("/{id}/tg-chat-id")
    public ResponseEntity<String> getTgChatId(@PathVariable String id) {
        String tgChatId = ownerService.getTgChatIdByOwnerId(id);
        return ResponseEntity.ok(tgChatId);
    }
}