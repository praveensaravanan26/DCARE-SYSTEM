# Chapter 7: Evidence-Aware Risk Engine & Decision Formulation

## 7.1 Cross-Document Consistency Matrix
The consistency engine validates data harmony across all submitted documents for a claim:

| Consistency Rule | Verification Logic | Severity | Action if Inconsistent |
| :--- | :--- | :--- | :--- |
| **Document Date Alignment** | Compares Incident Date on Claim Form vs. Police Accident Report. | `MEDIUM` | Risk penalty (+18) & verification flag. |
| **Claim vs. Invoice Amount** | Compares Claimed Amount vs. Grand Total on Repair Bill (Tolerance: $100). | `MEDIUM` | Risk penalty (+15) & amount variance note. |
| **Vehicle Registration Match** | Validates Vehicle Number across Policy, Claim Form, and Repair Invoice. | `HIGH` | Risk penalty (+22) & chassis verification required. |
| **Invoice Reference Number** | Compares entered Invoice # against OCR extracted invoice text. | `MEDIUM` | Risk penalty (+12) & billing check. |
| **Cryptographic Hash Collision** | Matches SHA-256 file hash against all previously uploaded documents. | `CRITICAL` | Risk penalty (+45, floor 78) & duplicate alert. |
| **Invoice Number Multi-Use** | Detects if Invoice ID exists on any other historical claim. | `HIGH` | Risk penalty (+35) & fraud audit flag. |

---

## 7.2 Composite Risk Scoring Formula
The DCARE composite risk score $S_{\text{risk}} \in [0, 100]$ is computed as a weighted combination of five distinct signals:

$$S_{\text{risk}} = 0.35 \cdot S_{\text{supervised}} + 0.15 \cdot S_{\text{anomaly}} + 0.25 \cdot S_{\text{mismatch}} + 0.15 \cdot S_{\text{duplicate}} + 0.10 \cdot S_{\text{historical}}$$

Where:
- $S_{\text{supervised}} = 100 \cdot P_{\text{XGBoost}}(\text{Fraud})$
- $S_{\text{anomaly}} = \text{clamp}(120 \cdot (0.25 - \text{score}_{\text{IsoForest}}), 0, 100)$
- $S_{\text{mismatch}} = \min(100, 22.0 \cdot N_{\text{mismatches}})$
- $S_{\text{duplicate}} = 90.0$ if hash collision, else $75.0$ if invoice reuse, else $0.0$
- $S_{\text{historical}} = \min(100, 18.0 \cdot Z_{\text{score}} + 25.0 \cdot \text{Freq}_{90d})$

### Evidence Floor Conditions:
- If cryptographic duplicate file or invoice reuse is detected: $S_{\text{risk}} \ge 78.0$ (High/Critical).
- If $\ge 2$ cross-document mismatches are detected: $S_{\text{risk}} \ge 62.0$ (High).

---

## 7.3 Risk Tiers and Decision Support Actions
| Risk Tier | Score Range | System Recommendation | Operational Routing |
| :--- | :--- | :--- | :--- |
| **LOW** | $0.0 - 29.9$ | Eligible for Straight-Through Processing (STP) | Fast-track 1-click approval by Claim Officer. |
| **MEDIUM** | $30.0 - 59.9$ | Standard Claim Officer Verification Required | Standard desk verification of document fields. |
| **HIGH** | $60.0 - 79.9$ | Enhanced Investigation Recommended | Field inspection / Workshop contact. |
| **CRITICAL** | $80.0 - 100.0$ | Priority Senior Fraud Investigation Routing | Immediate escalation to Special Investigation Unit (SIU). |
