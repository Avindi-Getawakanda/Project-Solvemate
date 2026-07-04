package com.solvemate.service;

import com.solvemate.model.Report;
import com.solvemate.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;


@Service
public class ReportService {

    private final ReportRepository repository;

    public ReportService(ReportRepository repository) {
        this.repository = repository;
    }

   

    public Report generateReport(Report report) {
        return repository.save(report);
    }

   

    public List<Report> getAllReports() {
        return repository.findAll();
    }

    public Report getReportById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
    }

    
    public byte[] exportReportAsText(Long id) {
        Report report = getReportById(id);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        ps.println("============================================");
        ps.println("           SOLVEMATE REPORT                 ");
        ps.println("============================================");
        ps.println();
        ps.println("Polymer        : " + report.getPolymerName());
        ps.println("Solvent        : " + report.getSolventName());
        ps.println("Compat. Score  : " + report.getCompatibilityScore());
        ps.println("Trial Result   : " + report.getTrialResult());
        ps.println("Observation    : " + report.getOutcomeObservation());
        ps.println("Cost Analysis  : $" + report.getCostAnalysis());
        ps.println("Env. Impact    : " + report.getEnvImpactSummary());
        ps.println("EU Compliance  : " + report.getEuComplianceStatus());
        ps.println("Final Decision : " + report.getFinalDecision());
        ps.println();
        ps.println("============================================");
        ps.close();

        return baos.toByteArray();
    }
}