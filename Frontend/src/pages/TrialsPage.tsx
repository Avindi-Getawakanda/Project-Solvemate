import { useState, useEffect, useMemo } from "react";
import type { TrialResponse, TrialData, PolymerResponse, SolventResponse } from "../services/api";
import { getAllTrials, recordTrial, getAllPolymers, getAllSolvents } from "../services/api";
import SearchableSelect from "../components/SearchableSelect";
import "../styles/trials.css";
import "../styles/table.css";
import "../styles/modal.css";

export default function TrialsPage() {
    const [trials, setTrials]           = useState<TrialResponse[]>([]);
    const [polymers, setPolymers]       = useState<PolymerResponse[]>([]);
    const [solvents, setSolvents]       = useState<SolventResponse[]>([]);
    const [loading, setLoading]         = useState(true);
    const [error, setError]             = useState("");
    const [actionMsg, setActionMsg]     = useState("");
    const [showModal, setShowModal]     = useState(false);
    const [submitting, setSubmitting]   = useState(false);

    const [polymerName, setPolymerName]     = useState("");
    const [solventName, setSolventName]     = useState("");
    const [trialResult, setTrialResult]     = useState("SUCCESSFUL");
    const [observation, setObservation]     = useState("");
    const [temperature, setTemperature]     = useState(25);
    const [concentration, setConcentration] = useState(10);

    const user = JSON.parse(localStorage.getItem("user") || "{}");

    useEffect(() => { fetchAll(); }, []);

    const fetchAll = async () => {
        try {
            setLoading(true);
            const [t, p, s] = await Promise.all([getAllTrials(), getAllPolymers(), getAllSolvents()]);
            setTrials(t); setPolymers(p); setSolvents(s);
        } catch { setError("Failed to load data"); }
        finally { setLoading(false); }
    };

    const handleSubmit = async () => {
        if (!polymerName.trim() || !solventName.trim()) {
            setError("Please select both a polymer and solvent"); return;
        }
        try {
            setSubmitting(true);
            const data: TrialData = {
                polymerName, solventName, trialResult,
                outcomeObservation: observation,
                temperature, concentration,
                performedBy: user.fullName || "Lab User"
            };
            await recordTrial(data);
            flash("Trial recorded successfully ✓");
            setShowModal(false);
            resetForm();
            fetchAll();
        } catch (err: unknown) {
            setError(err instanceof Error ? err.message : "Failed to record trial");
        } finally { setSubmitting(false); }
    };

    const resetForm = () => {
        setPolymerName(""); setSolventName(""); setTrialResult("SUCCESSFUL");
        setObservation(""); setTemperature(25); setConcentration(10);
    };

    const flash = (msg: string) => { setActionMsg(msg); setTimeout(() => setActionMsg(""), 3000); };

    const total      = trials.length;
    const successful = trials.filter(t => t.trialResult === "SUCCESSFUL").length;
    const partial    = trials.filter(t => t.trialResult === "PARTIALLY_SUCCESSFUL").length;
    const failed     = trials.filter(t => t.trialResult === "FAILED").length;

    const badgeClass = (r: string) =>
        r === "SUCCESSFUL" ? "trial-badge successful" :
            r === "PARTIALLY_SUCCESSFUL" ? "trial-badge partial" : "trial-badge failed";

    const badgeLabel = (r: string) =>
        r === "SUCCESSFUL" ? "successful" :
            r === "PARTIALLY_SUCCESSFUL" ? "partial" : "failed";

    const polymerOptions = useMemo(() =>
        polymers.map(p => ({ value: p.polymerName, label: p.polymerName, sublabel: p.polymerCategory })),
        [polymers]
    );

    const solventOptions = useMemo(() =>
        solvents.map(s => ({
            value: s.name,
            label: s.name,
            sublabel: s.chemicalFormula ? `${s.chemicalFormula} · ${s.envImpactScore}` : s.envImpactScore,
        })),
        [solvents]
    );

    const getRating = (r: string) =>
        r === "SUCCESSFUL" ? 5 : r === "PARTIALLY_SUCCESSFUL" ? 3 : 1;

    const starRating = (r: string) => {
        const n = getRating(r);
        return `★ ${n}/5`;
    };

    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1 className="page-title">Trial Management</h1>
                    <p className="page-subtitle">Record and track laboratory trial results</p>
                </div>
            </div>

            {actionMsg && <div className="flash-success">{actionMsg}</div>}
            {error && <div className="flash-error">{error}<button onClick={() => setError("")}>✕</button></div>}

            <div className="trial-stats-grid">
                <div className="trial-stat-card">
                    <p className="trial-stat-label">Total Trials</p>
                    <p className="trial-stat-value neutral">{total}</p>
                </div>
                <div className="trial-stat-card">
                    <p className="trial-stat-label">Successful</p>
                    <p className="trial-stat-value green">{successful}</p>
                </div>
                <div className="trial-stat-card">
                    <p className="trial-stat-label">Partial</p>
                    <p className="trial-stat-value orange">{partial}</p>
                </div>
                <div className="trial-stat-card">
                    <p className="trial-stat-label">Failed</p>
                    <p className="trial-stat-value red">{failed}</p>
                </div>
            </div>

            <div className="trial-records-card">
                <div className="trial-records-header">
                    <h2>Trial Records</h2>
                    <button className="btn-primary" onClick={() => setShowModal(true)}>
                        + Record New Trial
                    </button>
                </div>

                {loading ? <div className="loading-state">Loading trials...</div> :
                    trials.length === 0 ? (
                        <div className="empty-state" style={{ border: "none" }}>
                            <h3>No trials recorded yet</h3>
                            <p>Click "Record New Trial" to get started.</p>
                        </div>
                    ) : (
                        <table className="data-table">
                            <thead>
                            <tr>
                                <th>Date</th>
                                <th>Polymer</th>
                                <th>Solvent</th>
                                <th>Temperature</th>
                                <th>Concentration</th>
                                <th>Result</th>
                                <th>Rating</th>
                                <th>Conducted By</th>
                            </tr>
                            </thead>
                            <tbody>
                            {trials.map(t => (
                                <tr key={t.trialId}>
                                    <td className="td-muted">
                                        {new Date(t.trialDate).toLocaleDateString("en-GB")}
                                    </td>
                                    <td className="td-bold">{t.polymerName}</td>
                                    <td>{t.solventName}</td>
                                    <td>{t.temperature}°C</td>
                                    <td>{t.concentration}%</td>
                                    <td>
                                            <span className={badgeClass(t.trialResult)}>
                                                {badgeLabel(t.trialResult)}
                                            </span>
                                    </td>
                                    <td className="td-rating">{starRating(t.trialResult)}</td>
                                    <td>{t.performedBy}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
            </div>

            {showModal && (
                <div className="modal-overlay" onClick={() => { setShowModal(false); resetForm(); setError(""); }}>
                    <div className="modal-card trial-modal" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3>Record New Trial</h3>
                            <button className="modal-close" onClick={() => { setShowModal(false); resetForm(); setError(""); }}>✕</button>
                        </div>

                        {error && <p className="modal-error">{error}</p>}

                        <div className="modal-body">
                            <div className="modal-row">
                                <div>
                                    <label>Polymer</label>
                                    <SearchableSelect
                                        options={polymerOptions}
                                        value={polymerName}
                                        onChange={setPolymerName}
                                        placeholder="Search and select polymer…"
                                        emptyMessage="No polymers match your search"
                                    />
                                </div>
                                <div>
                                    <label>Solvent</label>
                                    <SearchableSelect
                                        options={solventOptions}
                                        value={solventName}
                                        onChange={setSolventName}
                                        placeholder="Search and select solvent…"
                                        emptyMessage="No solvents match your search"
                                    />
                                </div>
                                <div>
                                    <label>Trial Result</label>
                                    <select value={trialResult} onChange={e => setTrialResult(e.target.value)}>
                                        <option value="SUCCESSFUL">Successful</option>
                                        <option value="PARTIALLY_SUCCESSFUL">Partially Successful</option>
                                        <option value="FAILED">Failed</option>
                                    </select>
                                </div>
                            </div>

                            <div className="modal-row">
                                <div>
                                    <label>Temperature (°C)</label>
                                    <input type="number" value={temperature}
                                           onChange={e => setTemperature(parseFloat(e.target.value) || 0)} />
                                </div>
                                <div>
                                    <label>Concentration (%)</label>
                                    <input type="number" value={concentration}
                                           onChange={e => setConcentration(parseFloat(e.target.value) || 0)} />
                                </div>
                                <div>
                                    <label>Conducted By</label>
                                    <input value={user.fullName || "Lab User"} disabled
                                           style={{ background: "#f3f4f6", color: "#9ca3af" }} />
                                </div>
                            </div>

                            <label>Outcome Observation</label>
                            <textarea value={observation} onChange={e => setObservation(e.target.value)}
                                      placeholder="Describe what happened — dissolution behaviour, clarity, residues, any issues..."
                                      rows={3}
                                      style={{ width: "100%", padding: "10px 12px", border: "1px solid #d1d5db",
                                          borderRadius: "10px", fontSize: "14px", resize: "vertical",
                                          fontFamily: "inherit", background: "#f9fafb", boxSizing: "border-box" }} />
                        </div>

                        <div className="modal-footer">
                            <button className="btn-cancel" onClick={() => { setShowModal(false); resetForm(); setError(""); }}>
                                Cancel
                            </button>
                            <button className="btn-save" onClick={handleSubmit} disabled={submitting}>
                                {submitting ? "Saving..." : "Save Trial"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
