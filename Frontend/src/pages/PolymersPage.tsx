import { useState, useEffect } from "react";
import type { PolymerResponse, PolymerData } from "../services/api";
import { getAllPolymers, addPolymer, updatePolymer, deletePolymer } from "../services/api";
import PolymerModal from "../components/PolymerModal";
import "../styles/table.css";

export default function PolymersPage() {
    const [polymers, setPolymers]             = useState<PolymerResponse[]>([]);
    const [loading, setLoading]               = useState(true);
    const [error, setError]                   = useState("");
    const [search, setSearch]                 = useState("");
    const [showModal, setShowModal]           = useState(false);
    const [editing, setEditing]               = useState<PolymerResponse | null>(null);
    const [deleteConfirm, setDeleteConfirm]   = useState<number | null>(null);
    const [actionMsg, setActionMsg]           = useState("");

    const user = JSON.parse(localStorage.getItem("user") || "{}");
    const isAdmin = user.role === "ADMIN";

    useEffect(() => { fetchPolymers(); }, []);

    const fetchPolymers = async () => {
        try { setLoading(true); setPolymers(await getAllPolymers()); }
        catch { setError("Failed to load polymers"); }
        finally { setLoading(false); }
    };

    const handleSave = async (data: PolymerData) => {
        try {
            if (editing) { await updatePolymer(editing.polymerId, data); flash("Polymer updated ✓"); }
            else { await addPolymer(data); flash("Polymer added ✓"); }
            setShowModal(false); setEditing(null); fetchPolymers();
        } catch (err: unknown) { setError(err instanceof Error ? err.message : "Error"); }
    };

    const handleDelete = async (id: number) => {
        try { await deletePolymer(id); setDeleteConfirm(null); flash("Polymer deleted ✓"); fetchPolymers(); }
        catch (err: unknown) { setError(err instanceof Error ? err.message : "Error"); }
    };

    const flash = (msg: string) => { setActionMsg(msg); setTimeout(() => setActionMsg(""), 3000); };

    const filtered = polymers.filter(p =>
        p.polymerName.toLowerCase().includes(search.toLowerCase()) ||
        p.polymerCategory.toLowerCase().includes(search.toLowerCase())
    );

    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1 className="page-title">Polymer Management</h1>
                    <p className="page-subtitle">Manage polymer Hansen solubility parameters</p>
                </div>
                {isAdmin && <button className="btn-primary" onClick={() => { setEditing(null); setShowModal(true); }}>+ Add Polymer</button>}
            </div>
            {actionMsg && <div className="flash-success">{actionMsg}</div>}
            {error && <div className="flash-error">{error} <button onClick={() => setError("")}>✕</button></div>}
            <div className="search-bar">
                <span className="search-icon">🔍</span>
                <input placeholder="Search by name or category..." value={search} onChange={e => setSearch(e.target.value)} />
                <span className="result-count">{filtered.length} polymer{filtered.length !== 1 ? "s" : ""}</span>
            </div>
            {loading ? <div className="loading-state">Loading polymers...</div> : filtered.length === 0 ? (
                <div className="empty-state"><p>▣</p><h3>No polymers found</h3><p>{isAdmin ? 'Click "Add Polymer" to get started.' : "No polymers available yet."}</p></div>
            ) : (
                <div className="table-wrapper">
                    <table className="data-table">
                        <thead><tr><th>#</th><th>Polymer Name</th><th>Category</th><th>δD</th><th>δP</th><th>δH</th><th>R₀</th><th>δT</th>{isAdmin && <th>Actions</th>}</tr></thead>
                        <tbody>
                        {filtered.map((p, i) => (
                            <tr key={p.polymerId}>
                                <td className="td-muted">{i + 1}</td>
                                <td className="td-bold">{p.polymerName}</td>
                                <td><span className="badge badge-blue">{p.polymerCategory}</span></td>
                                <td>{p.deltaD.toFixed(2)}</td><td>{p.deltaP.toFixed(2)}</td><td>{p.deltaH.toFixed(2)}</td>
                                <td>{p.r0.toFixed(2)}</td><td>{p.deltaT.toFixed(2)}</td>
                                {isAdmin && <td className="td-actions">
                                    <button className="btn-edit" onClick={() => { setEditing(p); setShowModal(true); }}>Edit</button>
                                    <button className="btn-delete" onClick={() => setDeleteConfirm(p.polymerId)}>Delete</button>
                                </td>}
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
            {showModal && <PolymerModal existing={editing} onSave={handleSave} onClose={() => { setShowModal(false); setEditing(null); }} />}
            {deleteConfirm !== null && (
                <div className="modal-overlay" onClick={() => setDeleteConfirm(null)}>
                    <div className="confirm-dialog" onClick={e => e.stopPropagation()}>
                        <h3>Delete Polymer?</h3><p>This action cannot be undone.</p>
                        <div className="confirm-actions">
                            <button className="btn-cancel" onClick={() => setDeleteConfirm(null)}>Cancel</button>
                            <button className="btn-danger" onClick={() => handleDelete(deleteConfirm)}>Delete</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
