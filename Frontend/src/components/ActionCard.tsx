import type { ActionIconType } from "./ActionIcons";
import { ActionIcon } from "./ActionIcons";

interface Props {
    title:        string;
    description?: string;
    icon:         ActionIconType;
    onClick:      () => void;
}

export default function ActionCard({ title, description, icon, onClick }: Props) {
    return (
        <button className="action-card" onClick={onClick}>
            <span className="action-card-icon">
                <ActionIcon type={icon} size={20} />
            </span>
            <span className="action-card-label">{title}</span>
            {description && <span className="action-card-desc">{description}</span>}
        </button>
    );
}
