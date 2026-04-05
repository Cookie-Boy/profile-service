package ru.sibsutis.profile.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerRequest {
    private String id;           // Keycloak ID
    private String tgChatId;
    private String firstName;
    private String lastName;
    private String phone;
}