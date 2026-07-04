package com.solvemate.service.impl;

import com.solvemate.dto.DashboardStatsResponse;
import com.solvemate.dto.DashboardStatsResponse.RecentTrialItem;
import com.solvemate.dto.DashboardStatsResponse.TopSolventItem;
import com.solvemate.model.CompatibilityResult;
import com.solvemate.model.Trial;
import com.solvemate.repository.*;
import com.solvemate.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PolymerRepository             polymerRepository;
    private final SolventRepository             solventRepository;
    private final TrialRepository               trialRepository;
    private final ReportRepository              reportRepository;
    private final UserRepository                userRepository;
    private final CompatibilityResultRepository compatibilityResultRepository;

    public DashboardServiceImpl(PolymerRepository polymerRepository,
                                SolventRepository solventRepository,
                                TrialRepository trialRepository,
                                ReportRepository reportRepository,
                                UserRepository userRepository,
                                CompatibilityResultRepository compatibilityResultRepository) {
        this.polymerRepository             = polymerRepository;
        this.solventRepository             = solventRepository;
        this.trialRepository               = trialRepository;
        this.reportRepository              = reportRepository;
        this.userRepository                = userRepository;
        this.compatibilityResultRepository = compatibilityResultRepository;
    }

    @Override
    public DashboardStatsResponse getStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setPolymerCount(polymerRepository.count());
        stats.setSolventCount(solventRepository.count());
        stats.setTrialCount(trialRepository.count());
        stats.setReportCount(reportRepository.count());
        stats.setUserCount(userRepository.count());
        stats.setRecentTrials(buildRecentTrials());
        stats.setTopSolvents(buildTopSolvents());
        return stats;
    }

    private List<RecentTrialItem> buildRecentTrials() {
        List<Trial> trials = trialRepository.findAllByOrderByTrialDateDesc();
        List<RecentTrialItem> items = new ArrayList<>();
        int limit = Math.min(5, trials.size());
        for (int i = 0; i < limit; i++) {
            Trial t = trials.get(i);
            String date = t.getTrialDate() != null
                    ? t.getTrialDate().format(DATE_FMT)
                    : "—";
            items.add(new RecentTrialItem(
                    t.getTrialId(),
                    t.getPolymerName(),
                    t.getSolventName(),
                    t.getTrialResult(),
                    date
            ));
        }
        return items;
    }

    private List<TopSolventItem> buildTopSolvents() {
        List<CompatibilityResult> all = compatibilityResultRepository.findAll();
        Map<String, Double> bestBySolvent = new LinkedHashMap<>();

        for (CompatibilityResult r : all) {
            String name = r.getSolvent().getName();
            double prob = r.getMlProbability();
            bestBySolvent.merge(name, prob, Math::max);
        }

        return bestBySolvent.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(e -> new TopSolventItem(e.getKey(), Math.round(e.getValue() * 1000.0) / 10.0))
                .toList();
    }
}
