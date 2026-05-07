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
    private String id;
    private Long vkUserId;
    private String firstName;
    private String lastName;
    private String phone;
}