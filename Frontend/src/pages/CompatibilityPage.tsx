import { useState, useEffect, useMemo } from "react";
import type { PolymerResponse, CompatibilityResult, CompatibilityAnalysis, DashboardStats } from "../services/api";
import { getAllPolymers, runCompatibilityAnalysis, getDashboardStats } from "../services/api";
import "../styles/compatibility.css";

const LOADING_STEPS = [
    "Scoring full solvent catalog…",
    "Ranking by ML probability and RED index…",
    "Preparing recommendations and explanations…",
];

function pct(val: number) {
    const p = val <= 1 ? val * 100 : val;
    return Math.min(p, 100).toFixed(1);
}

function bar(val: number) {
    return Math.min(val <= 1 ? val * 100 : val, 100);
}

function badge(result: string) {
    if (result === "COMPATIBLE")     return { label: "Compatible",     cls: "compat-compatible" };
    if (result === "BORDERLINE")     return { label: "Borderline",     cls: "compat-borderline" };
    return                              { label: "Not Compatible", cls: "compat-incompatible" };
}

function resultKey(r: CompatibilityResult) {
    return r.resultId ?? r.solventId;
}

interface ResultCardProps {
    r: CompatibilityResult;
    variant: "recommended" | "not-recommended";
    expanded: number | null;
    onToggle: (key: number) => void;
}

