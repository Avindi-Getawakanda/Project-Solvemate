package com.solvemate.controller;

import com.solvemate.model.Report;
import com.solvemate.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return service.generateReport(report);
    }

    @GetMapping
    public List<Report> getAllReports() {
        return service.getAllReports();
    }

    @GetMapping("/{id}")
    public Report getReportById(@PathVariable Long id) {
        return service.getReportById(id);
    }

    
    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportReport(@PathVariable Long id) {
        byte[] fileBytes = service.exportReportAsText(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=solvemate-report-" + id + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(fileBytes);
    }
}