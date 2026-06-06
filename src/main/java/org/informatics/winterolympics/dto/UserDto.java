package org.informatics.winterolympics.dto;

import java.util.List;

public record UserDto(String username, String keycloakUserId, List<String> roles) {}
