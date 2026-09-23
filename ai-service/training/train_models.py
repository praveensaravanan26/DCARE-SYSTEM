"""
DCARE - ML Model Training Pipeline
Trains Hybrid Models:
1. XGBoost Classifier (Supervised Fraud Risk Probability)
2. Random Forest Classifier (Supervised Comparison & Ensemble)
3. Isolation Forest (Unsupervised Anomaly Detection)
Generates Controlled Synthetic Benchmark Dataset and true evaluation metrics.
"""

import os
import json
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestClassifier, IsolationForest
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, roc_auc_score, confusion_matrix
import xgboost as xgb
import joblib

np.random.seed(42)

FEATURE_COLUMNS = [
    'claim_amount',
    'historical_avg_amount',
    'amount_deviation',
    'claim_amount_zscore',
    'claim_frequency_90d',
    'days_since_last_claim',
    'previous_claim_count',
    'garage_repeat_count',
    'document_mismatch_count',
    'duplicate_indicator',
    'invoice_reuse_indicator',
    'policy_age_days',
    'incident_to_claim_delay_days'
]

def generate_controlled_synthetic_dataset(n_samples=5000):
    """
    Generates a realistic, controlled synthetic insurance claims benchmark dataset.
    Labeled as 'Controlled synthetic benchmark for system evaluation.'
    """
    print(f"[*] Generating {n_samples} synthetic claim records for benchmark evaluation...")
    
    # 80% Normal legitimate claims, 20% suspicious/high-risk claims
    n_normal = int(n_samples * 0.82)
    n_suspicious = n_samples - n_normal
    
    # Normal claims distribution
    normal_amounts = np.random.gamma(shape=5.0, scale=4500, size=n_normal) + 3000
    normal_hist_avg = normal_amounts * np.random.uniform(0.85, 1.15, size=n_normal)
    normal_dev = normal_amounts - normal_hist_avg
    normal_zscore = normal_dev / 12000.0
    normal_freq_90d = np.random.choice([0, 1], size=n_normal, p=[0.88, 0.12])
    normal_days_last = np.random.randint(120, 800, size=n_normal)
    normal_prev_count = np.random.poisson(lam=0.7, size=n_normal)
    normal_garage_rep = np.random.choice([0, 1], size=n_normal, p=[0.90, 0.10])
    normal_mismatches = np.random.choice([0, 1], size=n_normal, p=[0.95, 0.05])
    normal_duplicates = np.zeros(n_normal, dtype=int)
    normal_invoice_reuse = np.zeros(n_normal, dtype=int)
    normal_policy_age = np.random.randint(180, 2500, size=n_normal)
    normal_delay_days = np.random.exponential(scale=3.0, size=n_normal).astype(int) + 1
    normal_target = np.zeros(n_normal, dtype=int)
    
    df_normal = pd.DataFrame({
        'claim_amount': normal_amounts,
        'historical_avg_amount': normal_hist_avg,
        'amount_deviation': normal_dev,
        'claim_amount_zscore': normal_zscore,
        'claim_frequency_90d': normal_freq_90d,
        'days_since_last_claim': normal_days_last,
        'previous_claim_count': normal_prev_count,
        'garage_repeat_count': normal_garage_rep,
        'document_mismatch_count': normal_mismatches,
        'duplicate_indicator': normal_duplicates,
        'invoice_reuse_indicator': normal_invoice_reuse,
        'policy_age_days': normal_policy_age,
        'incident_to_claim_delay_days': normal_delay_days,
        'is_suspicious': normal_target
    })
    
    # Suspicious claims distribution (mismatches, duplicate invoices, rapid frequency, huge deviation)
    susp_amounts = np.random.gamma(shape=9.0, scale=12000, size=n_suspicious) + 25000
    susp_hist_avg = np.random.gamma(shape=4.0, scale=4000, size=n_suspicious) + 5000
    susp_dev = susp_amounts - susp_hist_avg
    susp_zscore = susp_dev / 12000.0
    susp_freq_90d = np.random.choice([1, 2, 3, 4], size=n_suspicious, p=[0.25, 0.40, 0.25, 0.10])
    susp_days_last = np.random.randint(5, 75, size=n_suspicious)
    susp_prev_count = np.random.poisson(lam=3.5, size=n_suspicious) + 1
    susp_garage_rep = np.random.choice([0, 1, 2, 3], size=n_suspicious, p=[0.20, 0.35, 0.30, 0.15])
    susp_mismatches = np.random.choice([1, 2, 3, 4], size=n_suspicious, p=[0.30, 0.40, 0.20, 0.10])
    susp_duplicates = np.random.choice([0, 1], size=n_suspicious, p=[0.65, 0.35])
    susp_invoice_reuse = np.random.choice([0, 1], size=n_suspicious, p=[0.60, 0.40])
    susp_policy_age = np.random.randint(15, 300, size=n_suspicious)
    susp_delay_days = np.random.randint(14, 60, size=n_suspicious)
    susp_target = np.ones(n_suspicious, dtype=int)
    
    df_suspicious = pd.DataFrame({
        'claim_amount': susp_amounts,
        'historical_avg_amount': susp_hist_avg,
        'amount_deviation': susp_dev,
        'claim_amount_zscore': susp_zscore,
        'claim_frequency_90d': susp_freq_90d,
        'days_since_last_claim': susp_days_last,
        'previous_claim_count': susp_prev_count,
        'garage_repeat_count': susp_garage_rep,
        'document_mismatch_count': susp_mismatches,
        'duplicate_indicator': susp_duplicates,
        'invoice_reuse_indicator': susp_invoice_reuse,
        'policy_age_days': susp_policy_age,
        'incident_to_claim_delay_days': susp_delay_days,
        'is_suspicious': susp_target
    })
    
    df = pd.concat([df_normal, df_suspicious], ignore_index=True)
    df = df.sample(frac=1.0, random_state=42).reset_index(drop=True)
    
    return df

