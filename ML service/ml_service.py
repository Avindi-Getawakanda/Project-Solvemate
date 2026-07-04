"""
SolveMate ML Microservice — with Explainable AI (Plain English)
"""
from flask import Flask, request, jsonify
import pandas as pd
import numpy as np
import joblib
import shap
import os

app = Flask(__name__)

MODEL_PATH = "solvemate_ml_model.pkl"
if not os.path.exists(MODEL_PATH):
    raise FileNotFoundError(f"Model not found: {MODEL_PATH}. Run 01_train_model.py first.")

saved   = joblib.load(MODEL_PATH)
model   = saved['model'] if isinstance(saved, dict) else saved
FEATURES = saved['features'] if isinstance(saved, dict) else [
    'delta_d_solvent','delta_p_solvent','delta_h_solvent','molar_volume_cm3_mol',
    'delta_d_polymer','delta_p_polymer','delta_h_polymer',
    'delta_d_diff','delta_p_diff','delta_h_diff',
    'ra_value','red_computed','r0_polymer'
]

print(f"[ML] Model loaded | Features: {len(FEATURES)}")

explainer = shap.TreeExplainer(model)
print("[ML] SHAP explainer ready")

# ── Plain English label + explanation generator ───────────────────────────────
FEATURE_LABELS = {
    'delta_d_solvent':      'Dispersion Attraction of Solvent',
    'delta_p_solvent':      'Polarity of Solvent',
    'delta_h_solvent':      'Hydrogen Bonding of Solvent',
    'molar_volume_cm3_mol': 'Molecule Size of Solvent',
    'delta_d_polymer':      'Dispersion Attraction of Polymer',
    'delta_p_polymer':      'Polarity of Polymer',
    'delta_h_polymer':      'Hydrogen Bonding of Polymer',
    'delta_d_diff':         'Dispersion Match',
    'delta_p_diff':         'Polarity Match',
    'delta_h_diff':         'Hydrogen Bonding Match',
    'ra_value':             'Overall Chemical Distance',
    'red_computed':         'Compatibility Index (RED)',
    'r0_polymer':           'Polymer Tolerance Range',
}

def plain_english(feature, shap_val, contribution, input_row):
    direction = shap_val > 0
    pct = round(contribution, 1)

    explanations = {
        'red_computed': (
            f"The compatibility index (RED = {input_row.get('red_computed',0):.2f}) is the strongest signal. "
            f"RED < 1 means compatible, RED > 1 means not compatible. "
            f"This {'strongly supports' if direction else 'strongly opposes'} compatibility ({pct}% influence)."
        ),
        'ra_value': (
            f"The overall chemical distance between solvent and polymer is "
            f"{'small — they are chemically similar' if direction else 'large — they are chemically different'}. "
            f"This contributes {pct}% to the prediction."
        ),
        'delta_d_diff': (
            f"The dispersion forces of the solvent and polymer "
            f"{'match well' if abs(input_row.get('delta_d_diff',0)) < 2 else 'do not match well'}. "
            f"Dispersion matching contributes {pct}% to the prediction."
        ),
        'delta_p_diff': (
            f"The polarity of the solvent and polymer "
            f"{'are similar' if abs(input_row.get('delta_p_diff',0)) < 3 else 'are quite different'}. "
            f"Polarity matching contributes {pct}% to the prediction."
        ),
        'delta_h_diff': (
            f"The hydrogen bonding strength of the solvent and polymer "
            f"{'match closely' if abs(input_row.get('delta_h_diff',0)) < 3 else 'differ significantly'}. "
            f"Hydrogen bonding contributes {pct}% to the prediction."
        ),
        'molar_volume_cm3_mol': (
            f"The solvent molecule size (molar volume = {input_row.get('molar_volume_cm3_mol',0):.1f} cm³/mol) "
            f"{'is suitable' if direction else 'may cause steric issues'}. "
            f"Molecule size contributes {pct}% to the prediction."
        ),
        'r0_polymer': (
            f"The polymer's tolerance range (R0 = {input_row.get('r0_polymer',0):.1f}) "
            f"{'is wide enough to accept this solvent' if direction else 'is too narrow for this solvent'}. "
            f"This contributes {pct}% to the prediction."
        ),
    }

    default = (
        f"{FEATURE_LABELS.get(feature, feature)} "
        f"{'pushes toward compatible' if direction else 'pushes toward incompatible'} "
        f"and contributes {pct}% to the prediction."
    )
    return explanations.get(feature, default)


def build_input(data):
    dD_s = float(data['delta_d_solvent'])
    dP_s = float(data['delta_p_solvent'])
    dH_s = float(data['delta_h_solvent'])
    dD_p = float(data['delta_d_polymer'])
    dP_p = float(data['delta_p_polymer'])
    dH_p = float(data['delta_h_polymer'])
    mv   = float(data['molar_volume_cm3_mol'])
    r0   = float(data.get('r0_polymer', 10.0))

    dd = dD_s - dD_p
    dp = dP_s - dP_p
    dh = dH_s - dH_p
    ra = (4*dd**2 + dp**2 + dh**2) ** 0.5
    red = ra / r0 if r0 > 0 else 99.0

    row = {
        'delta_d_solvent':      dD_s, 'delta_p_solvent':      dP_s,
        'delta_h_solvent':      dH_s, 'molar_volume_cm3_mol': mv,
        'delta_d_polymer':      dD_p, 'delta_p_polymer':      dP_p,
        'delta_h_polymer':      dH_p, 'delta_d_diff':          dd,
        'delta_p_diff':         dp,   'delta_h_diff':          dh,
        'ra_value':             ra,   'red_computed':          red,
        'r0_polymer':           r0,
    }
    return pd.DataFrame([[row[f] for f in FEATURES]], columns=FEATURES), row


