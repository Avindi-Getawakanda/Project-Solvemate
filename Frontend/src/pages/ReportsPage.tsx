import { useState, useEffect } from "react";
import type { TrialResponse } from "../services/api";
import { getAllTrials } from "../services/api";
import ReportPage from "./ReportPage";
import "../styles/table.css";


export default function ReportsPage() {
    const [trials, setTrials]               = useState<TrialResponse[]>([]);
    const [loading, setLoading]             = useState(true);
    const [error, setError]                 = useState("");
    const [search, setSearch]               = useState("");
    const [selectedTrial, setSelectedTrial] = useState<TrialResponse | null>(null);

    useEffect(() => {
        getAllTrials()
            .then(setTrials)
            .catch(() => setError("Failed to load trial records"))
            .finally(() => setLoading(false));
    }, []);

    
    if (selectedTrial) {
        return (
            <ReportPage
                trial={selectedTrial}
                onBack={() => setSelectedTrial(null)}
            />
        );
    }

    const badgeClass = (r: string) =>
        r === "SUCCESSFUL" ? "trial-badge successful" :
            r === "PARTIALLY_SUCCESSFUL" ? "trial-badge partial" : "trial-badge failed";

    const badgeLabel = (r: string) =>
        r === "SUCCESSFUL" ? "successful" :
            r === "PARTIALLY_SUCCESSFUL" ? "partial" : "failed";

    const filtered = trials.filter(t =>
        t.polymerName.toLowerCase().includes(search.toLowerCase()) ||
        t.solventName.toLowerCase().includes(search.toLowerCase())
    );

    return (
        <div className="page-container">
            {}
            <div className="page-header">
                <div>
                    <h1 className="page-title">Report Generation</h1>
                    <p className="page-subtitle">Generate comprehensive reports from trial records</p>
                </div>
            </div>

            {error && <div className="flash-error">{error}</div>}

            {}
            <div className="reports-info-banner">
                <span>📋</span>
                <p>Select a completed trial below and click <strong>View Report</strong> to generate a full analytical report including compatibility analysis, cost, environmental impact and EU compliance.</p>
            </div>

            {}
            <div className="search-bar">
                <span className="search-icon">🔍</span>
                <input placeholder="Search by polymer or solvent name..."
                       value={search} onChange={e => setSearch(e.target.value)} />
                <span className="result-count">{filtered.length} trial{filtered.length !== 1 ? "s" : ""}</span>
            </div>

            {}
            {loading ? <div className="loading-state">Loading trial records...</div> :
                filtered.length === 0 ? (
                    <div className="empty-state">
                        <p>☰</p>
                        <h3>No trial records found</h3>
                        <p>Record trials in the <strong>Trials</strong> section first, then come here to generate reports.</p>
                    </div>
                ) : (
                    <div className="table-wrapper">
                        <table className="data-table">
                            <thead>
                            <tr>
                                <th>Date</th>
                                <th>Polymer</th>
                                <th>Solvent</th>
                                <th>Temperature</th>
                                <th>Concentration</th>
                                <th>Result</th>
                                <th>Conducted By</th>
                                <th>Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            {filtered.map(t => (
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
                                    <td>{t.performedBy}</td>
                                    <td>
                                        <button
                                            className="view-report-btn"
                                            onClick={() => setSelectedTrial(t)}>
                                            👁 View Report
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
        </div>
    );
}
