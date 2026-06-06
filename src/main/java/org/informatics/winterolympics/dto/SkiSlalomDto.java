package org.informatics.winterolympics.dto;

public record SkiSlalomDto(
        Long id,
        String name,
        String sex,
        int minAge,
        int run1Competitors,
        int run2Competitors,
        String timeOfEvent
) {}
