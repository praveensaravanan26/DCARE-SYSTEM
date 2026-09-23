import React, { useState } from 'react';
import { api } from '../api/client';
import { useNavigate } from 'react-router-dom';
import { Shield, ArrowRight, FileText, CheckCircle2, AlertCircle, Info, UploadCloud } from 'lucide-react';

export const ClaimCreatePage = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    policyNumber: 'POL-2026-8841',
    claimType: 'ACCIDENT',
    incidentDate: new Date().toISOString().split('T')[0],
    incidentLocation: '',
    claimedAmount: '',
    vehicleNumber: '',
    garageName: '',
    invoiceNumber: '',
    priority: 'NORMAL',
    description: ''
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!formData.policyNumber || !formData.vehicleNumber || !formData.claimedAmount || !formData.incidentLocation) {
      setError('Please fill in all mandatory fields marked with an asterisk (*).');
      return;
    }

    setLoading(true);

    try {
      const payload = {
        ...formData,
        claimedAmount: parseFloat(formData.claimedAmount)
      };
      const created = await api.createClaim(payload);
      navigate(`/claims/${created.id}/documents`);
    } catch (err) {
      setError(err.message || 'Failed to register claim.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-wrapper space-y-6 max-w-4xl">
      {/* Header */}
      <div className="pb-4 border-b border-slate-800">
        <h1 className="text-2xl font-bold text-white tracking-tight font-heading flex items-center gap-2">
          <FileText size={24} className="text-blue-400" />
          File New Insurance Claim
        </h1>
        <p className="text-xs text-slate-400 mt-1">
          Initiate claim registration. After registration, supporting repair invoices, FIRs, and damage estimates will be ingested for automated AI OCR cross-validation.
        </p>
      </div>

      {/* Information Banner */}
      <div className="p-4 rounded-xl bg-blue-950/30 border border-blue-800/40 text-xs text-blue-200 flex items-start gap-3">
        <Info size={18} className="text-blue-400 shrink-0 mt-0.5" />
        <div className="space-y-1">
          <div className="font-bold text-blue-300">Claim Ingestion & Document Verification Process:</div>
          <div className="text-slate-300 leading-relaxed text-[11px]">
            Step 1: Enter basic incident details below &bull; Step 2: Ingest PDF / Image documents (Repair Invoices, Police FIR, Damage Photos) &bull; Step 3: DCARE AI performs automated OCR, cross-field entity consistency check, and fraud risk score evaluation.
          </div>
        </div>
      </div>

      {error && (
        <div className="p-3.5 rounded-xl bg-red-950/50 border border-red-800/50 text-red-200 text-xs font-medium flex items-center gap-2">
          <AlertCircle size={16} className="text-red-400 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Claim Form */}
      <form onSubmit={handleSubmit} className="dcare-card p-6 space-y-5">
        <div className="text-sm font-bold text-white border-b border-slate-800/80 pb-2">
          1. Policy & Incident Information
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Policy Number *
            </label>
            <input
              type="text"
              required
              value={formData.policyNumber}
              onChange={(e) => setFormData({ ...formData, policyNumber: e.target.value })}
              placeholder="e.g. POL-2026-8841"
              className="dcare-input font-mono"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Vehicle Registration Number *
            </label>
            <input
              type="text"
              required
              value={formData.vehicleNumber}
              onChange={(e) => setFormData({ ...formData, vehicleNumber: e.target.value })}
              placeholder="e.g. MH-02-CB-4091"
              className="dcare-input font-mono"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Claim Type *
            </label>
            <select
              value={formData.claimType}
              onChange={(e) => setFormData({ ...formData, claimType: e.target.value })}
              className="dcare-select"
            >
              <option value="ACCIDENT">Collision / Road Accident</option>
              <option value="THEFT">Theft / Partial Component Theft</option>
              <option value="THIRD_PARTY">Third Party Property / Liability</option>
              <option value="WINDSHIELD">Windshield / Glass Damage</option>
              <option value="NATURAL_DISASTER">Natural Calamity / Flood / Fire</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Claim Priority
            </label>
            <select
              value={formData.priority}
              onChange={(e) => setFormData({ ...formData, priority: e.target.value })}
              className="dcare-select"
            >
              <option value="LOW">Low (Standard Triage)</option>
              <option value="NORMAL">Normal</option>
              <option value="HIGH">High Priority</option>
              <option value="URGENT">Urgent (Expedited Review)</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Incident Date *
            </label>
            <input
              type="date"
              required
              value={formData.incidentDate}
              onChange={(e) => setFormData({ ...formData, incidentDate: e.target.value })}
              className="dcare-input font-mono"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Claimed Amount ($ USD) *
            </label>
            <input
              type="number"
              step="0.01"
              min="1"
              required
              value={formData.claimedAmount}
              onChange={(e) => setFormData({ ...formData, claimedAmount: e.target.value })}
              placeholder="e.g. 18500.00"
              className="dcare-input font-mono"
            />
          </div>

          <div className="sm:col-span-2">
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Incident Location *
            </label>
            <input
              type="text"
              required
              value={formData.incidentLocation}
              onChange={(e) => setFormData({ ...formData, incidentLocation: e.target.value })}
              placeholder="e.g. Outer Ring Road, Mumbai, Maharashtra"
              className="dcare-input"
            />
          </div>
        </div>

        <div className="text-sm font-bold text-white border-b border-slate-800/80 pb-2 pt-3">
          2. Repair & Supporting Reference (Optional)
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Authorized Repair Center / Garage Name
            </label>
            <input
              type="text"
              value={formData.garageName}
              onChange={(e) => setFormData({ ...formData, garageName: e.target.value })}
              placeholder="e.g. Apex Auto Body Care Center"
              className="dcare-input"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Repair Invoice / Estimate Reference No.
            </label>
            <input
              type="text"
              value={formData.invoiceNumber}
              onChange={(e) => setFormData({ ...formData, invoiceNumber: e.target.value })}
              placeholder="e.g. INV-2026-891"
              className="dcare-input font-mono"
            />
          </div>

          <div className="sm:col-span-2">
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Accident Narrative & Damage Details
            </label>
            <textarea
              rows={3}
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="Describe the incident sequence, affected vehicle sections, and road conditions..."
              className="dcare-input resize-none"
            />
          </div>
        </div>

        <div className="pt-4 border-t border-slate-800 flex justify-between items-center">
          <div className="text-[11px] text-slate-500">
            * Mandatory fields required for underwriting registration
          </div>
          <div className="flex gap-3">
            <button
              type="button"
              onClick={() => navigate('/claims')}
              className="dcare-btn-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="dcare-btn-primary"
            >
              {loading ? 'Creating...' : 'Proceed to Ingest Documents'}
              <ArrowRight size={14} />
            </button>
          </div>
        </div>
      </form>
    </div>
  );
};
