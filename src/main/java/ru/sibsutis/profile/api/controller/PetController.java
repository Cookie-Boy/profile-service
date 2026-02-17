package ru.sibsutis.profile.api.controller;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibsutis.profile.core.model.Pet;
import ru.sibsutis.profile.core.service.PetService;

import java.io.IOException;

@RestController
@RequestMapping("/profile/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pet registerPet(@RequestBody Pet pet) {
        return petService.registerPet(pet);
    }

    @PutMapping("/{id}")
    public Pet updatePet(@PathVariable String id, @RequestBody Pet pet) {
        return petService.updatePet(id, pet);
    }

    @GetMapping("/{id}")
    public Pet getPet(@PathVariable String id) {
        return petService.getPetById(id);
    }

    @GetMapping("/qr/{qrCode}")
    public Pet getPetByQrCode(@PathVariable String qrCode) {
        return petService.getPetByQrCode(qrCode);
    }

    @GetMapping(value = "/{id}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getPetQrCode(@PathVariable String id,
                                             @RequestParam(defaultValue = "300") int width,
                                             @RequestParam(defaultValue = "300") int height)
            throws IOException, WriterException {
        return petService.generateQrCodeForPet(id, width, height);
    }
}