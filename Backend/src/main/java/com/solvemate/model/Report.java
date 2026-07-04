package com.solvemate.model;

import jakarta.persistence.*;


@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String polymerName;
    private String solventName;
    private double compatibilityScore;
    private String trialResult;         
    private String outcomeObservation;

    private double costAnalysis;
    private String envImpactSummary;
    private String euComplianceStatus;
    private String finalDecision;

    
    public Report() {}

    

    public Long getId()                   { return id; }
    public String getPolymerName()        { return polymerName; }
    public String getSolventName()        { return solventName; }
    public double getCompatibilityScore() { return compatibilityScore; }
    public String getTrialResult()        { return trialResult; }
    public String getOutcomeObservation() { return outcomeObservation; }
    public double getCostAnalysis()       { return costAnalysis; }
    public String getEnvImpactSummary()   { return envImpactSummary; }
    public String getEuComplianceStatus() { return euComplianceStatus; }
    public String getFinalDecision()      { return finalDecision; }

  
    public void setPolymerName(String polymerName)               { this.polymerName = polymerName; }
    public void setSolventName(String solventName)               { this.solventName = solventName; }
    public void setCompatibilityScore(double compatibilityScore) { this.compatibilityScore = compatibilityScore; }
    public void setTrialResult(String trialResult)               { this.trialResult = trialResult; }
    public void setOutcomeObservation(String outcomeObservation) { this.outcomeObservation = outcomeObservation; }
    public void setCostAnalysis(double costAnalysis)             { this.costAnalysis = costAnalysis; }
    public void setEnvImpactSummary(String envImpactSummary)     { this.envImpactSummary = envImpactSummary; }
    public void setEuComplianceStatus(String euComplianceStatus) { this.euComplianceStatus = euComplianceStatus; }
    public void setFinalDecision(String finalDecision)           { this.finalDecision = finalDecision; }
}