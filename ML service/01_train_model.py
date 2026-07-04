import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, roc_auc_score, classification_report
import joblib

# ── Load dataset ──────────────────────────────────────────────────────────────
df = pd.read_csv("solvemate_dataset_full.csv")
print(f"Dataset loaded: {len(df)} rows")

# ── Feature engineering ───────────────────────────────────────────────────────
df['delta_d_diff'] = df['delta_d_solvent'] - df['delta_d_polymer']
df['delta_p_diff'] = df['delta_p_solvent'] - df['delta_p_polymer']
df['delta_h_diff'] = df['delta_h_solvent'] - df['delta_h_polymer']
df['ra_value']     = np.sqrt(
    4*df['delta_d_diff']**2 + df['delta_p_diff']**2 + df['delta_h_diff']**2
)
df['red_computed'] = df['ra_value'] / df['r0_polymer']

FEATURES = [
    'delta_d_solvent','delta_p_solvent','delta_h_solvent','molar_volume_cm3_mol',
    'delta_d_polymer','delta_p_polymer','delta_h_polymer',
    'delta_d_diff','delta_p_diff','delta_h_diff',
    'ra_value','red_computed','r0_polymer'
]

X = df[FEATURES]
y = df['compatible']

# ── Train ─────────────────────────────────────────────────────────────────────
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42
)

model = RandomForestClassifier(
    n_estimators=300,
    max_depth=None,
    min_samples_split=2,
    min_samples_leaf=1,
    max_features='sqrt',
    random_state=42,
    n_jobs=-1
)
model.fit(X_train, y_train)

# ── Evaluate ──────────────────────────────────────────────────────────────────
y_pred = model.predict(X_test)
y_prob = model.predict_proba(X_test)[:,1]

print(f"\nAccuracy: {accuracy_score(y_test, y_pred):.4f}")
print(f"ROC-AUC:  {roc_auc_score(y_test, y_prob):.4f}")
print("\nClassification Report:")
print(classification_report(y_test, y_pred))

# ── Save ──────────────────────────────────────────────────────────────────────
joblib.dump({'model': model, 'features': FEATURES}, 'solvemate_ml_model.pkl')
print("\nModel saved as solvemate_ml_model.pkl")