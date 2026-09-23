import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import { ShieldCheck, UserCheck, UserX, Sliders, RefreshCw, AlertTriangle, CheckCircle, XCircle, Clock, Shield } from 'lucide-react';

export const AdminPage = () => {
  const [users, setUsers] = useState([]);
  const [pendingApprovals, setPendingApprovals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionMessage, setActionMessage] = useState({ type: '', text: '' });

  // Risk scoring engine thresholds
  const [thresholds, setThresholds] = useState({
    lowUpper: 29,
    mediumUpper: 59,
    highUpper: 79,
    anomalyWeight: 0.15,
    supervisedWeight: 0.35,
    mismatchWeight: 0.25
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [allUsers, pending] = await Promise.all([
        api.getUsers().catch(() => []),
        api.getPendingApprovals().catch(() => [])
      ]);
      setUsers(allUsers || []);
      setPendingApprovals(pending || []);
    } catch (err) {
      console.error('Error loading admin data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (userId, userName) => {
    try {
      await api.approveUser(userId);
      setActionMessage({ type: 'success', text: `Access granted for ${userName}. Account activated.` });
      await loadData();
      setTimeout(() => setActionMessage({ type: '', text: '' }), 5000);
    } catch (err) {
      setActionMessage({ type: 'error', text: `Approval failed: ${err.message}` });
    }
  };

  const handleReject = async (userId, userName) => {
    if (!window.confirm(`Are you sure you want to reject the staff clearance request for ${userName}?`)) {
      return;
    }
    try {
      await api.rejectUser(userId);
      setActionMessage({ type: 'info', text: `Access request rejected for ${userName}.` });
      await loadData();
      setTimeout(() => setActionMessage({ type: '', text: '' }), 5000);
    } catch (err) {
      setActionMessage({ type: 'error', text: `Rejection failed: ${err.message}` });
    }
  };

  const handleToggleUser = async (userId) => {
    try {
      await api.toggleUserStatus(userId);
      await loadData();
    } catch (err) {
      alert('Failed to toggle status: ' + err.message);
    }
  };

  const getRoleLabel = (role) => {
    switch (role) {
      case 'ROLE_CLAIM_OFFICER':
        return 'Claim Review Officer';
      case 'ROLE_FRAUD_INVESTIGATOR':
        return 'Fraud Risk Investigator (SIU)';
      case 'ROLE_ADMIN':
        return 'System Administrator';
      case 'ROLE_CUSTOMER':
        return 'Policyholder';
      default:
        return role?.replace('ROLE_', '') || 'User';
    }
  };

  const getRoleBadgeStyle = (role) => {
    switch (role) {
      case 'ROLE_CLAIM_OFFICER':
        return 'bg-blue-950/70 text-blue-400 border-blue-800/50';
      case 'ROLE_FRAUD_INVESTIGATOR':
        return 'bg-purple-950/70 text-purple-400 border-purple-800/50';
      case 'ROLE_ADMIN':
        return 'bg-amber-950/70 text-amber-400 border-amber-800/50';
      default:
        return 'bg-slate-800 text-slate-300 border-slate-700';
    }
  };

  return (
    <div className="page-wrapper space-y-6">
      {/* Header */}
      <div className="pb-4 border-b border-slate-800 flex flex-col sm:flex-row justify-between sm:items-center gap-3">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight font-heading flex items-center gap-2">
            <ShieldCheck size={24} className="text-blue-400" />
            Security Administration & Staff Access Control
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Enterprise identity governance, staff security authorization, and risk engine configuration.
          </p>
        </div>
        <button
          onClick={loadData}
          disabled={loading}
          className="dcare-btn-secondary text-xs self-start sm:self-auto py-1.5 px-3 flex items-center gap-1.5"
        >
          <RefreshCw size={13} className={loading ? 'animate-spin' : ''} />
          Refresh Data
        </button>
      </div>

      {/* Action Message Alert */}
      {actionMessage.text && (
        <div
          className={`p-4 rounded-xl border text-xs flex items-center gap-2.5 font-medium ${
            actionMessage.type === 'success'
              ? 'bg-emerald-950/60 border-emerald-800/60 text-emerald-200'
              : actionMessage.type === 'error'
              ? 'bg-red-950/60 border-red-800/60 text-red-200'
              : 'bg-slate-900 border-slate-700 text-slate-200'
          }`}
        >
          {actionMessage.type === 'success' ? (
            <CheckCircle size={16} className="text-emerald-400 shrink-0" />
          ) : actionMessage.type === 'error' ? (
            <AlertTriangle size={16} className="text-red-400 shrink-0" />
          ) : (
            <Shield size={16} className="text-blue-400 shrink-0" />
          )}
          <span>{actionMessage.text}</span>
        </div>
      )}

      {/* PENDING STAFF APPROVALS SECTION */}
      <div className="dcare-card overflow-hidden border border-amber-900/40 bg-gradient-to-b from-slate-900 via-slate-900 to-amber-950/10">
        <div className="p-4 border-b border-slate-800 flex justify-between items-center bg-amber-950/20">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-amber-500/20 border border-amber-500/30 flex items-center justify-center text-amber-400">
              <Clock size={16} />
            </div>
            <div>
              <h3 className="font-heading font-semibold text-base text-white flex items-center gap-2">
                Pending Staff Clearances ({pendingApprovals.length})
              </h3>
              <p className="text-[11px] text-slate-400">
                Staff accounts with elevated privileges (Claim Officers & Fraud Investigators) awaiting administrator authorization.
              </p>
            </div>
          </div>
        </div>

        {pendingApprovals.length === 0 ? (
          <div className="p-8 text-center text-slate-400 text-xs">
            <CheckCircle size={32} className="mx-auto text-emerald-400/60 mb-2" />
            <div className="font-semibold text-slate-300">All Clearance Requests Processed</div>
            <div className="text-[11px] text-slate-500 mt-0.5">
              No pending staff applications currently require administrator authorization.
            </div>
          </div>
        ) : (
          <div className="divide-y divide-slate-800/60">
            {pendingApprovals.map((req) => (
              <div
                key={req.id}
                className="p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:bg-slate-850/40 transition"
              >
                <div className="space-y-1">
                  <div className="flex items-center gap-2 flex-wrap">
                    <span className="font-bold text-sm text-white">{req.fullName}</span>
                    <span
                      className={`text-[11px] font-semibold px-2.5 py-0.5 rounded-full border ${getRoleBadgeStyle(
                        req.role
                      )}`}
                    >
                      {getRoleLabel(req.role)}
                    </span>
                    <span className="text-[11px] font-mono px-2 py-0.5 rounded bg-amber-950/60 text-amber-400 border border-amber-800/40">
                      PENDING REVIEW
                    </span>
                  </div>
                  <div className="flex items-center gap-4 text-xs text-slate-400 font-mono">
                    <span>Email: <strong className="text-slate-300">{req.email}</strong></span>
                    {req.phoneNumber && <span>Phone: {req.phoneNumber}</span>}
                    <span>Applied: {new Date(req.createdAt).toLocaleDateString()}</span>
                  </div>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                  <button
                    onClick={() => handleApprove(req.id, req.fullName)}
                    className="flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-xs transition shadow-lg shadow-emerald-600/20"
                  >
                    <UserCheck size={14} />
                    Approve Access
                  </button>
                  <button
                    onClick={() => handleReject(req.id, req.fullName)}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-red-950/60 hover:bg-red-900/60 text-red-300 border border-red-800/50 font-semibold text-xs transition"
                  >
                    <UserX size={14} />
                    Reject
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* ALL REGISTERED USERS TABLE */}
      <div className="dcare-card overflow-hidden">
        <div className="p-4 border-b border-slate-800 flex justify-between items-center">
          <div>
            <h3 className="font-heading font-semibold text-base text-white">
              System Accounts & Role Directory ({users.length})
            </h3>
            <p className="text-[11px] text-slate-400 mt-0.5">
              Comprehensive list of active and deactivated system users across all roles.
            </p>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="dcare-table">
            <thead>
              <tr>
                <th>User / Identity</th>
                <th>Role</th>
                <th>Clearance</th>
                <th>Account Status</th>
                <th>Registered Date</th>
                <th>Access Action</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td>
                    <div className="font-semibold text-slate-200">{u.fullName}</div>
                    <div className="font-mono text-xs text-slate-400">{u.email}</div>
                  </td>
                  <td>
                    <span
                      className={`font-mono text-xs font-bold px-2 py-0.5 rounded border ${getRoleBadgeStyle(
                        u.role
                      )}`}
                    >
                      {u.role?.replace('ROLE_', '')}
                    </span>
                  </td>
                  <td>
                    <span
                      className={`badge ${
                        u.isApproved
                          ? 'badge-low'
                          : u.approvalStatus === 'REJECTED'
                          ? 'badge-critical'
                          : 'badge-high'
                      }`}
                    >
                      {u.approvalStatus || (u.isApproved ? 'APPROVED' : 'PENDING')}
                    </span>
                  </td>
                  <td>
                    <span className={`badge ${u.isActive ? 'badge-low' : 'badge-critical'}`}>
                      {u.isActive ? 'ACTIVE' : 'INACTIVE'}
                    </span>
                  </td>
                  <td className="text-xs text-slate-400">
                    {new Date(u.createdAt).toLocaleDateString()}
                  </td>
                  <td>
                    {u.role !== 'ROLE_ADMIN' ? (
                      <button
                        onClick={() => handleToggleUser(u.id)}
                        className={`text-xs px-2.5 py-1 rounded font-semibold transition ${
                          u.isActive
                            ? 'bg-red-950/40 text-red-400 border border-red-800/40 hover:bg-red-900/40'
                            : 'bg-emerald-950/40 text-emerald-400 border border-emerald-800/40 hover:bg-emerald-900/40'
                        }`}
                      >
                        {u.isActive ? 'Deactivate' : 'Activate'}
                      </button>
                    ) : (
                      <span className="text-[11px] text-slate-500 font-mono">Protected Root</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Threshold Configuration */}
      <div className="dcare-card p-6 space-y-4">
        <h3 className="font-heading font-semibold text-base text-white flex items-center gap-2">
          <Sliders size={18} className="text-blue-400" />
          Automated Risk Scoring Engine Weights
        </h3>
        <p className="text-xs text-slate-400">
          Ensemble component weight parameters for automated claim triage and fraud propensity evaluation.
        </p>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
          <div>
            <label className="block text-slate-400 mb-1 font-semibold">Supervised XGBoost Weight</label>
            <input
              type="number"
              step="0.05"
              value={thresholds.supervisedWeight}
              onChange={(e) => setThresholds({ ...thresholds, supervisedWeight: parseFloat(e.target.value) })}
              className="dcare-input font-mono"
            />
          </div>

          <div>
            <label className="block text-slate-400 mb-1 font-semibold">Cross-Document Inconsistency Weight</label>
            <input
              type="number"
              step="0.05"
              value={thresholds.mismatchWeight}
              onChange={(e) => setThresholds({ ...thresholds, mismatchWeight: parseFloat(e.target.value) })}
              className="dcare-input font-mono"
            />
          </div>

          <div>
            <label className="block text-slate-400 mb-1 font-semibold">Isolation Forest Anomaly Weight</label>
            <input
              type="number"
              step="0.05"
              value={thresholds.anomalyWeight}
              onChange={(e) => setThresholds({ ...thresholds, anomalyWeight: parseFloat(e.target.value) })}
              className="dcare-input font-mono"
            />
          </div>
        </div>
      </div>
    </div>
  );
};
