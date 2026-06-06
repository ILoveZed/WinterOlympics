package org.informatics.winterolympics.controller;

import org.informatics.winterolympics.dto.ChangePasswordRequest;
import org.informatics.winterolympics.dto.RegistrationDto;
import org.informatics.winterolympics.dto.UpdateProfileRequest;
import org.informatics.winterolympics.dto.UserProfileDto;
import org.informatics.winterolympics.service.AdminService;
import org.informatics.winterolympics.service.RegistrationService;
import org.informatics.winterolympics.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AdminService adminService;
    private final RegistrationService registrationService;

    public UserController(UserService userService, AdminService adminService,
                          RegistrationService registrationService) {
        this.userService = userService;
        this.adminService = adminService;
        this.registrationService = registrationService;
    }

    private String userId(Authentication auth) {
        return ((JwtAuthenticationToken) auth).getToken().getSubject();
    }

    @GetMapping
    public ResponseEntity<UserProfileDto> getProfile(Authentication auth) {
        return ResponseEntity.ok(userService.getUserProfile(userId(auth)));
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(Authentication auth, @RequestBody UpdateProfileRequest request) {
        try {
            userService.updateProfile(userId(auth), request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Profile update failed: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(Authentication auth, @RequestBody ChangePasswordRequest request) {
        try {
            adminService.changePassword(userId(auth), request.newPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Password change failed: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/registrations")
    public ResponseEntity<List<RegistrationDto>> getMyRegistrations(Authentication auth) {
        return ResponseEntity.ok(registrationService.getMyRegistrations(userId(auth)));
    }

    @PostMapping("/apply/slalom/{slalomId}")
    public ResponseEntity<String> applyForSlalom(Authentication auth, @PathVariable Long slalomId) {
        try {
            registrationService.applyForSlalom(userId(auth), slalomId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Apply for slalom {} failed: {}", slalomId, e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/apply/biathlon/{biathlonId}")
    public ResponseEntity<String> applyForBiathlon(Authentication auth, @PathVariable Long biathlonId) {
        try {
            registrationService.applyForBiathlon(userId(auth), biathlonId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Apply for biathlon {} failed: {}", biathlonId, e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
