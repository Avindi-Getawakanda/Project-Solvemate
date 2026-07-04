package com.solvemate.dto;

import java.util.List;

public class DashboardStatsResponse {

    private long polymerCount;
    private long solventCount;
    private long trialCount;
    private long reportCount;
    private long userCount;
    private List<RecentTrialItem> recentTrials;
    private List<TopSolventItem> topSolvents;

    public static class RecentTrialItem {
        private Long   trialId;
        private String polymerName;
        private String solventName;
        private String trialResult;
        private String trialDate;

        public RecentTrialItem() {}

        public RecentTrialItem(Long trialId, String polymerName, String solventName,
                               String trialResult, String trialDate) {
            this.trialId = trialId;
            this.polymerName = polymerName;
            this.solventName = solventName;
            this.trialResult = trialResult;
            this.trialDate = trialDate;
        }

        public Long   getTrialId()      { return trialId; }
        public String getPolymerName()  { return polymerName; }
        public String getSolventName()  { return solventName; }
        public String getTrialResult()  { return trialResult; }
        public String getTrialDate()    { return trialDate; }

        public void setTrialId(Long v)         { this.trialId = v; }
        public void setPolymerName(String v)   { this.polymerName = v; }
        public void setSolventName(String v)   { this.solventName = v; }
        public void setTrialResult(String v)   { this.trialResult = v; }
        public void setTrialDate(String v)     { this.trialDate = v; }
    }

    public static class TopSolventItem {
        private String solventName;
        private double mlProbability;

        public TopSolventItem() {}

        public TopSolventItem(String solventName, double mlProbability) {
            this.solventName = solventName;
            this.mlProbability = mlProbability;
        }

        public String getSolventName()   { return solventName; }
        public double getMlProbability() { return mlProbability; }

        public void setSolventName(String v)   { this.solventName = v; }
        public void setMlProbability(double v) { this.mlProbability = v; }
    }

    public DashboardStatsResponse() {}

    public long getPolymerCount()                    { return polymerCount; }
    public long getSolventCount()                    { return solventCount; }
    public long getTrialCount()                      { return trialCount; }
    public long getReportCount()                     { return reportCount; }
    public long getUserCount()                       { return userCount; }
    public List<RecentTrialItem> getRecentTrials()   { return recentTrials; }
    public List<TopSolventItem> getTopSolvents()     { return topSolvents; }

    public void setPolymerCount(long v)                    { this.polymerCount = v; }
    public void setSolventCount(long v)                    { this.solventCount = v; }
    public void setTrialCount(long v)                      { this.trialCount = v; }
    public void setReportCount(long v)                     { this.reportCount = v; }
    public void setUserCount(long v)                       { this.userCount = v; }
    public void setRecentTrials(List<RecentTrialItem> v)   { this.recentTrials = v; }
    public void setTopSolvents(List<TopSolventItem> v)     { this.topSolvents = v; }
}
