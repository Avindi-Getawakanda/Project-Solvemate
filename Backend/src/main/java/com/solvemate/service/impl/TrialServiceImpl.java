package com.solvemate.service.impl;

import com.solvemate.model.Trial;
import com.solvemate.repository.TrialRepository;
import com.solvemate.service.TrialService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TrialServiceImpl implements TrialService {

    private final TrialRepository trialRepository;

    public TrialServiceImpl(TrialRepository trialRepository) {
        this.trialRepository = trialRepository;
    }

    @Override
    public Trial recordTrial(Trial trial) {
        return trialRepository.save(trial);
    }

    @Override
    public List<Trial> getAllTrials() {
        return trialRepository.findAllByOrderByTrialDateDesc();
    }

    @Override
    public Trial getTrialById(Long id) {
        return trialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trial not found with id: " + id));
    }

    @Override
    public List<Trial> getTrialsByPolymer(String polymerName) {
        return trialRepository.findByPolymerNameOrderByTrialDateDesc(polymerName);
    }
}