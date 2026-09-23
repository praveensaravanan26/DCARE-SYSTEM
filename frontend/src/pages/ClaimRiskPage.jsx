import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { RiskGauge } from '../components/RiskGauge';
import { ConsistencyMatrix } from '../components/ConsistencyMatrix';
import { ShapVisualizer } from '../components/ShapVisualizer';
import { EvidencePanel } from '../components/EvidencePanel';
import {
  Sparkles,
  ShieldCheck,
  Search,
  ArrowRight,
  FolderOpen,
  FileCheck2,
  CheckCircle2,
  XCircle,
  HelpCircle
} from 'lucide-react';

export const ClaimRiskPage = () => {
  const { id } = useParams();
  const { canReview } = useAuth();

  const [claim, setClaim] = useState(null);
  const [riskData, setRiskData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [evaluating, setEvaluating] = useState(false);

  // Review Modal State
  const [reviewModalOpen, setReviewModalOpen] = useState(false);
  const [decision, setDecision] = useState('APPROVE');
  const [notes, setNotes] = useState('');
  const [submittingReview, setSubmittingReview] = useState(false);

  useEffect(() => {
    loadRiskData();
  }, [id]);

  const loadRiskData = async () => {
    setLoading(true);
    try {
      const data = await api.getRiskDetails(id);
      setClaim(data.claim);
      setRiskData(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleReevaluate = async () => {
    setEvaluating(true);
    try {
      const res = await api.evaluateClaimRisk(id);
      setClaim(res.claim);
      setRiskData(res);
    } catch (err) {
      alert('Risk assessment failed: ' + err.message);
    } finally {
      setEvaluating(false);
    }
  };

  const handleReviewSubmit = async (e) => {
    e.preventDefault();
    if (!notes.trim()) return;
    setSubmittingReview(true);

    try {
      await api.submitReview(id, decision, notes);
      setReviewModalOpen(false);
      setNotes('');
      await loadRiskData();
      alert('Review decision successfully submitted and recorded in audit trail.');
    } catch (err) {
      alert('Review submission failed: ' + err.message);
    } finally {
      setSubmittingReview(false);
    }
  };

  const assessment = riskData?.assessment;
  const validations = riskData?.validations || [];
  const evidence = riskData?.evidence || [];
  const features = riskData?.features || {};

  let shapSummary = null;
  if (assessment?.shapSummaryJson) {
    try {
      shapSummary = JSON.parse(assessment.shapSummaryJson);
    } catch (ignored) {}
  }

  return (
    <div className="page-wrapper space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight font-heading">
            Risk Evaluation & Explainability Hub
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Claim #{claim?.claimNumber} • Evidence-aware hybrid scoring (XGBoost + Random Forest + Isolation Forest)
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Link to={`/claims/${id}/documents`} className="dcare-btn-secondary">
            <FolderOpen size={14} />
            <span>Documents & OCR</span>
          </Link>

          <button
            onClick={handleReevaluate}
            disabled={evaluating}
            className="dcare-btn-secondary"
          >
            <Sparkles size={14} className={evaluating ? 'animate-spin text-amber-400' : 'text-amber-400'} />
            <span>{evaluating ? 'Evaluating...' : 'Re-Calculate Risk'}</span>
          </button>

          {canReview && (
            <button
              onClick={() => setReviewModalOpen(true)}
              className="dcare-btn-primary"
            >
              <FileCheck2 size={14} />
              <span>Submit Review Decision</span>
            </button>
          )}
        </div>
      </div>

      {/* Top Grid: Risk Gauge + Historical Feature Summary */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1">
          <RiskGauge
            score={assessment?.overallRiskScore || claim?.riskScore || 0}
            level={assessment?.riskLevel || claim?.riskLevel || 'LOW'}
            recommendation={assessment?.recommendation}
          />
        </div>

        <div className="lg:col-span-2 dcare-card space-y-4">
          <h3 className="font-heading font-semibold text-sm text-slate-300 uppercase tracking-wider">
            Engineered Multi-Source Feature Signals
          </h3>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs">
            <div className="bg-slate-900 p-3 rounded-lg border border-slate-800">
              <span className="text-slate-500 block">Supervised Prob:</span>
              <span className="font-mono font-bold text-slate-200 text-sm">
                {(Number(features.supervisedProb || 0) * 100).toFixed(1)}%
              </span>
            </div>

            <div className="bg-slate-900 p-3 rounded-lg border border-slate-800">
              <span className="text-slate-500 block">Anomaly Score:</span>
              <span className="font-mono font-bold text-slate-200 text-sm">
                {Number(features.anomalyScore || 0).toFixed(1)} / 100
              </span>
            </div>

            <div className="bg-slate-900 p-3 rounded-lg border border-slate-800">
              <span className="text-slate-500 block">Amount Z-Score:</span>
              <span className="font-mono font-bold text-amber-300 text-sm">
                {Number(features.claimAmountZscore || 0).toFixed(2)} σ
              </span>
            </div>

            <div className="bg-slate-900 p-3 rounded-lg border border-slate-800">
              <span className="text-slate-500 block">90-Day Velocity:</span>
              <span className="font-mono font-bold text-slate-200 text-sm">
                {features.claimFrequency90d || 0} claims
              </span>
            </div>

            <div className="bg-slate-900 p-3 rounded-lg border border-slate-800">
              <span className="text-slate-500 block">Document Mismatches:</span>
              <span className="font-mono font-bold text-orange-400 text-sm">
                {features.documentMismatchCount || 0}
              </span>
            </div>

            <div className="bg-slate-900 p-3 rounded-lg border border-slate-800">
              <span className="text-slate-500 block">Duplicate File Flag:</span>
              <span className="font-mono font-bold text-slate-200 text-sm">
                {features.duplicateIndicator > 0 ? 'DETECTED' : 'None'}
              </span>
            </div>

            <div className="bg-slate-900 p-3 rounded-lg border border-slate-800">
              <span className="text-slate-500 block">Garage Invocations:</span>
              <span className="font-mono font-bold text-slate-200 text-sm">
                {features.garageRepeatCount || 0} times
              </span>
            </div>

            <div className="bg-slate-900 p-3 rounded-lg border border-slate-800">
              <span className="text-slate-500 block">Filing Delay:</span>
              <span className="font-mono font-bold text-slate-200 text-sm">
                {features.incidentToClaimDelayDays || 1} days
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Cross-Document Consistency Matrix */}
      <ConsistencyMatrix validations={validations} />

      {/* SHAP Feature Attribution */}
      <ShapVisualizer shapSummary={shapSummary} />

      {/* Risk Evidence Panel */}
      <EvidencePanel evidence={evidence} />

      {/* Review Modal */}
      {reviewModalOpen && (
        <div className="modal-overlay">
          <div className="modal-card p-6">
            <h3 className="font-heading font-bold text-lg text-white mb-2">
              Submit Claim Review Decision
            </h3>
            <p className="text-xs text-slate-400 mb-4">
              Authorized decision-support action. The final review decision, note, and reviewer timestamp will be recorded immutably in the audit log.
            </p>

            <form onSubmit={handleReviewSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Review Decision *
                </label>
                <select
                  value={decision}
                  onChange={(e) => setDecision(e.target.value)}
                  className="dcare-select"
                >
                  <option value="APPROVE">Approve Claim (Clear for Settlement)</option>
                  <option value="REQUEST_INFORMATION">Request Additional Information (Verification Needed)</option>
                  <option value="SEND_TO_INVESTIGATION">Send to Fraud Investigation Unit</option>
                  <option value="REJECT">Reject / Decline Claim</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Investigation & Reviewer Notes *
                </label>
                <textarea
                  required
                  rows={4}
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                  placeholder="State the objective rationale, verified document evidence, and action taken..."
                  className="dcare-input resize-none"
                />
              </div>

              <div className="pt-4 border-t border-slate-800 flex justify-end gap-3">
                <button
                  type="button"
                  onClick={() => setReviewModalOpen(false)}
                  className="dcare-btn-secondary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submittingReview}
                  className="dcare-btn-primary"
                >
                  {submittingReview ? 'Submitting...' : 'Commit Decision'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
