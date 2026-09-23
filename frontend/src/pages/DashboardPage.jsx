import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/client';
import { StatusBadge } from '../components/StatusBadge';
import {
  FileText,
  AlertTriangle,
  Clock,
  ShieldCheck,
  ArrowRight,
  PlusCircle,
  FileSpreadsheet,
  ShieldAlert,
  BarChart3,
  UserCheck,
  CheckCircle2,
  AlertCircle,
  TrendingUp,
  FileSearch,
  Users
} from 'lucide-react';

export const DashboardPage = () => {
  const { user, isCustomer, isOfficer, isInvestigator, isAdmin } = useAuth();
  const [claims, setClaims] = useState([]);
  const [pendingApprovals, setPendingApprovals] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, [user]);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      const claimsData = await api.getClaims().catch(() => []);
      setClaims(claimsData || []);

      if (user?.role === 'ROLE_ADMIN') {
        const pending = await api.getPendingApprovals().catch(() => []);
        setPendingApprovals(pending || []);
      }
    } catch (err) {
      console.error('Failed to load dashboard:', err);
    } finally {
      setLoading(false);
    }
  };

  const pendingReview = claims.filter((c) => c.status === 'SUBMITTED' || c.status === 'UNDER_REVIEW');
  const highRiskClaims = claims.filter((c) => c.riskLevel === 'HIGH' || c.riskLevel === 'CRITICAL');
  const approvedClaims = claims.filter((c) => c.status === 'APPROVED');
  const totalSettledValue = approvedClaims.reduce((acc, c) => acc + (Number(c.claimedAmount) || 0), 0);

  const getRoleTitle = () => {
    switch (user?.role) {
      case 'ROLE_CLAIM_OFFICER':
        return 'Claim Review Officer Console';
      case 'ROLE_FRAUD_INVESTIGATOR':
        return 'Special Investigation Unit (SIU) Portal';
      case 'ROLE_ADMIN':
        return 'Executive Administration Dashboard';
      case 'ROLE_CUSTOMER':
      default:
        return 'Policyholder Self-Service Portal';
    }
  };

  return (
    <div className="page-wrapper space-y-6">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-blue-900/30 via-slate-900 to-indigo-900/20 border border-slate-800 rounded-2xl p-6 relative overflow-hidden">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 relative z-10">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="text-xs font-mono font-bold text-blue-400 bg-blue-950/80 px-2.5 py-0.5 rounded-full border border-blue-800/50">
                {user?.role?.replace('ROLE_', '')}
              </span>
              <span className="text-xs text-slate-400">Enterprise Workspace</span>
            </div>
            <h1 className="text-2xl font-bold text-white font-heading">
              Welcome back, {user?.fullName || 'User'}
            </h1>
            <p className="text-xs text-slate-400 mt-1 max-w-xl">
              {getRoleTitle()} — Real-time automated document OCR intelligence, cross-validation, and explainable risk evaluation.
            </p>
          </div>

          <div className="flex items-center gap-3">
            <Link
              to="/claims/new"
              className="dcare-btn-primary py-2 px-4 text-xs font-bold shadow-lg shadow-blue-600/30"
            >
              <PlusCircle size={15} />
              Submit Claim
            </Link>
            <Link
              to="/claims"
              className="dcare-btn-secondary py-2 px-4 text-xs font-semibold"
            >
              <FileSpreadsheet size={15} />
              View Registry
            </Link>
          </div>
        </div>
      </div>

      {/* Admin Quick Alert: Pending Clearances */}
      {isAdmin && pendingApprovals.length > 0 && (
        <div className="p-4 rounded-xl bg-amber-950/40 border border-amber-800/50 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-lg bg-amber-500/20 border border-amber-500/30 flex items-center justify-center text-amber-400 shrink-0">
              <Users size={20} />
            </div>
            <div>
              <div className="font-bold text-amber-300 text-sm">
                {pendingApprovals.length} Staff Clearance {pendingApprovals.length === 1 ? 'Application' : 'Applications'} Pending Review
              </div>
              <div className="text-xs text-amber-400/80 mt-0.5">
                New staff accounts (Claim Officers / Investigators) require administrator authorization to access the system.
              </div>
            </div>
          </div>
          <Link
            to="/admin"
            className="px-3.5 py-1.5 rounded-lg bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs shrink-0 transition flex items-center gap-1.5"
          >
            Review Applications <ArrowRight size={13} />
          </Link>
        </div>
      )}

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="dcare-card flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-blue-600/10 border border-blue-500/20 flex items-center justify-center text-blue-400 shrink-0">
            <FileText size={22} />
          </div>
          <div>
            <div className="text-2xl font-extrabold text-white font-heading">
              {claims.length}
            </div>
            <div className="text-xs font-semibold text-slate-400">Total Claims Ingested</div>
          </div>
        </div>

        <div className="dcare-card flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-amber-600/10 border border-amber-500/20 flex items-center justify-center text-amber-400 shrink-0">
            <Clock size={22} />
          </div>
          <div>
            <div className="text-2xl font-extrabold text-white font-heading">
              {pendingReview.length}
            </div>
            <div className="text-xs font-semibold text-slate-400">Under Review / Triage</div>
          </div>
        </div>

        <div className="dcare-card flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-red-600/10 border border-red-500/20 flex items-center justify-center text-red-400 shrink-0">
            <AlertTriangle size={22} />
          </div>
          <div>
            <div className="text-2xl font-extrabold text-white font-heading">
              {highRiskClaims.length}
            </div>
            <div className="text-xs font-semibold text-slate-400">High Risk & Flagged</div>
          </div>
        </div>

        <div className="dcare-card flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-emerald-600/10 border border-emerald-500/20 flex items-center justify-center text-emerald-400 shrink-0">
            <ShieldCheck size={22} />
          </div>
          <div>
            <div className="text-2xl font-extrabold text-white font-heading">
              {approvedClaims.length}
            </div>
            <div className="text-xs font-semibold text-slate-400">Approved & Settled</div>
          </div>
        </div>
      </div>

      {/* Main Grid: Recent Claims + Role-Tailored Action Center */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recent Claims Table */}
        <div className="lg:col-span-2 dcare-card overflow-hidden">
          <div className="p-4 border-b border-slate-800 flex justify-between items-center">
            <h3 className="font-heading font-semibold text-base text-white">
              {isCustomer ? 'My Recent Insurance Claims' : 'Active Claims Ingestion Feed'}
            </h3>
            <Link to="/claims" className="text-xs text-blue-400 hover:text-blue-300 flex items-center gap-1 font-semibold">
              View All ({claims.length}) <ArrowRight size={13} />
            </Link>
          </div>

          <div className="overflow-x-auto">
            <table className="dcare-table">
              <thead>
                <tr>
                  <th>Claim Number</th>
                  <th>Type & Policy</th>
                  <th>Amount</th>
                  <th>Status</th>
                  <th>Risk Assessment</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {claims.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="text-center text-slate-500 py-10">
                      <FileText size={32} className="mx-auto text-slate-600 mb-2" />
                      <div className="text-sm font-semibold text-slate-400">No insurance claims registered yet</div>
                      <p className="text-xs text-slate-500 mt-1">Submit a new insurance claim to start automated document processing.</p>
                      <Link to="/claims/new" className="dcare-btn-primary text-xs py-1.5 px-3 inline-flex mt-3">
                        <PlusCircle size={13} /> Submit First Claim
                      </Link>
                    </td>
                  </tr>
                ) : (
                  claims.slice(0, 6).map((c) => (
                    <tr key={c.id}>
                      <td className="font-mono font-bold text-blue-400">
                        {c.claimNumber}
                      </td>
                      <td>
                        <div className="font-semibold text-slate-200">{c.claimType}</div>
                        <div className="text-xs text-slate-400 font-mono">{c.policyNumber}</div>
                      </td>
                      <td className="font-semibold text-slate-200">
                        ${Number(c.claimedAmount || 0).toLocaleString()}
                      </td>
                      <td>
                        <StatusBadge status={c.status} />
                      </td>
                      <td>
                        <StatusBadge status={c.riskLevel} type="risk" />
                      </td>
                      <td>
                        <Link
                          to={`/claims/${c.id}`}
                          className="px-2.5 py-1 text-xs rounded bg-slate-800 hover:bg-slate-700 text-slate-200 font-medium transition"
                        >
                          View
                        </Link>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Role-Tailored Operational Center */}
        <div className="space-y-4">
          {/* CUSTOMER WIDGET */}
          {isCustomer && (
            <div className="dcare-card p-5 space-y-4">
              <div className="flex items-center gap-2 pb-3 border-b border-slate-800">
                <ShieldCheck size={18} className="text-blue-400" />
                <h3 className="font-heading font-semibold text-base text-white">
                  Policyholder Quick Actions
                </h3>
              </div>

              <div className="space-y-2.5">
                <Link
                  to="/claims/new"
                  className="flex items-center justify-between p-3 rounded-xl bg-blue-950/40 border border-blue-800/40 hover:bg-blue-900/40 transition group"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-lg bg-blue-600/20 text-blue-400 flex items-center justify-center">
                      <PlusCircle size={16} />
                    </div>
                    <div>
                      <div className="text-xs font-bold text-slate-200 group-hover:text-white">
                        File New Insurance Claim
                      </div>
                      <div className="text-[11px] text-slate-400">Upload repair invoices & FIR</div>
                    </div>
                  </div>
                  <ArrowRight size={14} className="text-slate-500 group-hover:text-blue-400 transition" />
                </Link>

                <Link
                  to="/claims"
                  className="flex items-center justify-between p-3 rounded-xl bg-slate-850/60 border border-slate-800 hover:border-slate-700 transition group"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-lg bg-slate-800 text-slate-300 flex items-center justify-center">
                      <FileSpreadsheet size={16} />
                    </div>
                    <div>
                      <div className="text-xs font-bold text-slate-200 group-hover:text-white">
                        Track Claim Status
                      </div>
                      <div className="text-[11px] text-slate-400">Live review & disbursement stage</div>
                    </div>
                  </div>
                  <ArrowRight size={14} className="text-slate-500 group-hover:text-slate-300 transition" />
                </Link>
              </div>

              <div className="pt-3 border-t border-slate-800/80">
                <div className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">
                  Active Motor Policy
                </div>
                <div className="p-3 rounded-lg bg-slate-950 border border-slate-800">
                  <div className="flex justify-between text-xs">
                    <span className="text-slate-400">Policy:</span>
                    <span className="font-mono font-bold text-slate-200">POL-2026-8841</span>
                  </div>
                  <div className="flex justify-between text-xs mt-1">
                    <span className="text-slate-400">Coverage:</span>
                    <span className="text-emerald-400 font-semibold">Comprehensive Auto</span>
                  </div>
                  <div className="flex justify-between text-xs mt-1">
                    <span className="text-slate-400">Max Limit:</span>
                    <span className="font-mono text-slate-300">$100,000.00</span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* CLAIM OFFICER WIDGET */}
          {isOfficer && (
            <div className="dcare-card p-5 space-y-4">
              <div className="flex items-center gap-2 pb-3 border-b border-slate-800">
                <FileSearch size={18} className="text-blue-400" />
                <h3 className="font-heading font-semibold text-base text-white">
                  Officer Triage Queue
                </h3>
              </div>

              <div className="space-y-2 text-xs">
                <div className="flex justify-between p-2.5 rounded-lg bg-slate-900 border border-slate-800">
                  <span className="text-slate-400">Awaiting Triage:</span>
                  <span className="font-bold text-amber-400 font-mono">{pendingReview.length} claims</span>
                </div>
                <div className="flex justify-between p-2.5 rounded-lg bg-slate-900 border border-slate-800">
                  <span className="text-slate-400">STP Fast-Track Qualified:</span>
                  <span className="font-bold text-emerald-400 font-mono">
                    {claims.filter((c) => c.riskLevel === 'LOW').length} claims
                  </span>
                </div>
                <div className="flex justify-between p-2.5 rounded-lg bg-slate-900 border border-slate-800">
                  <span className="text-slate-400">Discrepancy Flags:</span>
                  <span className="font-bold text-red-400 font-mono">{highRiskClaims.length} claims</span>
                </div>
              </div>

              <div className="space-y-2 pt-2">
                <Link
                  to="/claims"
                  className="w-full dcare-btn-primary justify-center text-xs py-2"
                >
                  <FileSearch size={14} /> Open Triage Workflow
                </Link>
                <Link
                  to="/analytics"
                  className="w-full dcare-btn-secondary justify-center text-xs py-2"
                >
                  <BarChart3 size={14} /> View Claims Analytics
                </Link>
              </div>
            </div>
          )}

          {/* FRAUD INVESTIGATOR WIDGET */}
          {isInvestigator && (
            <div className="dcare-card p-5 space-y-4">
              <div className="flex items-center gap-2 pb-3 border-b border-slate-800">
                <ShieldAlert size={18} className="text-red-400" />
                <h3 className="font-heading font-semibold text-base text-white">
                  SIU Fraud Operations
                </h3>
              </div>

              <div className="p-3 rounded-xl bg-red-950/30 border border-red-800/40 space-y-2">
                <div className="text-xs font-bold text-red-300 flex items-center justify-between">
                  <span>Critical Risk Claims</span>
                  <span className="font-mono bg-red-900/60 px-2 py-0.5 rounded text-red-200">
                    {highRiskClaims.length} flagged
                  </span>
                </div>
                <p className="text-[11px] text-slate-400 leading-relaxed">
                  High-risk claims with SHAP explainability anomalies, cross-document inconsistencies, or SHA-256 duplicate collision alerts.
                </p>
              </div>

              <div className="space-y-2">
                <Link
                  to="/investigations"
                  className="w-full dcare-btn-primary justify-center text-xs py-2 bg-red-600 hover:bg-red-500"
                >
                  <ShieldAlert size={14} /> SIU Risk Dossiers
                </Link>
                <Link
                  to="/audit-logs"
                  className="w-full dcare-btn-secondary justify-center text-xs py-2"
                >
                  <FileText size={14} /> Inspect Forensic Audit Logs
                </Link>
              </div>
            </div>
          )}

          {/* SYSTEM ADMIN WIDGET */}
          {isAdmin && (
            <div className="dcare-card p-5 space-y-4">
              <div className="flex items-center gap-2 pb-3 border-b border-slate-800">
                <ShieldCheck size={18} className="text-amber-400" />
                <h3 className="font-heading font-semibold text-base text-white">
                  System Governance
                </h3>
              </div>

              <div className="space-y-2.5">
                <Link
                  to="/admin"
                  className="flex items-center justify-between p-3 rounded-xl bg-amber-950/30 border border-amber-800/40 hover:bg-amber-900/40 transition group"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-lg bg-amber-500/20 text-amber-400 flex items-center justify-center">
                      <Users size={16} />
                    </div>
                    <div>
                      <div className="text-xs font-bold text-slate-200 group-hover:text-white">
                        Staff Clearances
                      </div>
                      <div className="text-[11px] text-slate-400">
                        {pendingApprovals.length} pending review
                      </div>
                    </div>
                  </div>
                  <ArrowRight size={14} className="text-slate-500 group-hover:text-amber-400 transition" />
                </Link>

                <Link
                  to="/audit-logs"
                  className="flex items-center justify-between p-3 rounded-xl bg-slate-850/60 border border-slate-800 hover:border-slate-700 transition group"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-lg bg-slate-800 text-slate-300 flex items-center justify-center">
                      <FileText size={16} />
                    </div>
                    <div>
                      <div className="text-xs font-bold text-slate-200 group-hover:text-white">
                        Compliance Audit Trail
                      </div>
                      <div className="text-[11px] text-slate-400">Immutable RBAC event log</div>
                    </div>
                  </div>
                  <ArrowRight size={14} className="text-slate-500 group-hover:text-slate-300 transition" />
                </Link>
              </div>
            </div>
          )}

          {/* System Intelligence Badge */}
          <div className="p-4 rounded-xl bg-slate-900 border border-slate-800 text-xs space-y-2">
            <div className="flex items-center gap-2 font-bold text-slate-300">
              <div className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
              DCARE AI Engine Status
            </div>
            <div className="grid grid-cols-2 gap-2 text-[11px] text-slate-400 font-mono">
              <div>OCR Pipeline: <span className="text-emerald-400 font-semibold">Active</span></div>
              <div>XGBoost / SHAP: <span className="text-emerald-400 font-semibold">Active</span></div>
              <div>Isolation Forest: <span className="text-emerald-400 font-semibold">Active</span></div>
              <div>Rule Engine: <span className="text-emerald-400 font-semibold">Active</span></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
