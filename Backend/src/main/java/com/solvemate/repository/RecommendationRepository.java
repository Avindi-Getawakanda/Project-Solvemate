package com.solvemate.repository;

import com.solvemate.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByPolymer_PolymerIdOrderByGeneratedAtDesc(Long polymerId);

    Optional<Recommendation> findTopByPolymer_PolymerIdOrderByGeneratedAtDesc(Long polymerId);
}
