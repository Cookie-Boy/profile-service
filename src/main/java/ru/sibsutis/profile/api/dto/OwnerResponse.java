package ru.sibsutis.profile.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerResponse {
    private String id;
    private Long vkUserId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
}
