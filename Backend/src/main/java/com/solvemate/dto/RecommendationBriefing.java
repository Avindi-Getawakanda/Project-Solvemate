package com.solvemate.dto;

public class RecommendationBriefing {

    private String compatibilityAssessment;
    private String healthSafetyAssessment;
    private String environmentalImpactAssessment;
    private String regulatoryComplianceAssessment;
    private String costPracticalityAssessment;
    private String overallRecommendation;
    private String saferAlternative;

    public RecommendationBriefing() {}

    public String getCompatibilityAssessment()           { return compatibilityAssessment; }
    public String getHealthSafetyAssessment()            { return healthSafetyAssessment; }
    public String getEnvironmentalImpactAssessment()     { return environmentalImpactAssessment; }
    public String getRegulatoryComplianceAssessment()    { return regulatoryComplianceAssessment; }
    public String getCostPracticalityAssessment()        { return costPracticalityAssessment; }
    public String getOverallRecommendation()             { return overallRecommendation; }
    public String getSaferAlternative()                  { return saferAlternative; }

    public void setCompatibilityAssessment(String v)           { this.compatibilityAssessment = v; }
    public void setHealthSafetyAssessment(String v)            { this.healthSafetyAssessment = v; }
    public void setEnvironmentalImpactAssessment(String v)     { this.environmentalImpactAssessment = v; }
    public void setRegulatoryComplianceAssessment(String v)    { this.regulatoryComplianceAssessment = v; }
    public void setCostPracticalityAssessment(String v)        { this.costPracticalityAssessment = v; }
    public void setOverallRecommendation(String v)             { this.overallRecommendation = v; }
    public void setSaferAlternative(String v)                  { this.saferAlternative = v; }
}
