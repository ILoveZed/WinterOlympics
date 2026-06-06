package org.informatics.winterolympics.controller;

import org.informatics.winterolympics.dto.*;
import org.informatics.winterolympics.service.ManagerService;
import org.informatics.winterolympics.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

    private static final Logger log = LoggerFactory.getLogger(ManagerController.class);

    private final ManagerService managerService;
    private final RegistrationService registrationService;

    public ManagerController(ManagerService managerService, RegistrationService registrationService) {
        this.managerService = managerService;
        this.registrationService = registrationService;
    }

    @GetMapping("/games")
    public ResponseEntity<List<GameDto>> getAllGames() {
        return ResponseEntity.ok(managerService.getAllGames());
    }

    @PostMapping("/games")
    public ResponseEntity<GameDto> createGame(@RequestBody CreateGameRequest request) {
        return ResponseEntity.ok(managerService.createGame(request));
    }

    @PostMapping("/games/{gameId}/slalom")
    public ResponseEntity<String> addSlalom(@PathVariable Long gameId,
                                            @RequestBody CreateSkiSlalomRequest request) {
        try {
            managerService.addSlalom(gameId, request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Add slalom failed: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<String> deleteGame(@PathVariable Long gameId) {
        try {
            managerService.deleteGame(gameId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Delete game {} failed: {}", gameId, e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/games/{gameId}/slalom/{slalomId}")
    public ResponseEntity<String> deleteSlalom(@PathVariable Long gameId,
                                               @PathVariable Long slalomId) {
        try {
            managerService.deleteSlalom(slalomId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Delete slalom {} failed: {}", slalomId, e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/games/{gameId}/biathlon/{biathlonId}")
    public ResponseEntity<String> deleteBiathlon(@PathVariable Long gameId,
                                                 @PathVariable Long biathlonId) {
        try {
            managerService.deleteBiathlon(biathlonId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Delete biathlon {} failed: {}", biathlonId, e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/games/{gameId}/biathlon")
    public ResponseEntity<String> addBiathlon(@PathVariable Long gameId,
                                              @RequestBody CreateBiathlonRequest request) {
        try {
            managerService.addBiathlon(gameId, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Add biathlon failed: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/applications")
    public ResponseEntity<List<RegistrationDto>> getAllApplications() {
        return ResponseEntity.ok(registrationService.getAllRegistrations());
    }

    @PutMapping("/applications/{id}/accept")
    public ResponseEntity<String> acceptApplication(@PathVariable Long id) {
        try {
            registrationService.accept(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Accept application {} failed: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/applications/{id}/reject")
    public ResponseEntity<String> rejectApplication(@PathVariable Long id) {
        try {
            registrationService.reject(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Reject application {} failed: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
