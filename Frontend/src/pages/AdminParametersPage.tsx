import { useState } from "react";

export default function AdminParametersPage() {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    const today = new Date().toLocaleDateString("en-GB");

    const [threshold, setThreshold]       = useState(0.5);
    const [topK, setTopK]                 = useState(5);
    const [saved, setSaved]               = useState(false);
    const [lastModified, setLastModified] = useState(today);

    const handleSave = () => {
        localStorage.setItem("solvemate_params", JSON.stringify({
            threshold, topK,
            modifiedBy: user.fullName || "Admin",
            modifiedAt: today
        }));
        setLastModified(today);
        setSaved(true);
        setTimeout(() => setSaved(false), 3000);
    };

    const features = [
        { feature: "delta_d_solvent",      desc: "Dispersion parameter of solvent" },
        { feature: "delta_p_solvent",      desc: "Polar parameter of solvent" },
        { feature: "delta_h_solvent",      desc: "Hydrogen bonding parameter of solvent" },
        { feature: "molar_volume_cm3_mol", desc: "Molar volume of solvent (cm³/mol)" },
        { feature: "delta_d_polymer",      desc: "Dispersion parameter of polymer" },
        { feature: "delta_p_polymer",      desc: "Polar parameter of polymer" },
        { feature: "delta_h_polymer",      desc: "Hydrogen bonding parameter of polymer" },
    ];

    return (
        <>
            <h1 className="admin-page-title">Compatibility Parameters</h1>
            <p className="admin-page-subtitle">Configure ML model settings and recommendation thresholds</p>

            {saved && <div className="flash-success">Parameters saved successfully ✓</div>}

            <div className="params-layout">

                <div className="params-card">
                    <p className="params-section-title">⚙ System Parameters</p>

                    <p className="params-group-title">ML Model</p>
                    <p className="params-group-desc">
                        The system uses a trained Random Forest Classifier to predict
                        polymer-solvent compatibility. The model was trained on 800
                        real polymer-solvent pairs.
                    </p>

                    <div style={{ background: "#f9fafb", borderRadius: "12px", padding: "16px 20px", marginBottom: "24px" }}>
                        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" }}>
                            <div>
                                <p style={{ margin: "0 0 4px", fontSize: "12px", color: "#9ca3af", fontWeight: 600, textTransform: "uppercase" }}>Algorithm</p>
                                <p style={{ margin: 0, fontSize: "15px", fontWeight: 700, color: "#111827" }}>Random Forest Classifier</p>
                            </div>
                            <div>
                                <p style={{ margin: "0 0 4px", fontSize: "12px", color: "#9ca3af", fontWeight: 600, textTransform: "uppercase" }}>Number of Trees</p>
                                <p style={{ margin: 0, fontSize: "15px", fontWeight: 700, color: "#111827" }}>200</p>
                            </div>
                            <div>
                                <p style={{ margin: "0 0 4px", fontSize: "12px", color: "#9ca3af", fontWeight: 600, textTransform: "uppercase" }}>Training Dataset</p>
                                <p style={{ margin: 0, fontSize: "15px", fontWeight: 700, color: "#111827" }}>800 polymer-solvent pairs</p>
                            </div>
                            <div>
                                <p style={{ margin: "0 0 4px", fontSize: "12px", color: "#9ca3af", fontWeight: 600, textTransform: "uppercase" }}>Model File</p>
                                <p style={{ margin: 0, fontSize: "15px", fontWeight: 700, color: "#111827" }}>solvemate_ml_model.pkl</p>
                            </div>
                        </div>
                    </div>

                    <hr className="param-divider" />

                    <p className="params-group-title">Input Features</p>
                    <p className="params-group-desc">
                        These 7 features are sent to the ML model for each solvent-polymer pair.
                    </p>

                    <div style={{ background: "#f9fafb", borderRadius: "12px", padding: "16px 20px", marginBottom: "24px" }}>
                        {features.map((f, i) => (
                            <div key={i} style={{ display: "flex", justifyContent: "space-between", padding: "8px 0", borderBottom: i < 6 ? "1px solid #e5e7eb" : "none" }}>
                                <span style={{ fontFamily: "monospace", fontSize: "13px", color: "#2563eb" }}>{f.feature}</span>
                                <span style={{ fontSize: "13px", color: "#6b7280" }}>{f.desc}</span>
                            </div>
                        ))}
                    </div>

                    <hr className="param-divider" />

                    <p className="params-group-title">Recommendation Settings</p>
                    <p className="params-group-desc">
                        Configure how the ML model output is used to generate recommendations.
                    </p>

                    <div className="param-row">
                        <div className="param-label-row">
                            <span className="param-label">Compatibility Threshold</span>
                            <span className="param-value">{threshold}</span>
                        </div>
                        <input
                            type="range" min={0.1} max={0.9} step={0.05}
                            value={threshold}
                            className="param-slider"
                            onChange={e => setThreshold(parseFloat(e.target.value))}
                        />
                        <p className="param-hint">
                            Solvents with ML probability ≥ {threshold} are marked as Compatible
                        </p>
                    </div>

                    <div className="param-row">
                        <div className="param-label-row">
                            <span className="param-label">Top K Recommendations</span>
                            <span className="param-value">{topK}</span>
                        </div>
                        <input
                            type="range" min={3} max={10} step={1}
                            value={topK}
                            className="param-slider"
                            onChange={e => setTopK(parseInt(e.target.value))}
                        />
                        <p className="param-hint">
                            Number of top solvents to recommend (currently Top {topK})
                        </p>
                    </div>

                    <button className="param-save-btn" onClick={handleSave}>
                        💾 Save Changes
                    </button>
                </div>

                <div className="params-right-panel">

                    <div className="params-info-card">
                        <p className="params-info-title">How the ML Model Works</p>
                        <div className="params-info-item">
                            <h4>1. Feature Extraction</h4>
                            <p>Hansen Solubility Parameters (δD, δP, δH) and molar volume are extracted for both polymer and solvent</p>
                        </div>
                        <div className="params-info-item">
                            <h4>2. Random Forest Prediction</h4>
                            <p>200 decision trees vote on compatibility. The result is P(compatible) — a probability from 0.0 to 1.0</p>
                        </div>
                        <div className="params-info-item">
                            <h4>3. Ranking</h4>
                            <p>All solvents are sorted by ML probability descending. Top {topK} are returned as recommendations</p>
                        </div>
                    </div>

                    <div className="params-info-card">
                        <p className="params-info-title">🕐 Change History</p>
                        <div className="change-history-item">
                            <strong>Last Modified</strong><br />
                            {lastModified}<br />
                            By: {user.fullName || "Admin User"}<br /><br />
                            <span style={{ color: "#9ca3af" }}>No previous changes recorded</span>
                        </div>
                    </div>

                    <div className="params-info-card">
                        <p className="params-info-title">ML Output</p>
                        <div className="formula-box">
                            <p className="formula-text">Output: P(compatible) ∈ [0.0, 1.0]</p>
                            <p className="formula-text">Score = P(compatible) × 100%</p>
                            <p className="formula-text">Compatible if P ≥ {threshold}</p>
                            <hr style={{ border: "none", borderTop: "1px solid #e5e7eb", margin: "10px 0" }} />
                            <p className="formula-text" style={{ fontSize: "11px", color: "#9ca3af" }}>
                                Algorithm: Random Forest (n=200)<br />
                                Library: scikit-learn (Python)<br />
                                Served via: Flask REST API (port 5000)
                            </p>
                        </div>
                    </div>

                </div>
            </div>
        </>
    );
}
