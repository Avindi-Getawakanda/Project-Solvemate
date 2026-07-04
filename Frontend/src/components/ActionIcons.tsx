interface IconProps {
    size?: number;
    className?: string;
}

export function AnalysisIcon({ size = 20, className = "" }: IconProps) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className} aria-hidden="true">
            <path d="M4 19h16M7 16V9m5 7V5m5 11v-4" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" />
            <circle cx="7" cy="7" r="1.5" fill="currentColor" />
            <circle cx="12" cy="3" r="1.5" fill="currentColor" />
            <circle cx="17" cy="9" r="1.5" fill="currentColor" />
        </svg>
    );
}

export function TrialIcon({ size = 20, className = "" }: IconProps) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className} aria-hidden="true">
            <path
                d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2"
                stroke="currentColor"
                strokeWidth="1.75"
                strokeLinecap="round"
            />
            <rect x="9" y="3" width="6" height="4" rx="1" stroke="currentColor" strokeWidth="1.75" />
            <path d="M9 12h6M9 16h4" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" />
        </svg>
    );
}

export function ReportIcon({ size = 20, className = "" }: IconProps) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className={className} aria-hidden="true">
            <path
                d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8l-6-6z"
                stroke="currentColor"
                strokeWidth="1.75"
                strokeLinejoin="round"
            />
            <path d="M14 2v6h6M8 13h8M8 17h5" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" />
        </svg>
    );
}

export type ActionIconType = "analysis" | "trial" | "report";

const MAP = {
    analysis: AnalysisIcon,
    trial:    TrialIcon,
    report:   ReportIcon,
} as const;

export function ActionIcon({ type, size = 20 }: { type: ActionIconType; size?: number }) {
    const Icon = MAP[type];
    return <Icon size={size} />;
}
