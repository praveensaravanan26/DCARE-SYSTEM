import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { StatusBadge } from '../components/StatusBadge';
import {
  FileText,
  ShieldAlert,
  FolderOpen,
  ArrowRight,
  Clock,
  Car,
  DollarSign,
  MapPin,
  Calendar,
  Sparkles,
  CheckCircle2
} from 'lucide-react';

export const ClaimDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { canReview } = useAuth();

  const [claim, setClaim] = useState(null);
  const [documents, setDocuments] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [evaluating, setEvaluating] = useState(false);

  useEffect(() => {
    loadClaimDetails();
  }, [id]);

  const loadClaimDetails = async () => {
    setLoading(true);
    try {
      const [claimData, docsData, revsData] = await Promise.all([
        api.getClaimById(id),
        api.getDocumentsForClaim(id).catch(() => []),
        api.getReviews(id).catch(() => [])
      ]);
      setClaim(claimData);
      setDocuments(docsData || []);
      setReviews(revsData || []);
    } catch (err) {
      console.error('Failed to load claim details', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRunEvaluation = async () => {
    setEvaluating(true);
    try {
      await api.evaluateClaimRisk(id);
      await loadClaimDetails();
      navigate(`/claims/${id}/risk`);
    } catch (err) {
      alert('Risk evaluation failed: ' + err.message);
    } finally {
      setEvaluating(false);
    }
  };

  if (loading) {
    return (
      <div className="page-wrapper text-center py-20 text-slate-400">
        Loading claim profile and risk metadata...
      </div>
    );
  }

  if (!claim) {
    return (
      <div className="page-wrapper text-center py-20 text-slate-400">
        Claim not found.
      </div>
    );
  }

  return (
    <div className="page-wrapper space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
        <div>
          <div className="flex items-center gap-3">
            <h1 className="text-2xl font-bold text-white tracking-tight font-heading">
              Claim #{claim.claimNumber}
            </h1>
            <StatusBadge status={claim.status} />
            <StatusBadge status={claim.riskLevel} type="risk" />
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Policy Reference: <span className="font-mono text-slate-200">{claim.policyNumber}</span> • Registered on {new Date(claim.createdAt).toLocaleDateString()}
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Link to={`/claims/${id}/documents`} className="dcare-btn-secondary">
            <FolderOpen size={14} />
            <span>Documents ({documents.length})</span>
          </Link>

          <Link to={`/claims/${id}/risk`} className="dcare-btn-secondary">
            <ShieldAlert size={14} />
            <span>Risk Hub</span>
          </Link>

          <button
            onClick={handleRunEvaluation}
            disabled={evaluating}
            className="dcare-btn-primary"
          >
            <Sparkles size={14} className={evaluating ? 'animate-spin' : ''} />
            <span>{evaluating ? 'Analyzing AI...' : 'Run Risk Assessment'}</span>
          </button>
        </div>
      </div>

      {/* Claim Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="dcare-card space-y-4">
          <h3 className="font-heading font-semibold text-sm text-slate-400 uppercase tracking-wider">
            Incident Overview
          </h3>
          <div className="space-y-2.5 text-xs">
            <div className="flex justify-between">
              <span className="text-slate-500">Claim Type:</span>
              <span className="font-semibold text-slate-200">{claim.claimType}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Incident Date:</span>
              <span className="font-mono font-semibold text-slate-200">{claim.incidentDate}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Location:</span>
              <span className="font-semibold text-slate-200 truncate max-w-[180px]">{claim.incidentLocation}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Priority:</span>
              <span className="font-semibold text-slate-200">{claim.priority}</span>
            </div>
          </div>
        </div>

        <div className="dcare-card space-y-4">
          <h3 className="font-heading font-semibold text-sm text-slate-400 uppercase tracking-wider">
            Financials & Workshop
          </h3>
          <div className="space-y-2.5 text-xs">
            <div className="flex justify-between">
              <span className="text-slate-500">Claimed Amount:</span>
              <span className="font-mono font-bold text-slate-100 text-sm">
                ${Number(claim.claimedAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Approved Amount:</span>
              <span className="font-mono font-semibold text-emerald-400">
                {claim.approvedAmount ? `$${Number(claim.approvedAmount).toLocaleString()}` : 'Pending Assessment'}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Garage / Workshop:</span>
              <span className="font-semibold text-slate-200">{claim.garageName || 'Not Specified'}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Invoice Number:</span>
              <span className="font-mono font-semibold text-slate-200">{claim.invoiceNumber || '—'}</span>
            </div>
          </div>
        </div>

        <div className="dcare-card space-y-4">
          <h3 className="font-heading font-semibold text-sm text-slate-400 uppercase tracking-wider">
            Vehicle Profile
          </h3>
          <div className="space-y-2.5 text-xs">
            <div className="flex justify-between">
              <span className="text-slate-500">Registration Number:</span>
              <span className="font-mono font-bold text-blue-400">{claim.vehicleNumber}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Associated Policy:</span>
              <span className="font-mono font-semibold text-slate-200">{claim.policyNumber}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Uploaded Documents:</span>
              <span className="font-semibold text-slate-200">{documents.length} files</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Risk Score:</span>
              <span className="font-mono font-bold text-amber-400">{claim.riskScore || 0} / 100</span>
            </div>
          </div>
        </div>
      </div>

      {/* Description */}
      <div className="dcare-card p-5">
        <h3 className="font-heading font-semibold text-sm text-slate-300 mb-2">
          Claim Narrative & Incident Description
        </h3>
        <p className="text-xs text-slate-300 leading-relaxed bg-slate-900/60 p-3.5 rounded-lg border border-slate-800">
          {claim.description || 'No detailed narrative provided.'}
        </p>
      </div>

      {/* Decision / Review History */}
      {reviews.length > 0 && (
        <div className="dcare-card">
          <div className="p-4 border-b border-slate-800">
            <h3 className="font-heading font-semibold text-base text-white">
              Investigation & Review Decision Log
            </h3>
          </div>
          <div className="p-4 space-y-3">
            {reviews.map((r, idx) => (
              <div key={r.id || idx} className="bg-slate-900 p-3.5 rounded-lg border border-slate-800 text-xs">
                <div className="flex justify-between items-center mb-1.5">
                  <span className="font-bold text-slate-200">
                    {r.reviewerName} ({r.reviewerRole?.replace('ROLE_', '')})
                  </span>
                  <span className="text-slate-400 text-[11px]">
                    {new Date(r.createdAt).toLocaleString()}
                  </span>
                </div>
                <div className="flex items-center gap-2 mb-2">
                  <span className="text-slate-400">Decision:</span>
                  <span className="font-bold text-blue-400">{r.decision}</span>
                  <span className="text-slate-500">•</span>
                  <span className="text-slate-400">{r.previousStatus} → {r.newStatus}</span>
                </div>
                <p className="text-slate-300 bg-slate-950 p-2.5 rounded border border-slate-800/80">
                  {r.notes}
                </p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
