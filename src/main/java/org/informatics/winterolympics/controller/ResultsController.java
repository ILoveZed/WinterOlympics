package org.informatics.winterolympics.controller;

import org.informatics.winterolympics.dto.*;
import org.informatics.winterolympics.service.ResultsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/results")
@PreAuthorize("hasRole('MANAGER')")
public class ResultsController {

    private static final Logger log = LoggerFactory.getLogger(ResultsController.class);

    private final ResultsService resultsService;

    public ResultsController(ResultsService resultsService) {
        this.resultsService = resultsService;
    }

    @PostMapping("/slalom/{slalomId}/run1")
    public ResponseEntity<SlalomResultsDto> saveSlalomRun1(
            @PathVariable Long slalomId,
            @RequestBody List<SlalomResultEntry> entries) {
        try {
            resultsService.saveSlalomRun1Results(slalomId, entries);
            return ResponseEntity.ok(resultsService.getSlalomResults(slalomId));
        } catch (Exception e) {
            log.error("Save slalom run1 {} failed: {}", slalomId, e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/slalom/{slalomId}/run2")
    public ResponseEntity<SlalomResultsDto> saveSlalomRun2(
            @PathVariable Long slalomId,
            @RequestBody List<SlalomRun2Entry> entries) {
        try {
            resultsService.saveSlalomRun2Results(slalomId, entries);
            return ResponseEntity.ok(resultsService.getSlalomResults(slalomId));
        } catch (Exception e) {
            log.error("Save slalom run2 {} failed: {}", slalomId, e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/biathlon/{biathlonId}")
    public ResponseEntity<BiathlonResultsDto> saveBiathlonResults(
            @PathVariable Long biathlonId,
            @RequestBody List<BiathlonResultEntry> entries) {
        try {
            resultsService.saveBiathlonResults(biathlonId, entries);
            return ResponseEntity.ok(resultsService.getBiathlonResults(biathlonId));
        } catch (Exception e) {
            log.error("Save biathlon results {} failed: {}", biathlonId, e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}
