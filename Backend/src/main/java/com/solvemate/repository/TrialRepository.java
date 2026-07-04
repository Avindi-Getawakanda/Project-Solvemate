package com.solvemate.repository;

import com.solvemate.model.Trial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrialRepository extends JpaRepository<Trial, Long> {

    List<Trial> findByPolymerNameOrderByTrialDateDesc(String polymerName);
    List<Trial> findByTrialResultOrderByTrialDateDesc(String trialResult);
    List<Trial> findAllByOrderByTrialDateDesc();
}