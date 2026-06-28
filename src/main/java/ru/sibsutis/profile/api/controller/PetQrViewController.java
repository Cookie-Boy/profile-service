package ru.sibsutis.profile.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.sibsutis.profile.api.dto.OwnerResponse;
import ru.sibsutis.profile.api.dto.PetResponse;
import ru.sibsutis.profile.core.service.OwnerService;
import ru.sibsutis.profile.core.service.PetService;

@Controller
@RequestMapping("/profile/pets")
@RequiredArgsConstructor
public class PetQrViewController {

    private final PetService petService;
    private final OwnerService ownerService;

    @GetMapping("/qr-scan/{qrCode}")
    public String viewPetByQrCode(@PathVariable String qrCode, Model model) {
        PetResponse pet = petService.getPetByQrCode(qrCode);
        OwnerResponse owner = ownerService.getOwnerById(pet.getOwnerId());

        model.addAttribute("pet", pet);
        model.addAttribute("owner", owner);

        return "pet-info";
    }
}