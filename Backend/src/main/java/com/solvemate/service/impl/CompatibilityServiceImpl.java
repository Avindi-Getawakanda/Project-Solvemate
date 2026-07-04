package com.solvemate.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    private final PolymerRepository             polymerRepository;
    private final SolventRepository             solventRepository;
    private final CompatibilityResultRepository resultRepository;
    private final HttpClient                    httpClient;
    private final ObjectMapper                  objectMapper;

    public CompatibilityServiceImpl(PolymerRepository polymerRepository,
                                    SolventRepository solventRepository,
                                    CompatibilityResultRepository resultRepository) {
        this.polymerRepository = polymerRepository;
        this.solventRepository = solventRepository;
        this.resultRepository  = resultRepository;
        this.httpClient        = HttpClient.newHttpClient();
        this.objectMapper      = new ObjectMapper();
    }

    @Override
    public CompatibilityAnalysisResponse analyze(Long polymerId) {
        Polymer polymer = polymerRepository.findById(polymerId)
                .orElseThrow(() -> new BadRequestException("Polymer not found: " + polymerId));

        List<Solvent> solvents = solventRepository.findAll();
        if (solvents.isEmpty()) throw new BadRequestException("No solvents found");

        List<Map<String, Object>> allScored = callMlBatch(polymer, solvents);
        allScored.sort((a, b) -> Double.compare(
                (Double) b.get("probability"), (Double) a.get("probability")));

        AnalysisSummary summary = buildSummary(allScored, solvents.size());

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
                polymer, topList, solvents.size(), "RECOMMENDED", true);
        List<CompatibilityResultResponse> notRecommended = buildResponses(
                polymer, bottomList, solvents.size(), "NOT_RECOMMENDED", false);

        CompatibilityAnalysisResponse response = new CompatibilityAnalysisResponse();
        response.setRecommended(recommended);
        response.setNotRecommended(notRecommended);
        response.setSummary(summary);
        return response;
    }

    @Override
    public List<CompatibilityResultResponse> getResultsForPolymer(Long polymerId) {
        return resultRepository
                .findByPolymer_PolymerIdOrderByCompatibilityScoreDesc(polymerId)
                .stream()
                .map(r -> {
                    CompatibilityResultResponse resp = new CompatibilityResultResponse();
                    resp.setResultId(r.getResultId());
                    resp.setPolymerId(r.getPolymer().getPolymerId());
                    resp.setPolymerName(r.getPolymer().getPolymerName());
                    resp.setSolventId(r.getSolvent().getSolventId());
                    resp.setSolventName(r.getSolvent().getName());
                    resp.setMlProbability(r.getMlProbability());
                    resp.setCompatibilityScore(r.getCompatibilityScore());
                    resp.setRedValue(r.getRedValue());
                    resp.setRaValue(r.getRaValue());
                    resp.setRankPosition(r.getRankPosition());
                    resp.setResult(r.getResult());
                    resp.setRecommendationType("RECOMMENDED");
                    return resp;
                }).toList();
    }

    private AnalysisSummary buildSummary(List<Map<String, Object>> scored, int total) {
        List<Double> probs = scored.stream()
                .map(m -> (Double) m.get("probability"))
                .sorted()
                .toList();

        int high = 0, moderate = 0, low = 0, redCompat = 0;
        for (Map<String, Object> item : scored) {
            double p = (Double) item.get("probability");
            double red = (Double) item.get("red_value");
            if (p >= 0.7) high++;
            else if (p >= 0.5) moderate++;
            else low++;
            if (red < 1.0) redCompat++;
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
        return summary;
    }

    private List<CompatibilityResultResponse> buildResponses(
            Polymer polymer, List<Map<String, Object>> items,
            int totalSolvents, String type, boolean persist) {

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

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rawExplanation = (List<Map<String, Object>>) item.get("explanation");
            if (rawExplanation != null) {
                resp.setExplanation(toShapList(rawExplanation));
            }

            responses.add(resp);
        }
        return responses;
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
