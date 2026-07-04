package com.solvemate.controller;

import com.solvemate.dto.ApiResponse;
import com.solvemate.dto.SolventRequest;
import com.solvemate.dto.SolventResponse;
import com.solvemate.service.SolventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solvents")
@CrossOrigin(origins = "*")
public class SolventController {

    private final SolventService solventService;

    public SolventController(SolventService solventService) {
        this.solventService = solventService;
    }

    // POST /api/solvents
    @PostMapping
    public ResponseEntity<SolventResponse> addSolvent(@Valid @RequestBody SolventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solventService.addSolvent(request));
    }

    // PUT /api/solvents/{id}
    @PutMapping("/{id}")
    public ResponseEntity<SolventResponse> updateSolvent(@PathVariable Long id,
                                                         @Valid @RequestBody SolventRequest request) {
        return ResponseEntity.ok(solventService.updateSolvent(id, request));
    }

    // DELETE /api/solvents/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSolvent(@PathVariable Long id) {
        return ResponseEntity.ok(solventService.deleteSolvent(id));
    }

    // GET /api/solvents
    @GetMapping
    public ResponseEntity<List<SolventResponse>> getAllSolvents() {
        return ResponseEntity.ok(solventService.getAllSolvents());
    }

    // GET /api/solvents/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SolventResponse> getSolventById(@PathVariable Long id) {
        return ResponseEntity.ok(solventService.getSolventById(id));
    }

    // GET /api/solvents/eu-status?banned=true
    @GetMapping("/eu-status")
    public ResponseEntity<List<SolventResponse>> getSolventsByEuStatus(
            @RequestParam boolean banned) {
        return ResponseEntity.ok(solventService.getSolventsByEuStatus(banned));
    }

    // GET /api/solvents/env-score?score=LOW
    @GetMapping("/env-score")
    public ResponseEntity<List<SolventResponse>> getSolventsByEnvScore(
            @RequestParam String score) {
        return ResponseEntity.ok(solventService.getSolventsByEnvScore(score));
    }
}