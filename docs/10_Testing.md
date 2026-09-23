# Chapter 10: Verification, Testing & Quality Assurance

## 10.1 Automated End-to-End Verification Suite (`scripts/verify_e2e_workflow.py`)
DCARE includes an automated integration testing script testing all 4 benchmark scenarios:

### Test Execution Commands:
```bash
# Run automated verification suite
cd /Users/praveensaravanan/Desktop/DCARE_Final_Year_Project
./ai-service/venv/bin/python scripts/verify_e2e_workflow.py
```

### Verified Scenarios:
1. **Scenario 1 (Clean Claim):**
   - Submits $18,500 claim with matching repair invoice & accident report.
   - Verified: Low Risk (12.3/100), 0 mismatches, STP candidate, Officer approved.
2. **Scenario 2 (Cross-Document Discrepancy):**
   - Submits claim with mismatched incident dates (Aug 12 vs Aug 14) and amounts ($42,000 vs $49,500).
   - Verified: Discrepancy caught by Consistency Engine, referred to SIU.
3. **Scenario 3 (High Claim Velocity / Outlier):**
   - Submits extreme outlier amount ($128,000) with repeated 90-day frequency.
   - Verified: High risk score, SHAP attributions isolating Amount Deviation and Z-Score.
4. **Scenario 4 (Duplicate Document / Reused Invoice):**
   - Submits invoice previously utilized in Scenario 1.
   - Verified: Cryptographic SHA-256 hash collision and duplicate invoice alert triggered.
5. **Analytics & Audit Verification:**
   - Verified: Dashboard metrics, ML benchmark retrieval, and 60+ immutable audit log entries.
