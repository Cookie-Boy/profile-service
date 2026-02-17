package ru.sibsutis.profile.core.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sibsutis.profile.core.model.Pet;
import ru.sibsutis.profile.core.repository.PetRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public Pet registerPet(Pet pet) {
        String qrCode = generateUniqueQrCode();
        pet.setQrCode(qrCode);
        return petRepository.save(pet);
    }

    public Pet updatePet(String id, Pet updatedPet) {
        return petRepository.findById(id)
                .map(pet -> {
                    pet.setSpecies(updatedPet.getSpecies());
                    pet.setAge(updatedPet.getAge());
                    pet.setBreed(updatedPet.getBreed());
                    pet.setChipNumber(updatedPet.getChipNumber());
                    pet.setMedicalRecord(updatedPet.getMedicalRecord());
                    return petRepository.save(pet);
                })
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));
    }

    public Pet getPetByQrCode(String qrCode) {
        return petRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("Pet not found for QR code: " + qrCode));
    }

    public Pet getPetById(String id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));
    }

    public byte[] generateQrCodeForPet(String petId, int width, int height) throws IOException, WriterException {
        Pet pet = getPetById(petId);
        String url = buildPetInfoUrl(pet.getQrCode());
        return generateQrCodeImage(url, width, height);
    }

    private byte[] generateQrCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    private String generateUniqueQrCode() {
        return UUID.randomUUID().toString();
    }

    private String buildPetInfoUrl(String qrCode) {
        return baseUrl + "/profile/pets/qr-scan/" + qrCode;
    }
}