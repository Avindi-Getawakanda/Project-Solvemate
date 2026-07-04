import { useState, useEffect } from "react";
import type { PolymerData, PolymerResponse } from "../services/api";
import "../styles/modal.css";

interface Props {
    existing?: PolymerResponse | null;
    onSave: (data: PolymerData) => void;
    onClose: () => void;
}

const CATEGORIES = ["Thermoplastic", "Elastomer", "Thermoset", "Biopolymer", "Copolymer", "Other"];

function getInitial(existing?: PolymerResponse | null) {
    return {
        polymerName:     existing?.polymerName     ?? "",
        polymerCategory: existing?.polymerCategory ?? "Thermoplastic",
        deltaD:          existing?.deltaD          ?? 0,
        deltaP:          existing?.deltaP          ?? 0,
        deltaH:          existing?.deltaH          ?? 0,
        r0:              existing?.r0              ?? 0,
    };
}

export default function PolymerModal({ existing, onSave, onClose }: Props) {
    const init = getInitial(existing);
    const [polymerName,     setPolymerName]     = useState(init.polymerName);
    const [polymerCategory, setPolymerCategory] = useState(init.polymerCategory);
    const [deltaD,          setDeltaD]          = useState(init.deltaD);
    const [deltaP,          setDeltaP]          = useState(init.deltaP);
    const [deltaH,          setDeltaH]          = useState(init.deltaH);
    const [r0,              setR0]              = useState(init.r0);
    const [error,           setError]           = useState("");

    // Only re-initialise when switching to a different polymer record
    useEffect(() => {
        const s = getInitial(existing);
        setPolymerName(s.polymerName);
        setPolymerCategory(s.polymerCategory);
        setDeltaD(s.deltaD);
        setDeltaP(s.deltaP);
        setDeltaH(s.deltaH);
        setR0(s.r0);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [existing?.polymerId]);

    const handleSubmit = () => {
        if (!polymerName.trim()) { setError("Polymer name is required"); return; }
        if (r0 <= 0) { setError("R0 must be greater than 0"); return; }
        setError("");
        onSave({ polymerName, polymerCategory, deltaD, deltaP, deltaH, r0 });
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-card" onClick={e => e.stopPropagation()}>
                <div className="modal-header">
                    <h3>{existing ? "Edit Polymer" : "Add Polymer"}</h3>
                    <button className="modal-close" onClick={onClose}>✕</button>
                </div>

                {error && <p className="modal-error">{error}</p>}

                <div className="modal-body">
                    <label>Polymer Name</label>
                    <input
                        value={polymerName}
                        onChange={e => setPolymerName(e.target.value)}
                        placeholder="e.g. Polystyrene (PS)"
                    />

                    <label>Category</label>
                    <select value={polymerCategory} onChange={e => setPolymerCategory(e.target.value)}>
                        {CATEGORIES.map(c => <option key={c}>{c}</option>)}
                    </select>

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

                    <label>R₀ (Interaction Radius)</label>
                    <input type="number" step="0.01" value={r0}
                           onChange={e => setR0(parseFloat(e.target.value) || 0)}
                           placeholder="e.g. 12.68" />
                </div>

                <div className="modal-footer">
                    <button className="btn-cancel" onClick={onClose}>Cancel</button>
                    <button className="btn-save" onClick={handleSubmit}>
                        {existing ? "Update" : "Add Polymer"}
                    </button>
                </div>
            </div>
        </div>
    );
}