function ResultCard({ r, variant, expanded, onToggle }: ResultCardProps) {
    const b = badge(r.result);
    const key = resultKey(r);
    const isExp = expanded === key;

    return (
        <div className={`result-card ${variant} ${r.rankPosition === 1 && variant === "recommended" ? "rank-1" : ""}`}>
            <div className="result-rank-badge">
                {variant === "recommended" ? `#${r.rankPosition}` : "↓"}
            </div>
            <div className="result-main">
                <div className="result-name-row">
                    <h3>{r.solventName}</h3>
                    <span className={`compat-badge ${b.cls}`}>{b.label}</span>
                </div>

                <div className="result-metrics">
                    <div className="metric-chip">
                        <span>ML Confidence</span>
                        <strong>{pct(r.mlProbability)}%</strong>
                    </div>
                    <div className="metric-chip">
                        <span>RED Index</span>
                        <strong className={r.redValue != null && r.redValue < 1 ? "text-green" : r.redValue != null && r.redValue < 1.5 ? "text-yellow" : "text-red"}>
                            {r.redValue != null ? r.redValue.toFixed(2) : "—"}
                        </strong>
                    </div>
                    <div className="metric-chip">
                        <span>Ra Distance</span>
                        <strong>{r.raValue != null ? r.raValue.toFixed(1) : "—"}</strong>
                    </div>
                </div>

                <div className="score-bar-wrap">
                    <span className="score-label">Relative score</span>
                    <div className="score-bar">
                        <div className="score-fill ml-fill" style={{ width: `${bar(r.mlProbability)}%` }} />
                    </div>
                    <span className="score-val">{pct(r.mlProbability)}%</span>
                </div>

                {r.explanation && (
                    <button className="explain-btn" onClick={() => onToggle(isExp ? -1 : key)}>
                        {isExp ? "Hide Explanation" : "View Explanation"}
                    </button>
                )}

                {isExp && r.explanation && (
                    <div className="shap-panel">
                        <p className="shap-title">Why <strong>{r.solventName}</strong> was ranked here</p>
                        <div className="shap-cards">
                            {r.explanation.map((e, i) => (
                                <div key={i} className={`shap-card ${e.shapValue > 0 ? "shap-positive" : "shap-negative"}`}>
                                    <div className="shap-card-header">
                                        <span className="shap-card-label">{e.label}</span>
                                        <span className="shap-card-pct">{e.contribution.toFixed(1)}%</span>
                                    </div>
                                    <div className="shap-bar-track">
                                        <div className="shap-bar-fill" style={{
                                            width: `${Math.min(e.contribution, 100)}%`,
                                            background: e.shapValue > 0 ? "var(--color-primary)" : "var(--color-danger)"
                                        }} />
                                    </div>
                                    {e.plainEnglish && <p className="shap-card-text">{e.plainEnglish}</p>}
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default function CompatibilityPage() {
    const [polymers, setPolymers]             = useState<PolymerResponse[]>([]);
    const [stats, setStats]                   = useState<DashboardStats | null>(null);
    const [analysis, setAnalysis]             = useState<CompatibilityAnalysis | null>(null);
    const [selectedId, setSelectedId]         = useState<number | null>(null);
    const [loading, setLoading]               = useState(false);
    const [loadingStep, setLoadingStep]       = useState(0);
    const [polymerLoading, setPolymerLoading] = useState(true);
    const [error, setError]                   = useState("");
    const [ran, setRan]                       = useState(false);
    const [expanded, setExpanded]             = useState<number | null>(null);
    const [polymerSearch, setPolymerSearch]   = useState("");

    useEffect(() => {
        Promise.all([getAllPolymers(), getDashboardStats()])
            .then(([p, s]) => { setPolymers(p); setStats(s); })
            .catch(() => setError("Failed to load data"))
            .finally(() => setPolymerLoading(false));
    }, []);

    useEffect(() => {
        if (!loading) { setLoadingStep(0); return; }
        const timers = [
            setTimeout(() => setLoadingStep(1), 800),
            setTimeout(() => setLoadingStep(2), 2500),
        ];
        return () => timers.forEach(clearTimeout);
    }, [loading]);

    const handleRun = async () => {
        if (!selectedId) { setError("Please select a polymer first"); return; }
        setError(""); setLoading(true); setRan(false); setExpanded(null); setAnalysis(null);
        try {
            const data = await runCompatibilityAnalysis(selectedId);
            setAnalysis(data); setRan(true);
        } catch (err: unknown) {
            setError(err instanceof Error ? err.message : "Analysis failed");
        } finally { setLoading(false); }
    };

    const selectedPolymer = polymers.find(p => p.polymerId === selectedId);
    const solventCount    = stats?.solventCount ?? analysis?.summary?.solventsAnalysed ?? 0;

    const filteredPolymers = useMemo(() => {
        const q = polymerSearch.trim().toLowerCase();
        if (!q) return polymers;
        return polymers.filter(p =>
            p.polymerName.toLowerCase().includes(q) ||
            p.polymerCategory.toLowerCase().includes(q)
        );
    }, [polymers, polymerSearch]);

    const summary = analysis?.summary;

    return (
        <div className="page-container compat-page">
            <div className="compat-hero">
                <div className="compat-hero-text">
                    <span className="compat-hero-tag">Compatibility Analysis</span>
                    <h1 className="compat-hero-title">Polymer–Solvent Matching</h1>
                    <p className="compat-hero-desc">
                        Ranked recommendations validated against Hansen RED index and ML confidence scores.
                    </p>
                </div>
                <div className="compat-hero-stats">
                    <div className="compat-stat"><strong>{solventCount || "—"}</strong><span>Solvents</span></div>
                    <div className="compat-stat"><strong>{stats?.polymerCount ?? "—"}</strong><span>Polymers</span></div>
                    <div className="compat-stat"><strong>10 + 5</strong><span>Results shown</span></div>
                </div>
            </div>

            {error && <div className="flash-error">{error} <button onClick={() => setError("")}>✕</button></div>}

            <div className="compat-layout">
                <aside className="compat-sidebar">
                    <div className="compat-sidebar-header">
                        <h2>Select Polymer</h2>
                        <span className="compat-count">{filteredPolymers.length}</span>
                    </div>
                    <input className="compat-search" placeholder="Search polymers…"
                        value={polymerSearch} onChange={e => setPolymerSearch(e.target.value)} />

                    <div className="polymer-list">
                        {polymerLoading ? (
                            <div className="loading-state">Loading…</div>
                        ) : filteredPolymers.map(p => (
                            <button key={p.polymerId}
                                className={`polymer-list-item ${selectedId === p.polymerId ? "selected" : ""}`}
                                onClick={() => { setSelectedId(p.polymerId); setRan(false); setAnalysis(null); setExpanded(null); }}>
                                <span className="polymer-list-name">{p.polymerName}</span>
                                <span className="polymer-list-cat">{p.polymerCategory}</span>
                            </button>
                        ))}
                    </div>

                    {selectedPolymer && (
                        <div className="polymer-params-panel">
                            <p className="params-title">HSP Parameters</p>
                            <div className="params-grid">
                                <div className="param-item"><span>δD</span><strong>{selectedPolymer.deltaD}</strong></div>
                                <div className="param-item"><span>δP</span><strong>{selectedPolymer.deltaP}</strong></div>
                                <div className="param-item"><span>δH</span><strong>{selectedPolymer.deltaH}</strong></div>
                                <div className="param-item"><span>R₀</span><strong>{selectedPolymer.r0}</strong></div>
                            </div>
                        </div>
                    )}

                    <button className="btn-run" onClick={handleRun} disabled={!selectedId || loading}>
                        {loading ? "Running…" : "Run Analysis"}
                    </button>
                </aside>

                <main className="compat-main">
                    {loading && (
                        <div className="compat-loading-panel">
                            <div className="compat-spinner" />
                            <h3>Analysing {selectedPolymer?.polymerName}</h3>
                            <p className="compat-loading-step">{LOADING_STEPS[loadingStep]}</p>
                            <div className="compat-progress-track">
                                <div className="compat-progress-fill"
                                    style={{ width: `${((loadingStep + 1) / LOADING_STEPS.length) * 100}%` }} />
                            </div>
                        </div>
                    )}

                    {!loading && !ran && (
                        <div className="compat-empty-panel">
                            <h3>Ready to analyse</h3>
                            <p>Select a polymer to receive the top 10 recommended solvents and 5 least compatible alternatives.</p>
                            <ul className="compat-info-list">
                                <li><strong>ML Confidence</strong> — model probability (relative ranking)</li>
                                <li><strong>RED Index</strong> — Hansen criterion (below 1.0 = compatible)</li>
                                <li>High ML scores alone do not guarantee lab success — always validate with trials.</li>
                            </ul>
                        </div>
                    )}

                    {ran && analysis && summary && (
                        <div className="results-section">
                            <div className="analysis-summary-banner">
                                <div className="summary-main">
                                    <h3>Analysis Summary</h3>
                                    <p>
                                        Evaluated <strong>{summary.solventsAnalysed}</strong> solvents for{" "}
                                        <strong>{selectedPolymer?.polymerName}</strong>.
                                        {" "}{summary.highConfidenceCount} scored above 70% ML confidence.
                                        {" "}{summary.redCompatibleCount} passed the Hansen RED threshold (RED &lt; 1.0).
                                    </p>
                                </div>
                                <div className="summary-stats">
                                    <div><span>Top score</span><strong>{pct(summary.topProbability)}%</strong></div>
                                    <div><span>Median</span><strong>{pct(summary.medianProbability)}%</strong></div>
                                    <div><span>RED-pass</span><strong>{summary.redCompatibleCount}</strong></div>
                                </div>
                            </div>

                            <section className="results-group">
                                <div className="results-group-header">
                                    <h2>Top 10 Recommended</h2>
                                    <p>Highest ML confidence with Hansen RED validation</p>
                                </div>
                                <div className="results-list scrollable-results">
                                    {analysis.recommended.map(r => (
                                        <ResultCard key={resultKey(r)} r={r} variant="recommended"
                                            expanded={expanded} onToggle={k => setExpanded(k === -1 ? null : k)} />
                                    ))}
                                </div>
                            </section>

                            <section className="results-group not-rec-group">
                                <div className="results-group-header">
                                    <h2>Least Compatible</h2>
                                    <p>Lowest-scoring solvents — avoid for this polymer</p>
                                </div>
                                <div className="results-list">
                                    {analysis.notRecommended.map(r => (
                                        <ResultCard key={resultKey(r)} r={r} variant="not-recommended"
                                            expanded={expanded} onToggle={k => setExpanded(k === -1 ? null : k)} />
                                    ))}
                                </div>
                            </section>
                        </div>
                    )}
                </main>
            </div>
        </div>
    );
}
