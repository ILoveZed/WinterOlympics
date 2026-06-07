package org.informatics.winterolympics.controller;

import org.informatics.winterolympics.dto.BiathlonResultsDto;
import org.informatics.winterolympics.dto.GameDto;
import org.informatics.winterolympics.dto.SlalomResultsDto;
import org.informatics.winterolympics.service.ManagerService;
import org.informatics.winterolympics.service.ResultsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private static final Logger log = LoggerFactory.getLogger(PublicController.class);

    private final ManagerService managerService;
    private final ResultsService resultsService;

    public PublicController(ManagerService managerService, ResultsService resultsService) {
        this.managerService = managerService;
        this.resultsService = resultsService;
    }

    @GetMapping("/games")
    public ResponseEntity<List<GameDto>> getAllGames() {
        return ResponseEntity.ok(managerService.getAllGames());
    }

    @GetMapping("/results/slalom/{slalomId}")
    public ResponseEntity<SlalomResultsDto> getSlalomResults(@PathVariable Long slalomId) {
        try {
            return ResponseEntity.ok(resultsService.getSlalomResults(slalomId));
        } catch (RuntimeException e) {
            log.error("Get slalom results {} failed: {}", slalomId, e.getMessage());
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/results/biathlon/{biathlonId}")
    public ResponseEntity<BiathlonResultsDto> getBiathlonResults(@PathVariable Long biathlonId) {
        try {
            return ResponseEntity.ok(resultsService.getBiathlonResults(biathlonId));
        } catch (RuntimeException e) {
            log.error("Get biathlon results {} failed: {}", biathlonId, e.getMessage());
            return ResponseEntity.status(404).build();
        }
    }
}
