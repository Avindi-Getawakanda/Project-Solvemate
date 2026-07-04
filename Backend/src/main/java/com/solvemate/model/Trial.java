package com.solvemate.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "trials")
public class Trial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trialId;

    @Column(nullable = false)
    private String polymerName;

    @Column(nullable = false)
    private String solventName;


    @Column(nullable = false)
    private String trialResult;

    private String outcomeObservation;

    private double temperature;     // °C
    private double concentration;   // %

    private String performedBy;     // lab user name

    private LocalDateTime trialDate;

    //Constructors

    public Trial() {
        this.trialDate = LocalDateTime.now();
    }

    public Trial(String polymerName, String solventName, String trialResult,
                 String outcomeObservation, double temperature, double concentration,
                 String performedBy) {
        this();
        this.polymerName        = polymerName;
        this.solventName        = solventName;
        this.trialResult        = trialResult;
        this.outcomeObservation = outcomeObservation;
        this.temperature        = temperature;
        this.concentration      = concentration;
        this.performedBy        = performedBy;
    }

    //getters

    public Long getTrialId()              { return trialId; }
    public String getPolymerName()        { return polymerName; }
    public String getSolventName()        { return solventName; }
    public String getTrialResult()        { return trialResult; }
    public String getOutcomeObservation() { return outcomeObservation; }
    public double getTemperature()        { return temperature; }
    public double getConcentration()      { return concentration; }
    public String getPerformedBy()        { return performedBy; }
    public LocalDateTime getTrialDate()   { return trialDate; }

    //setters

    public void setPolymerName(String polymerName)               { this.polymerName = polymerName; }
    public void setSolventName(String solventName)               { this.solventName = solventName; }
    public void setTrialResult(String trialResult)               { this.trialResult = trialResult; }
    public void setOutcomeObservation(String outcomeObservation) { this.outcomeObservation = outcomeObservation; }
    public void setTemperature(double temperature)               { this.temperature = temperature; }
    public void setConcentration(double concentration)           { this.concentration = concentration; }
    public void setPerformedBy(String performedBy)               { this.performedBy = performedBy; }
    public void setTrialDate(LocalDateTime trialDate)            { this.trialDate = trialDate; }
}