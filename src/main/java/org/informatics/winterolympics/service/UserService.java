package org.informatics.winterolympics.service;

import jakarta.ws.rs.core.Response;
import org.informatics.winterolympics.config.KeycloakProperties;
import org.informatics.winterolympics.dto.LoginRequest;
import org.informatics.winterolympics.dto.LoginResponse;
import org.informatics.winterolympics.dto.RegisterRequest;
import org.informatics.winterolympics.model.Athlete;
import org.informatics.winterolympics.model.User;
import org.informatics.winterolympics.repository.UserRepository;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.informatics.winterolympics.dto.UpdateProfileRequest;
import org.informatics.winterolympics.dto.UserProfileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final Keycloak keycloak;
    private final WebClient webClient;

    private final String serverUrl;
    private final String realm;
    private final String clientId;
    private final String clientSecret;
    private final String targetClientId;

    public UserService(
            UserRepository userRepository,
            Keycloak keycloak,
            KeycloakProperties keycloakProperties
    )
    {
        this.keycloak = keycloak;
        this.userRepository = userRepository;

        this.serverUrl = keycloakProperties.getServerUrl();
        this.realm = keycloakProperties.getRealm();
        this.clientId = keycloakProperties.getAdminClientId();
        this.clientSecret = keycloakProperties.getAdminClientSecret();
        this.targetClientId = keycloakProperties.getTargetClientId();
        this.webClient = WebClient.create(serverUrl);
    }

    public void register(RegisterRequest request){
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(true);
        user.setEmailVerified(true);

        Response response = keycloak.realm(realm).users().create(user);

        if (response.getStatus() == 409) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (response.getStatus() != 201) {
            throw new RuntimeException("Could not create Keycloak user. Status: " + response.getStatus());
        }

        String userId = CreatedResponseUtil.getCreatedId(response);

        CredentialRepresentation password = new CredentialRepresentation();
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(request.password());
        password.setTemporary(false);

        keycloak.realm(realm)
                .users()
                .get(userId)
                .resetPassword(password);

        ClientRepresentation client = keycloak.realm(realm)
                .clients()
                .findByClientId(targetClientId)
                .get(0);

        RoleRepresentation userRole = keycloak.realm(realm)
                .clients()
                .get(client.getId())
                .roles()
                .get("USER")
                .toRepresentation();

        keycloak.realm(realm)
                .users()
                .get(userId)
                .roles()
                .clientLevel(client.getId())
                .add(List.of(userRole));

        addUserToDb(request, userId);
    }

    public LoginResponse login(LoginRequest request) {
        String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        Map<?, ?> rawResponse = webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", request.username())
                        .with("password", request.password()))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (rawResponse == null) {
            throw new RuntimeException("Login failed");
        }

        String accessToken = rawResponse.get("access_token").toString();
        String refreshToken = rawResponse.get("refresh_token").toString();
        Integer expiresIn = (Integer) rawResponse.get("expires_in");
        String tokenType = rawResponse.get("token_type").toString();

        return new LoginResponse(
                accessToken,
                refreshToken,
                expiresIn,
                tokenType
        );
    }

    public UserProfileDto getUserProfile(String keycloakUserId) {
        UserRepresentation ku = keycloak.realm(realm).users().get(keycloakUserId).toRepresentation();
        User user = userRepository.findByKeycloakUserId(keycloakUserId).orElseThrow();
        Athlete athlete = user.getAthlete();
        return new UserProfileDto(
                ku.getUsername(),
                ku.getFirstName(),
                ku.getLastName(),
                athlete != null ? athlete.getCountry() : null,
                athlete != null ? athlete.getSex() : null,
                athlete != null && athlete.getDateOfBirth() != null ? athlete.getDateOfBirth().toString() : null
        );
    }

    @Transactional
    public void updateProfile(String keycloakUserId, UpdateProfileRequest request) {
        UserRepresentation ku = keycloak.realm(realm).users().get(keycloakUserId).toRepresentation();
        ku.setFirstName(request.firstName());
        ku.setLastName(request.lastName());
        keycloak.realm(realm).users().get(keycloakUserId).update(ku);

        User user = userRepository.findByKeycloakUserId(keycloakUserId).orElseThrow();
        Athlete athlete = user.getAthlete();
        if (athlete != null) {
            athlete.setName(request.firstName() + " " + request.lastName());
            athlete.setCountry(request.country());
        }
        userRepository.save(user);
    }

    public void addUserToDb (RegisterRequest request, String keycloakUserId){
        Athlete athlete = new Athlete(
                request.firstName() + " " + request.lastName(),
                request.country(),
                request.sex(),
                request.birthDate()
        );

        User user = new User(request.username(), keycloakUserId, athlete);

        userRepository.save(user);
    }
}
