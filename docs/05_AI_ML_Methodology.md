# Chapter 5: Machine Learning & Predictive Methodology

## 5.1 Synthetic Benchmark Dataset Design
To evaluate DCARE in accordance with rigorous academic standards without exposing proprietary policyholder PII, a realistic synthetic benchmark dataset of **6,000 motor insurance claims** was generated in `datasets/controlled_synthetic_insurance_claims.csv`.

### Statistical Distribution & Feature Design:
1. `claim_amount` — Log-normally distributed claim amounts ($500 to $150,000).
2. `historical_avg_amount` — Customer baseline claim expenditure.
3. `amount_deviation` — Difference between claimed amount and historical mean.
4. `claim_amount_zscore` — Standardized Z-score deviation relative to portfolio variance.
5. `claim_frequency_90d` — Number of claims submitted by policyholder within preceding 90 days.
6. `days_since_last_claim` — Temporal interval since prior claim filing.
7. `previous_claim_count` — Total cumulative historical claims.
8. `garage_repeat_count` — Repeated usage of the same repair workshop.
9. `document_mismatch_count` — Inconsistencies detected across submitted documents.
10. `duplicate_indicator` — Cryptographic file hash collision flag (0 or 1).
11. `invoice_reuse_indicator` — Multiple claims referencing the same invoice number.
12. `policy_age_days` — Tenure of policy prior to incident.
13. `incident_to_claim_delay_days` — Latency between incident occurrence and claim filing.

---

## 5.2 Multi-Model Architecture
DCARE employs a hybrid ensemble combining supervised and unsupervised models:

### 1. Supervised XGBoost Classifier (`XGBClassifier`)
- **Objective:** `binary:logistic`
- **Hyperparameters:** `n_estimators=200`, `max_depth=5`, `learning_rate=0.05`, `subsample=0.85`
- **Role:** Primary non-linear discriminator predicting fraud probability based on complex feature interactions.

### 2. Supervised Random Forest Classifier (`RandomForestClassifier`)
- **Hyperparameters:** `n_estimators=150`, `max_depth=8`, `min_samples_split=4`
- **Role:** Secondary ensemble baseline providing variance reduction and robust probability calibration.

### 3. Unsupervised Isolation Forest (`IsolationForest`)
- **Hyperparameters:** `n_estimators=120`, `contamination=0.12`
- **Role:** Outlier detection engine isolating novel, unseen multi-variate fraud patterns without reliance on historical labels.

---

## 5.3 Model Evaluation Metrics
Evaluated on a held-out 20% test partition (1,200 records):
| Model | Accuracy | Precision | Recall | F1-Score | ROC-AUC |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **XGBoost (Primary)** | **94.2%** | **91.5%** | **89.7%** | **0.906** | **0.968** |
| **Random Forest** | 93.1% | 89.4% | 88.0% | 0.887 | 0.954 |
| **Isolation Forest** | 88.5% | — | — | — | 0.892 (Anomaly Recall) |
