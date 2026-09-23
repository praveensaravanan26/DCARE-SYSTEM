# Chapter 8: Explainable AI (XAI) & TreeSHAP Attribution

## 8.1 The Need for Explainability in Insurance AI
In high-stakes financial operations, black-box predictions are legally and operationally unviable. Insurance regulations (such as EU AI Act and NAIC Model Governance) mandate that automated risk assessments must provide interpretable rationales.

---

## 8.2 TreeSHAP Mathematical Foundation
DCARE uses TreeSHAP, an exact and computationally efficient polynomial-time algorithm for computing Shapley values on decision tree ensembles:

$$\phi_i(x) = \sum_{S \subseteq F \setminus \{i\}} \frac{|S|!(|F| - |S| - 1)!}{|F|!} \left[ f_x(S \cup \{i\}) - f_x(S) \right]$$

Where:
- $\phi_i(x)$ is the marginal feature attribution of feature $i$ for claim instance $x$.
- $F$ is the total set of features.
- $S$ represents subsets of features.
- $f_x(S)$ is the model prediction conditioned on feature subset $S$.

---

## 8.3 Feature Attributions and Interpretation Generation
For each evaluated claim, DCARE generates:
1. **Quantitative SHAP Values ($\phi_i$):** Numerical score contributions (positive values increase risk; negative values decrease risk).
2. **Impact Direction:** Categorized into `INCREASES_RISK` or `DECREASES_RISK`.
3. **Natural-Language Audit Interpretations:**
   - *Example 1:* `"Amount deviation of $113,233.33 elevated the predictive risk signal by +6.71 points."`
   - *Example 2:* `"Claim amount is within typical statistical variance for this policy (-1.42 points)."`
   - *Example 3:* `"Document data alignment lowered risk profile across all verified fields."`
4. **Visual Waterfall Breakdown:** Rendered dynamically in the React frontend `ShapVisualizer` component.
