package org.informatics.winterolympics.dto;

import java.util.List;

public record GameDto(
        Long id,
        String name,
        String country,
        String city,
        String year,
        List<SkiSlalomDto> skiSlaloms,
        List<BiathlonDto> biathlons
) {}
