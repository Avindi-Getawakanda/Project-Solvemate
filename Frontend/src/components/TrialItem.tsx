interface Props {
    title:  string;
    date:   string;
    status: "successful" | "partial" | "failed";
}

export default function TrialItem({ title, date, status }: Props) {
    const badgeClass =
        status === "successful" ? "trial-item-badge success" :
        status === "partial"    ? "trial-item-badge partial" :
                                  "trial-item-badge failed";

    const badgeLabel =
        status === "successful" ? "successful" :
        status === "partial"    ? "partial"    : "failed";

    return (
        <div className="trial-item">
            <div className="trial-item-left">
                <span className="trial-item-icon">{status === "successful" ? "✓" : status === "partial" ? "~" : "×"}</span>
                <div>
                    <p className="trial-item-title">{title}</p>
                    <p className="trial-item-date">{date}</p>
                </div>
            </div>
            <span className={badgeClass}>{badgeLabel}</span>
        </div>
    );
}
