package ru.sibsutis.profile.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.sibsutis.profile.core.model.Pet;
import ru.sibsutis.profile.core.service.PetService;

@Controller
@RequestMapping("/profile/pets")
@RequiredArgsConstructor
public class PetQrViewController {

    private final PetService petService;

    @GetMapping("/qr-scan/{qrCode}")
    public String viewPetByQrCode(@PathVariable String qrCode, Model model) {
        Pet pet = petService.getPetByQrCode(qrCode);
        model.addAttribute("pet", pet);
        return "pet-info"; // В будущем должна быть страница на фронте -> UI профиля питомца, но пока так
    }
}
