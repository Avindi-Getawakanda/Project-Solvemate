package com.solvemate.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solvemate.config.SolventHazardNotes;
import com.solvemate.dto.RecommendationBriefing;
import com.solvemate.dto.CompatibilityAnalysisResponse;
import com.solvemate.dto.CompatibilityAnalysisResponse.AnalysisSummary;
import com.solvemate.dto.CompatibilityResultResponse;
import com.solvemate.dto.CompatibilityResultResponse.ShapExplanation;
import com.solvemate.exception.BadRequestException;
import com.solvemate.model.CompatibilityResult;
import com.solvemate.model.Polymer;
import com.solvemate.model.Solvent;
import com.solvemate.repository.CompatibilityResultRepository;
import com.solvemate.repository.PolymerRepository;
import com.solvemate.repository.SolventRepository;
import com.solvemate.service.CompatibilityService;
import com.solvemate.service.RecommendationBriefingGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
@Transactional
public class CompatibilityServiceImpl implements CompatibilityService {

    private static final int    TOP_K            = 10;
    private static final int    BOTTOM_K         = 5;
    private static final int    SHAP_TOP_K       = 3;
    private static final String ML_PREDICT_URL   = "http://localhost:5000/predict";
    private static final String ML_BATCH_URL     = "http://localhost:5000/predict/batch";

    // Green Solvent Recommendation Engine weights
    private static final double GREEN_ML_WEIGHT    = 0.6;
    private static final double GREEN_SCORE_WEIGHT = 0.4;
    private static final double EU_BAN_PENALTY     = 0.1;

    private final PolymerRepository             polymerRepository;
    private final SolventRepository             solventRepository;
    private final CompatibilityResultRepository resultRepository;
    private final RecommendationBriefingGenerator briefingGenerator;
    private final HttpClient                    httpClient;
    private final ObjectMapper                  objectMapper;

    public CompatibilityServiceImpl(PolymerRepository polymerRepository,
                                    SolventRepository solventRepository,
                                    CompatibilityResultRepository resultRepository,
                                    RecommendationBriefingGenerator briefingGenerator) {
        this.polymerRepository = polymerRepository;
        this.solventRepository = solventRepository;
        this.resultRepository  = resultRepository;
        this.briefingGenerator = briefingGenerator;
        this.httpClient        = HttpClient.newHttpClient();
        this.objectMapper      = new ObjectMapper();
    }

    @Override
    public CompatibilityAnalysisResponse analyze(Long polymerId, boolean greenMode) {
        Polymer polymer = polymerRepository.findById(polymerId)
                .orElseThrow(() -> new BadRequestException("Polymer not found: " + polymerId));

        List<Solvent> solvents = solventRepository.findAll();
        if (solvents.isEmpty()) throw new BadRequestException("No solvents found");

        List<Map<String, Object>> allScored = callMlBatch(polymer, solvents);

        for (Map<String, Object> item : allScored) {
            Solvent solvent = (Solvent) item.get("solvent");
            double probability = (Double) item.get("probability");
            double green = greenScore(solvent);
            item.put("green_score", green);
            item.put("combined_score", GREEN_ML_WEIGHT * probability + GREEN_SCORE_WEIGHT * green);
        }

        String rankKey = greenMode ? "combined_score" : "probability";
        allScored.sort((a, b) -> Double.compare(
                (Double) b.get(rankKey), (Double) a.get(rankKey)));

        AnalysisSummary summary = buildSummary(allScored, solvents.size(), greenMode);

        int topCount = Math.min(TOP_K, allScored.size());
        List<Map<String, Object>> topList = new ArrayList<>(allScored.subList(0, topCount));

        int bottomStart = Math.max(0, allScored.size() - BOTTOM_K);
        List<Map<String, Object>> bottomList = new ArrayList<>(allScored.subList(bottomStart, allScored.size()));
        Collections.reverse(bottomList);

        for (int i = 0; i < Math.min(SHAP_TOP_K, topList.size()); i++) {
            Map<String, Object> item = topList.get(i);
            Solvent solvent = (Solvent) item.get("solvent");
            Map<String, Object> detailed = callMlServiceWithShap(polymer, solvent);
            item.put("explanation", detailed.get("explanation"));
        }

        resultRepository.deleteByPolymer_PolymerId(polymerId);

        List<CompatibilityResultResponse> recommended = buildResponses(
                polymer, topList, solvents.size(), "RECOMMENDED", true, topList);
        List<CompatibilityResultResponse> notRecommended = buildResponses(
                polymer, bottomList, solvents.size(), "NOT_RECOMMENDED", false, topList);

        CompatibilityAnalysisResponse response = new CompatibilityAnalysisResponse();
        response.setRecommended(recommended);
        response.setNotRecommended(notRecommended);
        response.setSummary(summary);
        return response;
    }

