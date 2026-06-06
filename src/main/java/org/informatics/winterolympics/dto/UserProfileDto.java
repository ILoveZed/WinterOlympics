package org.informatics.winterolympics.dto;

public record UserProfileDto(
        String username,
        String firstName,
        String lastName,
        String country,
        String sex,
        String birthDate
) {}
