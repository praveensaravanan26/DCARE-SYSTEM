import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';
import {
  Upload,
  FileText,
  CheckCircle,
  Clock,
  Edit2,
  RefreshCw,
  ExternalLink,
  ShieldAlert,
  ArrowRight
} from 'lucide-react';

export const ClaimDocumentsPage = () => {
  const { id } = useParams();
  const { canReview } = useAuth();

  const [claim, setClaim] = useState(null);
  const [documents, setDocuments] = useState([]);
  const [extractedFields, setExtractedFields] = useState([]);
  const [loading, setLoading] = useState(true);

  // Upload State
  const [selectedFile, setSelectedFile] = useState(null);
  const [docType, setDocType] = useState('CLAIM_FORM');
  const [uploading, setUploading] = useState(false);
  const [uploadError, setUploadError] = useState('');

  // Correction Modal State
  const [editingField, setEditingField] = useState(null);
  const [correctedValue, setCorrectedValue] = useState('');
  const [correcting, setCorrecting] = useState(false);

  useEffect(() => {
    loadAll();
  }, [id]);

  const loadAll = async () => {
    setLoading(true);
    try {
      const [claimData, docsData, fieldsData] = await Promise.all([
        api.getClaimById(id),
        api.getDocumentsForClaim(id),
        api.getExtractedFields(id)
      ]);
      setClaim(claimData);
      setDocuments(docsData || []);
      setExtractedFields(fieldsData || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleFileUpload = async (e) => {
    e.preventDefault();
    if (!selectedFile) return;
    setUploadError('');
    setUploading(true);

    try {
      await api.uploadDocument(id, docType, selectedFile);
      setSelectedFile(null);
      await loadAll();
    } catch (err) {
      setUploadError(err.message || 'File upload failed.');
    } finally {
      setUploading(false);
    }
  };

  const handleFieldCorrection = async (e) => {
    e.preventDefault();
    if (!editingField || !correctedValue.trim()) return;
    setCorrecting(true);

    try {
      await api.correctField(id, editingField.fieldName, correctedValue);
      setEditingField(null);
      setCorrectedValue('');
      await loadAll();
    } catch (err) {
      alert('Correction failed: ' + err.message);
    } finally {
      setCorrecting(false);
    }
  };

  return (
    <div className="page-wrapper space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight font-heading">
            Document Intelligence & OCR Workspace
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Claim #{claim?.claimNumber} • Ingest documents, trigger OCR, verify provenance, and inspect extracted entities.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Link to={`/claims/${id}/risk`} className="dcare-btn-primary">
            <ShieldAlert size={14} />
            <span>Proceed to Risk Hub</span>
            <ArrowRight size={14} />
          </Link>
        </div>
      </div>

      {/* Upload Box */}
      <div className="dcare-card p-6">
        <h3 className="font-heading font-semibold text-base text-white mb-2">
          Upload Supporting Insurance Document
        </h3>
        <p className="text-xs text-slate-400 mb-4">
          Supported formats: PDF, PNG, JPG, JPEG (Max 25MB). Server computes SHA-256 cryptographic hash and extracts structured entities.
        </p>

        {uploadError && (
          <div className="mb-4 p-3 rounded-lg bg-red-950/40 border border-red-800 text-red-300 text-xs">
            {uploadError}
          </div>
        )}

        <form onSubmit={handleFileUpload} className="grid grid-cols-1 sm:grid-cols-3 gap-4 items-end">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Document Classification Type *
            </label>
            <select
              value={docType}
              onChange={(e) => setDocType(e.target.value)}
              className="dcare-select"
            >
              <option value="CLAIM_FORM">Claim Notification Form</option>
              <option value="ACCIDENT_REPORT">Police / Accident FIR Report</option>
              <option value="REPAIR_INVOICE">Repair & Parts Tax Invoice</option>
              <option value="POLICY_DOCUMENT">Insurance Policy Schedule</option>
              <option value="REPAIR_ESTIMATE">Repair Estimate / Quotation</option>
              <option value="DRIVING_LICENSE">Driving License (DL)</option>
              <option value="VEHICLE_RC">Vehicle Registration (RC)</option>
              <option value="OTHER">Other Supporting Document</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Select File *
            </label>
            <input
              type="file"
              required
              accept=".pdf,.png,.jpg,.jpeg"
              onChange={(e) => setSelectedFile(e.target.files[0])}
              className="dcare-input text-xs"
            />
          </div>

          <button
            type="submit"
            disabled={uploading || !selectedFile}
            className="dcare-btn-primary justify-center py-2.5"
          >
            <Upload size={14} className={uploading ? 'animate-spin' : ''} />
            <span>{uploading ? 'Uploading & Processing OCR...' : 'Upload & Process OCR'}</span>
          </button>
        </form>
      </div>

      {/* Uploaded Documents List */}
      <div className="dcare-card overflow-hidden">
        <div className="p-4 border-b border-slate-800 flex justify-between items-center">
          <h3 className="font-heading font-semibold text-base text-white">
            Ingested Documents ({documents.length})
          </h3>
          <button onClick={loadAll} className="text-xs text-slate-400 hover:text-slate-200 flex items-center gap-1">
            <RefreshCw size={12} /> Refresh
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="dcare-table">
            <thead>
              <tr>
                <th>Document Type</th>
                <th>Original Filename</th>
                <th>File Hash (SHA-256)</th>
                <th>Size</th>
                <th>OCR Status</th>
                <th>Uploaded At</th>
              </tr>
            </thead>
            <tbody>
              {documents.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center text-slate-500 py-8">
                    No documents uploaded yet. Upload claim form, police FIR, or invoice above.
                  </td>
                </tr>
              ) : (
                documents.map((d) => (
                  <tr key={d.id}>
                    <td className="font-semibold text-slate-200">
                      {d.documentType}
                    </td>
                    <td className="text-slate-300 font-mono text-xs">
                      {d.originalFilename}
                    </td>
                    <td className="font-mono text-xs text-slate-400 truncate max-w-[140px]" title={d.fileHashSha256}>
                      {d.fileHashSha256?.substring(0, 16)}...
                    </td>
                    <td className="text-xs text-slate-400">
                      {(Number(d.fileSize) / 1024).toFixed(1)} KB
                    </td>
                    <td>
                      <span className={`badge ${d.ocrStatus === 'COMPLETED' ? 'badge-low' : 'badge-medium'}`}>
                        {d.ocrStatus === 'COMPLETED' ? <CheckCircle size={12} /> : <Clock size={12} />}
                        {d.ocrStatus}
                      </span>
                    </td>
                    <td className="text-xs text-slate-400">
                      {new Date(d.createdAt).toLocaleDateString()}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Extracted Structured Fields Table */}
      <div className="dcare-card overflow-hidden">
        <div className="p-4 border-b border-slate-800 flex justify-between items-center">
          <div>
            <h3 className="font-heading font-semibold text-base text-white">
              Normalized Structured Entities & Provenance
            </h3>
            <p className="text-xs text-slate-400">
              Fields parsed via regex, heuristic rules, and OCR intelligence. Authorized reviewers may edit values.
            </p>
          </div>
          <span className="text-xs px-2.5 py-1 rounded bg-slate-800 text-slate-300 font-mono font-semibold">
            {extractedFields.length} Extracted Fields
          </span>
        </div>

        <div className="overflow-x-auto">
          <table className="dcare-table">
            <thead>
              <tr>
                <th>Field Name</th>
                <th>Normalized Value</th>
                <th>Confidence</th>
                <th>Provenance Source</th>
                <th>Verified</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {extractedFields.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center text-slate-500 py-8">
                    No fields extracted yet. Upload files to extract structured entities.
                  </td>
                </tr>
              ) : (
                extractedFields.map((f) => (
                  <tr key={f.id}>
                    <td className="font-semibold text-slate-200">
                      {f.fieldName}
                    </td>
                    <td className="font-mono text-xs font-bold text-amber-300">
                      {f.normalizedValue}
                    </td>
                    <td className="text-xs text-slate-300 font-mono">
                      {(Number(f.confidenceScore || 1.0) * 100).toFixed(0)}%
                    </td>
                    <td>
                      <span className={`badge ${
                        f.provenance === 'MANUAL_CORRECTION' ? 'badge-high' :
                        f.provenance === 'SYSTEM_INFERENCE' ? 'badge-medium' : 'badge-info'
                      }`}>
                        {f.provenance}
                      </span>
                    </td>
                    <td>
                      <span className={`text-xs font-semibold ${f.isVerified ? 'text-emerald-400' : 'text-slate-500'}`}>
                        {f.isVerified ? 'Yes' : 'Auto'}
                      </span>
                    </td>
                    <td>
                      <button
                        onClick={() => {
                          setEditingField(f);
                          setCorrectedValue(f.normalizedValue);
                        }}
                        className="p-1.5 rounded bg-slate-800 hover:bg-slate-700 text-slate-300 transition"
                        title="Correct Field Value"
                      >
                        <Edit2 size={13} />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Field Correction Modal */}
      {editingField && (
        <div className="modal-overlay">
          <div className="modal-card p-6">
            <h3 className="font-heading font-bold text-lg text-white mb-2">
              Manual Field Correction
            </h3>
            <p className="text-xs text-slate-400 mb-4">
              Update extracted field value. The provenance tag will be updated to <span className="font-mono text-orange-400">MANUAL_CORRECTION</span> and logged in the audit trail.
            </p>

            <form onSubmit={handleFieldCorrection} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Field Key
                </label>
                <input
                  type="text"
                  disabled
                  value={editingField.fieldName}
                  className="dcare-input bg-slate-900 opacity-70"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Corrected Value *
                </label>
                <input
                  type="text"
                  required
                  value={correctedValue}
                  onChange={(e) => setCorrectedValue(e.target.value)}
                  className="dcare-input font-mono"
                />
              </div>

              <div className="pt-4 border-t border-slate-800 flex justify-end gap-3">
                <button
                  type="button"
                  onClick={() => setEditingField(null)}
                  className="dcare-btn-secondary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={correcting}
                  className="dcare-btn-primary"
                >
                  {correcting ? 'Saving...' : 'Save & Record in Audit Log'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
