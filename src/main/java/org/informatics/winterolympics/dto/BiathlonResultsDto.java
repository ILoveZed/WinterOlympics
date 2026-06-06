package org.informatics.winterolympics.dto;

import java.util.List;

public record BiathlonResultsDto(
        Long biathlonId,
        String biathlonName,
        String gameName,
        int penaltyTimeSeconds,
        List<BiathlonAthleteResultDto> athletes
) {}
