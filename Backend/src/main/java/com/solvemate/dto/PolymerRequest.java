package com.solvemate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PolymerRequest {

    @NotBlank(message = "Polymer name is required")
    private String polymerName;

    @NotBlank(message = "Polymer category is required")
    private String polymerCategory;

    @NotNull(message = "Delta D is required")
    private Double deltaD;

    @NotNull(message = "Delta P is required")
    private Double deltaP;

    @NotNull(message = "Delta H is required")
    private Double deltaH;

    @NotNull(message = "R0 (interaction radius) is required")
    @Positive(message = "R0 must be positive")
    private Double r0;

    // ── Getters / Setters ──────────────────────────────────────────────────────

    public String getPolymerName()     { return polymerName; }
    public String getPolymerCategory() { return polymerCategory; }
    public Double getDeltaD()          { return deltaD; }
    public Double getDeltaP()          { return deltaP; }
    public Double getDeltaH()          { return deltaH; }
    public Double getR0()              { return r0; }

    public void setPolymerName(String polymerName)         { this.polymerName = polymerName; }
    public void setPolymerCategory(String polymerCategory) { this.polymerCategory = polymerCategory; }
    public void setDeltaD(Double deltaD)                   { this.deltaD = deltaD; }
    public void setDeltaP(Double deltaP)                   { this.deltaP = deltaP; }
    public void setDeltaH(Double deltaH)                   { this.deltaH = deltaH; }
    public void setR0(Double r0)                           { this.r0 = r0; }
}
