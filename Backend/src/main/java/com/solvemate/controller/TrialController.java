package com.solvemate.controller;

import com.solvemate.model.Trial;
import com.solvemate.service.TrialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/trials")
@CrossOrigin(origins = "*")
public class TrialController {

    private final TrialService trialService;

    public TrialController(TrialService trialService) {
        this.trialService = trialService;
    }

    @PostMapping
    public ResponseEntity<Trial> recordTrial(@RequestBody Trial trial) {
        return ResponseEntity.ok(trialService.recordTrial(trial));
    }

    @GetMapping
    public ResponseEntity<List<Trial>> getAllTrials() {
        return ResponseEntity.ok(trialService.getAllTrials());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trial> getTrialById(@PathVariable Long id) {
        return ResponseEntity.ok(trialService.getTrialById(id));
    }

    @GetMapping("/polymer/{polymerName}")
    public ResponseEntity<List<Trial>> getByPolymer(@PathVariable String polymerName) {
        return ResponseEntity.ok(trialService.getTrialsByPolymer(polymerName));
    }
}

