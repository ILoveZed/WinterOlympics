package org.informatics.winterolympics.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Integer expiresIn,
        String tokenType
) {
}
