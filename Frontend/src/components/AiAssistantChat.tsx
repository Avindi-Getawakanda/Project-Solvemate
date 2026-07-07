import { useState, useRef, useEffect } from "react";
import { askAiAssistant } from "../services/api";
import "../styles/aiAssistant.css";

interface ChatMessage {
    role: "user" | "assistant";
    text: string;
}

interface AiAssistantChatProps {
    polymerId: number;
    polymerName: string;
}

function formatAnswer(text: string) {
    return text
        .split("\n")
        .map(line => line.trim())
        .filter(line => line.length > 0)
        .map(line => line.replace(/^\*\s*/, "• "));
}

const SUGGESTIONS = [
    "Which solvent is the most environmentally friendly?",
    "Explain the top recommendation in simple terms",
    "Are any of these solvents banned in the EU?",
];

export default function AiAssistantChat({ polymerId, polymerName }: AiAssistantChatProps) {
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [input, setInput]       = useState("");
    const [loading, setLoading]   = useState(false);
    const [error, setError]       = useState("");
    const bottomRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages, loading]);

    const send = async (question: string) => {
        const q = question.trim();
        if (!q || loading) return;
        setError("");
        setMessages(prev => [...prev, { role: "user", text: q }]);
        setInput("");
        setLoading(true);
        try {
            const res = await askAiAssistant(polymerId, q);
            setMessages(prev => [...prev, { role: "assistant", text: res.answer }]);
        } catch (err: unknown) {
            setError(err instanceof Error ? err.message : "The assistant could not respond");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="ai-assistant-panel">
            <div className="ai-assistant-header">
                <span className="ai-assistant-icon">🧪</span>
                <div>
                    <h3>AI Research Assistant</h3>
                    <p>Ask questions about the {polymerName} results above — answers are grounded in this analysis only.</p>
                </div>
            </div>

            <div className="ai-chat-log">
                {messages.length === 0 && !loading && (
                    <div className="ai-chat-empty">
                        <p>Try asking:</p>
                        <div className="ai-suggestions">
                            {SUGGESTIONS.map(s => (
                                <button key={s} className="ai-suggestion-chip" onClick={() => send(s)}>
                                    {s}
                                </button>
                            ))}
                        </div>
                    </div>
                )}

                {messages.map((m, i) => (
                    <div key={i} className={`ai-chat-message ${m.role}`}>
                        {m.role === "assistant" && <span className="ai-chat-avatar">🧪</span>}
                        <div className="ai-chat-bubble">
                            {m.role === "assistant"
                                ? formatAnswer(m.text).map((line, j) => <p key={j}>{line}</p>)
                                : <p>{m.text}</p>}
                        </div>
                    </div>
                ))}

                {loading && (
                    <div className="ai-chat-message assistant">
                        <span className="ai-chat-avatar">🧪</span>
                        <div className="ai-chat-bubble ai-typing">
                            <span></span><span></span><span></span>
                        </div>
                    </div>
                )}

                <div ref={bottomRef} />
            </div>

            {error && <div className="ai-chat-error">{error}</div>}

            <form className="ai-chat-input-row" onSubmit={e => { e.preventDefault(); send(input); }}>
                <input
                    className="ai-chat-input"
                    placeholder={`Ask about ${polymerName}'s compatibility results…`}
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    disabled={loading}
                />
                <button type="submit" className="ai-chat-send" disabled={loading || !input.trim()}>
                    Send
                </button>
            </form>
        </div>
    );
}