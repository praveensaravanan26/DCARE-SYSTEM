import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import { Link } from 'react-router-dom';
import { StatusBadge } from '../components/StatusBadge';
import {
  ShieldAlert,
  Search,
  Filter,
  ArrowRight,
  FileCheck2,
  AlertTriangle,
  RefreshCw
} from 'lucide-react';

export const InvestigationsPage = () => {
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedClaim, setSelectedClaim] = useState(null);
  const [decision, setDecision] = useState('SEND_TO_INVESTIGATION');
  const [notes, setNotes] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadClaims();
  }, []);

  const loadClaims = async () => {
    setLoading(true);
    try {
      const data = await api.getClaims();
      // Filter claims that require attention (High Risk, Critical Risk, or Pending Review)
      const triage = (data || []).filter(
        (c) =>
          c.riskLevel === 'HIGH' ||
          c.riskLevel === 'CRITICAL' ||
          c.riskLevel === 'MEDIUM' ||
          c.status === 'ENHANCED_REVIEW' ||
          c.status === 'INVESTIGATION' ||
          c.status === 'VALIDATION_REQUIRED'
      );
      setClaims(triage);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleReviewSubmit = async (e) => {
    e.preventDefault();
    if (!selectedClaim || !notes.trim()) return;
    setSubmitting(true);

    try {
      await api.submitReview(selectedClaim.id, decision, notes);
      setSelectedClaim(null);
      setNotes('');
      await loadClaims();
      alert('Review decision submitted successfully.');
    } catch (err) {
      alert('Review failed: ' + err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="page-wrapper space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight font-heading flex items-center gap-2">
            <ShieldAlert size={24} className="text-orange-400" />
            Fraud Risk & Investigation Workspace
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Prioritized queue of claims flagged with high-risk evidence, document mismatches, or statistical anomalies.
          </p>
        </div>

        <button onClick={loadClaims} className="dcare-btn-secondary">
          <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
          <span>Refresh Queue</span>
        </button>
      </div>

      <div className="dcare-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="dcare-table">
            <thead>
              <tr>
                <th>Claim #</th>
                <th>Policy & Vehicle</th>
                <th>Claimed Amount</th>
                <th>Status</th>
                <th>Risk Score</th>
                <th>Risk Level</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {claims.length === 0 ? (
                <tr>
                  <td colSpan={7} className="text-center text-slate-500 py-12">
                    No active high-risk investigation alerts in the queue.
                  </td>
                </tr>
              ) : (
                claims.map((c) => (
                  <tr key={c.id}>
                    <td className="font-mono font-bold text-blue-400">
                      {c.claimNumber}
                    </td>
                    <td>
                      <div className="font-semibold text-slate-200">{c.policyNumber}</div>
                      <div className="text-xs text-slate-400 font-mono">{c.vehicleNumber}</div>
                    </td>
                    <td className="font-semibold text-slate-200 font-mono">
                      ${Number(c.claimedAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}
                    </td>
                    <td>
                      <StatusBadge status={c.status} />
                    </td>
                    <td className="font-mono font-bold text-amber-400">
                      {Number(c.riskScore || 0).toFixed(1)} / 100
                    </td>
                    <td>
                      <StatusBadge status={c.riskLevel} type="risk" />
                    </td>
                    <td>
                      <div className="flex items-center gap-2">
                        <Link
                          to={`/claims/${c.id}/risk`}
                          className="dcare-btn-secondary text-xs py-1 px-2.5"
                        >
                          Risk Hub
                        </Link>
                        <button
                          onClick={() => {
                            setSelectedClaim(c);
                            setDecision('APPROVE');
                          }}
                          className="dcare-btn-primary text-xs py-1 px-2.5"
                        >
                          Review
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Review Modal */}
      {selectedClaim && (
        <div className="modal-overlay">
          <div className="modal-card p-6">
            <h3 className="font-heading font-bold text-lg text-white mb-1">
              Submit Review for Claim #{selectedClaim.claimNumber}
            </h3>
            <p className="text-xs text-slate-400 mb-4">
              Policy: <span className="font-mono text-slate-200">{selectedClaim.policyNumber}</span> • Risk Score: <span className="font-mono text-amber-400 font-bold">{selectedClaim.riskScore}</span>
            </p>

            <form onSubmit={handleReviewSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Decision *
                </label>
                <select
                  value={decision}
                  onChange={(e) => setDecision(e.target.value)}
                  className="dcare-select"
                >
                  <option value="APPROVE">Approve Claim</option>
                  <option value="REQUEST_INFORMATION">Request Additional Information</option>
                  <option value="SEND_TO_INVESTIGATION">Send to Fraud Investigation Unit</option>
                  <option value="REJECT">Reject / Decline Claim</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Reviewer Notes *
                </label>
                <textarea
                  required
                  rows={4}
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                  placeholder="State the objective reasons and findings from document intelligence..."
                  className="dcare-input resize-none"
                />
              </div>

              <div className="pt-4 border-t border-slate-800 flex justify-end gap-3">
                <button
                  type="button"
                  onClick={() => setSelectedClaim(null)}
                  className="dcare-btn-secondary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="dcare-btn-primary"
                >
                  {submitting ? 'Saving...' : 'Commit Decision'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
