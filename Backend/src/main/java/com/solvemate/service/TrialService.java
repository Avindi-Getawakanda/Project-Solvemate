package com.solvemate.service;

import com.solvemate.model.Trial;
import java.util.List;


public interface TrialService {
    Trial recordTrial(Trial trial);
    List<Trial> getAllTrials();
    Trial getTrialById(Long id);
    List<Trial> getTrialsByPolymer(String polymerName);
}