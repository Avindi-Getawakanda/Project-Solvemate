package com.solvemate.service;

import com.solvemate.config.SolventHazardNotes;
import com.solvemate.dto.RecommendationBriefing;
import com.solvemate.model.Polymer;
import com.solvemate.model.Solvent;
import org.springframework.stereotype.Component;

/**
 * Generates expert laboratory decision-support narratives for each recommendation.
 * Every section explains WHY a factor matters and HOW it affects lab work — not just WHAT the label is.
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
        b.setCostPracticalityAssessment(costSection(solvent));
        b.setOverallRecommendation(overallSection(polymer, solvent, probability, red, recommendationType, rankPosition));
        b.setSaferAlternative(saferAlternativeSection(solvent, saferAlternative, probability, recommendationType));
        return b;
    }

    private String compatibilitySection(Polymer polymer, Solvent solvent,
                                        double probability, double red, double ra, String type) {
        String name = solvent.getName();
        String polymerName = polymer.getPolymerName();
        int mlPct = (int) Math.round(probability * 100);

        String redInterpretation;
        if (red < 0.8) {
            redInterpretation = "well within the compatible range, indicating strong Hansen parameter alignment between solvent and polymer";
        } else if (red < 1.0) {
            redInterpretation = "below the critical threshold of 1.0, suggesting the solvent should dissolve or swell this polymer under typical laboratory conditions";
        } else if (red < 1.5) {
            redInterpretation = "in the borderline zone (1.0–1.5), meaning partial dissolution or swelling is possible but not reliably predictable — trial validation is essential";
        } else {
            redInterpretation = "above 1.5, which Hansen theory treats as incompatible — the chemical distance between solvent and polymer is too large for reliable dissolution";
        }

        String mlNote = type.equals("NOT_RECOMMENDED")
                ? String.format("The machine learning model assigns only %d%% confidence to this pairing, ranking it among the poorest matches in the catalog.", mlPct)
                : String.format("The machine learning model assigns %d%% confidence to this pairing based on Hansen parameters, molar volume, and historical compatibility patterns from 27,000+ polymer–solvent records.", mlPct);

        return String.format(
                "%s was evaluated against %s using Hansen Solubility Parameters. The RED index is %.2f — %s. " +
                "The overall chemical distance (Ra = %.1f) reflects how closely the solvent's dispersion, polarity, and hydrogen-bonding characteristics match the polymer. %s " +
                "In practice, a low RED with strong ML confidence means you can proceed to a small-scale dissolution trial with reasonable expectation of success; a high RED means time and material are better spent on higher-ranked alternatives.",
                name, polymerName, red, redInterpretation, ra, mlNote
        );
    }

    private String healthSafetySection(Solvent solvent) {
        String name = solvent.getName();
        String knownHazard = SolventHazardNotes.lookup(name);

        if (knownHazard != null) {
            return String.format(
                    "%s carries documented health hazards: %s. " +
                    "Researchers handling this solvent should consult the Safety Data Sheet (SDS), use appropriate personal protective equipment (gloves, eye protection, fume hood), " +
                    "and minimise exposure duration. For routine laboratory work, consider whether a less hazardous alternative can achieve the same dissolution outcome.",
                    name, capitalizeFirst(knownHazard)
            );
        }

        double toxicity = solvent.getToxicityLevel();
        if (toxicity > 10) {
            return String.format(
                    "%s is recorded with an elevated toxicity index (%.1f) in the SolveMate catalog. " +
                    "Higher toxicity ratings indicate greater potential for acute or chronic health effects during handling, storage, or accidental exposure. " +
                    "Work under ventilated conditions, restrict quantities to what the experiment requires, and ensure waste is collected in labelled hazardous-waste containers rather than general drains.",
                    name, toxicity
            );
        }

        if (toxicity > 0) {
            return String.format(
                    "%s has a moderate toxicity rating (%.1f) in the catalog. " +
                    "Under normal laboratory conditions with standard PPE and fume-cupboard use, routine handling poses manageable risk. " +
                    "Avoid skin contact and inhalation of vapours, especially during heating or agitation when evaporation increases airborne exposure.",
                    name, toxicity
            );
        }

        return String.format(
                "%s does not have a specific toxicity classification in the SolveMate catalog, which typically indicates no widely documented severe acute hazard — but this does not replace the Safety Data Sheet. " +
                "All solvents should be handled with standard laboratory safety practices: nitrile gloves, eye protection, and work in a ventilated fume hood. " +
                "Never assume a solvent is harmless without reviewing its SDS for flash point, vapour pressure, and chronic exposure limits.",
                name
        );
    }

    private String environmentalSection(Solvent solvent) {
        String name = solvent.getName();
        String env = solvent.getEnvImpactScore() == null ? "MEDIUM" : solvent.getEnvImpactScore().toUpperCase();

        return switch (env) {
            case "LOW" -> String.format(
                    "%s is classified as low environmental impact in the SolveMate catalog. " +
                    "Low-impact solvents are generally more biodegradable, less persistent in water and soil, and produce fewer harmful by-products during disposal. " +
                    "This makes them preferable for laboratories aiming to reduce ecological footprint. " +
                    "Still collect waste in appropriate solvent-recovery containers — even low-impact solvents should not be poured down drains.",
                    name
            );
            case "HIGH" -> String.format(
                    "%s has a high environmental impact rating. Solvents in this category are often poorly biodegradable, may persist in soil and water after disposal, and can contribute to long-term environmental contamination if not managed through licensed hazardous-waste channels. " +
                    "If your laboratory has sustainability targets or green chemistry policies, this solvent should be used only when safer alternatives cannot achieve the required dissolution, and quantities should be minimised.",
                    name
            );
            default -> String.format(
                    "%s has a moderate environmental impact rating. It is neither among the cleanest nor the most problematic solvents in the catalog. " +
                    "Moderate-impact solvents require standard waste segregation and should be compared against lower-impact alternatives before scaling up any process. " +
                    "Document solvent usage volumes in your lab records to support future substitution decisions.",
                    name
            );
        };
    }

    private String regulatorySection(Solvent solvent) {
        String name = solvent.getName();

        if (solvent.isEuBanStatus()) {
            return String.format(
                    "%s is flagged as subject to European Union regulatory restrictions in the SolveMate catalog. " +
                    "Restricted solvents may appear on REACH candidate lists, SVHC (Substances of Very High Concern) registers, or national ban schedules because of carcinogenic, reprotoxic, or persistent environmental properties. " +
                    "Using this solvent may require additional risk assessments, exposure monitoring, substitution analysis, and documented justification for continued use. " +
                    "Check your institution's chemical approval process before ordering or using this material.",
                    name
            );
        }

        return String.format(
                "%s is not flagged with EU ban or restriction status in the SolveMate catalog, indicating no current entry on major European restriction lists used by this system. " +
                "Regulatory status can change — verify against the latest REACH and national chemical inventories before long-term adoption. " +
                "Maintaining an approved-chemicals list in your laboratory management system helps ensure compliance during audits and safety inspections.",
                name
        );
    }

    private String costSection(Solvent solvent) {
        String name = solvent.getName();
        double cost = solvent.getCostPerLiter();

        if (cost <= 0) {
            return String.format(
                    "Cost data for %s is not available in the catalog. Before committing to a process, obtain a supplier quote and factor in purity grade, shipping, and minimum order quantities — specialty solvents can significantly affect project budgets.",
                    name
            );
        }

        if (cost < 12) {
            return String.format(
                    "%s is economically favourable at approximately $%.2f per litre. " +
                    "Low-cost solvents reduce per-experiment material expense, which matters when running screening studies or teaching laboratories with limited budgets. " +
                    "Confirm that the supplier purity grade (e.g. ACS, HPLC) meets your experimental requirements — cheaper grades may contain impurities that affect dissolution results.",
                    name, cost
            );
        }

        if (cost < 25) {
            return String.format(
                    "%s costs approximately $%.2f per litre — a moderate price point typical of common laboratory solvents. " +
                    "For routine compatibility trials using small volumes (10–50 mL), cost is unlikely to be a limiting factor. " +
                    "For scale-up or continuous processes, calculate total solvent consumption including recovery losses before finalising this choice.",
                    name, cost
            );
        }

        return String.format(
                "%s is relatively expensive at approximately $%.2f per litre. " +
                "High-cost solvents increase the economic burden of screening, scale-up, and waste disposal. " +
                "If a lower-cost alternative from the recommended list achieves similar RED and ML scores, it may deliver the same laboratory outcome at lower total cost. " +
                "Order only the quantity needed for immediate experiments to avoid stock expiry and capital tied up in inventory.",
                name, cost
        );
    }

    private String overallSection(Polymer polymer, Solvent solvent,
                                  double probability, double red, String type, int rank) {
        String name = solvent.getName();
        String polymerName = polymer.getPolymerName();

        if ("NOT_RECOMMENDED".equals(type)) {
            return String.format(
                    "%s is not recommended for use with %s. It ranked among the lowest-scoring solvents in the full catalog analysis, with a RED index of %.2f and ML confidence of %d%%. " +
                    "Selecting this solvent risks failed dissolution, wasted polymer material, and unnecessary exposure to any associated health or environmental hazards. " +
                    "Choose a solvent from the Top 10 Recommended list instead.",
                    name, polymerName, red, (int) Math.round(probability * 100)
            );
        }

        boolean redOk = red < 1.0;
        boolean mlOk  = probability >= 0.5;
        boolean rankTop3 = rank <= 3;

        if (redOk && mlOk && rankTop3) {
            return String.format(
                    "%s is strongly recommended as rank #%d for %s. It combines favourable Hansen compatibility (RED = %.2f), solid ML confidence (%d%%), and a practical profile for laboratory use. " +
                    "This is a suitable primary candidate for your first dissolution trial — start with a small-scale test at room temperature, record observations in SolveMate Trials, and adjust concentration or temperature based on results.",
                    name, rank, polymerName, red, (int) Math.round(probability * 100)
            );
        }

        if (redOk && mlOk) {
            return String.format(
                    "%s is recommended (rank #%d) for %s with acceptable compatibility indicators (RED = %.2f, ML = %d%%). " +
                    "It provides a viable option for laboratory trials, though higher-ranked solvents on this list may offer slightly better parameter alignment. " +
                    "Proceed with a controlled trial and compare results against the top-ranked alternatives if initial dissolution is incomplete.",
                    name, rank, polymerName, red, (int) Math.round(probability * 100)
            );
        }

        return String.format(
                "%s appears at rank #%d but shows mixed compatibility signals for %s (RED = %.2f, ML = %d%%). " +
                "It may work under specific conditions (elevated temperature, higher concentration, extended contact time) but is not a first-choice solvent. " +
                "Prioritise higher-ranked options with RED below 1.0 before investing significant lab time in this pairing.",
                name, rank, polymerName, red, (int) Math.round(probability * 100)
        );
    }

    private String saferAlternativeSection(Solvent current, Solvent alternative,
                                           double probability, String type) {
        if (!"RECOMMENDED".equals(type) || alternative == null) {
            return null;
        }
        if (current.getSolventId().equals(alternative.getSolventId())) {
            return null;
        }

        boolean currentHighEnv = "HIGH".equalsIgnoreCase(current.getEnvImpactScore());
        boolean altBetterEnv   = "LOW".equalsIgnoreCase(alternative.getEnvImpactScore())
                || ("MEDIUM".equalsIgnoreCase(alternative.getEnvImpactScore()) && currentHighEnv);
        boolean currentEuBan   = current.isEuBanStatus();
        boolean currentExpensive = current.getCostPerLiter() > 20;

        if (!currentEuBan && !currentHighEnv && !currentExpensive) {
            return String.format(
                    "%s already presents a relatively balanced profile among recommended options. " +
                    "If you wish to explore alternatives, review other entries in the Top 10 list with similar ML scores and compare their RED indices side by side.",
                    current.getName()
            );
        }

        StringBuilder reason = new StringBuilder();
        if (currentEuBan) reason.append("avoiding EU regulatory restrictions");
        if (currentHighEnv) {
            if (reason.length() > 0) reason.append(" and ");
            reason.append("reducing environmental impact");
        }
        if (currentExpensive) {
            if (reason.length() > 0) reason.append(" and ");
            reason.append("lowering material cost");
        }

        return String.format(
                "Consider %s as a safer alternative to %s when %s. " +
                "%s is also recommended for this polymer and offers a %s environmental rating%s. " +
                "Run a parallel small-scale trial comparing both solvents to confirm dissolution quality before committing to either for larger experiments.",
                alternative.getName(),
                current.getName(),
                reason,
                alternative.getName(),
                alternative.getEnvImpactScore() != null ? alternative.getEnvImpactScore().toLowerCase() : "favourable",
                alternative.isEuBanStatus() ? "" : " with no EU restriction flag"
        );
    }

    private static String capitalizeFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
