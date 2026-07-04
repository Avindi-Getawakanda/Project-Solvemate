import { useState, useEffect } from "react";
import type { PolymerResponse, PolymerData } from "../services/api";
import { getAllPolymers, addPolymer, updatePolymer, deletePolymer } from "../services/api";
import PolymerModal from "../components/PolymerModal";

export default function AdminPolymersPage() {
    const [polymers, setPolymers]           = useState<PolymerResponse[]>([]);
    const [loading, setLoading]             = useState(true);
    const [search, setSearch]               = useState("");
    const [showModal, setShowModal]         = useState(false);
    const [editing, setEditing]             = useState<PolymerResponse | null>(null);
    const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null);
    const [actionMsg, setActionMsg]         = useState("");
    const [error, setError]                 = useState("");

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
        <>
            <h1 className="admin-page-title">Polymer Management</h1>
            <p className="admin-page-subtitle">Manage polymer database and Hansen parameters</p>

            {actionMsg && <div className="flash-success">{actionMsg}</div>}
            {error && <div className="flash-error">{error}<button onClick={() => setError("")}>✕</button></div>}

            <div className="admin-table-card">
                <div className="admin-table-header">
                    <h2>Polymers ({filtered.length})</h2>
                    <button className="admin-btn-primary" onClick={() => { setEditing(null); setShowModal(true); }}>
                        + Add Polymer
                    </button>
                </div>

                <div className="admin-search">
                    <span>🔍</span>
                    <input
                        placeholder="Search polymers..."
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                    />
                </div>

                {loading ? <div className="loading-state">Loading...</div> : filtered.length === 0 ? (
                    <div className="empty-state"><p>▣</p><h3>No polymers found</h3></div>
                ) : (
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th>Polymer Name</th>
                                <th>Type</th>
                                <th>δD</th>
                                <th>δP</th>
                                <th>δH</th>
                                <th>R₀</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filtered.map(p => (
                                <tr key={p.polymerId}>
                                    <td className="td-name">{p.polymerName}</td>
                                    <td>{p.polymerCategory}</td>
                                    <td className="td-dim">{p.deltaD}</td>
                                    <td className="td-dim">{p.deltaP}</td>
                                    <td className="td-dim">{p.deltaH}</td>
                                    <td className="td-dim">{p.r0}</td>
                                    <td>
                                        <button className="icon-btn edit" onClick={() => { setEditing(p); setShowModal(true); }}>✎</button>
                                        <button className="icon-btn delete" onClick={() => setDeleteConfirm(p.polymerId)}>🗑</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>

            {showModal && (
                <PolymerModal
                    existing={editing}
                    onSave={handleSave}
                    onClose={() => { setShowModal(false); setEditing(null); }}
                />
            )}

            {deleteConfirm !== null && (
                <div className="modal-overlay" onClick={() => setDeleteConfirm(null)}>
                    <div className="confirm-dialog" onClick={e => e.stopPropagation()}>
                        <h3>Delete Polymer?</h3>
                        <p>This action cannot be undone.</p>
                        <div className="confirm-actions">
                            <button className="btn-cancel" onClick={() => setDeleteConfirm(null)}>Cancel</button>
                            <button className="btn-danger" onClick={() => handleDelete(deleteConfirm)}>Delete</button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}
