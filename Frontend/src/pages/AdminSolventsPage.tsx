import { useState, useEffect, useMemo } from "react";
import type { SolventResponse, SolventData } from "../services/api";
import { getAllSolvents, addSolvent, updateSolvent, deleteSolvent } from "../services/api";
import SolventModal from "../components/SolventModal";
import "../styles/table.css";

export default function AdminSolventsPage() {
    const [solvents, setSolvents]           = useState<SolventResponse[]>([]);
    const [loading, setLoading]             = useState(true);
    const [error, setError]                 = useState("");
    const [search, setSearch]               = useState("");
    const [filterEnv, setFilterEnv]         = useState("ALL");
    const [showModal, setShowModal]         = useState(false);
    const [editing, setEditing]             = useState<SolventResponse | null>(null);
    const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null);
    const [actionMsg, setActionMsg]         = useState("");
    const [page, setPage]                   = useState(1);
    const PAGE_SIZE = 50;

    useEffect(() => { fetchSolvents(); }, []);

    const fetchSolvents = async () => {
        try { setLoading(true); setSolvents(await getAllSolvents()); }
        catch { setError("Failed to load solvents"); }
        finally { setLoading(false); }
    };

    const handleSave = async (data: SolventData) => {
        try {
            if (editing) { await updateSolvent(editing.solventId, data); flash("Solvent updated ✓"); }
            else { await addSolvent(data); flash("Solvent added ✓"); }
            setShowModal(false); setEditing(null); fetchSolvents();
        } catch (err: unknown) { setError(err instanceof Error ? err.message : "Error"); }
    };

    const handleDelete = async (id: number) => {
        try { await deleteSolvent(id); setDeleteConfirm(null); flash("Solvent deleted ✓"); fetchSolvents(); }
        catch (err: unknown) { setError(err instanceof Error ? err.message : "Error"); }
    };

    const flash = (msg: string) => { setActionMsg(msg); setTimeout(() => setActionMsg(""), 3000); };

    const envBadge = (score: string) =>
        score === "LOW" ? "badge-green" : score === "MEDIUM" ? "badge-yellow" : "badge-red";

    const filtered = useMemo(() => solvents.filter(s => {
        const matchSearch = s.name.toLowerCase().includes(search.toLowerCase());
        const matchEnv    = filterEnv === "ALL" || s.envImpactScore === filterEnv;
        return matchSearch && matchEnv;
    }), [solvents, search, filterEnv]);

    const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
    const paged = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

    useEffect(() => { setPage(1); }, [search, filterEnv]);

    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1 className="page-title">Solvent Management</h1>
                    <p className="page-subtitle">
                        {loading ? "Loading catalog…" : `${solvents.length} solvents · HSP, cost & EU regulatory data`}
                    </p>
                </div>
                <button className="btn-primary" onClick={() => { setEditing(null); setShowModal(true); }}>
                    + Add Solvent
                </button>
            </div>

            {actionMsg && <div className="flash-success">{actionMsg}</div>}
            {error && <div className="flash-error">{error}<button onClick={() => setError("")}>✕</button></div>}

            <div className="search-bar">
                <span className="search-icon">🔍</span>
                <input placeholder="Search by name..." value={search} onChange={e => setSearch(e.target.value)} />
                <select className="filter-select" value={filterEnv} onChange={e => setFilterEnv(e.target.value)}>
                    <option value="ALL">All Env. Scores</option>
                    <option value="LOW">LOW</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HIGH">HIGH</option>
                </select>
                <span className="result-count">{filtered.length} of {solvents.length} solvents</span>
            </div>

            {loading ? <div className="loading-state">Loading...</div> : filtered.length === 0 ? (
                <div className="empty-state"><p>◔</p><h3>No solvents found</h3><p>Click "Add Solvent" to get started.</p></div>
            ) : (
                <div className="table-wrapper">
                    <table className="data-table">
                        <thead>
                            <tr><th>#</th><th>Name</th><th>δD</th><th>δP</th><th>δH</th><th>Cost/L</th><th>Env.</th><th>EU Ban</th><th>Actions</th></tr>
                        </thead>
                        <tbody>
                            {paged.map((s, i) => (
                                <tr key={s.solventId}>
                                    <td className="td-muted">{(page - 1) * PAGE_SIZE + i + 1}</td>
                                    <td className="td-bold">{s.name}</td>
                                    <td>{s.deltaD.toFixed(2)}</td>
                                    <td>{s.deltaP.toFixed(2)}</td>
                                    <td>{s.deltaH.toFixed(2)}</td>
                                    <td>${s.costPerLiter.toFixed(2)}</td>
                                    <td><span className={`badge ${envBadge(s.envImpactScore)}`}>{s.envImpactScore}</span></td>
                                    <td><span className={`badge ${s.euBanStatus ? "badge-red" : "badge-green"}`}>{s.euBanStatus ? "Banned" : "Allowed"}</span></td>
                                    <td className="td-actions">
                                        <button className="btn-edit" onClick={() => { setEditing(s); setShowModal(true); }}>Edit</button>
                                        <button className="btn-delete" onClick={() => setDeleteConfirm(s.solventId)}>Delete</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    {totalPages > 1 && (
                        <div className="pagination">
                            <button disabled={page === 1} onClick={() => setPage(p => p - 1)}>← Prev</button>
                            <span>Page {page} of {totalPages}</span>
                            <button disabled={page === totalPages} onClick={() => setPage(p => p + 1)}>Next →</button>
                        </div>
                    )}
                </div>
            )}

            {showModal && <SolventModal existing={editing} onSave={handleSave} onClose={() => { setShowModal(false); setEditing(null); }} />}

            {deleteConfirm !== null && (
                <div className="modal-overlay" onClick={() => setDeleteConfirm(null)}>
                    <div className="confirm-dialog" onClick={e => e.stopPropagation()}>
                        <h3>Delete Solvent?</h3><p>This action cannot be undone.</p>
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
