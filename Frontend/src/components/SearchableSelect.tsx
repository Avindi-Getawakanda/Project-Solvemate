import { useState, useRef, useEffect, useMemo } from "react";
import "../styles/searchable-select.css";

export interface SearchableOption {
    value: string;
    label: string;
    sublabel?: string;
}

interface Props {
    options: SearchableOption[];
    value: string;
    onChange: (value: string) => void;
    placeholder?: string;
    disabled?: boolean;
    emptyMessage?: string;
}

export default function SearchableSelect({
    options, value, onChange, placeholder = "Search and select…",
    disabled = false, emptyMessage = "No matches found",
}: Props) {
    const [open, setOpen]           = useState(false);
    const [query, setQuery]         = useState("");
    const [highlight, setHighlight] = useState(0);
    const wrapRef = useRef<HTMLDivElement>(null);
    const inputRef = useRef<HTMLInputElement>(null);

    const selected = options.find(o => o.value === value);

    const filtered = useMemo(() => {
        const q = query.trim().toLowerCase();
        if (!q) return options.slice(0, 80);
        return options.filter(o =>
            o.label.toLowerCase().includes(q) ||
            (o.sublabel && o.sublabel.toLowerCase().includes(q))
        ).slice(0, 80);
    }, [options, query]);

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (wrapRef.current && !wrapRef.current.contains(e.target as Node)) {
                setOpen(false);
                setQuery("");
            }
        };
        document.addEventListener("mousedown", handler);
        return () => document.removeEventListener("mousedown", handler);
    }, []);

    useEffect(() => { setHighlight(0); }, [query, open]);

    const pick = (val: string) => {
        onChange(val);
        setOpen(false);
        setQuery("");
    };

    const onKeyDown = (e: React.KeyboardEvent) => {
        if (!open && (e.key === "ArrowDown" || e.key === "Enter")) {
            setOpen(true); return;
        }
        if (e.key === "ArrowDown") {
            e.preventDefault();
            setHighlight(h => Math.min(h + 1, filtered.length - 1));
        } else if (e.key === "ArrowUp") {
            e.preventDefault();
            setHighlight(h => Math.max(h - 1, 0));
        } else if (e.key === "Enter" && filtered[highlight]) {
            e.preventDefault();
            pick(filtered[highlight].value);
        } else if (e.key === "Escape") {
            setOpen(false);
            setQuery("");
        }
    };

    return (
        <div className={`searchable-select ${disabled ? "disabled" : ""}`} ref={wrapRef}>
            <div
                className={`searchable-trigger ${open ? "open" : ""}`}
                onClick={() => { if (!disabled) { setOpen(true); setTimeout(() => inputRef.current?.focus(), 0); } }}
            >
                {open ? (
                    <input
                        ref={inputRef}
                        className="searchable-input"
                        value={query}
                        onChange={e => { setQuery(e.target.value); setOpen(true); }}
                        onKeyDown={onKeyDown}
                        placeholder={placeholder}
                        disabled={disabled}
                    />
                ) : (
                    <span className={selected ? "searchable-value" : "searchable-placeholder"}>
                        {selected ? selected.label : placeholder}
                    </span>
                )}
                <span className="searchable-chevron">{open ? "▲" : "▼"}</span>
            </div>

            {open && (
                <div className="searchable-dropdown">
                    {filtered.length === 0 ? (
                        <div className="searchable-empty">{emptyMessage}</div>
                    ) : (
                        <>
                            {query && filtered.length === 80 && (
                                <div className="searchable-hint">Showing first 80 matches — refine your search</div>
                            )}
                            {filtered.map((o, i) => (
                                <button
                                    key={o.value}
                                    type="button"
                                    className={`searchable-option ${i === highlight ? "highlighted" : ""} ${o.value === value ? "selected" : ""}`}
                                    onMouseEnter={() => setHighlight(i)}
                                    onClick={() => pick(o.value)}
                                >
                                    <span className="searchable-option-label">{o.label}</span>
                                    {o.sublabel && <span className="searchable-option-sub">{o.sublabel}</span>}
                                </button>
                            ))}
                        </>
                    )}
                </div>
            )}
        </div>
    );
}
