package com.solvemate.service.impl;

import com.solvemate.exception.BadRequestException;
import com.solvemate.model.CompatibilityResult;
import com.solvemate.model.Polymer;
import com.solvemate.model.Solvent;
import com.solvemate.repository.CompatibilityResultRepository;
import com.solvemate.repository.PolymerRepository;
import com.solvemate.service.AiAssistantService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    private final PolymerRepository polymerRepository;
    private final CompatibilityResultRepository resultRepository;
    private final GeminiService geminiService;

    public AiAssistantServiceImpl(PolymerRepository polymerRepository,
                                  CompatibilityResultRepository resultRepository,
                                  GeminiService geminiService) {
        this.polymerRepository = polymerRepository;
        this.resultRepository  = resultRepository;
        this.geminiService     = geminiService;
    }

    @Override
    public String answer(Long polymerId, String question) {
        Polymer polymer = polymerRepository.findById(polymerId)
                .orElseThrow(() -> new BadRequestException("Polymer not found: " + polymerId));

        List<CompatibilityResult> results =
                resultRepository.findByPolymer_PolymerIdOrderByCompatibilityScoreDesc(polymerId);

        if (results.isEmpty()) {
            return "I don't have any compatibility results for " + polymer.getPolymerName()
                    + " yet. Run the analysis on the Compatibility page first, then ask me again.";
        }

        String context = buildContext(polymer, results);
        String prompt = context
                + "\n\nUser question: " + question
                + "\n\nAnswer in plain, non-technical language a person without a chemistry background "
                + "could understand. Only use the data above. If the question asks about something not "
                + "covered by this data, say so honestly instead of guessing. Keep the answer concise "
                + "(a few sentences, or a short list if comparing multiple solvents).";

        return geminiService.ask(prompt);
    }

    private String buildContext(Polymer polymer, List<CompatibilityResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a chemistry research assistant embedded in SolveMate, a solvent-polymer ")
                .append("compatibility tool. Here is the compatibility analysis data for one polymer.\n\n");

        sb.append("Polymer: ").append(polymer.getPolymerName())
                .append(" (category: ").append(polymer.getPolymerCategory()).append(")\n")
                .append("Hansen Solubility Parameters: deltaD=").append(polymer.getDeltaD())
                .append(", deltaP=").append(polymer.getDeltaP())
                .append(", deltaH=").append(polymer.getDeltaH())
                .append(", R0=").append(polymer.getR0()).append("\n\n");

        sb.append("Top compatible solvents (ranked by score):\n");
        int rank = 1;
        for (CompatibilityResult r : results) {
            Solvent s = r.getSolvent();
            sb.append(rank++).append(". ").append(s.getName())
                    .append(" — ML confidence: ").append(Math.round(r.getMlProbability() * 100)).append("%")
                    .append(", RED index: ").append(String.format("%.2f", r.getRedValue()))
                    .append(", classification: ").append(r.getResult())
                    .append(", environmental impact: ").append(s.getEnvImpactScore())
                    .append(", EU restricted: ").append(s.isEuBanStatus() ? "yes" : "no")
                    .append(", cost per liter: ").append(s.getCostPerLiter())
                    .append("\n");
        }
        return sb.toString();
    }
}