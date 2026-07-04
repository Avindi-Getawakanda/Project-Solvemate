import { useState, useEffect } from "react";
import Sidebar from "../components/Sidebar";
import StatCard from "../components/StatCard";
import ActionCard from "../components/ActionCard";
import TrialItem from "../components/TrialItem";
import PolymersPage from "./PolymersPage";
import SolventsPage from "./SolventsPage";
import CompatibilityPage from "./CompatibilityPage";
import TrialsPage from "./TrialsPage";
import ReportsPage from "./ReportsPage";
import { getDashboardStats, type DashboardStats } from "../services/api";
import "../styles/layout.css";

export default function UserDashboard() {
    const [activePage, setActivePage] = useState("dashboard");
    const user = JSON.parse(localStorage.getItem("user") || "{}");

    const renderPage = () => {
        switch (activePage) {
            case "polymers":      return <PolymersPage />;
            case "solvents":      return <SolventsPage />;
            case "compatibility": return <CompatibilityPage />;
            case "trials":        return <TrialsPage />;
            case "reports":       return <ReportsPage />;
            default:              return <DashboardHome onNavigate={setActivePage} user={user} />;
        }
    };

    return (
        <div className="dashboard-layout">
            <Sidebar
                fullName={user.fullName || "Lab User"}
                email={user.email || "user@solvemate.lab"}
                role={user.role || "LAB_USER"}
                activePage={activePage}
                onNavigate={setActivePage}
            />
            <main className="dashboard-main">
                {renderPage()}
            </main>
        </div>
    );
}

function trialStatus(result: string): "successful" | "partial" | "failed" {
    if (result === "SUCCESSFUL") return "successful";
    if (result === "PARTIALLY_SUCCESSFUL") return "partial";
    return "failed";
}

function DashboardHome({ onNavigate, user }: { onNavigate: (p: string) => void; user: { fullName?: string } }) {
    const [stats, setStats]       = useState<DashboardStats | null>(null);
    const [loading, setLoading]   = useState(true);

    useEffect(() => {
        getDashboardStats()
            .then(setStats)
            .catch(() => setStats(null))
            .finally(() => setLoading(false));
    }, []);

    const today = new Date().toLocaleDateString("en-GB", {
        weekday: "long", day: "numeric", month: "long", year: "numeric"
    });

    return (
        <>
            <div className="dashboard-header">
                <div>
                    <p className="dashboard-date">{today}</p>
                    <h1>Welcome back, {user.fullName || "Lab User"}</h1>
                    <p className="dashboard-subline">Your laboratory workspace — run analyses, record trials, and generate reports.</p>
                </div>
            </div>

            <div className="stats-grid">
                <StatCard title="Polymers" value={loading ? "—" : String(stats?.polymerCount ?? 0)} subtitle="In catalog" icon="" accentClass="blue" loading={loading} />
                <StatCard title="Solvents" value={loading ? "—" : String(stats?.solventCount ?? 0)} subtitle="Available for analysis" icon="" accentClass="teal" loading={loading} />
                <StatCard title="Trials Recorded" value={loading ? "—" : String(stats?.trialCount ?? 0)} subtitle="Laboratory experiments" icon="" accentClass="purple" loading={loading} />
                <StatCard title="Reports" value={loading ? "—" : String(stats?.reportCount ?? 0)} subtitle="Generated summaries" icon="" accentClass="green" loading={loading} />
            </div>

            <h2 className="section-title">Quick Actions</h2>
            <div className="actions-grid">
                <ActionCard title="Run Compatibility Analysis" description="Find top solvents for a polymer" icon="analysis" onClick={() => onNavigate("compatibility")} />
                <ActionCard title="Record Trial" description="Log a lab experiment result" icon="trial" onClick={() => onNavigate("trials")} />
                <ActionCard title="View Reports" description="Review generated summaries" icon="report" onClick={() => onNavigate("reports")} />
            </div>

            <h2 className="section-title">Recent Trials</h2>
            <div className="trials-panel">
                {loading ? (
                    <div className="loading-state">Loading recent trials…</div>
                ) : stats?.recentTrials && stats.recentTrials.length > 0 ? (
                    stats.recentTrials.map(t => (
                        <TrialItem
                            key={t.trialId}
                            title={`${t.polymerName} + ${t.solventName}`}
                            date={t.trialDate}
                            status={trialStatus(t.trialResult)}
                        />
                    ))
                ) : (
                    <div className="dashboard-empty-panel">
                        <p>No trials recorded yet.</p>
                        <button className="btn-primary" onClick={() => onNavigate("trials")}>Record First Trial</button>
                    </div>
                )}
            </div>
        </>
    );
}
