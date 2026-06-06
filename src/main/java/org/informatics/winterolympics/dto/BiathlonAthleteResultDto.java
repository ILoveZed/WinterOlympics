package org.informatics.winterolympics.dto;

public record BiathlonAthleteResultDto(
        Long registrationId,
        String athleteName,
        String username,
        Double raceTimeSeconds,
        Integer numberOfMisses,
        Double finalTime,
        Integer rank
) {}