    /**
     * Composite 0–1 sustainability score for a solvent.
     * Built from envImpactScore (LOW/MEDIUM/HIGH) and euBanStatus, which are
     * populated in the catalog today. toxicityLevel is blended in once it is
     * greater than 0 — the seeded catalog currently leaves it at 0, so this
     * keeps the score accurate now and automatically improves once real
     * toxicity data is added, without any code change.
     */
    private double greenScore(Solvent solvent) {
        double envScore = switch (solvent.getEnvImpactScore() == null ? "" : solvent.getEnvImpactScore().toUpperCase()) {
            case "LOW"    -> 1.0;
            case "MEDIUM" -> 0.55;
            case "HIGH"   -> 0.15;
            default       -> 0.5;
        };

        double score = envScore;
        if (solvent.getToxicityLevel() > 0) {
            double toxicityFactor = Math.max(0.0, 1.0 - (solvent.getToxicityLevel() / 20.0));
            score = (envScore + toxicityFactor) / 2.0;
        }

        if (solvent.isEuBanStatus()) {
            score *= EU_BAN_PENALTY;
        }

        return Math.max(0.0, Math.min(1.0, score));
    }

    /**
     * Builds a one-sentence, non-technical explanation of the compatibility
     * result plus the sustainability tradeoff, in the style:
     * "Benzene is highly compatible with Polystyrene — but it causes cancer."
     */
    private String greenInsight(String polymerName, Solvent solvent, double probability) {
        String compatDescriptor;
        if (probability >= 0.7)      compatDescriptor = "highly compatible with";
        else if (probability >= 0.5) compatDescriptor = "moderately compatible with";
        else                          compatDescriptor = "not a good match for";

        String hazard = SolventHazardNotes.lookup(solvent.getName());
        if (hazard == null) {
            if (solvent.isEuBanStatus()) {
                hazard = "it is restricted or banned under EU chemical regulations";
            } else if ("HIGH".equalsIgnoreCase(solvent.getEnvImpactScore())) {
                hazard = "it has a high environmental impact";
            } else if ("MEDIUM".equalsIgnoreCase(solvent.getEnvImpactScore())) {
                hazard = "it has a moderate environmental impact — worth comparing against greener alternatives";
            }
        }

        if (hazard == null) {
            return solvent.getName() + " is " + compatDescriptor + " " + polymerName
                    + " — and it also carries a low environmental impact rating.";
        }
        return solvent.getName() + " is " + compatDescriptor + " " + polymerName
                + " — but " + hazard + ".";
    }

