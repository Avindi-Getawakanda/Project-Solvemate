package com.solvemate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SolventRequest {

    @NotBlank(message = "Solvent name is required")
    private String name;

    private String chemicalFormula;

    @NotNull(message = "Delta D is required")
    private Double deltaD;

    @NotNull(message = "Delta P is required")
    private Double deltaP;

    @NotNull(message = "Delta H is required")
    private Double deltaH;

    private Double molarVolume;

    private Double costPerLiter;

    @NotBlank(message = "Environmental impact score is required")
    @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "envImpactScore must be LOW, MEDIUM, or HIGH")
    private String envImpactScore;

    private Double toxicityLevel;

    private Boolean euBanStatus;

    // ── Getters / Setters ──────────────────────────────────────────────────────

    public String getName()             { return name; }
    public String getChemicalFormula()  { return chemicalFormula; }
    public Double getDeltaD()           { return deltaD; }
    public Double getDeltaP()           { return deltaP; }
    public Double getDeltaH()           { return deltaH; }
    public Double getMolarVolume()      { return molarVolume; }
    public Double getCostPerLiter()     { return costPerLiter; }
    public String getEnvImpactScore()   { return envImpactScore; }
    public Double getToxicityLevel()    { return toxicityLevel; }
    public Boolean getEuBanStatus()     { return euBanStatus; }

    public void setName(String name)                      { this.name = name; }
    public void setChemicalFormula(String chemicalFormula){ this.chemicalFormula = chemicalFormula; }
    public void setDeltaD(Double deltaD)                  { this.deltaD = deltaD; }
    public void setDeltaP(Double deltaP)                  { this.deltaP = deltaP; }
    public void setDeltaH(Double deltaH)                  { this.deltaH = deltaH; }
    public void setMolarVolume(Double molarVolume)        { this.molarVolume = molarVolume; }
    public void setCostPerLiter(Double costPerLiter)      { this.costPerLiter = costPerLiter; }
    public void setEnvImpactScore(String envImpactScore)  { this.envImpactScore = envImpactScore; }
    public void setToxicityLevel(Double toxicityLevel)    { this.toxicityLevel = toxicityLevel; }
    public void setEuBanStatus(Boolean euBanStatus)       { this.euBanStatus = euBanStatus; }
}
