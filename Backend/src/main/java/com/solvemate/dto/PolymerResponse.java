package com.solvemate.dto;

public class PolymerResponse {

    private Long polymerId;
    private String polymerName;
    private String polymerCategory;
    private double deltaD;
    private double deltaP;
    private double deltaH;
    private double r0;
    private double deltaT;

    public PolymerResponse() {}

    public PolymerResponse(Long polymerId, String polymerName, String polymerCategory,
                            double deltaD, double deltaP, double deltaH,
                            double r0, double deltaT) {
        this.polymerId       = polymerId;
        this.polymerName     = polymerName;
        this.polymerCategory = polymerCategory;
        this.deltaD          = deltaD;
        this.deltaP          = deltaP;
        this.deltaH          = deltaH;
        this.r0              = r0;
        this.deltaT          = deltaT;
    }

    public Long getPolymerId()        { return polymerId; }
    public String getPolymerName()    { return polymerName; }
    public String getPolymerCategory(){ return polymerCategory; }
    public double getDeltaD()         { return deltaD; }
    public double getDeltaP()         { return deltaP; }
    public double getDeltaH()         { return deltaH; }
    public double getR0()             { return r0; }
    public double getDeltaT()         { return deltaT; }

    public void setPolymerId(Long polymerId)               { this.polymerId = polymerId; }
    public void setPolymerName(String polymerName)         { this.polymerName = polymerName; }
    public void setPolymerCategory(String polymerCategory) { this.polymerCategory = polymerCategory; }
    public void setDeltaD(double deltaD)                   { this.deltaD = deltaD; }
    public void setDeltaP(double deltaP)                   { this.deltaP = deltaP; }
    public void setDeltaH(double deltaH)                   { this.deltaH = deltaH; }
    public void setR0(double r0)                           { this.r0 = r0; }
    public void setDeltaT(double deltaT)                   { this.deltaT = deltaT; }
}
