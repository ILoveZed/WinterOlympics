package org.informatics.winterolympics.dto;

public record LoginRequest(
        String username,
        String password
) {
}
