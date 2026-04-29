package ru.sibsutis.profile.api.controller;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sibsutis.profile.api.dto.PetRequest;
import ru.sibsutis.profile.api.dto.PetResponse;
import ru.sibsutis.profile.core.service.PetService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profile/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponse registerPet(@RequestBody PetRequest petRequest) {
        return petService.registerPet(petRequest);
    }

    @PutMapping("/{id}")
    public PetResponse updatePet(@PathVariable String id, @RequestBody PetRequest petRequest) {
        return petService.updatePet(id, petRequest);
    }

    @GetMapping("/{id}")
    public PetResponse getPet(@PathVariable String id) {
        return petService.getPetById(id);
    }

    @GetMapping
    public List<PetResponse> getPets(@RequestParam String ownerId) {
        if (ownerId == null) {
            return petService.getAllPets();
        }
        return petService.getOwnerPets(ownerId);
    }

    @GetMapping("/qr/{qrCode}")
    public PetResponse getPetByQrCode(@PathVariable String qrCode) {
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