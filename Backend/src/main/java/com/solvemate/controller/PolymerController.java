package com.solvemate.controller;

import com.solvemate.dto.ApiResponse;
import com.solvemate.dto.PolymerRequest;
import com.solvemate.dto.PolymerResponse;
import com.solvemate.service.PolymerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PolymerController - REST controller for the Polymer Management Module.
 *
 * OOP Concept - Separation of Concerns:
 *   Controller only handles HTTP request/response mapping.
 *   All business logic is delegated to PolymerService.
 *
 * Endpoints:
 *   POST   /api/polymers           - Add new polymer      (ADMIN)
 *   PUT    /api/polymers/{id}      - Update polymer        (ADMIN)
 *   DELETE /api/polymers/{id}      - Delete polymer        (ADMIN)
 *   GET    /api/polymers           - Get all polymers      (ALL)
 *   GET    /api/polymers/{id}      - Get polymer by ID     (ALL)
 *   GET    /api/polymers/category/{cat} - Filter by category (ALL)
 */
@RestController
@RequestMapping("/api/polymers")
@CrossOrigin(origins = "*")
public class PolymerController {

    private final PolymerService polymerService;

    public PolymerController(PolymerService polymerService) {
        this.polymerService = polymerService;
    }

    @PostMapping
    public ResponseEntity<PolymerResponse> addPolymer(
            @Valid @RequestBody PolymerRequest request) {
        return ResponseEntity.ok(polymerService.addPolymer(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PolymerResponse> updatePolymer(
            @PathVariable Long id,
            @Valid @RequestBody PolymerRequest request) {
        return ResponseEntity.ok(polymerService.updatePolymer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePolymer(@PathVariable Long id) {
        return ResponseEntity.ok(polymerService.deletePolymer(id));
    }

    @GetMapping
    public ResponseEntity<List<PolymerResponse>> getAllPolymers() {
        return ResponseEntity.ok(polymerService.getAllPolymers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolymerResponse> getPolymerById(@PathVariable Long id) {
        return ResponseEntity.ok(polymerService.getPolymerById(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<PolymerResponse>> getByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(polymerService.getPolymersByCategory(category));
    }
}
