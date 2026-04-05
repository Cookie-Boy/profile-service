package ru.sibsutis.profile.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sibsutis.profile.api.dto.OwnerResponse;
import ru.sibsutis.profile.api.dto.PetResponse;
import ru.sibsutis.profile.core.service.OwnerService;
import ru.sibsutis.profile.core.service.PetService;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final PetService petService;
    private final OwnerService ownerService;

    @GetMapping("/owner/tg-chat-id")  // /owner/tg-chat-id?petId=...
    public String getOwnerTgChatId(@RequestParam String petId) {
        PetResponse pet = petService.getPetById(petId);
        OwnerResponse owner = ownerService.getOwnerById(pet.getOwnerId());
        return owner.getTgChatId();
    }
}
