import { useState, useEffect } from "react";
import type { TrialResponse, PolymerResponse, SolventResponse } from "../services/api";
import { getAllPolymers, getAllSolvents } from "../services/api";
import "../styles/report.css";

interface Props {
    trial: TrialResponse;
    onBack: () => void;
}

export default function ReportPage({ trial, onBack }: Props) {
    const [polymer, setPolymer]     = useState<PolymerResponse | null>(null);
    const [solvent, setSolvent]     = useState<SolventResponse | null>(null);
    const generatedAt               = new Date().toLocaleString("en-GB");
    const trialDate                 = new Date(trial.trialDate).toLocaleDateString("en-GB");

    useEffect(() => {
        
        Promise.all([getAllPolymers(), getAllSolvents()]).then(([polymers, solvents]) => {
            setPolymer(polymers.find(p => p.polymerName === trial.polymerName) ?? null);
            setSolvent(solvents.find(s => s.name === trial.solventName) ?? null);
        });
    }, [trial]);

   

    const compatScore = polymer && solvent
        ? calcCompatScore(polymer, solvent)
        : 0;

    const raValue = polymer && solvent
        ? calcRa(polymer, solvent)
        : 0;

    const redValue = polymer && solvent
        ? raValue / polymer.r0
        : 0;

    const rating = trial.trialResult === "SUCCESSFUL" ? 5
                 : trial.trialResult === "PARTIALLY_SUCCESSFUL" ? 3 : 1;

    const envScore    = solvent ? getEnvScore(solvent.envImpactScore) : 0;
    const euStatus    = solvent?.euBanStatus ? "Restricted Use" : "Compliant";
    const euClass     = solvent?.euBanStatus ? "eu-restricted" : "eu-compliant";

    const finalRec    = getFinalRecommendation(compatScore, trial.trialResult,
                                               solvent?.euBanStatus ?? false);

    const estimatedUsage = (trial.concentration / 100 * 15).toFixed(1);
    const totalCost      = solvent
        ? (solvent.costPerLiter * parseFloat(estimatedUsage)).toFixed(2)
        : "0.00";

    

    const handlePrint = () => window.print();

    const handleDownload = () => {
        const content = buildReportText(trial, polymer, solvent, compatScore,
                                        raValue, redValue, rating, envScore,
                                        euStatus, finalRec, generatedAt);
        const blob = new Blob([content], { type: "text/plain" });
        const url  = URL.createObjectURL(blob);
        const a    = document.createElement("a");
        a.href     = url;
        a.download = `SolveMate-Report-${trial.polymerName}-${trial.solventName}.txt`;
        a.click();
        URL.revokeObjectURL(url);
    };

    const resultLabel = trial.trialResult === "SUCCESSFUL" ? "successful"
                      : trial.trialResult === "PARTIALLY_SUCCESSFUL" ? "partial" : "failed";

    return (
        <div className="report-page" id="report-printable">
            {}
            <div className="report-topbar no-print">
                <button className="back-btn" onClick={onBack}>← Back to Trials List</button>
                <div className="report-actions">
                    <button className="report-action-btn" onClick={handleDownload}>
                        ⬇ Download PDF
                    </button>
                    <button className="report-action-btn" onClick={handlePrint}>
                        🖨 Print
                    </button>
                </div>
            </div>

            {}
            <div className="report-document">

                {}
                <div className="report-title-block">
                    <h1>SolveMate Analysis Report</h1>
                    <p>Polymer-Solvent Compatibility Analysis</p>
                    <p className="report-dates">
                        Trial conducted on {trialDate}<br />
                        Report generated on {generatedAt}
                    </p>
                </div>

                <hr className="report-divider" />

                {}
                <div className="report-section">
                    <h2><span className="section-dot blue"></span>Trial Information</h2>
                    <div className="report-grid-2">
                        <div className="report-field">
                            <label>Conducted By</label>
                            <p>{trial.performedBy}</p>
                        </div>
                        <div className="report-field">
                            <label>Date</label>
                            <p>{trialDate}</p>
                        </div>
                        <div className="report-field">
                            <label>Temperature</label>
                            <p>{trial.temperature}°C</p>
                        </div>
                        <div className="report-field">
                            <label>Concentration</label>
                            <p>{trial.concentration}%</p>
                        </div>
                        <div className="report-field">
                            <label>Result</label>
                            <span className={`trial-badge ${resultLabel}`}>{resultLabel}</span>
                        </div>
                        <div className="report-field">
                            <label>Rating</label>
                            <p>{"★".repeat(rating)}{"☆".repeat(5 - rating)} {rating}/5</p>
                        </div>
                    </div>
                    {trial.outcomeObservation && (
                        <div className="report-field full">
                            <label>Observation</label>
                            <p>{trial.outcomeObservation}</p>
                        </div>
                    )}
                </div>

                {}
                <div className="report-section">
                    <h2><span className="section-dot blue"></span>Polymer Information</h2>
                    <div className="report-info-box">
                        <div className="report-grid-2">
                            <div className="report-field">
                                <label>Name</label>
                                <p>{trial.polymerName}</p>
                            </div>
                            <div className="report-field">
                                <label>Type</label>
                                <p>{polymer?.polymerCategory ?? "—"}</p>
                            </div>
                            <div className="report-field">
                                <label>Hansen Parameters</label>
                                <p>δD: {polymer?.deltaD ?? "—"}, δP: {polymer?.deltaP ?? "—"}, δH: {polymer?.deltaH ?? "—"}</p>
                            </div>
                            <div className="report-field">
                                <label>Interaction Radius</label>
                                <p>R₀: {polymer?.r0 ?? "—"}</p>
                            </div>
                        </div>
                    </div>
                </div>

                {}
                <div className="report-section">
                    <h2><span className="section-dot teal"></span>Solvent Details</h2>
                    <div className="report-info-box">
                        <div className="report-grid-2">
                            <div className="report-field">
                                <label>Name</label>
                                <p>{trial.solventName}</p>
                            </div>
                            <div className="report-field">
                                <label>Formula</label>
                                <p>{solvent?.chemicalFormula ?? "—"}</p>
                            </div>
                            <div className="report-field">
                                <label>Hansen Parameters</label>
                                <p>δD: {solvent?.deltaD ?? "—"}, δP: {solvent?.deltaP ?? "—"}, δH: {solvent?.deltaH ?? "—"}</p>
                            </div>
                            <div className="report-field">
                                <label>Safety Level</label>
                                <span className={`safety-badge ${solvent?.envImpactScore?.toLowerCase() ?? "low"}`}>
                                    {solvent?.envImpactScore?.toLowerCase() ?? "—"}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                {}
                <div className="report-section">
                    <h2><span className="section-dot purple"></span>Compatibility Analysis</h2>
                    <div className="report-info-box">
                        <div className="compat-score-display">
                            <div className="compat-score-circle">
                                <span className="compat-score-value">{compatScore.toFixed(1)}%</span>
                                <span className="compat-score-label">Compatibility Score</span>
                            </div>
                            <div className="compat-details">
                                <div className="compat-bar-wrap">
                                    <span>Compatibility</span>
                                    <div className="compat-bar">
                                        <div className="compat-bar-fill" style={{ width: `${compatScore}%` }} />
                                    </div>
                                    <span>{compatScore.toFixed(1)}%</span>
                                </div>
                                <div className="report-grid-3">
                                    <div className="report-field">
                                        <label>Ra (Hansen Distance)</label>
                                        <p>{raValue.toFixed(3)}</p>
                                    </div>
                                    <div className="report-field">
                                        <label>RED Value</label>
                                        <p className={redValue < 1.0 ? "text-green" : redValue < 1.5 ? "text-yellow" : "text-red"}>
                                            {redValue.toFixed(3)}
                                        </p>
                                    </div>
                                    <div className="report-field">
                                        <label>Compatibility</label>
                                        <p>{redValue < 1.0 ? "Compatible" : redValue < 1.5 ? "Borderline" : "Incompatible"}</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {}
                <div className="report-section">
                    <h2><span className="report-section-icon">$</span> Cost Analysis</h2>
                    <div className="report-info-box">
                        <div className="report-grid-3">
                            <div className="report-field">
                                <label>Cost per Liter</label>
                                <p>${solvent?.costPerLiter?.toFixed(2) ?? "0.00"}</p>
                            </div>
                            <div className="report-field">
                                <label>Estimated Usage</label>
                                <p>{estimatedUsage}L</p>
                            </div>
                            <div className="report-field">
                                <label>Total Cost</label>
                                <p className="cost-total">${totalCost}</p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* ── 6. Environmental Impact ──────────────────────────── */}
                <div className="report-section">
                    <h2><span className="report-section-icon">🌿</span> Environmental Impact</h2>
                    <div className="report-info-box">
                        <div className="env-score-row">
                            <div>
                                <label className="report-field-label">Environmental Score</label>
                                <p className={`env-score-value ${envScore >= 70 ? "text-green" : envScore >= 40 ? "text-yellow" : "text-red"}`}>
                                    {envScore}/100
                                </p>
                            </div>
                            <span className={`env-badge ${solvent?.envImpactScore?.toLowerCase() ?? "low"}`}>
                                {solvent?.envImpactScore === "LOW" ? "Low Impact"
                               : solvent?.envImpactScore === "MEDIUM" ? "Moderate"
                               : "High Impact"}
                            </span>
                        </div>
                    </div>
                </div>

                {}
                <div className="report-section">
                    <h2><span className="report-section-icon">🛡</span> EU Regulatory Compliance</h2>
                    <div className="report-info-box">
                        <div className="report-field">
                            <label>Status</label>
                            <span className={`eu-badge ${euClass}`}>{euStatus}</span>
                        </div>
                        <p className="eu-note">
                            {solvent?.euBanStatus
                                ? "This solvent has usage restrictions under EU regulations. Additional safety measures may be required."
                                : "This solvent is compliant with EU REACH regulations. No additional restrictions apply."}
                        </p>
                    </div>
                </div>

                {}
                <div className="report-section">
                    <div className="final-rec-box">
                        <h3>Final Recommendation</h3>
                        <p className="final-rec-text">{finalRec}</p>
                        <div className="final-rec-details">
                            <div>
                                <span className="final-rec-label">Trial Result:</span>
                                <span className={`trial-badge ${resultLabel}`}>{resultLabel}</span>
                                <span className="final-rec-sub">
                                    at {trial.temperature}°C with {trial.concentration}% concentration
                                </span>
                            </div>
                            <p>Laboratory Rating: {"★".repeat(rating)}{"☆".repeat(5 - rating)} {rating}/5</p>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    );
}



