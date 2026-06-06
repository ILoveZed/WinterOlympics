package org.informatics.winterolympics.controller;

import org.informatics.winterolympics.dto.AssignRolesRequest;
import org.informatics.winterolympics.dto.ChangePasswordRequest;
import org.informatics.winterolympics.dto.RegisterRequest;
import org.informatics.winterolympics.dto.UserDto;
import org.informatics.winterolympics.service.AdminService;
import org.informatics.winterolympics.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            log.error("Admin create user failed for '{}': {}", request.username(), e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{keycloakUserId}")
    public ResponseEntity<String> deleteUser(@PathVariable String keycloakUserId) {
        try {
            adminService.deleteUser(keycloakUserId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Delete user {} failed: {}", keycloakUserId, e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/users/{keycloakUserId}/password")
    public ResponseEntity<String> changePassword(
            @PathVariable String keycloakUserId,
            @RequestBody ChangePasswordRequest request) {
        try {
            adminService.changePassword(keycloakUserId, request.newPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Change password for {} failed: {}", keycloakUserId, e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/users/{keycloakUserId}/roles")
    public ResponseEntity<String> assignRoles(
            @PathVariable String keycloakUserId,
            @RequestBody AssignRolesRequest request) {
        try {
            adminService.assignRoles(keycloakUserId, request.roles());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Assign roles for {} failed: {}", keycloakUserId, e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
