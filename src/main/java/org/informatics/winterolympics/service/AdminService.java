package org.informatics.winterolympics.service;

import org.informatics.winterolympics.config.KeycloakProperties;
import org.informatics.winterolympics.dto.UserDto;
import org.informatics.winterolympics.repository.UserRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AdminService {

    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final String realm;
    private final String targetClientId;

    private static final Set<String> MANAGED_ROLES = Set.of("USER", "ADMIN", "MANAGER");

    public AdminService(Keycloak keycloak, UserRepository userRepository, KeycloakProperties props) {
        this.keycloak = keycloak;
        this.userRepository = userRepository;
        this.realm = props.getRealm();
        this.targetClientId = props.getTargetClientId();
    }

    private String getClientInternalId() {
        return keycloak.realm(realm).clients()
                .findByClientId(targetClientId).get(0).getId();
    }

    public List<UserDto> getAllUsers() {
        String clientId = getClientInternalId();
        return keycloak.realm(realm).users().list().stream()
                .map(u -> {
                    List<String> roles = keycloak.realm(realm)
                            .users().get(u.getId())
                            .roles().clientLevel(clientId)
                            .listEffective().stream()
                            .map(RoleRepresentation::getName)
                            .filter(MANAGED_ROLES::contains)
                            .toList();
                    return new UserDto(u.getUsername(), u.getId(), roles);
                })
                .toList();
    }

    public void deleteUser(String keycloakUserId) {
        keycloak.realm(realm).users().get(keycloakUserId).remove();
        userRepository.findByKeycloakUserId(keycloakUserId).ifPresent(userRepository::delete);
    }

    public void changePassword(String keycloakUserId, String newPassword) {
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(newPassword);
        cred.setTemporary(false);
        keycloak.realm(realm).users().get(keycloakUserId).resetPassword(cred);
    }

    public void assignRoles(String keycloakUserId, List<String> roleNames) {
        String clientId = getClientInternalId();

        List<RoleRepresentation> existing = keycloak.realm(realm)
                .users().get(keycloakUserId)
                .roles().clientLevel(clientId)
                .listEffective().stream()
                .filter(r -> MANAGED_ROLES.contains(r.getName()))
                .toList();

        if (!existing.isEmpty()) {
            keycloak.realm(realm).users().get(keycloakUserId)
                    .roles().clientLevel(clientId).remove(existing);
        }

        if (!roleNames.isEmpty()) {
            List<RoleRepresentation> toAdd = roleNames.stream()
                    .filter(MANAGED_ROLES::contains)
                    .map(name -> keycloak.realm(realm).clients().get(clientId)
                            .roles().get(name).toRepresentation())
                    .toList();
            keycloak.realm(realm).users().get(keycloakUserId)
                    .roles().clientLevel(clientId).add(toAdd);
        }
    }
}
