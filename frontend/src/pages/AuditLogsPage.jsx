import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import { ScrollText, Search, RefreshCw, Clock } from 'lucide-react';

export const AuditLogsPage = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterAction, setFilterAction] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadLogs();
  }, []);

  const loadLogs = async () => {
    setLoading(true);
    try {
      const data = await api.getAuditLogs();
      setLogs(data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const filteredLogs = logs.filter((l) => {
    const matchesAction = filterAction === 'ALL' || l.action === filterAction;
    const matchesSearch =
      l.userEmail?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      l.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      l.entityId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      l.entityType?.toLowerCase().includes(searchTerm.toLowerCase());

    return matchesAction && matchesSearch;
  });

  return (
    <div className="page-wrapper space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight font-heading flex items-center gap-2">
            <ScrollText size={24} className="text-blue-400" />
            Immutable System Audit Trail
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Tamper-resistant event ledger capturing logins, OCR processing, field corrections, risk assessments, and reviewer decisions.
          </p>
        </div>

        <button onClick={loadLogs} className="dcare-btn-secondary">
          <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
          <span>Refresh Ledger</span>
        </button>
      </div>

      <div className="dcare-card p-4 flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search size={16} className="absolute left-3 top-3 text-slate-500" />
          <input
            type="text"
            placeholder="Search user, action, description..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="dcare-input pl-9 text-xs"
          />
        </div>

        <div className="flex items-center gap-2 text-xs text-slate-400">
          <span>Action Filter:</span>
          <select
            value={filterAction}
            onChange={(e) => setFilterAction(e.target.value)}
            className="dcare-select text-xs py-1.5"
          >
            <option value="ALL">All Actions</option>
            <option value="LOGIN">LOGIN</option>
            <option value="CLAIM_CREATED">CLAIM_CREATED</option>
            <option value="DOCUMENT_UPLOADED">DOCUMENT_UPLOADED</option>
            <option value="OCR_COMPLETED">OCR_COMPLETED</option>
            <option value="FIELD_CORRECTED">FIELD_CORRECTED</option>
            <option value="RISK_CALCULATED">RISK_CALCULATED</option>
            <option value="DECISION_MADE">DECISION_MADE</option>
          </select>
        </div>
      </div>

      <div className="dcare-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="dcare-table">
            <thead>
              <tr>
                <th>Timestamp</th>
                <th>User / Initiator</th>
                <th>Action</th>
                <th>Target Entity</th>
                <th>Description</th>
              </tr>
            </thead>
            <tbody>
              {filteredLogs.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center text-slate-500 py-12">
                    No audit records match the current filter.
                  </td>
                </tr>
              ) : (
                filteredLogs.map((l) => (
                  <tr key={l.id}>
                    <td className="text-xs text-slate-400 font-mono whitespace-nowrap">
                      {new Date(l.createdAt).toLocaleString()}
                    </td>
                    <td className="text-xs font-semibold text-slate-200">
                      {l.userEmail}
                    </td>
                    <td>
                      <span className="font-mono text-xs font-bold text-blue-400 bg-blue-950/60 px-2 py-0.5 rounded border border-blue-800/40">
                        {l.action}
                      </span>
                    </td>
                    <td className="font-mono text-xs text-slate-400">
                      {l.entityType}
                    </td>
                    <td className="text-xs text-slate-300">
                      {l.description}
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
