package org.informatics.winterolympics.dto;

import java.util.List;

public record SlalomResultsDto(
        Long slalomId,
        String slalomName,
        String gameName,
        int run2Qualifiers,
        List<SlalomAthleteResultDto> athletes
) {}
