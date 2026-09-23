import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import { StatusBadge } from '../components/StatusBadge';
import { Search, Filter, PlusCircle, ArrowRight, RefreshCw } from 'lucide-react';

export const ClaimsListPage = () => {
  const { isCustomer } = useAuth();
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [riskFilter, setRiskFilter] = useState('ALL');

  useEffect(() => {
    loadClaims();
  }, []);

  const loadClaims = async () => {
    setLoading(true);
    try {
      const data = await api.getClaims();
      setClaims(data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const filteredClaims = claims.filter((c) => {
    const matchesSearch =
      c.claimNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.policyNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.vehicleNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.claimType?.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesStatus = statusFilter === 'ALL' || c.status === statusFilter;
    const matchesRisk = riskFilter === 'ALL' || c.riskLevel === riskFilter;

    return matchesSearch && matchesStatus && matchesRisk;
  });

  return (
    <div className="page-wrapper space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">
            {isCustomer ? 'My Insurance Claims' : 'Claims Intelligence Directory'}
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Search, filter, inspect extracted entities, and review automated risk scores.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button onClick={loadClaims} className="dcare-btn-secondary" title="Refresh Claims">
            <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
            <span>Refresh</span>
          </button>
          <Link to="/claims/new" className="dcare-btn-primary">
            <PlusCircle size={15} />
            <span>New Claim</span>
          </Link>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="dcare-card p-4 flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search size={16} className="absolute left-3 top-3 text-slate-500" />
          <input
            type="text"
            placeholder="Search claim, policy, vehicle..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="dcare-input pl-9 text-xs"
          />
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <div className="flex items-center gap-2 text-xs text-slate-400">
            <Filter size={14} />
            <span>Status:</span>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="dcare-select text-xs py-1.5"
            >
              <option value="ALL">All Statuses</option>
              <option value="DRAFT">Draft</option>
              <option value="SUBMITTED">Submitted</option>
              <option value="DOCUMENT_PROCESSING">OCR Processing</option>
              <option value="VALIDATION_REQUIRED">Verification Needed</option>
              <option value="RISK_ASSESSED">Risk Evaluated</option>
              <option value="ENHANCED_REVIEW">Enhanced Review</option>
              <option value="APPROVED">Approved</option>
              <option value="REJECTED">Rejected</option>
              <option value="INVESTIGATION">Investigation</option>
            </select>
          </div>

          <div className="flex items-center gap-2 text-xs text-slate-400">
            <span>Risk Level:</span>
            <select
              value={riskFilter}
              onChange={(e) => setRiskFilter(e.target.value)}
              className="dcare-select text-xs py-1.5"
            >
              <option value="ALL">All Risk Levels</option>
              <option value="LOW">Low Risk</option>
              <option value="MEDIUM">Medium Risk</option>
              <option value="HIGH">High Risk</option>
              <option value="CRITICAL">Critical Alert</option>
            </select>
          </div>
        </div>
      </div>

      {/* Claims Table */}
      <div className="dcare-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="dcare-table">
            <thead>
              <tr>
                <th>Claim #</th>
                <th>Policy & Vehicle</th>
                <th>Claim Type</th>
                <th>Incident Date</th>
                <th>Claimed Amount</th>
                <th>Status</th>
                <th>Risk Evaluation</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {filteredClaims.length === 0 ? (
                <tr>
                  <td colSpan={8} className="text-center text-slate-500 py-12">
                    No insurance claims matched your filter criteria.
                  </td>
                </tr>
              ) : (
                filteredClaims.map((c) => (
                  <tr key={c.id}>
                    <td className="font-mono font-bold text-blue-400">
                      {c.claimNumber}
                    </td>
                    <td>
                      <div className="font-semibold text-slate-200">{c.policyNumber}</div>
                      <div className="text-xs text-slate-400 font-mono">{c.vehicleNumber}</div>
                    </td>
                    <td className="font-medium text-slate-300">
                      {c.claimType}
                    </td>
                    <td className="text-slate-300 text-xs font-mono">
                      {c.incidentDate}
                    </td>
                    <td className="font-semibold text-slate-200 font-mono">
                      ${Number(c.claimedAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}
                    </td>
                    <td>
                      <StatusBadge status={c.status} />
                    </td>
                    <td>
                      <div className="flex items-center gap-2">
                        <StatusBadge status={c.riskLevel} type="risk" />
                        {c.riskScore > 0 && (
                          <span className="text-xs font-mono font-bold text-slate-300">
                            {Number(c.riskScore).toFixed(1)}
                          </span>
                        )}
                      </div>
                    </td>
                    <td>
                      <Link
                        to={`/claims/${c.id}`}
                        className="dcare-btn-secondary text-xs py-1 px-3"
                      >
                        Inspect <ArrowRight size={12} />
                      </Link>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
