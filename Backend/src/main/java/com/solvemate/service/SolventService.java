package com.solvemate.service;

import com.solvemate.dto.ApiResponse;
import com.solvemate.dto.SolventRequest;
import com.solvemate.dto.SolventResponse;

import java.util.List;

public interface SolventService {

    SolventResponse addSolvent(SolventRequest request);

    SolventResponse updateSolvent(Long id, SolventRequest request);

    ApiResponse deleteSolvent(Long id);

    SolventResponse getSolventById(Long id);

    List<SolventResponse> getAllSolvents();

    List<SolventResponse> getSolventsByEuStatus(boolean banned);

    List<SolventResponse> getSolventsByEnvScore(String score);
}
