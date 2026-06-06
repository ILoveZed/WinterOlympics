package org.informatics.winterolympics.dto;

public record BiathlonResultEntry(
        Long registrationId,
        double raceTimeSeconds,
        int numberOfMisses
) {}
