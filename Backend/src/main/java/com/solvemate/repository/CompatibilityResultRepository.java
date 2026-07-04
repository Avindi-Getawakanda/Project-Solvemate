package com.solvemate.repository;

import com.solvemate.model.CompatibilityResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CompatibilityResultRepository extends JpaRepository<CompatibilityResult, Long> {

    List<CompatibilityResult> findByPolymer_PolymerIdOrderByCompatibilityScoreDesc(Long polymerId);

    List<CompatibilityResult> findByPolymer_PolymerIdAndResultOrderByCompatibilityScoreDesc(
            Long polymerId,
            String result
    );

    List<CompatibilityResult> findTop5ByOrderByMlProbabilityDesc();

    @Transactional
    void deleteByPolymer_PolymerId(Long polymerId);
}