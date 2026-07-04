import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { logoutUser, getDashboardStats, type DashboardStats } from "../services/api";
import SolveMateLogo from "../components/SolveMateLogo";
import AdminPolymersPage from "./AdminPolymersPage";
import AdminSolventsPage from "./AdminSolventsPage";
import AdminUsersPage from "./AdminUsersPage";
import AdminParametersPage from "./AdminParametersPage";
import "../styles/admin.css";

export default function AdminDashboard() {
    const [activePage, setActivePage] = useState("dashboard");
    const navigate = useNavigate();
    const user = JSON.parse(localStorage.getItem("user") || "{}");

    const handleLogout = async () => {
        try { await logoutUser(); } catch { /* ignore */ }
        finally { localStorage.removeItem("user"); navigate("/"); }
    };

    const menuItems = [
        { key: "dashboard",   label: "Dashboard",   icon: "⊞" },
        { key: "polymers",    label: "Polymers",     icon: "▣" },
        { key: "solvents",    label: "Solvents",     icon: "◎" },
        { key: "users",       label: "Users",        icon: "👤" },
        { key: "parameters",  label: "Parameters",   icon: "⚙" },
    ];

    const renderPage = () => {
        switch (activePage) {
            case "polymers":   return <AdminPolymersPage />;
            case "solvents":   return <AdminSolventsPage />;
            case "users":      return <AdminUsersPage />;
            case "parameters": return <AdminParametersPage />;
            default:           return <AdminHome onNavigate={setActivePage} />;
        }
    };

    return (
        <div className="admin-layout">
            <aside className="admin-sidebar">
                <div>
                    <div className="admin-brand">
                        <div className="admin-logo"><SolveMateLogo size={22} /></div>
                        <div>
                            <h2>SolveMate</h2>
                            <p>Admin Console</p>
                        </div>
                    </div>
                    <nav className="admin-nav">
                        {menuItems.map(item => (
                            <button
                                key={item.key}
                                className={`admin-nav-item ${activePage === item.key ? "active" : ""}`}
                                onClick={() => setActivePage(item.key)}
                            >
                                <span className="admin-nav-icon">{item.icon}</span>
                                {item.label}
                            </button>
                        ))}
                    </nav>
                </div>
                <div className="admin-sidebar-footer">
                    <div className="admin-user-info">
                        <p className="admin-user-name">{user.fullName || "Admin User"}</p>
                        <p className="admin-user-email">{user.email || "admin@solvemate.lab"}</p>
                        <span className="admin-user-role">Admin</span>
                    </div>
                    <button className="admin-logout-btn" onClick={handleLogout}>
                        ↪ Logout
                    </button>
                </div>
            </aside>
            <main className="admin-main">
                {renderPage()}
            </main>
        </div>
    );
}

function AdminHome({ onNavigate }: { onNavigate: (p: string) => void }) {
    const [stats, setStats]     = useState<DashboardStats | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getDashboardStats()
            .then(setStats)
            .catch(() => setStats(null))
            .finally(() => setLoading(false));
    }, []);

    return (
        <>
            <h1 className="admin-page-title">System Overview</h1>
            <p className="admin-page-subtitle">Live metrics from your SolveMate deployment.</p>

            <div className="admin-stats-grid">
                <div className="admin-stat-card">
                    <div className="admin-stat-header">
                        <p className="admin-stat-label">Users</p>
                        <span className="admin-stat-icon blue">👥</span>
                    </div>
                    <p className="admin-stat-value">{loading ? "—" : stats?.userCount ?? 0}</p>
                    <p className="admin-stat-sub">Registered accounts</p>
                </div>
                <div className="admin-stat-card">
                    <div className="admin-stat-header">
                        <p className="admin-stat-label">Polymers</p>
                        <span className="admin-stat-icon teal">▣</span>
                    </div>
                    <p className="admin-stat-value">{loading ? "—" : stats?.polymerCount ?? 0}</p>
                    <p className="admin-stat-sub">In catalog</p>
                </div>
                <div className="admin-stat-card">
                    <div className="admin-stat-header">
                        <p className="admin-stat-label">Solvents</p>
                        <span className="admin-stat-icon green">◎</span>
                    </div>
                    <p className="admin-stat-value">{loading ? "—" : stats?.solventCount ?? 0}</p>
                    <p className="admin-stat-sub">In catalog</p>
                </div>
                <div className="admin-stat-card">
                    <div className="admin-stat-header">
                        <p className="admin-stat-label">Trials</p>
                        <span className="admin-stat-icon purple">T</span>
                    </div>
                    <p className="admin-stat-value">{loading ? "—" : stats?.trialCount ?? 0}</p>
                    <p className="admin-stat-sub">Recorded experiments</p>
                </div>
            </div>

            <p className="admin-section-title">Management</p>
            <div className="admin-actions-grid">
                <button className="admin-action-card" onClick={() => onNavigate("polymers")}>
                    <span className="admin-action-icon">▣</span>
                    <span className="admin-action-label">Manage Polymers</span>
                </button>
                <button className="admin-action-card" onClick={() => onNavigate("solvents")}>
                    <span className="admin-action-icon">◎</span>
                    <span className="admin-action-label">Manage Solvents</span>
                </button>
                <button className="admin-action-card" onClick={() => onNavigate("parameters")}>
                    <span className="admin-action-icon">⚙</span>
                    <span className="admin-action-label">ML Parameters</span>
                </button>
                <button className="admin-action-card" onClick={() => onNavigate("users")}>
                    <span className="admin-action-icon">👤</span>
                    <span className="admin-action-label">Manage Users</span>
                </button>
            </div>

            <div className="admin-bottom-grid">
                <div className="admin-card">
                    <p className="admin-card-title">Recent Trials</p>
                    {loading ? (
                        <p className="admin-empty-text">Loading…</p>
                    ) : stats?.recentTrials && stats.recentTrials.length > 0 ? (
                        stats.recentTrials.map(t => (
                            <div key={t.trialId} className="admin-activity-item">
                                <span>{t.polymerName} + {t.solventName}</span>
                                <span>{t.trialDate}</span>
                            </div>
                        ))
                    ) : (
                        <p className="admin-empty-text">No trials recorded yet.</p>
                    )}
                </div>
                <div className="admin-card">
                    <p className="admin-card-title">Top ML-Ranked Solvents</p>
                    {loading ? (
                        <p className="admin-empty-text">Loading…</p>
                    ) : stats?.topSolvents && stats.topSolvents.length > 0 ? (
                        stats.topSolvents.map((s, i) => (
                            <div key={i} className="admin-solvent-item">
                                <span className="admin-solvent-name">{s.solventName}</span>
                                <span className="admin-solvent-pct">{s.mlProbability.toFixed(1)}%</span>
                            </div>
                        ))
                    ) : (
                        <p className="admin-empty-text">Run a compatibility analysis to populate rankings.</p>
                    )}
                </div>
            </div>
        </>
    );
}
