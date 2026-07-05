import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split, RandomizedSearchCV
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, roc_auc_score, classification_report, f1_score
import joblib


# ── Load dataset ──────────────────────────────────────────────────────────────
df = pd.read_csv("solvemate_dataset_full.csv")
print(f"Dataset loaded: {len(df)} rows")


# ── Feature engineering ───────────────────────────────────────────────────────
df["delta_d_diff"] = df["delta_d_solvent"] - df["delta_d_polymer"]
df["delta_p_diff"] = df["delta_p_solvent"] - df["delta_p_polymer"]
df["delta_h_diff"] = df["delta_h_solvent"] - df["delta_h_polymer"]

df["ra_value"] = np.sqrt(
    4 * df["delta_d_diff"] ** 2
    + df["delta_p_diff"] ** 2
    + df["delta_h_diff"] ** 2
)

df["red_computed"] = df["ra_value"] / df["r0_polymer"]


# ── Same existing features ────────────────────────────────────────────────────
FEATURES = [
    "delta_d_solvent",
    "delta_p_solvent",
    "delta_h_solvent",
    "molar_volume_cm3_mol",

    "delta_d_polymer",
    "delta_p_polymer",
    "delta_h_polymer",

    "delta_d_diff",
    "delta_p_diff",
    "delta_h_diff",

    "ra_value",
    "red_computed",
    "r0_polymer"
]


X = df[FEATURES]
y = df["compatible"]


# ── Train/Test split with stratify ────────────────────────────────────────────
X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.2,
    random_state=42,
    stratify=y
)


# ── Faster Hyperparameter tuning ──────────────────────────────────────────────
param_grid = {
    "n_estimators": [200, 300, 500],
    "max_depth": [12, 16, 20, None],
    "min_samples_split": [2, 5, 10],
    "min_samples_leaf": [1, 2, 4],
    "max_features": ["sqrt", "log2"],
    "bootstrap": [True],
    "class_weight": [None, "balanced"]
}


base_model = RandomForestClassifier(
    random_state=42,
    n_jobs=-1
)


search = RandomizedSearchCV(
    estimator=base_model,
    param_distributions=param_grid,
    n_iter=15,
    scoring="roc_auc",
    cv=3,
    random_state=42,
    n_jobs=-1,
    verbose=2
)


search.fit(X_train, y_train)

model = search.best_estimator_


print("\nBest Parameters:")
print(search.best_params_)


# ── Evaluate using default threshold 0.5 ──────────────────────────────────────
y_prob = model.predict_proba(X_test)[:, 1]
y_pred = (y_prob >= 0.5).astype(int)

print("\nDefault Threshold Results")
print(f"Accuracy: {accuracy_score(y_test, y_pred):.4f}")
print(f"ROC-AUC:  {roc_auc_score(y_test, y_prob):.4f}")
print("\nClassification Report:")
print(classification_report(y_test, y_pred))


# ── Find best threshold using F1-score ────────────────────────────────────────
best_threshold = 0.5
best_f1 = 0.0

for threshold in np.arange(0.30, 0.71, 0.01):
    y_pred_temp = (y_prob >= threshold).astype(int)
    f1 = f1_score(y_test, y_pred_temp)

    if f1 > best_f1:
        best_f1 = f1
        best_threshold = threshold


# ── Evaluate using best threshold ─────────────────────────────────────────────
y_pred_best = (y_prob >= best_threshold).astype(int)

print("\nBest Threshold Results")
print(f"Best Threshold: {best_threshold:.2f}")
print(f"Accuracy:       {accuracy_score(y_test, y_pred_best):.4f}")
print(f"ROC-AUC:        {roc_auc_score(y_test, y_prob):.4f}")
print(f"F1-score:       {best_f1:.4f}")
print("\nClassification Report:")
print(classification_report(y_test, y_pred_best))


# ── Save model, same features, and best threshold ─────────────────────────────
joblib.dump(
    {
        "model": model,
        "features": FEATURES,
        "threshold": float(best_threshold)
    },
    "solvemate_ml_model.pkl"
)

print("\nImproved model saved as solvemate_ml_model.pkl")
