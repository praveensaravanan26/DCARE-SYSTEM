"""
DCARE Explainable AI (XAI) Engine
Calculates SHAP (SHapley Additive exPlanations) for tree-based supervised fraud risk model.
Generates human-interpretable feature contribution attributions without causal overreach.
"""

import os
import json
import numpy as np
import pandas as pd
import shap
import joblib

FEATURE_NAMES_MAP = {
    'claim_amount': 'Claim Amount Value',
    'historical_avg_amount': 'Historical Average Claim Amount',
    'amount_deviation': 'Amount Deviation from Baseline',
    'claim_amount_zscore': 'Claim Amount Standardized Z-Score',
    'claim_frequency_90d': 'Recent 90-Day Claim Frequency',
    'days_since_last_claim': 'Interval Days Since Prior Claim',
    'previous_claim_count': 'Cumulative Historical Claims Count',
    'garage_repeat_count': 'Repeated Garage Workshop Invocations',
    'document_mismatch_count': 'Cross-Document Inconsistency Count',
    'duplicate_indicator': 'Duplicate File/Record Indicator',
    'invoice_reuse_indicator': 'Invoice Number Multi-Use Indicator',
    'policy_age_days': 'Policy Age (Tenure)',
    'incident_to_claim_delay_days': 'Filing Delay Days'
}

class ShapEngine:
    def __init__(self, model_path: str, config_path: str):
        self.model = joblib.load(model_path)
        with open(config_path, 'r') as f:
            self.feature_columns = json.load(f)['feature_columns']
        
        # Initialize SHAP TreeExplainer
        self.explainer = shap.TreeExplainer(self.model)

    def explain(self, feature_dict: dict) -> dict:
        """
        Generates feature contributions and natural language attribution for a single claim.
        """
        df = pd.DataFrame([[feature_dict.get(col, 0.0) for col in self.feature_columns]], columns=self.feature_columns)
        
        shap_values = self.explainer.shap_values(df)
        if isinstance(shap_values, list):
            sv = shap_values[1][0] if len(shap_values) > 1 else shap_values[0][0]
        elif len(shap_values.shape) == 2:
            sv = shap_values[0]
        else:
            sv = shap_values[0][0]

        base_value = float(self.explainer.expected_value) if not isinstance(self.explainer.expected_value, (list, np.ndarray)) else float(self.explainer.expected_value[-1])

        contributions = []
        for col, val, s_val in zip(self.feature_columns, df.iloc[0], sv):
            contributions.append({
                'feature_key': col,
                'feature_name': FEATURE_NAMES_MAP.get(col, col),
                'value': float(val),
                'shap_value': float(s_val),
                'abs_shap': abs(float(s_val)),
                'impact': 'INCREASES_RISK' if s_val > 0 else 'DECREASES_RISK',
                'interpretation': self._get_feature_interpretation(col, val, s_val)
            })

        # Sort by absolute magnitude of SHAP contribution
        contributions.sort(key=lambda x: x['abs_shap'], reverse=True)

        top_positive = [c for c in contributions if c['shap_value'] > 0.01][:4]
        top_negative = [c for c in contributions if c['shap_value'] < -0.01][:4]

        return {
            'base_value': base_value,
            'top_contributors': contributions[:6],
            'positive_contributors': top_positive,
            'negative_contributors': top_negative,
            'all_contributions': contributions
        }

    def _get_feature_interpretation(self, col: str, val: float, shap_val: float) -> str:
        if col == 'document_mismatch_count':
            if val > 0:
                return f"{int(val)} cross-document discrepancies contributed to higher risk scoring."
            return "Document data alignment lowered risk profile."
        elif col == 'claim_amount_zscore':
            if val > 1.5:
                return f"Standardized amount deviation ({val:.2f} Z-Score) contributed to elevated risk signal."
            return "Claimed amount is within typical statistical variance for this policy."
        elif col == 'claim_frequency_90d':
            if val > 0:
                return f"Filing of {int(val)} claims within 90 days contributed to increased evaluation scrutiny."
            return "Zero claims in past 90 days contributed positively to standard processing."
        elif col == 'duplicate_indicator':
            if val > 0:
                return "Cryptographic document hash duplication contributed significantly to risk scoring."
            return "Unique document verification passed."
        elif col == 'days_since_last_claim':
            if val < 45:
                return f"Short interval of {int(val)} days since previous claim elevated pattern alert."
            return "Normal claim separation timeline observed."
        else:
            direction = "elevated" if shap_val > 0 else "reduced"
            return f"Feature value of {val:.2f} {direction} the predictive risk signal."
