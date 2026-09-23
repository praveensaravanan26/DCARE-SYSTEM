# Chapter 11: Demonstration & Academic Presentation Guide

## 11.1 Demo User Credentials
The system comes pre-configured with four demo personas with 1-click login buttons on the login page:

| Persona | Email | Password | Role Description |
| :--- | :--- | :--- | :--- |
| **Claim Officer** | `officer@dcare.local` | `Officer@123` | Operational review, STP clearance, desk verification. |
| **Fraud Investigator** | `investigator@dcare.local` | `Investigator@123` | Special Investigation Unit (SIU), deep dive, SHAP review. |
| **Customer / Policyholder** | `customer@dcare.local` | `Customer@123` | FNOL claim filing, document upload, status tracking. |
| **Administrator** | `admin@dcare.local` | `Admin@123` | User account management, audit logs, system overview. |

---

## 11.2 Step-by-Step Live Demo Walkthrough
1. **Login:** Open `http://localhost:5173` and click **"Claim Officer"**.
2. **Dashboard Overview:** View live claim volume stats, pending review count, and the 4 pre-loaded benchmark scenarios.
3. **Submit Scenario 1 (Clean Claim):**
   - Click **"Scenario 1: Normal Claim"** from Dashboard.
   - Form pre-fills automatically with clean parameters. Click **"Proceed to Document Upload"**.
   - Upload `datasets/demo_documents/scenario1_repair_invoice.pdf` and `scenario1_accident_report.pdf`.
   - Click **"Run Full AI Risk Evaluation"**.
   - View: Low Risk (12.3/100, Green Gauge), Consistency Matrix all PASSED, SHAP attribution reducing risk. Click **"Submit Review"** -> Select **"Approve Claim"**.
4. **Submit Scenario 2 (Document Mismatch):**
   - Click **"Scenario 2: Document Mismatch"**.
   - Upload `datasets/demo_documents/scenario2_repair_invoice_mismatch.pdf` and `scenario2_accident_report_mismatch.pdf`.
   - Run Risk Evaluation. View: Consistency matrix flags Date Mismatch (Aug 12 vs Aug 14) and Amount Variance ($42,000 vs $49,500). Click **"Submit Review"** -> **"Send to Fraud Investigation Unit"**.
5. **Switch Role to Fraud Investigator:**
   - Click user avatar in top-right navbar -> Select **"Fraud Investigator"**.
   - Navigate to **"Investigations Hub"** (`/investigations`). View the escalated claim, review SHAP waterfall, and record investigation findings.
6. **Analytics & Performance:**
   - Click **"Analytics"** (`/analytics`) to view real-time risk doughnut charts, status bars, and trained ML benchmark metrics (94.2% Accuracy, 0.968 ROC-AUC).
7. **Audit Trail:**
   - Switch to **"System Administrator"** -> Navigate to **"Audit Logs"** (`/audit-logs`) to demonstrate the immutable compliance trail.
