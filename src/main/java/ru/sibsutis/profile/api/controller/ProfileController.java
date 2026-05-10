package ru.sibsutis.profile.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.sibsutis.profile.api.dto.OwnerResponse;
import ru.sibsutis.profile.api.dto.PetResponse;
import ru.sibsutis.profile.core.service.OwnerService;
import ru.sibsutis.profile.core.service.PetService;

import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final PetService petService;
    private final OwnerService ownerService;

    @GetMapping("/owners/vk-user-id")  // /owner/tg-chat-id?petId=...
    public Long getVkUserIdByPetId(@RequestParam String petId) {
        PetResponse pet = petService.getPetById(petId);
        OwnerResponse owner = ownerService.getOwnerById(pet.getOwnerId());
        return owner.getVkUserId();
    }

    @GetMapping("/pets/vk-user-id/{vkUserId}")
    public List<PetResponse> getPetsByVkUserId(@PathVariable Long vkUserId) {
        OwnerResponse owner = ownerService.getOwnerByVkUserId(vkUserId);
        return petService.getPetsByOwnerId(owner.getId());
    }
}
