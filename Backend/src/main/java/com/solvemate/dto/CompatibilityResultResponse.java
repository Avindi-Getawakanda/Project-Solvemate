package com.solvemate.dto;

import java.util.List;

public class CompatibilityResultResponse {

    private Long   resultId;
    private Long   polymerId;
    private String polymerName;
    private Long   solventId;
    private String solventName;
    private double mlProbability;
    private double compatibilityScore;
    private double redValue;
    private double raValue;
    private int    rankPosition;
    private String result;
    private String recommendationType;
    private int    solventsAnalysed;
    private List<ShapExplanation> explanation;
    private double greenScore;
    private String envImpactScore;
    private boolean euBanStatus;
    private String greenInsight;
    private RecommendationBriefing briefing;

    public static class ShapExplanation {
        private String feature;
        private String label;
        private double shapValue;
        private double contribution;
        private String plainEnglish;

        public ShapExplanation() {}

        public String getFeature()       { return feature; }
        public String getLabel()         { return label; }
        public double getShapValue()     { return shapValue; }
        public double getContribution()  { return contribution; }
        public String getPlainEnglish()  { return plainEnglish; }

        public void setFeature(String feature)             { this.feature = feature; }
        public void setLabel(String label)                 { this.label = label; }
        public void setShapValue(double shapValue)         { this.shapValue = shapValue; }
        public void setContribution(double contribution)   { this.contribution = contribution; }
        public void setPlainEnglish(String plainEnglish)   { this.plainEnglish = plainEnglish; }
    }

    public CompatibilityResultResponse() {}

    public Long   getResultId()                        { return resultId; }
    public Long   getPolymerId()                       { return polymerId; }
    public String getPolymerName()                     { return polymerName; }
    public Long   getSolventId()                       { return solventId; }
    public String getSolventName()                     { return solventName; }
    public double getMlProbability()                   { return mlProbability; }
    public double getCompatibilityScore()              { return compatibilityScore; }
    public double getRedValue()                          { return redValue; }
    public double getRaValue()                           { return raValue; }
    public int    getRankPosition()                    { return rankPosition; }
    public String getResult()                          { return result; }
    public String getRecommendationType()              { return recommendationType; }
    public int    getSolventsAnalysed()                { return solventsAnalysed; }
    public List<ShapExplanation> getExplanation()      { return explanation; }
    public double  getGreenScore()                     { return greenScore; }
    public String  getEnvImpactScore()                 { return envImpactScore; }
    public boolean getEuBanStatus()                     { return euBanStatus; }
    public String  getGreenInsight()                    { return greenInsight; }
    public RecommendationBriefing getBriefing()         { return briefing; }

    public void setResultId(Long v)                    { this.resultId = v; }
    public void setPolymerId(Long v)                   { this.polymerId = v; }
    public void setPolymerName(String v)               { this.polymerName = v; }
    public void setSolventId(Long v)                   { this.solventId = v; }
    public void setSolventName(String v)               { this.solventName = v; }
    public void setMlProbability(double v)             { this.mlProbability = v; }
    public void setCompatibilityScore(double v)        { this.compatibilityScore = v; }
    public void setRedValue(double v)                  { this.redValue = v; }
    public void setRaValue(double v)                   { this.raValue = v; }
    public void setRankPosition(int v)                 { this.rankPosition = v; }
    public void setResult(String v)                    { this.result = v; }
    public void setRecommendationType(String v)        { this.recommendationType = v; }
    public void setSolventsAnalysed(int v)             { this.solventsAnalysed = v; }
    public void setExplanation(List<ShapExplanation> v){ this.explanation = v; }
    public void setGreenScore(double v)                { this.greenScore = v; }
    public void setEnvImpactScore(String v)            { this.envImpactScore = v; }
    public void setEuBanStatus(boolean v)               { this.euBanStatus = v; }
    public void setGreenInsight(String v)               { this.greenInsight = v; }
    public void setBriefing(RecommendationBriefing v)   { this.briefing = v; }
}