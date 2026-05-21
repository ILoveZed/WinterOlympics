package org.informatics.winterolympics.dto;

import java.sql.Date;

public record RegisterRequest(
        String username,
        String password,
        String firstName,
        String lastName,
        String country,
        String sex,
        Date birthDate) {

}
