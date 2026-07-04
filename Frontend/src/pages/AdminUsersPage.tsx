import { useState, useEffect } from "react";
import { getAllUsers } from "../services/api";
import type { UserResponse } from "../services/api";
import "../styles/table.css";

export default function AdminUsersPage() {
    const [users, setUsers]     = useState<UserResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError]     = useState("");
    const [search, setSearch]   = useState("");

    useEffect(() => {
        getAllUsers()
            .then(setUsers)
            .catch(() => setError("Failed to load users"))
            .finally(() => setLoading(false));
    }, []);

    const filtered = users.filter(u =>
        u.fullName.toLowerCase().includes(search.toLowerCase()) ||
        u.email.toLowerCase().includes(search.toLowerCase()) ||
        u.role.toLowerCase().includes(search.toLowerCase())
    );

    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1 className="page-title">User Management</h1>
                    <p className="page-subtitle">View all registered users in the system</p>
                </div>
            </div>

            {error && <div className="flash-error">{error}</div>}

            <div className="search-bar">
                <span className="search-icon">🔍</span>
                <input placeholder="Search by name, email or role..." value={search} onChange={e => setSearch(e.target.value)} />
                <span className="result-count">{filtered.length} users</span>
            </div>

            {loading ? <div className="loading-state">Loading users...</div> : filtered.length === 0 ? (
                <div className="empty-state"><p>👤</p><h3>No users found</h3></div>
            ) : (
                <div className="table-wrapper">
                    <table className="data-table">
                        <thead>
                            <tr><th>#</th><th>Full Name</th><th>Email</th><th>Role</th><th>Status</th></tr>
                        </thead>
                        <tbody>
                            {filtered.map((u, i) => (
                                <tr key={u.userId}>
                                    <td className="td-muted">{i + 1}</td>
                                    <td className="td-bold">{u.fullName}</td>
                                    <td className="td-muted">{u.email}</td>
                                    <td>
                                        <span className={`badge ${u.role === "ADMIN" ? "badge-blue" : "badge-green"}`}>
                                            {u.role}
                                        </span>
                                    </td>
                                    <td>
                                        <span className="badge badge-green">ACTIVE</span>
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