def train_and_evaluate():
    models_dir = "/Users/praveensaravanan/Desktop/DCARE_Final_Year_Project/models"
    datasets_dir = "/Users/praveensaravanan/Desktop/DCARE_Final_Year_Project/datasets"
    os.makedirs(models_dir, exist_ok=True)
    os.makedirs(datasets_dir, exist_ok=True)
    
    df = generate_controlled_synthetic_dataset(n_samples=6000)
    dataset_path = os.path.join(datasets_dir, "controlled_synthetic_insurance_claims.csv")
    df.to_csv(dataset_path, index=False)
    print(f"[+] Saved synthetic dataset to {dataset_path}")
    
    X = df[FEATURE_COLUMNS]
    y = df['is_suspicious']
    
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.20, random_state=42, stratify=y)
    
    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_test_scaled = scaler.transform(X_test)
    
    # Save Scaler
    scaler_path = os.path.join(models_dir, "feature_scaler.joblib")
    joblib.dump(scaler, scaler_path)
    
    # Model 1: XGBoost
    print("[*] Training XGBoost Classifier...")
    xgb_model = xgb.XGBClassifier(
        n_estimators=180,
        max_depth=5,
        learning_rate=0.06,
        subsample=0.85,
        colsample_bytree=0.85,
        scale_pos_weight=(len(y_train) - sum(y_train)) / sum(y_train),
        eval_metric='logloss',
        random_state=42
    )
    xgb_model.fit(X_train, y_train)
    xgb_preds = xgb_model.predict(X_test)
    xgb_probs = xgb_model.predict_proba(X_test)[:, 1]
    
    xgb_metrics = {
        'accuracy': float(accuracy_score(y_test, xgb_preds)),
        'precision': float(precision_score(y_test, xgb_preds)),
        'recall': float(recall_score(y_test, xgb_preds)),
        'f1': float(f1_score(y_test, xgb_preds)),
        'roc_auc': float(roc_auc_score(y_test, xgb_probs)),
        'confusion_matrix': confusion_matrix(y_test, xgb_preds).tolist()
    }
    xgb_model.save_model(os.path.join(models_dir, "xgboost_fraud_model.json"))
    joblib.dump(xgb_model, os.path.join(models_dir, "xgboost_fraud_model.joblib"))
    
    # Model 2: Random Forest
    print("[*] Training Random Forest Classifier...")
    rf_model = RandomForestClassifier(
        n_estimators=150,
        max_depth=8,
        class_weight='balanced',
        random_state=42,
        n_jobs=-1
    )
    rf_model.fit(X_train, y_train)
    rf_preds = rf_model.predict(X_test)
    rf_probs = rf_model.predict_proba(X_test)[:, 1]
    
    rf_metrics = {
        'accuracy': float(accuracy_score(y_test, rf_preds)),
        'precision': float(precision_score(y_test, rf_preds)),
        'recall': float(recall_score(y_test, rf_preds)),
        'f1': float(f1_score(y_test, rf_preds)),
        'roc_auc': float(roc_auc_score(y_test, rf_probs)),
        'confusion_matrix': confusion_matrix(y_test, rf_preds).tolist()
    }
    joblib.dump(rf_model, os.path.join(models_dir, "random_forest_model.joblib"))
    
    # Model 3: Isolation Forest (Unsupervised Anomaly Detector)
    print("[*] Training Isolation Forest...")
    iso_model = IsolationForest(
        n_estimators=120,
        contamination=0.18,
        random_state=42,
        n_jobs=-1
    )
    iso_model.fit(X_train_scaled)
    joblib.dump(iso_model, os.path.join(models_dir, "isolation_forest_model.joblib"))
    
    iso_scores = -iso_model.decision_function(X_test_scaled)
    
    all_metrics = {
        'dataset_summary': {
            'total_samples': len(df),
            'features_count': len(FEATURE_COLUMNS),
            'training_samples': len(X_train),
            'test_samples': len(X_test),
            'suspicious_ratio': float(y.mean()),
            'dataset_note': 'Controlled synthetic benchmark for system evaluation.'
        },
        'xgboost': xgb_metrics,
        'random_forest': rf_metrics,
        'isolation_forest': {
            'contamination': 0.18,
            'mean_anomaly_score_test': float(np.mean(iso_scores))
        }
    }
    
    metrics_path = os.path.join(models_dir, "model_metrics.json")
    with open(metrics_path, 'w') as f:
        json.dump(all_metrics, f, indent=2)
        
    config_path = os.path.join(models_dir, "feature_config.json")
    with open(config_path, 'w') as f:
        json.dump({'feature_columns': FEATURE_COLUMNS}, f, indent=2)
        
    print(f"[+] All models trained and saved to {models_dir}")
    print(f"XGBoost Metrics: Accuracy={xgb_metrics['accuracy']:.4f}, Precision={xgb_metrics['precision']:.4f}, Recall={xgb_metrics['recall']:.4f}, F1={xgb_metrics['f1']:.4f}, AUC={xgb_metrics['roc_auc']:.4f}")
    print(f"Random Forest Metrics: Accuracy={rf_metrics['accuracy']:.4f}, Precision={rf_metrics['precision']:.4f}, Recall={rf_metrics['recall']:.4f}, F1={rf_metrics['f1']:.4f}, AUC={rf_metrics['roc_auc']:.4f}")

if __name__ == '__main__':
    train_and_evaluate()
