package org.informatics.winterolympics.service;

import jakarta.ws.rs.core.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.informatics.winterolympics.config.KeycloakProperties;
import org.informatics.winterolympics.dto.LoginRequest;
import org.informatics.winterolympics.dto.LoginResponse;
import org.informatics.winterolympics.dto.RegisterRequest;
import org.informatics.winterolympics.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private Keycloak keycloak;
    @Mock private RealmResource realmResource;
    @Mock private UsersResource usersResource;
    @Mock private UserResource userResource;
    @Mock private ClientsResource clientsResource;
    @Mock private ClientResource clientResource;
    @Mock private RolesResource rolesResource;
    @Mock private RoleResource roleResource;
    @Mock private RoleMappingResource roleMappingResource;
    @Mock private RoleScopeResource roleScopeResource;

    private MockWebServer mockWebServer;
    private UserService userService;

    private static final String REALM = "TestRealm";
    private static final String TARGET_CLIENT_ID = "spring-api";
    private static final String KEYCLOAK_USER_ID = "user-uuid-123";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        KeycloakProperties props = new KeycloakProperties();
        props.setServerUrl("http://localhost:" + mockWebServer.getPort());
        props.setRealm(REALM);
        props.setAdminClientId("admin-cli");
        props.setAdminClientSecret("secret");
        props.setTargetClientId(TARGET_CLIENT_ID);

        userService = new UserService(userRepository, keycloak, props);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void register_success_createsUserInKeycloakAndSavesToDb() {
        Response createdResponse = mock(Response.class);
        when(createdResponse.getStatus()).thenReturn(201);
        when(createdResponse.getStatusInfo()).thenReturn(Response.Status.CREATED);
        when(createdResponse.getLocation()).thenReturn(
                URI.create("http://localhost/admin/realms/TestRealm/users/" + KEYCLOAK_USER_ID));

        ClientRepresentation clientRep = new ClientRepresentation();
        clientRep.setId("client-uuid");

        RoleRepresentation userRole = new RoleRepresentation();
        userRole.setName("USER");

        when(keycloak.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(realmResource.clients()).thenReturn(clientsResource);
        when(usersResource.create(any())).thenReturn(createdResponse);
        when(usersResource.get(KEYCLOAK_USER_ID)).thenReturn(userResource);
        when(clientsResource.findByClientId(TARGET_CLIENT_ID)).thenReturn(List.of(clientRep));
        when(clientsResource.get("client-uuid")).thenReturn(clientResource);
        when(clientResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get("USER")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(userRole);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.clientLevel("client-uuid")).thenReturn(roleScopeResource);

        RegisterRequest request = new RegisterRequest(
                "testuser", "pass123", "John", "Doe",
                "Bulgaria", "Male", Date.valueOf("2000-01-01"));

        userService.register(request);

        verify(usersResource).create(any());
        verify(userResource).resetPassword(any());
        verify(roleScopeResource).add(any());
        verify(userRepository).save(any());
    }

    @Test
    void register_keycloakReturns409_throwsRuntimeException() {
        Response conflictResponse = mock(Response.class);
        when(conflictResponse.getStatus()).thenReturn(409);

        when(keycloak.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any())).thenReturn(conflictResponse);

        RegisterRequest request = new RegisterRequest(
                "existinguser", "pass123", "John", "Doe",
                "Bulgaria", "Male", Date.valueOf("2000-01-01"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.register(request));
        assertTrue(ex.getMessage().contains("409"));
    }

    @Test
    void login_validCredentials_returnsLoginResponse() {
        String tokenJson = """
                {
                  "access_token": "eyJ.access.token",
                  "refresh_token": "eyJ.refresh.token",
                  "expires_in": 300,
                  "token_type": "Bearer"
                }
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(tokenJson)
                .addHeader("Content-Type", "application/json"));

        LoginResponse response = userService.login(new LoginRequest("testuser", "pass123"));

        assertNotNull(response);
        assertEquals("eyJ.access.token", response.accessToken());
        assertEquals("eyJ.refresh.token", response.refreshToken());
        assertEquals(300, response.expiresIn());
        assertEquals("Bearer", response.tokenType());
    }

    @Test
    void login_serverReturnsError_throwsException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\":\"invalid_grant\"}")
                .addHeader("Content-Type", "application/json"));

        assertThrows(Exception.class, () -> userService.login(new LoginRequest("user", "wrongpass")));
    }
}
