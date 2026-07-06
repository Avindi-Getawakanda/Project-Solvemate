package com.solvemate.service;

import com.solvemate.config.SolventHazardNotes;
import com.solvemate.dto.RecommendationBriefing;
import com.solvemate.model.Polymer;
import com.solvemate.model.Solvent;
import org.springframework.stereotype.Component;

/**
 * Generates concise laboratory decision-support briefings.
 * Each section is 1–2 sentences: why it matters and what to do.
 */
@Component
public class RecommendationBriefingGenerator {

    public RecommendationBriefing generate(
            Polymer polymer,
            Solvent solvent,
            double probability,
            double red,
            double ra,
            String recommendationType,
            int rankPosition,
            Solvent saferAlternative) {

        RecommendationBriefing b = new RecommendationBriefing();
        b.setCompatibilityAssessment(compatibilitySection(polymer, solvent, probability, red, ra, recommendationType));
        b.setHealthSafetyAssessment(healthSafetySection(solvent));
        b.setEnvironmentalImpactAssessment(environmentalSection(solvent));
        b.setRegulatoryComplianceAssessment(regulatorySection(solvent));
        b.setCostPracticalityAssessment(practicalitySection(solvent, red, recommendationType));
        b.setOverallRecommendation(overallSection(polymer, solvent, probability, red, recommendationType, rankPosition));
        b.setSaferAlternative(saferAlternativeSection(solvent, saferAlternative, recommendationType));
        return b;
    }

    private String compatibilitySection(Polymer polymer, Solvent solvent,
                                        double probability, double red, double ra, String type) {
        int mlPct = (int) Math.round(probability * 100);

        String redNote;
        if (red < 1.0)      redNote = "Hansen-compatible (RED < 1.0)";
        else if (red < 1.5) redNote = "borderline (RED 1.0–1.5) — validate in a trial";
        else                redNote = "Hansen-incompatible (RED > 1.5)";

        if ("NOT_RECOMMENDED".equals(type)) {
            return String.format(
                    "%s vs %s: %s, ML %d%%. Poor match — use a top-ranked solvent instead.",
                    solvent.getName(), polymer.getPolymerName(), redNote, mlPct
            );
        }

        return String.format(
                "%s vs %s: %s, ML %d%%, Ra %.1f. %s",
                solvent.getName(), polymer.getPolymerName(), redNote, mlPct, ra,
                red < 1.0 && mlPct >= 50
                        ? "Good alignment — start with a small-scale dissolution trial."
                        : "Mixed signals — compare with higher-ranked options before committing."
        );
    }

    private String healthSafetySection(Solvent solvent) {
        String name = solvent.getName();
        String knownHazard = SolventHazardNotes.lookup(name);

        if (knownHazard != null) {
            return String.format(
                    "%s — %s. Work in a fume hood with PPE; avoid routine use unless necessary.",
                    name, capitalizeFirst(knownHazard)
            );
        }

        double toxicity = solvent.getToxicityLevel();
        if (toxicity > 10) {
            return String.format(
                    "Elevated toxicity index (%.1f). Use ventilated conditions, minimise quantity, and collect waste as hazardous.",
                    toxicity
            );
        }
        if (toxicity > 0) {
            return String.format(
                    "Moderate toxicity (%.1f). Standard PPE and fume-cupboard work are sufficient for routine handling.",
                    toxicity
            );
        }

        return String.format(
                "No severe hazard flagged in catalog — still check the SDS for flash point and exposure limits before use."
        );
    }

    private String environmentalSection(Solvent solvent) {
        String env = solvent.getEnvImpactScore() == null ? "MEDIUM" : solvent.getEnvImpactScore().toUpperCase();

        return switch (env) {
            case "LOW" -> String.format(
                    "Low environmental impact — preferable for green labs. Collect waste in solvent-recovery containers, not drains."
            );
            case "HIGH" -> String.format(
                    "High impact — poorly biodegradable and may persist after disposal. Minimise use and route waste through licensed hazardous-waste channels."
            );
            default -> String.format(
                    "Moderate impact — acceptable for small trials; compare lower-impact alternatives before scaling up."
            );
        };
    }

    private String regulatorySection(Solvent solvent) {
        if (solvent.isEuBanStatus()) {
            return String.format(
                    "%s is EU-restricted. Confirm institutional approval, exposure controls, and substitution justification before ordering.",
                    solvent.getName()
            );
        }
        return "No EU restriction flagged — verify current REACH status before long-term adoption.";
    }

    private String practicalitySection(Solvent solvent, double red, String type) {
        String name = solvent.getName();

        if ("NOT_RECOMMENDED".equals(type)) {
            return "Not suited for routine lab work with this polymer — use a top-ranked match to save trial time and material.";
        }
        if (red < 1.0) {
            return String.format(
                    "%s is a practical first choice — start with a small room-temp trial (10–50 mL) and confirm purity grade (ACS/HPLC) matches your protocol.",
                    name
            );
        }
        if (red < 1.5) {
            return String.format(
                    "%s may need longer contact time or higher temperature — allow extra trial time before scaling up.",
                    name
            );
        }
        return "Lower-priority option — only try if higher-ranked solvents fail in trial.";
    }

    private String overallSection(Polymer polymer, Solvent solvent,
                                  double probability, double red, String type, int rank) {
        String name = solvent.getName();
        String polymerName = polymer.getPolymerName();
        int mlPct = (int) Math.round(probability * 100);

        if ("NOT_RECOMMENDED".equals(type)) {
            return String.format(
                    "Avoid %s for %s (RED %.2f, ML %d%%) — pick from the Top 10 instead.",
                    name, polymerName, red, mlPct
            );
        }

        if (red < 1.0 && probability >= 0.5 && rank <= 3) {
            return String.format(
                    "Strong pick (#%d): %s + %s. Run a small room-temp trial first.",
                    rank, name, polymerName
            );
        }
        if (red < 1.0 && probability >= 0.5) {
            return String.format(
                    "Viable option (#%d) for %s — trial recommended; higher ranks may perform slightly better.",
                    rank, polymerName
            );
        }
        return String.format(
                "Mixed fit (#%d) for %s — only try if top options fail; may need higher temp or concentration.",
                rank, polymerName
        );
    }

    private String saferAlternativeSection(Solvent current, Solvent alternative, String type) {
        if (!"RECOMMENDED".equals(type) || alternative == null) {
            return null;
        }
        if (current.getSolventId().equals(alternative.getSolventId())) {
            return null;
        }

        return String.format(
                "Consider %s — better safety/sustainability profile with similar compatibility. Run a parallel trial to compare.",
                alternative.getName()
        );
    }

    private static String capitalizeFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
