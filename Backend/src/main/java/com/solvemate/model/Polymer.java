package com.solvemate.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Polymer entity - stores Hansen Solubility Parameters for each polymer.
 *
 * OOP Concepts:
 *  - Encapsulation: all fields are private with getters/setters
 *  - Abstraction: JPA @Entity hides SQL persistence details
 */
@Entity
@Table(name = "polymers")
public class Polymer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long polymerId;

    @Column(nullable = false, unique = true)
    private String polymerName;

    @Column(nullable = false)
    private String polymerCategory;   // e.g. Thermoplastic, Elastomer

    // Hansen Solubility Parameters (MPa^0.5)
    @Column(nullable = false)
    private double deltaD;            // Dispersion parameter

    @Column(nullable = false)
    private double deltaP;            // Polar parameter

    @Column(nullable = false)
    private double deltaH;            // Hydrogen bonding parameter

    @Column(nullable = false)
    private double r0;                // Interaction radius

    private double deltaT;            // Total solubility parameter

    private LocalDateTime createdAt;

    // ── Constructors ────────────────────────────────────────────────────────

    public Polymer() {
        this.createdAt = LocalDateTime.now();
    }

    public Polymer(String polymerName, String polymerCategory,
                   double deltaD, double deltaP, double deltaH, double r0) {
        this();
        this.polymerName     = polymerName;
        this.polymerCategory = polymerCategory;
        this.deltaD          = deltaD;
        this.deltaP          = deltaP;
        this.deltaH          = deltaH;
        this.r0              = r0;
        this.deltaT          = Math.sqrt(deltaD * deltaD + deltaP * deltaP + deltaH * deltaH);
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public Long getPolymerId()        { return polymerId; }
    public String getPolymerName()    { return polymerName; }
    public String getPolymerCategory(){ return polymerCategory; }
    public double getDeltaD()         { return deltaD; }
    public double getDeltaP()         { return deltaP; }
    public double getDeltaH()         { return deltaH; }
    public double getR0()             { return r0; }
    public double getDeltaT()         { return deltaT; }
    public LocalDateTime getCreatedAt(){ return createdAt; }

    // ── Setters ─────────────────────────────────────────────────────────────

    public void setPolymerName(String polymerName)       { this.polymerName = polymerName; }
    public void setPolymerCategory(String polymerCategory){ this.polymerCategory = polymerCategory; }
    public void setDeltaD(double deltaD)                  { this.deltaD = deltaD; recalcDeltaT(); }
    public void setDeltaP(double deltaP)                  { this.deltaP = deltaP; recalcDeltaT(); }
    public void setDeltaH(double deltaH)                  { this.deltaH = deltaH; recalcDeltaT(); }
    public void setR0(double r0)                          { this.r0 = r0; }

    // ── Helper ───────────────────────────────────────────────────────────────

    private void recalcDeltaT() {
        this.deltaT = Math.sqrt(deltaD * deltaD + deltaP * deltaP + deltaH * deltaH);
    }

    @Override
    public String toString() {
        return "Polymer{id=" + polymerId + ", name='" + polymerName + "', R0=" + r0 + "}";
    }
}
