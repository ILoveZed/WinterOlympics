package org.informatics.winterolympics.dto;

public record RegistrationDto(
        Long id,
        String competitionType,
        String competitionName,
        Long competitionId,
        String gameName,
        String username,
        String athleteName,
        String status,
        String appliedAt
) {}
