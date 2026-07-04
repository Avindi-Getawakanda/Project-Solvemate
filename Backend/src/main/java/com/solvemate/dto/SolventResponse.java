package com.solvemate.dto;

public class SolventResponse {

    private Long solventId;
    private String name;
    private String chemicalFormula;
    private double deltaD;
    private double deltaP;
    private double deltaH;
    private double molarVolume;
    private double deltaT;
    private double costPerLiter;
    private String envImpactScore;
    private double toxicityLevel;
    private boolean euBanStatus;

    public SolventResponse() {}

    // ── Getters / Setters ──────────────────────────────────────────────────────

    public Long getSolventId()          { return solventId; }
    public String getName()             { return name; }
    public String getChemicalFormula()  { return chemicalFormula; }
    public double getDeltaD()           { return deltaD; }
    public double getDeltaP()           { return deltaP; }
    public double getDeltaH()           { return deltaH; }
    public double getMolarVolume()      { return molarVolume; }
    public double getDeltaT()           { return deltaT; }
    public double getCostPerLiter()     { return costPerLiter; }
    public String getEnvImpactScore()   { return envImpactScore; }
    public double getToxicityLevel()    { return toxicityLevel; }
    public boolean isEuBanStatus()      { return euBanStatus; }

    public void setSolventId(Long solventId)              { this.solventId = solventId; }
    public void setName(String name)                      { this.name = name; }
    public void setChemicalFormula(String chemicalFormula){ this.chemicalFormula = chemicalFormula; }
    public void setDeltaD(double deltaD)                  { this.deltaD = deltaD; }
    public void setDeltaP(double deltaP)                  { this.deltaP = deltaP; }
    public void setDeltaH(double deltaH)                  { this.deltaH = deltaH; }
    public void setMolarVolume(double molarVolume)        { this.molarVolume = molarVolume; }
    public void setDeltaT(double deltaT)                  { this.deltaT = deltaT; }
    public void setCostPerLiter(double costPerLiter)      { this.costPerLiter = costPerLiter; }
    public void setEnvImpactScore(String envImpactScore)  { this.envImpactScore = envImpactScore; }
    public void setToxicityLevel(double toxicityLevel)    { this.toxicityLevel = toxicityLevel; }
    public void setEuBanStatus(boolean euBanStatus)       { this.euBanStatus = euBanStatus; }
}
