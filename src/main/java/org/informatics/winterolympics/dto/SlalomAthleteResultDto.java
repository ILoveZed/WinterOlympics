package org.informatics.winterolympics.dto;

public record SlalomAthleteResultDto(
        Long registrationId,
        String athleteName,
        String username,
        Double run1TimeSeconds,
        Integer run1Rank,
        boolean qualifiedForRun2,
        Double run2TimeSeconds,
        Double combinedTime,
        Integer finalRank
) {}
