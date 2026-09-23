"""
DCARE Evidence-Aware Risk Engine
Combines:
1. Supervised XGBoost Fraud Risk Probability
2. Supervised Random Forest Ensemble Comparison
3. Unsupervised Isolation Forest Anomaly Score
4. Cross-Document Consistency Matrix Penalties
5. Cryptographic Duplicate & Invoice Reuse Signals
6. Historical Claim Frequency & Amount Outlier Multipliers
"""

import os
import json
import numpy as np
import pandas as pd
import joblib
from explainability.shap_engine import ShapEngine

class RiskEngine:
    def __init__(self, models_dir: str):
        self.models_dir = models_dir
        
        # Load models
        xgb_path = os.path.join(models_dir, "xgboost_fraud_model.joblib")
        rf_path = os.path.join(models_dir, "random_forest_model.joblib")
        iso_path = os.path.join(models_dir, "isolation_forest_model.joblib")
        scaler_path = os.path.join(models_dir, "feature_scaler.joblib")
        config_path = os.path.join(models_dir, "feature_config.json")
        
        self.xgb_model = joblib.load(xgb_path)
        self.rf_model = joblib.load(rf_path)
        self.iso_model = joblib.load(iso_path)
        self.scaler = joblib.load(scaler_path)
        
        with open(config_path, 'r') as f:
            self.feature_columns = json.load(f)['feature_columns']
            
        self.shap_engine = ShapEngine(xgb_path, config_path)

    def assess_claim(self, features: dict, cross_doc_results: dict) -> dict:
        """
        Executes multi-model inference and synthesizes comprehensive risk assessment.
        """
        df_feat = pd.DataFrame([[features.get(col, 0.0) for col in self.feature_columns]], columns=self.feature_columns)
        
        # 1. Supervised XGBoost prediction
        xgb_prob = float(self.xgb_model.predict_proba(df_feat)[0, 1])
        supervised_score = round(xgb_prob * 100.0, 1)
        
        # 2. Random forest ensemble signal
        rf_prob = float(self.rf_model.predict_proba(df_feat)[0, 1])
        
        # 3. Isolation forest anomaly score
        df_scaled = self.scaler.transform(df_feat)
        raw_iso = float(self.iso_model.decision_function(df_scaled)[0])
        # Mapping anomaly score from [-0.5, 0.5] to [0, 100]
        anomaly_score = max(0.0, min(100.0, round((0.25 - raw_iso) * 120.0, 1)))
        
        # 4. Evidence Penalties
        mismatches = cross_doc_results.get('mismatch_count', 0)
        duplicate_ind = cross_doc_results.get('duplicate_indicator', 0)
        invoice_reuse = cross_doc_results.get('invoice_reuse_indicator', 0)
        
        mismatch_score = min(100.0, mismatches * 22.0)
        duplicate_score = 90.0 if duplicate_ind > 0 else (75.0 if invoice_reuse > 0 else 0.0)
        
        zscore = float(features.get('claim_amount_zscore', 0.0))
        freq90 = int(features.get('claim_frequency_90d', 0))
        historical_score = min(100.0, max(0.0, (zscore * 18.0) + (freq90 * 25.0)))
        
        # 5. Composite Weighted Risk Score
        # 35% Supervised ML + 15% Anomaly + 25% Cross-Document Mismatches + 15% Duplicate/Invoice + 10% Historical
        composite_score = (
            0.35 * supervised_score +
            0.15 * anomaly_score +
            0.25 * mismatch_score +
            0.15 * duplicate_score +
            0.10 * historical_score
        )
        
        # If severe direct evidence is present (like duplicate file or 3+ mismatches), establish floor
        if duplicate_ind > 0 or invoice_reuse > 0:
            composite_score = max(composite_score, 78.0)
        elif mismatches >= 2:
            composite_score = max(composite_score, 62.0)
        elif mismatches == 1 and zscore > 1.5:
            composite_score = max(composite_score, 45.0)

        final_score = round(max(0.0, min(100.0, composite_score)), 1)
        
        # 6. Risk Level Categorization
        if final_score < 30.0:
            risk_level = "LOW"
            recommendation = "Eligible for Straight-Through Processing (STP)"
        elif final_score < 60.0:
            risk_level = "MEDIUM"
            recommendation = "Standard Claim Officer Verification Required"
        elif final_score < 80.0:
            risk_level = "HIGH"
            recommendation = "Enhanced Investigation Recommended"
        else:
            risk_level = "CRITICAL"
            recommendation = "Priority Senior Fraud Investigation Routing"

        # 7. SHAP Feature Attribution
        shap_summary = self.shap_engine.explain(features)

        # 8. Compile Evidence List
        evidence = list(cross_doc_results.get('evidence', []))
        
        if zscore > 1.8:
            evidence.append({
                'type': 'HIGH_AMOUNT_DEVIATION',
                'description': f"Claim amount represents a significant statistical outlier ({zscore:.2f} standard deviations above baseline).",
                'severity': 'HIGH' if zscore > 2.5 else 'MEDIUM',
                'source_doc': 'HISTORICAL_PROFILE',
                'source_field': 'claimed_amount',
                'detected_value': f"Z-Score {zscore:.2f}",
                'expected_value': 'Within +/- 1.5 Z-Score',
                'contribution': 16.0
            })
            
        if freq90 >= 2:
            evidence.append({
                'type': 'HIGH_CLAIM_FREQUENCY',
                'description': f"High claim velocity detected: {freq90} previous claims submitted within recent 90-day window.",
                'severity': 'HIGH',
                'source_doc': 'HISTORICAL_PROFILE',
                'source_field': 'claim_frequency_90d',
                'detected_value': f"{freq90} claims / 90 days",
                'expected_value': '<= 1 claim / 90 days',
                'contribution': 20.0
            })

        if anomaly_score > 60.0:
            evidence.append({
                'type': 'ANOMALY_SIGNAL',
                'description': f"Unsupervised multi-variate anomaly score of {anomaly_score:.1f}/100 detected in claim feature distribution.",
                'severity': 'MEDIUM',
                'source_doc': 'ISOLATION_FOREST',
                'source_field': 'multivariate_features',
                'detected_value': f"Anomaly score {anomaly_score:.1f}",
                'expected_value': '< 40.0',
                'contribution': 12.0
            })

        if supervised_score > 50.0:
            evidence.append({
                'type': 'MODEL_SIGNAL',
                'description': f"Supervised XGBoost model predicted elevated fraud risk probability ({xgb_prob * 100:.1f}%).",
                'severity': 'HIGH' if supervised_score > 75.0 else 'MEDIUM',
                'source_doc': 'XGBOOST_MODEL',
                'source_field': 'risk_probability',
                'detected_value': f"{xgb_prob * 100:.1f}%",
                'expected_value': '< 30.0%',
                'contribution': round(supervised_score * 0.35, 1)
            })

        return {
            'risk_score': final_score,
            'risk_level': risk_level,
            'recommendation': recommendation,
            'supervised_prob': xgb_prob,
            'supervised_score': supervised_score,
            'rf_prob': rf_prob,
            'anomaly_score': anomaly_score,
            'mismatch_score': mismatch_score,
            'duplicate_score': duplicate_score,
            'historical_score': historical_score,
            'shap_summary': shap_summary,
            'evidence': evidence,
            'validations': cross_doc_results.get('validations', []),
            'duplicate_indicator': duplicate_ind,
            'invoice_reuse_indicator': invoice_reuse
        }
