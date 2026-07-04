package com.solvemate.repository;

import com.solvemate.model.Polymer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolymerRepository extends JpaRepository<Polymer, Long> {

    Optional<Polymer> findByPolymerName(String polymerName);

    boolean existsByPolymerName(String polymerName);

    List<Polymer> findByPolymerCategory(String category);
}
