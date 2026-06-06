package org.informatics.winterolympics.controller;

import org.informatics.winterolympics.dto.GameDto;
import org.informatics.winterolympics.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final ManagerService managerService;

    public PublicController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/games")
    public ResponseEntity<List<GameDto>> getAllGames() {
        return ResponseEntity.ok(managerService.getAllGames());
    }
}