function calcRa(p: PolymerResponse, s: SolventResponse): number {
    const dD = s.deltaD - p.deltaD;
    const dP = s.deltaP - p.deltaP;
    const dH = s.deltaH - p.deltaH;
    return Math.sqrt(4 * dD * dD + dP * dP + dH * dH);
}

function calcCompatScore(p: PolymerResponse, s: SolventResponse): number {
    const ra  = calcRa(p, s);
    const red = ra / p.r0;
    const physics = Math.max(0, 1 - red / 2);
    return Math.round(physics * 1000) / 10;
}

function getEnvScore(envImpactScore: string): number {
    if (envImpactScore === "LOW")    return 82;
    if (envImpactScore === "MEDIUM") return 45;
    return 18;
}

function getFinalRecommendation(score: number, result: string, euBan: boolean): string {

    if (result === "FAILED" || score < 30) {
        return `Based on Hansen Solubility Parameter analysis, this solvent is NOT RECOMMENDED for this polymer. The compatibility score of ${score.toFixed(1)}% indicates poor solubility.`;
    }
    if (euBan) {
        return `This solvent shows ${score >= 70 ? "good" : "moderate"} compatibility (${score.toFixed(1)}%) but is subject to EU usage restrictions. Use with additional safety measures and regulatory approval.`;
    }
    if (score >= 70 && result === "SUCCESSFUL") {
        return `Based on Hansen Solubility Parameter analysis, this solvent is HIGHLY RECOMMENDED. The compatibility score of ${score.toFixed(1)}% indicates excellent solubility.`;
    }
    return `Based on Hansen Solubility Parameter analysis, this solvent shows MODERATE compatibility (${score.toFixed(1)}%). Suitable for use under controlled conditions.`;
}