    @Override
    public List<CompatibilityResultResponse> getResultsForPolymer(Long polymerId) {
        List<CompatibilityResult> stored = resultRepository
                .findByPolymer_PolymerIdOrderByCompatibilityScoreDesc(polymerId);

        List<Map<String, Object>> pool = stored.stream()
                .limit(10)
                .map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("solvent", r.getSolvent());
                    m.put("probability", r.getMlProbability());
                    m.put("green_score", greenScore(r.getSolvent()));
                    return m;
                })
                .toList();

        return stored.stream()
                .map(r -> {
                    Polymer polymer = r.getPolymer();
                    Solvent solvent = r.getSolvent();
                    double prob = r.getMlProbability();
                    double red = r.getRedValue();
                    double ra = r.getRaValue();

                    CompatibilityResultResponse resp = new CompatibilityResultResponse();
                    resp.setResultId(r.getResultId());
                    resp.setPolymerId(polymer.getPolymerId());
                    resp.setPolymerName(polymer.getPolymerName());
                    resp.setSolventId(solvent.getSolventId());
                    resp.setSolventName(solvent.getName());
                    resp.setMlProbability(r.getMlProbability());
                    resp.setCompatibilityScore(r.getCompatibilityScore());
                    resp.setRedValue(red);
                    resp.setRaValue(ra);
                    resp.setRankPosition(r.getRankPosition());
                    resp.setResult(r.getResult());
                    resp.setRecommendationType("RECOMMENDED");
                    resp.setGreenScore(round(greenScore(solvent)));
                    resp.setEnvImpactScore(solvent.getEnvImpactScore());
                    resp.setEuBanStatus(solvent.isEuBanStatus());

                    Solvent saferAlt = findSaferAlternative(
                            solvent, prob, greenScore(solvent), pool);
                    RecommendationBriefing briefing = briefingGenerator.generate(
                            polymer, solvent, prob, red, ra, "RECOMMENDED",
                            r.getRankPosition(), saferAlt);
                    resp.setBriefing(briefing);
                    resp.setGreenInsight(briefing.getOverallRecommendation());
                    return resp;
                }).toList();
    }

    private AnalysisSummary buildSummary(List<Map<String, Object>> scored, int total, boolean greenMode) {
        List<Double> probs = scored.stream()
                .map(m -> (Double) m.get("probability"))
                .sorted()
                .toList();

        int high = 0, moderate = 0, low = 0, redCompat = 0;
        double greenTotal = 0.0;
        for (Map<String, Object> item : scored) {
            double p = (Double) item.get("probability");
            double red = (Double) item.get("red_value");
            if (p >= 0.7) high++;
            else if (p >= 0.5) moderate++;
            else low++;
            if (red < 1.0) redCompat++;
            greenTotal += (Double) item.get("green_score");
        }

        double median = probs.isEmpty() ? 0.0 :
                probs.get(probs.size() / 2);
        double top = probs.isEmpty() ? 0.0 : probs.get(probs.size() - 1);

        AnalysisSummary summary = new AnalysisSummary();
        summary.setSolventsAnalysed(total);
        summary.setHighConfidenceCount(high);
        summary.setModerateCount(moderate);
        summary.setLowCount(low);
        summary.setRedCompatibleCount(redCompat);
        summary.setTopProbability(round(top));
        summary.setMedianProbability(round(median));
        summary.setGreenModeActive(greenMode);
        summary.setAverageGreenScore(scored.isEmpty() ? 0.0 : round(greenTotal / scored.size()));
        return summary;
    }

    private List<CompatibilityResultResponse> buildResponses(
            Polymer polymer, List<Map<String, Object>> items,
            int totalSolvents, String type, boolean persist,
            List<Map<String, Object>> recommendedPool) {

        List<CompatibilityResultResponse> responses = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            Solvent solvent = (Solvent) item.get("solvent");
            double probability = (Double) item.get("probability");
            double red = (Double) item.get("red_value");
            double ra  = (Double) item.get("ra_value");
            double score = Math.round(probability * 10000.0) / 100.0;

            double dD = solvent.getDeltaD() - polymer.getDeltaD();
            double dP = solvent.getDeltaP() - polymer.getDeltaP();
            double dH = solvent.getDeltaH() - polymer.getDeltaH();

            CompatibilityResultResponse resp = new CompatibilityResultResponse();

            if (persist) {
                CompatibilityResult result = new CompatibilityResult(
                        polymer, solvent, dD, dP, dH, ra, red,
                        round(probability), score
                );
                result.setRankPosition(i + 1);
                result = resultRepository.save(result);
                resp.setResultId(result.getResultId());
            }

            resp.setPolymerId(polymer.getPolymerId());
            resp.setPolymerName(polymer.getPolymerName());
            resp.setSolventId(solvent.getSolventId());
            resp.setSolventName(solvent.getName());
            resp.setMlProbability(round(probability));
            resp.setCompatibilityScore(score);
            resp.setRedValue(round(red));
            resp.setRaValue(round(ra));
            resp.setRankPosition(i + 1);
            resp.setResult(classifyResult(red, probability));
            resp.setRecommendationType(type);
            resp.setSolventsAnalysed(totalSolvents);
            resp.setGreenScore(round((Double) item.get("green_score")));
            resp.setEnvImpactScore(solvent.getEnvImpactScore());
            resp.setEuBanStatus(solvent.isEuBanStatus());

            Solvent saferAlt = "RECOMMENDED".equals(type)
                    ? findSaferAlternative(solvent, probability, (Double) item.get("green_score"), recommendedPool)
                    : null;

            RecommendationBriefing briefing = briefingGenerator.generate(
                    polymer, solvent, probability, red, ra, type, i + 1, saferAlt);
            resp.setBriefing(briefing);
            resp.setGreenInsight(briefing.getOverallRecommendation());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rawExplanation = (List<Map<String, Object>>) item.get("explanation");
            if (rawExplanation != null) {
                resp.setExplanation(toShapList(rawExplanation));
            }

            responses.add(resp);
        }
        return responses;
    }

    private Solvent findSaferAlternative(Solvent current, double currentProb,
                                         double currentGreen, List<Map<String, Object>> pool) {
        if (pool == null) return null;

        boolean needsAlt = current.isEuBanStatus()
                || "HIGH".equalsIgnoreCase(current.getEnvImpactScore());
        if (!needsAlt) return null;

        Solvent best = null;
        double bestGreen = currentGreen;

        for (Map<String, Object> item : pool) {
            Solvent candidate = (Solvent) item.get("solvent");
            if (candidate.getSolventId().equals(current.getSolventId())) continue;

            double candProb  = (Double) item.get("probability");
            double candGreen = (Double) item.get("green_score");

            if (candProb < currentProb - 0.15) continue;
            if (candGreen <= bestGreen + 0.05) continue;
            if (current.isEuBanStatus() && candidate.isEuBanStatus()) continue;

            best = candidate;
            bestGreen = candGreen;
        }
        return best;
    }

    private String classifyResult(double red, double probability) {
        if (red < 1.0 && probability >= 0.5)  return "COMPATIBLE";
        if (red < 1.5 && probability >= 0.4)  return "BORDERLINE";
        return "NOT_COMPATIBLE";
    }

    private List<Map<String, Object>> callMlBatch(Polymer polymer, List<Solvent> solvents) {
        List<Map<String, Object>> scored = new ArrayList<>();

        try {
            ObjectNode body = objectMapper.createObjectNode();
            ObjectNode polymerNode = body.putObject("polymer");
            polymerNode.put("delta_d_polymer", polymer.getDeltaD());
            polymerNode.put("delta_p_polymer", polymer.getDeltaP());
            polymerNode.put("delta_h_polymer", polymer.getDeltaH());
            polymerNode.put("r0_polymer",      polymer.getR0());

            ArrayNode solventsArray = body.putArray("solvents");
            for (Solvent s : solvents) {
                ObjectNode node = solventsArray.addObject();
                node.put("solvent_id",           s.getSolventId());
                node.put("delta_d_solvent",      s.getDeltaD());
                node.put("delta_p_solvent",      s.getDeltaP());
                node.put("delta_h_solvent",      s.getDeltaH());
                node.put("molar_volume_cm3_mol", s.getMolarVolume());
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ML_BATCH_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                for (JsonNode r : json.get("results")) {
                    int index = r.get("index").asInt();
                    Map<String, Object> item = new HashMap<>();
                    item.put("solvent",      solvents.get(index));
                    item.put("probability",  r.get("probability").asDouble());
                    item.put("red_value",    r.get("red_value").asDouble());
                    item.put("ra_value",     r.get("ra_value").asDouble());
                    item.put("explanation",  null);
                    scored.add(item);
                }
                return scored;
            }
            System.err.println("[ML] Batch scoring failed: HTTP " + response.statusCode());
        } catch (Exception e) {
            System.err.println("[ML] Batch service unavailable: " + e.getMessage());
        }

        for (Solvent solvent : solvents) {
            Map<String, Object> item = callMlServiceWithShap(polymer, solvent);
            item.put("solvent", solvent);
            item.put("red_value", 0.0);
            item.put("ra_value",  0.0);
            item.put("explanation", null);
            scored.add(item);
        }
        return scored;
    }

    private Map<String, Object> callMlServiceWithShap(Polymer polymer, Solvent solvent) {
        Map<String, Object> result = new HashMap<>();
        result.put("probability", 0.0);
        result.put("explanation", null);

        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("delta_d_solvent",      solvent.getDeltaD());
            body.put("delta_p_solvent",      solvent.getDeltaP());
            body.put("delta_h_solvent",      solvent.getDeltaH());
            body.put("molar_volume_cm3_mol", solvent.getMolarVolume());
            body.put("delta_d_polymer",      polymer.getDeltaD());
            body.put("delta_p_polymer",      polymer.getDeltaP());
            body.put("delta_h_polymer",      polymer.getDeltaH());
            body.put("r0_polymer",           polymer.getR0());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ML_PREDICT_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                result.put("probability", json.get("probability").asDouble());

                if (json.has("explanation")) {
                    List<Map<String, Object>> explanation = new ArrayList<>();
                    for (JsonNode e : json.get("explanation")) {
                        Map<String, Object> entry = new LinkedHashMap<>();
                        entry.put("feature",      e.get("feature").asText());
                        entry.put("label",        e.get("label").asText());
                        entry.put("shap_value",   e.get("shap_value").asDouble());
                        entry.put("contribution", e.get("contribution").asDouble());
                        if (e.has("plain_english")) {
                            entry.put("plain_english", e.get("plain_english").asText());
                        }
                        explanation.add(entry);
                    }
                    result.put("explanation", explanation);
                }
            }
        } catch (Exception e) {
            System.err.println("[ML] Service unavailable for " + solvent.getName() + ": " + e.getMessage());
        }

        return result;
    }

    private List<ShapExplanation> toShapList(List<Map<String, Object>> raw) {
        List<ShapExplanation> shapList = new ArrayList<>();
        for (Map<String, Object> e : raw) {
            ShapExplanation shap = new ShapExplanation();
            shap.setFeature(e.get("feature").toString());
            shap.setLabel(e.get("label").toString());
            shap.setShapValue(((Number) e.get("shap_value")).doubleValue());
            shap.setContribution(((Number) e.get("contribution")).doubleValue());
            if (e.get("plain_english") != null) {
                shap.setPlainEnglish(e.get("plain_english").toString());
            }
            shapList.add(shap);
        }
        return shapList;
    }

    private double round(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}