package org.informatics.winterolympics.dto;

public record CreateSkiSlalomRequest(
        String name,
        String sex,
        int minAge,
        int run1Competitors,
        int run2Competitors,
        String timeOfEvent
) {}