function buildReportText(
    trial: TrialResponse, polymer: PolymerResponse | null, solvent: SolventResponse | null,
    compatScore: number, ra: number, red: number, rating: number,
    envScore: number, euStatus: string, finalRec: string, generatedAt: string
): string {
    return `
============================================================
              SOLVEMATE ANALYSIS REPORT
       Polymer-Solvent Compatibility Analysis
============================================================
Trial Date    : ${new Date(trial.trialDate).toLocaleDateString("en-GB")}
Report Date   : ${generatedAt}

TRIAL INFORMATION
-----------------
Conducted By  : ${trial.performedBy}
Temperature   : ${trial.temperature}°C
Concentration : ${trial.concentration}%
Result        : ${trial.trialResult}
Rating        : ${rating}/5
Observation   : ${trial.outcomeObservation}

POLYMER INFORMATION
-------------------
Name          : ${trial.polymerName}
Type          : ${polymer?.polymerCategory ?? "N/A"}
Hansen Params : δD=${polymer?.deltaD}, δP=${polymer?.deltaP}, δH=${polymer?.deltaH}
R₀            : ${polymer?.r0}

SOLVENT DETAILS
---------------
Name          : ${trial.solventName}
Formula       : ${solvent?.chemicalFormula ?? "N/A"}
Hansen Params : δD=${solvent?.deltaD}, δP=${solvent?.deltaP}, δH=${solvent?.deltaH}
Safety Level  : ${solvent?.envImpactScore ?? "N/A"}

COMPATIBILITY ANALYSIS
----------------------
Compatibility Score : ${compatScore.toFixed(1)}%
Ra (Distance)       : ${ra.toFixed(3)}
RED Value           : ${red.toFixed(3)}

COST ANALYSIS
-------------
Cost per Liter : $${solvent?.costPerLiter?.toFixed(2)}
Estimated Usage: ${(trial.concentration / 100 * 15).toFixed(1)}L
Total Cost     : $${((solvent?.costPerLiter ?? 0) * (trial.concentration / 100 * 15)).toFixed(2)}

ENVIRONMENTAL IMPACT
--------------------
Environmental Score : ${envScore}/100
Impact Level        : ${solvent?.envImpactScore}

EU REGULATORY COMPLIANCE
------------------------
Status : ${euStatus}

FINAL RECOMMENDATION
--------------------
${finalRec}

============================================================
              Generated by SolveMate System
============================================================
`.trim();
}
