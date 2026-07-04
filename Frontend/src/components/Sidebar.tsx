import { useNavigate } from "react-router-dom";
import { logoutUser } from "../services/api";
import SolveMateLogo from "./SolveMateLogo";

interface Props {
    fullName:   string;
    email:      string;
    role:       string;
    activePage: string;
    onNavigate: (page: string) => void;
}

const NAV = [
    { key: "dashboard",     label: "Dashboard" },
    { key: "polymers",      label: "Polymers" },
    { key: "solvents",      label: "Solvents" },
    { key: "compatibility", label: "Compatibility" },
    { key: "trials",        label: "Trials" },
    { key: "reports",       label: "Reports" },
];

export default function Sidebar({ fullName, email, role, activePage, onNavigate }: Props) {
    const navigate = useNavigate();

    const handleLogout = async () => {
        try { await logoutUser(); } catch { /* ignore */ }
        finally { localStorage.removeItem("user"); navigate("/"); }
    };

    return (
        <aside className="sidebar">
            <div className="sidebar-top">
                <div className="sidebar-brand">
                    <div className="sidebar-logo-mark">
                        <SolveMateLogo size={22} />
                    </div>
                    <div>
                        <h2>SolveMate</h2>
                        <p>Laboratory Platform</p>
                    </div>
                </div>
                <nav className="sidebar-nav">
                    {NAV.map(item => (
                        <button
                            key={item.key}
                            className={`sidebar-nav-item ${activePage === item.key ? "active" : ""}`}
                            onClick={() => onNavigate(item.key)}
                        >
                            <span className="sidebar-nav-dot" />
                            {item.label}
                        </button>
                    ))}
                </nav>
            </div>
            <div className="sidebar-footer">
                <div className="sidebar-user">
                    <p className="sidebar-user-name">{fullName}</p>
                    <p className="sidebar-user-email">{email}</p>
                    <span className="sidebar-user-role">{role === "ADMIN" ? "Administrator" : "Lab User"}</span>
                </div>
                <button className="sidebar-logout" onClick={handleLogout}>Sign out</button>
            </div>
        </aside>
    );
}
