package com.solvemate.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "solvents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Solvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long solventId;

    private String name;
    private String chemicalFormula;

    private double deltaD;
    private double deltaP;
    private double deltaH;
    private double molarVolume;
    private double costPerLiter;

    private String envImpactScore;
    private double toxicityLevel;
    private boolean euBanStatus;

    // deltaT = sqrt(deltaD² + deltaP² + deltaH²) — computed before save
    private double deltaT;
}