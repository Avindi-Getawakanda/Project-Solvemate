package com.solvemate.dto;

import java.util.List;

public class CompatibilityAnalysisResponse {

    private List<CompatibilityResultResponse> recommended;
    private List<CompatibilityResultResponse> notRecommended;
    private AnalysisSummary summary;

    public static class AnalysisSummary {
        private int    solventsAnalysed;
        private int    highConfidenceCount;
        private int    moderateCount;
        private int    lowCount;
        private int    redCompatibleCount;
        private double topProbability;
        private double medianProbability;
        private boolean greenModeActive;
        private double averageGreenScore;

        public AnalysisSummary() {}

        public int    getSolventsAnalysed()     { return solventsAnalysed; }
        public int    getHighConfidenceCount()  { return highConfidenceCount; }
        public int    getModerateCount()        { return moderateCount; }
        public int    getLowCount()             { return lowCount; }
        public int    getRedCompatibleCount()   { return redCompatibleCount; }
        public double getTopProbability()     { return topProbability; }
        public double getMedianProbability()  { return medianProbability; }
        public boolean getGreenModeActive()   { return greenModeActive; }
        public double getAverageGreenScore()  { return averageGreenScore; }

        public void setSolventsAnalysed(int v)     { this.solventsAnalysed = v; }
        public void setHighConfidenceCount(int v)  { this.highConfidenceCount = v; }
        public void setModerateCount(int v)        { this.moderateCount = v; }
        public void setLowCount(int v)             { this.lowCount = v; }
        public void setRedCompatibleCount(int v)   { this.redCompatibleCount = v; }
        public void setTopProbability(double v)    { this.topProbability = v; }
        public void setMedianProbability(double v) { this.medianProbability = v; }
        public void setGreenModeActive(boolean v)  { this.greenModeActive = v; }
        public void setAverageGreenScore(double v) { this.averageGreenScore = v; }
    }

    public CompatibilityAnalysisResponse() {}

    public List<CompatibilityResultResponse> getRecommended()     { return recommended; }
    public List<CompatibilityResultResponse> getNotRecommended()  { return notRecommended; }
    public AnalysisSummary getSummary()                         { return summary; }

    public void setRecommended(List<CompatibilityResultResponse> v)    { this.recommended = v; }
    public void setNotRecommended(List<CompatibilityResultResponse> v){ this.notRecommended = v; }
    public void setSummary(AnalysisSummary v)                          { this.summary = v; }
}