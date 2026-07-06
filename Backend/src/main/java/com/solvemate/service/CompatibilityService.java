package com.solvemate.service;

import com.solvemate.dto.CompatibilityAnalysisResponse;
import com.solvemate.dto.CompatibilityResultResponse;
import java.util.List;

public interface CompatibilityService {
    CompatibilityAnalysisResponse analyze(Long polymerId, boolean greenMode);
    List<CompatibilityResultResponse> getResultsForPolymer(Long polymerId);
}