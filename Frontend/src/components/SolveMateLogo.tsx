interface Props {
    size?: number;
    className?: string;
}

/** SolveMate brand mark — flask + analysis motif */
export default function SolveMateLogo({ size = 28, className = "" }: Props) {
    return (
        <svg
            width={size}
            height={size}
            viewBox="0 0 24 24"
            fill="none"
            className={className}
            aria-hidden="true"
        >
            <path
                d="M9 3H5a2 2 0 00-2 2v4m6-6h10a2 2 0 012 2v4M9 3v18m0 0h10a2 2 0 002-2V9M9 21H5a2 2 0 01-2-2V9m0 0h18"
                stroke="currentColor"
                strokeWidth="1.75"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
            <circle cx="15" cy="15" r="2.75" stroke="currentColor" strokeWidth="1.75" />
            <path
                d="M17.5 17.5L19.5 19.5"
                stroke="currentColor"
                strokeWidth="1.75"
                strokeLinecap="round"
            />
        </svg>
    );
}
