package com.solvemate.service.impl;

import com.solvemate.dto.ApiResponse;
import com.solvemate.dto.PolymerRequest;
import com.solvemate.dto.PolymerResponse;
import com.solvemate.exception.BadRequestException;
import com.solvemate.model.Polymer;
import com.solvemate.repository.PolymerRepository;
import com.solvemate.service.PolymerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PolymerServiceImpl - concrete implementation of PolymerService.
 *
 * OOP Concepts:
 *  - Polymorphism: implements PolymerService interface
 *  - Encapsulation: business logic is hidden inside this class
 *  - Single Responsibility: handles only polymer business rules
 */
@Service
public class PolymerServiceImpl implements PolymerService {

    private final PolymerRepository polymerRepository;

    public PolymerServiceImpl(PolymerRepository polymerRepository) {
        this.polymerRepository = polymerRepository;
    }

    // ── Add ───────────────────────────────────────────────────────────────────

    @Override
    public PolymerResponse addPolymer(PolymerRequest request) {
        if (polymerRepository.existsByPolymerName(request.getPolymerName())) {
            throw new BadRequestException("Polymer '" + request.getPolymerName() + "' already exists");
        }

        Polymer polymer = new Polymer(
                request.getPolymerName(),
                request.getPolymerCategory(),
                request.getDeltaD(),
                request.getDeltaP(),
                request.getDeltaH(),
                request.getR0()
        );

        polymerRepository.save(polymer);
        return mapToResponse(polymer);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Override
    public PolymerResponse updatePolymer(Long id, PolymerRequest request) {
        Polymer polymer = polymerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Polymer not found with id: " + id));

        polymer.setPolymerName(request.getPolymerName());
        polymer.setPolymerCategory(request.getPolymerCategory());
        polymer.setDeltaD(request.getDeltaD());
        polymer.setDeltaP(request.getDeltaP());
        polymer.setDeltaH(request.getDeltaH());
        polymer.setR0(request.getR0());

        polymerRepository.save(polymer);
        return mapToResponse(polymer);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Override
    public ApiResponse deletePolymer(Long id) {
        if (!polymerRepository.existsById(id)) {
            throw new BadRequestException("Polymer not found with id: " + id);
        }
        polymerRepository.deleteById(id);
        return new ApiResponse("Polymer deleted successfully");
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    public PolymerResponse getPolymerById(Long id) {
        Polymer polymer = polymerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Polymer not found with id: " + id));
        return mapToResponse(polymer);
    }

    @Override
    public List<PolymerResponse> getAllPolymers() {
        return polymerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PolymerResponse> getPolymersByCategory(String category) {
        return polymerRepository.findByPolymerCategory(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private PolymerResponse mapToResponse(Polymer polymer) {
        return new PolymerResponse(
                polymer.getPolymerId(),
                polymer.getPolymerName(),
                polymer.getPolymerCategory(),
                polymer.getDeltaD(),
                polymer.getDeltaP(),
                polymer.getDeltaH(),
                polymer.getR0(),
                polymer.getDeltaT()
        );
    }
}
