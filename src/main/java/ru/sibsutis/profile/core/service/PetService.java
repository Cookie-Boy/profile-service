package ru.sibsutis.profile.core.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sibsutis.profile.api.dto.PetRequest;
import ru.sibsutis.profile.api.dto.PetResponse;
import ru.sibsutis.profile.api.exception.ResourceNotFoundException;
import ru.sibsutis.profile.core.model.Pet;
import ru.sibsutis.profile.core.repository.PetRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public PetResponse registerPet(PetRequest request) {
        log.info("Registering new pet for ownerId: {}", request.getOwnerId());

        Pet pet = mapToEntity(request);
        String qrCode = generateUniqueQrCode();
        pet.setQrCode(qrCode);
        Pet savedPet = petRepository.save(pet);

        log.info("Pet registered with id: {}", savedPet.getId());
        return PetResponse.fromPet(savedPet);
    }

    public PetResponse updatePet(String id, PetRequest request) {
        log.info("Updating pet with id: {}", id);

        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        updateEntityFromRequest(existingPet, request);
        Pet updatedPet = petRepository.save(existingPet);

        log.info("Pet updated with id: {}", id);
        return PetResponse.fromPet(updatedPet);
    }

    public PetResponse getPetById(String id) {
        log.info("Fetching pet by id: {}", id);

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        return PetResponse.fromPet(pet);
    }

    public PetResponse getPetByQrCode(String qrCode) {
        log.info("Fetching pet by qrCode: {}", qrCode);

        Pet pet = petRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found for QR code: " + qrCode));

        return PetResponse.fromPet(pet);
    }

    public byte[] generateQrCodeForPet(String petId, int width, int height) throws IOException, WriterException {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

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

    // Маппинг из DTO в сущность
    private Pet mapToEntity(PetRequest request) {
        Pet pet = new Pet();
        pet.setOwnerId(request.getOwnerId());
        pet.setSpecies(request.getSpecies());
        pet.setAge(request.getAge());
        pet.setBreed(request.getBreed());
        pet.setChipNumber(request.getChipNumber());

        if (request.getMedicalRecord() != null) {
            Pet.MedicalRecord record = new Pet.MedicalRecord();
            record.setAllergies(request.getMedicalRecord().getAllergies());
            record.setChronicDiseases(request.getMedicalRecord().getChronicDiseases());

            if (request.getMedicalRecord().getVaccinations() != null) {
                record.setVaccinations(request.getMedicalRecord().getVaccinations().stream()
                        .map(v -> {
                            Pet.MedicalRecord.Vaccination vac = new Pet.MedicalRecord.Vaccination();
                            vac.setName(v.getName());
                            vac.setDate(v.getDate());
                            vac.setNextDueDate(v.getNextDueDate());
                            return vac;
                        })
                        .toList());
            }
            pet.setMedicalRecord(record);
        }

        if (request.getCollar() != null) {
            Pet.Collar collar = new Pet.Collar();
            collar.setActive(request.getCollar().getActive());

            if (request.getCollar().getHomeInfo() != null) {
                Pet.Collar.HomeInfo homeInfo = new Pet.Collar.HomeInfo();
                homeInfo.setAlerting(request.getCollar().getHomeInfo().getAlerting());
                homeInfo.setRadius(request.getCollar().getHomeInfo().getRadius());
                homeInfo.setLat(request.getCollar().getHomeInfo().getLat());
                homeInfo.setLon(request.getCollar().getHomeInfo().getLon());
                collar.setHomeInfo(homeInfo);
            }
            pet.setCollar(collar);
        }

        return pet;
    }

    // Обновление сущности из DTO (не обновляем qrCode и id)
    private void updateEntityFromRequest(Pet pet, PetRequest request) {
        if (request.getOwnerId() != null) {
            pet.setOwnerId(request.getOwnerId());
        }
        if (request.getSpecies() != null) {
            pet.setSpecies(request.getSpecies());
        }
        if (request.getAge() != null) {
            pet.setAge(request.getAge());
        }
        if (request.getBreed() != null) {
            pet.setBreed(request.getBreed());
        }
        if (request.getChipNumber() != null) {
            pet.setChipNumber(request.getChipNumber());
        }
        if (request.getMedicalRecord() != null) {
            // Полное обновление медкарты (можно реализовать частичное, но для простоты перезаписываем)
            Pet.MedicalRecord record = new Pet.MedicalRecord();
            record.setAllergies(request.getMedicalRecord().getAllergies());
            record.setChronicDiseases(request.getMedicalRecord().getChronicDiseases());
            if (request.getMedicalRecord().getVaccinations() != null) {
                record.setVaccinations(request.getMedicalRecord().getVaccinations().stream()
                        .map(v -> {
                            Pet.MedicalRecord.Vaccination vac = new Pet.MedicalRecord.Vaccination();
                            vac.setName(v.getName());
                            vac.setDate(v.getDate());
                            vac.setNextDueDate(v.getNextDueDate());
                            return vac;
                        })
                        .toList());
            }
            pet.setMedicalRecord(record);
        }
        if (request.getCollar() != null) {
            Pet.Collar collar = new Pet.Collar();
            collar.setActive(request.getCollar().getActive());
            if (request.getCollar().getHomeInfo() != null) {
                Pet.Collar.HomeInfo homeInfo = new Pet.Collar.HomeInfo();
                homeInfo.setAlerting(request.getCollar().getHomeInfo().getAlerting());
                homeInfo.setRadius(request.getCollar().getHomeInfo().getRadius());
                homeInfo.setLat(request.getCollar().getHomeInfo().getLat());
                homeInfo.setLon(request.getCollar().getHomeInfo().getLon());
                collar.setHomeInfo(homeInfo);
            }
            pet.setCollar(collar);
        }
    }
}