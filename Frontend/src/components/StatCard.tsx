interface Props {
    title:       string;
    value:       string;
    subtitle:    string;
    icon:        string;
    accentClass: string;
    loading?:    boolean;
}

export default function StatCard({ title, value, subtitle, icon, accentClass, loading }: Props) {
    return (
        <div className={`stat-card ${loading ? "stat-card-loading" : ""}`}>
            <div className="stat-card-header">
                <p className="stat-card-label">{title}</p>
                <span className={`stat-card-icon ${accentClass}`}>{icon}</span>
            </div>
            <p className="stat-card-value">{value}</p>
            <p className="stat-card-sub">{subtitle}</p>
        </div>
    );
}