def build_rows(polymer, solvents):
    """Build feature matrix for one polymer against many solvents."""
    dD_p = float(polymer['delta_d_polymer'])
    dP_p = float(polymer['delta_p_polymer'])
    dH_p = float(polymer['delta_h_polymer'])
    r0   = float(polymer.get('r0_polymer', 10.0))

    rows = []
    for s in solvents:
        dD_s = float(s['delta_d_solvent'])
        dP_s = float(s['delta_p_solvent'])
        dH_s = float(s['delta_h_solvent'])
        mv   = float(s['molar_volume_cm3_mol'])
        dd, dp, dh = dD_s - dD_p, dP_s - dP_p, dH_s - dH_p
        ra  = (4*dd**2 + dp**2 + dh**2) ** 0.5
        red = ra / r0 if r0 > 0 else 99.0
        rows.append([
            dD_s, dP_s, dH_s, mv, dD_p, dP_p, dH_p,
            dd, dp, dh, ra, red, r0
        ])
    return pd.DataFrame(rows, columns=FEATURES)


def get_class_shap_values(shap_values, sample_index=0, class_index=1):
    """Extract SHAP values for the positive (compatible) class across SHAP versions."""
    if isinstance(shap_values, list):
        return np.asarray(shap_values[class_index][sample_index])

    arr = np.asarray(shap_values)
    if arr.ndim == 3:
        return arr[sample_index, :, class_index]
    if arr.ndim == 2:
        if arr.shape[1] == 2 and arr.shape[0] == len(FEATURES):
            return arr[:, class_index]
        return arr[sample_index]
    return arr


@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "status": "ok",
        "model": "RandomForestClassifier",
        "explainability": "SHAP TreeExplainer",
        "features": len(FEATURES)
    })


@app.route("/predict", methods=["POST"])
def predict():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"error": "No JSON body"}), 400

        required = ["delta_d_solvent","delta_p_solvent","delta_h_solvent",
                    "molar_volume_cm3_mol","delta_d_polymer","delta_p_polymer","delta_h_polymer"]
        missing = [f for f in required if f not in data]
        if missing:
            return jsonify({"error": f"Missing: {missing}"}), 400

        input_df, input_row = build_input(data)

        probability = float(model.predict_proba(input_df)[0][1])
        compatible  = bool(model.predict(input_df)[0])

        shap_values = explainer.shap_values(input_df)
        sv = get_class_shap_values(shap_values)

        abs_sv = np.abs(sv)
        total  = abs_sv.sum()
        pct    = (abs_sv / total * 100).tolist() if total > 0 else [0.0]*len(FEATURES)

        explanation = []
        for i, feat in enumerate(FEATURES):
            explanation.append({
                "feature":      feat,
                "label":        FEATURE_LABELS.get(feat, feat),
                "shap_value":   round(float(sv[i]), 4),
                "contribution": round(pct[i], 1),
                "direction":    "favours" if sv[i] > 0 else "reduces",
                "plain_english": plain_english(feat, sv[i], pct[i], input_row)
            })

        explanation.sort(key=lambda x: abs(x["shap_value"]), reverse=True)
        top_explanation = explanation[:5]

        return jsonify({
            "compatible":      compatible,
            "probability":     round(probability, 4),
            "explanation":     top_explanation,
            "red_value":       round(input_row['red_computed'], 4),
            "ra_value":        round(input_row['ra_value'], 4),
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route("/predict/batch", methods=["POST"])
def predict_batch():
    """Score many solvents at once — no SHAP (fast ranking pass)."""
    try:
        data = request.get_json()
        if not data:
            return jsonify({"error": "No JSON body"}), 400

        polymer = data.get("polymer")
        solvents = data.get("solvents")
        if not polymer or not solvents:
            return jsonify({"error": "Missing polymer or solvents"}), 400

        required = ["delta_d_polymer", "delta_p_polymer", "delta_h_polymer"]
        missing = [f for f in required if f not in polymer]
        if missing:
            return jsonify({"error": f"Missing polymer fields: {missing}"}), 400

        input_df = build_rows(polymer, solvents)
        probabilities = model.predict_proba(input_df)[:, 1]

        results = []
        for i, s in enumerate(solvents):
            results.append({
                "index":        i,
                "solvent_id":   s.get("solvent_id"),
                "probability":  round(float(probabilities[i]), 4),
                "red_value":    round(float(input_df.iloc[i]['red_computed']), 4),
                "ra_value":     round(float(input_df.iloc[i]['ra_value']), 4),
            })

        return jsonify({
            "count":   len(results),
            "results": results,
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == "__main__":
    print("[ML] Starting SolveMate ML Microservice on port 5000...")
    app.run(host="0.0.0.0", port=5000, debug=False)