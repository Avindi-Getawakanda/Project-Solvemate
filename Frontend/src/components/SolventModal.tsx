import { useState, useEffect } from "react";
import type { SolventData, SolventResponse } from "../services/api";
import "../styles/modal.css";

interface Props {
    existing?: SolventResponse | null;
    onSave: (data: SolventData) => void;
    onClose: () => void;
}

const ENV_SCORES = ["LOW", "MEDIUM", "HIGH"];

export default function SolventModal({ existing, onSave, onClose }: Props) {
    const [name, setName]                   = useState("");
    const [chemicalFormula, setFormula]     = useState("");
    const [deltaD, setDeltaD]               = useState(0);
    const [deltaP, setDeltaP]               = useState(0);
    const [deltaH, setDeltaH]               = useState(0);
    const [molarVolume, setMolarVolume]     = useState(0);
    const [costPerLiter, setCost]           = useState(0);
    const [envImpactScore, setEnvScore]     = useState("LOW");
    const [toxicityLevel, setToxicity]      = useState(0);
    const [euBanStatus, setEuBan]           = useState(false);
    const [error, setError]                 = useState("");

    useEffect(() => {
        if (existing) {
            setName(existing.name);
            setFormula(existing.chemicalFormula || "");
            setDeltaD(existing.deltaD);
            setDeltaP(existing.deltaP);
            setDeltaH(existing.deltaH);
            setMolarVolume(existing.molarVolume);
            setCost(existing.costPerLiter);
            setEnvScore(existing.envImpactScore);
            setToxicity(existing.toxicityLevel);
            setEuBan(existing.euBanStatus);
        }
    }, [existing]);

    const handleSubmit = () => {
        if (!name.trim()) { setError("Solvent name is required"); return; }
        setError("");
        onSave({
            name, chemicalFormula, deltaD, deltaP, deltaH,
            molarVolume, costPerLiter, envImpactScore, toxicityLevel, euBanStatus
        });
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-card" onClick={e => e.stopPropagation()}>
                <div className="modal-header">
                    <h3>{existing ? "Edit Solvent" : "Add Solvent"}</h3>
                    <button className="modal-close" onClick={onClose}>✕</button>
                </div>

                {error && <p className="modal-error">{error}</p>}

                <div className="modal-body">
                    <div className="modal-row">
                        <div>
                            <label>Solvent Name</label>
                            <input value={name} onChange={e => setName(e.target.value)}
                                   placeholder="e.g. Acetone" />
                        </div>
                        <div>
                            <label>Chemical Formula</label>
                            <input value={chemicalFormula} onChange={e => setFormula(e.target.value)}
                                   placeholder="e.g. C3H6O" />
                        </div>
                    </div>

                    <div className="modal-row">
                        <div>
                            <label>δD (Dispersion)</label>
                            <input type="number" step="0.01" value={deltaD}
                                   onChange={e => setDeltaD(parseFloat(e.target.value) || 0)} />
                        </div>
                        <div>
                            <label>δP (Polar)</label>
                            <input type="number" step="0.01" value={deltaP}
                                   onChange={e => setDeltaP(parseFloat(e.target.value) || 0)} />
                        </div>
                        <div>
                            <label>δH (H-Bond)</label>
                            <input type="number" step="0.01" value={deltaH}
                                   onChange={e => setDeltaH(parseFloat(e.target.value) || 0)} />
                        </div>
                    </div>

                    <div className="modal-row">
                        <div>
                            <label>Molar Volume (cm³/mol)</label>
                            <input type="number" step="0.01" value={molarVolume}
                                   onChange={e => setMolarVolume(parseFloat(e.target.value) || 0)} />
                        </div>
                        <div>
                            <label>Cost Per Liter ($)</label>
                            <input type="number" step="0.01" value={costPerLiter}
                                   onChange={e => setCost(parseFloat(e.target.value) || 0)} />
                        </div>
                        <div>
                            <label>Toxicity Level</label>
                            <input type="number" step="0.1" value={toxicityLevel}
                                   onChange={e => setToxicity(parseFloat(e.target.value) || 0)} />
                        </div>
                    </div>

                    <div className="modal-row">
                        <div>
                            <label>Env. Impact Score</label>
                            <select value={envImpactScore} onChange={e => setEnvScore(e.target.value)}>
                                {ENV_SCORES.map(s => <option key={s}>{s}</option>)}
                            </select>
                        </div>
                        <div className="modal-checkbox-wrap">
                            <label>EU Ban Status</label>
                            <div className="modal-checkbox">
                                <input type="checkbox" id="euBan"
                                       checked={euBanStatus}
                                       onChange={e => setEuBan(e.target.checked)} />
                                <label htmlFor="euBan">Banned under EU regulation</label>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal-footer">
                    <button className="btn-cancel" onClick={onClose}>Cancel</button>
                    <button className="btn-save" onClick={handleSubmit}>
                        {existing ? "Update" : "Add Solvent"}
                    </button>
                </div>
            </div>
        </div>
    );
}
