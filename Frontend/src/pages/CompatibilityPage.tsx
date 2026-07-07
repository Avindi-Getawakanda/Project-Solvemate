import { useState, useEffect, useMemo, useRef } from "react";
import type { PolymerResponse, CompatibilityResult, CompatibilityAnalysis, DashboardStats } from "../services/api";
import { getAllPolymers, runCompatibilityAnalysis, getDashboardStats } from "../services/api";
import AiAssistantChat from "../components/AiAssistantChat";
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

function greenTier(score?: number) {
    if (score == null) return null;
    if (score >= 0.7) return { label: "Green", cls: "green-tier-high" };
    if (score >= 0.4) return { label: "Moderate", cls: "green-tier-mid" };
    return { label: "Low Green Score", cls: "green-tier-low" };
}

function resultKey(r: CompatibilityResult) {
    return r.resultId ?? r.solventId;
}

type ResultsTab = "recommended" | "avoid" | "ai";

interface TopPickHeroProps {
    r: CompatibilityResult;
    polymerName: string;
    expanded: number | null;
    onToggle: (key: number) => void;
}

function TopPickHero({ r, polymerName, expanded, onToggle }: TopPickHeroProps) {
    const preview = r.briefing?.overallRecommendation ?? r.greenInsight;
    const b = badge(r.result);
    const key = resultKey(r);
    const isExp = expanded === key;
    const briefing = r.briefing;
    const hasBriefing = Boolean(briefing);

    return (
        <div className="top-pick-hero">
            <div className="top-pick-badge">Top pick for {polymerName}</div>
            <div className="top-pick-main">
                <div className="top-pick-heading">
                    <span className="top-pick-rank">#1</span>
                    <h2>{r.solventName}</h2>
                    <span className={`compat-badge ${b.cls}`}>{b.label}</span>
                </div>
                <div className="top-pick-metrics">
                    <div><span>ML Confidence</span><strong>{pct(r.mlProbability)}%</strong></div>
                    <div><span>RED Index</span><strong>{r.redValue != null ? r.redValue.toFixed(2) : "—"}</strong></div>
                    <div><span>Ra Distance</span><strong>{r.raValue != null ? r.raValue.toFixed(1) : "—"}</strong></div>
                </div>
                {preview && <p className="top-pick-summary">{preview}</p>}
                {(hasBriefing || r.explanation) && (
                    <button type="button" className="top-pick-cta" onClick={() => onToggle(isExp ? -1 : key)}>
                        {isExp ? "Hide briefing" : "View full briefing"}
                    </button>
                )}
            </div>

            {isExp && hasBriefing && (
                <div className="briefing-panel top-pick-briefing">
                    <div className="briefing-sections">
                        {BRIEFING_SECTIONS.map(({ key: sectionKey, title }) => {
                            const text = briefing?.[sectionKey];
                            if (!text) return null;
                            return (
                                <article key={sectionKey} className="briefing-section">
                                    <h4 className="briefing-section-title">{title}</h4>
                                    <p className="briefing-section-text">{text}</p>
                                </article>
                            );
                        })}
                        {BRIEFING_TAIL.map(({ key: sectionKey, title }) => {
                            const text = briefing?.[sectionKey];
                            if (!text) return null;
                            return (
                                <article key={sectionKey} className={`briefing-section ${sectionKey === "overallRecommendation" ? "briefing-section-overall" : "briefing-section-safer"}`}>
                                    <h4 className="briefing-section-title">{title}</h4>
                                    <p className="briefing-section-text">{text}</p>
                                </article>
                            );
                        })}
                    </div>
                </div>
            )}
        </div>
    );
}

interface ResultsTabNavProps {
    active: ResultsTab;
    onChange: (tab: ResultsTab) => void;
    recommendedCount: number;
    avoidCount: number;
}

function ResultsTabNav({ active, onChange, recommendedCount, avoidCount }: ResultsTabNavProps) {
    const tabs: { id: ResultsTab; label: string; hint: string }[] = [
        { id: "recommended", label: `Best Solvents (${recommendedCount})`, hint: "Start here" },
        { id: "avoid",       label: `Avoid (${avoidCount})`,             hint: "Do not use" },
        { id: "ai",          label: "AI Assistant",                      hint: "Ask questions" },
    ];

    return (
        <nav className="results-tab-nav" aria-label="Analysis results">
            {tabs.map(tab => (
                <button
                    key={tab.id}
                    type="button"
                    className={`results-tab-btn ${active === tab.id ? "active" : ""}`}
                    onClick={() => onChange(tab.id)}
                >
                    <span className="results-tab-label">{tab.label}</span>
                    <span className="results-tab-hint">{tab.hint}</span>
                </button>
            ))}
        </nav>
    );
}

