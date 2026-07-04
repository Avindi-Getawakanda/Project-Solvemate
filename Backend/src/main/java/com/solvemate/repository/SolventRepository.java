package com.solvemate.repository;

import com.solvemate.model.Solvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolventRepository extends JpaRepository<Solvent, Long> {

    // Used by addSolvent() to check for duplicates
    boolean existsByName(String name);

    // Used by getSolventsByEuStatus()
    List<Solvent> findByEuBanStatus(boolean euBanStatus);

    // Used by getSolventsByEnvScore()
    List<Solvent> findByEnvImpactScore(String envImpactScore);
}