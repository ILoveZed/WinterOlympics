package org.informatics.winterolympics.dto;

public record CreateBiathlonRequest(
        String name,
        String sex,
        int minAge,
        int numCompetitors,
        int numLaps,
        int numShootings,
        int lapsBetweenShootings,
        int penaltyTimeSeconds,
        String timeOfEvent
) {}