const BRIEFING_SECTIONS: { key: keyof NonNullable<CompatibilityResult["briefing"]>; title: string }[] = [
    { key: "compatibilityAssessment",        title: "Compatibility Assessment" },
    { key: "healthSafetyAssessment",         title: "Health & Safety Assessment" },
    { key: "environmentalImpactAssessment",  title: "Environmental Impact Assessment" },
    { key: "regulatoryComplianceAssessment", title: "Regulatory & Compliance Assessment" },
    { key: "costPracticalityAssessment",     title: "Practicality Assessment" },
];

const BRIEFING_TAIL: { key: keyof NonNullable<CompatibilityResult["briefing"]>; title: string }[] = [
    { key: "overallRecommendation", title: "Overall Recommendation" },
    { key: "saferAlternative",      title: "Safer Alternative" },
];

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
    const green = greenTier(r.greenScore);
    const briefing = r.briefing;
    const preview = briefing?.overallRecommendation ?? r.greenInsight;
    const hasBriefing = Boolean(briefing);

    return (
        <div className={`result-card ${variant} ${r.rankPosition === 1 && variant === "recommended" ? "rank-1" : ""}`}>
            <div className="result-rank-badge">
                {variant === "recommended" ? `#${r.rankPosition}` : "↓"}
            </div>
            <div className="result-main">
                <div className="result-name-row">
                    <h3>{r.solventName}</h3>
                    <span className={`compat-badge ${b.cls}`}>{b.label}</span>
                    {green && <span className={`green-badge ${green.cls}`}>🌱 {green.label}</span>}
                    {r.euBanStatus && <span className="eu-ban-badge">⚠ EU Restricted</span>}
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
                    {r.greenScore != null && (
                        <div className="metric-chip">
                            <span>Green Score</span>
                            <strong>{Math.round(r.greenScore * 100)}%</strong>
                        </div>
                    )}
                </div>

                {preview && (
                    <p className="briefing-preview-line">{preview}</p>
                )}

                <div className="score-bar-wrap">
                    <span className="score-label">Relative score</span>
                    <div className="score-bar">
                        <div className="score-fill ml-fill" style={{ width: `${bar(r.mlProbability)}%` }} />
                    </div>
                    <span className="score-val">{pct(r.mlProbability)}%</span>
                </div>

                {(hasBriefing || r.explanation) && (
                    <button className="explain-btn" onClick={() => onToggle(isExp ? -1 : key)}>
                        {isExp ? "Hide Briefing" : "View Briefing"}
                    </button>
                )}

                {isExp && hasBriefing && (
                    <div className="briefing-panel">
                        <p className="briefing-panel-title">Briefing — <strong>{r.solventName}</strong></p>
                        <div className="briefing-sections">
                            {BRIEFING_SECTIONS.map(({ key: sectionKey, title }) => {
                                const text = briefing?.[sectionKey];
                                if (!text) return null;
                                return (
                                    <article key={sectionKey} className="briefing-section">
                                        <h4 className="briefing-section-title">{title}</h4>
                                        <p className="briefing-section-text">{text}</p>
                                    </article>
                                );
                            })}
                            {BRIEFING_TAIL.map(({ key: sectionKey, title }) => {
                                const text = briefing?.[sectionKey];
                                if (!text) return null;
                                return (
                                    <article key={sectionKey} className={`briefing-section ${sectionKey === "overallRecommendation" ? "briefing-section-overall" : "briefing-section-safer"}`}>
                                        <h4 className="briefing-section-title">{title}</h4>
                                        <p className="briefing-section-text">{text}</p>
                                    </article>
                                );
                            })}
                        </div>

                        {r.explanation && r.explanation.length > 0 && (
                            <div className="shap-panel shap-panel-nested">
                                <p className="shap-title">ML Feature Analysis</p>
                                <p className="shap-subtitle">How individual Hansen parameters contributed to the model ranking</p>
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
                )}

                {isExp && !hasBriefing && r.explanation && (
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
    const [greenMode, setGreenMode]           = useState(false);
    const [activeResultsTab, setActiveResultsTab] = useState<ResultsTab>("recommended");
    const resultsTopRef = useRef<HTMLDivElement>(null);

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
        setActiveResultsTab("recommended");
        try {
            const data = await runCompatibilityAnalysis(selectedId, greenMode);
            setAnalysis(data); setRan(true);
            if (data.recommended.length > 0) {
                setExpanded(resultKey(data.recommended[0]));
            }
        } catch (err: unknown) {
            setError(err instanceof Error ? err.message : "Analysis failed");
        } finally { setLoading(false); }
    };

    useEffect(() => {
        if (ran && analysis && !loading) {
            resultsTopRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
        }
    }, [ran, analysis, loading]);

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
            <div className="page-header compat-page-header">
                <div>
                    <h1 className="page-title">Polymer–Solvent Matching</h1>
                    <p className="page-subtitle">
                        Ranked recommendations validated against Hansen RED index and ML confidence scores.
                    </p>
                    <div className="compat-header-stats">
                        <span className="compat-header-stat"><strong>{solventCount || "—"}</strong> solvents</span>
                        <span className="compat-header-stat"><strong>{stats?.polymerCount ?? "—"}</strong> polymers</span>
                        <span className="compat-header-stat"><strong>10 + 5</strong> results shown</span>
                    </div>
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
                                onClick={() => { setSelectedId(p.polymerId); setRan(false); setAnalysis(null); setExpanded(null); setActiveResultsTab("recommended"); }}>
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

                    <label className="green-mode-toggle">
                        <span className="green-mode-label">
                            <span className="green-mode-icon" aria-hidden="true">🌱</span>
                            Green Mode
                        </span>
                        <span className="toggle-switch">
                            <input type="checkbox" checked={greenMode}
                                onChange={e => setGreenMode(e.target.checked)} />
                            <span className="toggle-track"><span className="toggle-thumb" /></span>
                        </span>
                    </label>
                    {greenMode && (
                        <p className="green-mode-hint">
                            Ranks by ML confidence and sustainability together — deprioritises toxic or EU-restricted solvents even if compatibility is high.
                        </p>
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
                            <h3>How it works</h3>
                            <ol className="compat-steps-list">
                                <li><strong>Select a polymer</strong> from the list on the left</li>
                                <li><strong>Run Analysis</strong> to rank all solvents in the catalog</li>
                                <li><strong>Review Best Solvents</strong> — your top picks appear first</li>
                                <li><strong>Check Avoid</strong> for solvents that failed compatibility</li>
                                <li><strong>Ask the AI Assistant</strong> if you need help deciding</li>
                            </ol>
                        </div>
                    )}

                    {ran && analysis && summary && (
                        <div className="results-section" ref={resultsTopRef}>
                            <div className="analysis-summary-banner">
                                <div className="summary-main">
                                    <h3>
                                        Results for {selectedPolymer?.polymerName}
                                        {summary.greenModeActive && <span className="green-mode-active-tag">🌱 Green Mode</span>}
                                    </h3>
                                    <p>
                                        {summary.solventsAnalysed} solvents analysed ·{" "}
                                        {summary.highConfidenceCount} above 70% ML ·{" "}
                                        {summary.redCompatibleCount} passed RED &lt; 1.0
                                    </p>
                                </div>
                                <div className="summary-stats">
                                    <div><span>Top score</span><strong>{pct(summary.topProbability)}%</strong></div>
                                    <div><span>Median</span><strong>{pct(summary.medianProbability)}%</strong></div>
                                    <div><span>RED-pass</span><strong>{summary.redCompatibleCount}</strong></div>
                                </div>
                            </div>

                            <ResultsTabNav
                                active={activeResultsTab}
                                onChange={setActiveResultsTab}
                                recommendedCount={analysis.recommended.length}
                                avoidCount={analysis.notRecommended.length}
                            />

                            {activeResultsTab === "recommended" && (
                                <div className="results-tab-panel">
                                    {analysis.recommended[0] && selectedPolymer && (
                                        <TopPickHero
                                            r={analysis.recommended[0]}
                                            polymerName={selectedPolymer.polymerName}
                                            expanded={expanded}
                                            onToggle={k => setExpanded(k === -1 ? null : k)}
                                        />
                                    )}

                                    {analysis.recommended.length > 1 && (
                                        <div className="results-group">
                                            <div className="results-group-header">
                                                <h2>Other recommended solvents</h2>
                                                <p>Ranks #2–{analysis.recommended.length} for {selectedPolymer?.polymerName}</p>
                                            </div>
                                            <div className="results-list">
                                                {analysis.recommended.slice(1).map(r => (
                                                    <ResultCard key={resultKey(r)} r={r} variant="recommended"
                                                        expanded={expanded} onToggle={k => setExpanded(k === -1 ? null : k)} />
                                                ))}
                                            </div>
                                        </div>
                                    )}

                                </div>
                            )}

                            {activeResultsTab === "avoid" && (
                                <div className="results-tab-panel">
                                    <div className="avoid-callout">
                                        <strong>Do not use these solvents</strong> with {selectedPolymer?.polymerName}.
                                        They scored lowest on ML confidence and Hansen compatibility.
                                    </div>
                                    <div className="results-list">
                                        {analysis.notRecommended.map(r => (
                                            <ResultCard key={resultKey(r)} r={r} variant="not-recommended"
                                                expanded={expanded} onToggle={k => setExpanded(k === -1 ? null : k)} />
                                        ))}
                                    </div>
                                </div>
                            )}

                            {activeResultsTab === "ai" && selectedPolymer && (
                                <div className="results-tab-panel">
                                    <AiAssistantChat
                                        polymerId={selectedPolymer.polymerId}
                                        polymerName={selectedPolymer.polymerName}
                                    />
                                </div>
                            )}
                        </div>
                    )}
                </main>
            </div>
        </div>
    );
}