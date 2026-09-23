import React from 'react';
import { CheckCircle, AlertTriangle, XCircle, Clock, Search, ShieldAlert, FileText, Check } from 'lucide-react';

export const StatusBadge = ({ status, type = 'status' }) => {
  if (type === 'risk') {
    const risk = String(status || 'LOW').toUpperCase();
    const config = {
      LOW: { label: 'Low Risk', class: 'badge-low', icon: CheckCircle },
      MEDIUM: { label: 'Medium Risk', class: 'badge-medium', icon: AlertTriangle },
      HIGH: { label: 'High Risk (Scrutiny)', class: 'badge-high', icon: ShieldAlert },
      CRITICAL: { label: 'Critical Alert', class: 'badge-critical', icon: AlertTriangle }
    }[risk] || { label: risk, class: 'badge-info', icon: AlertTriangle };

    const Icon = config.icon;
    return (
      <span className={`badge ${config.class}`}>
        <Icon size={13} />
        {config.label}
      </span>
    );
  }

  const s = String(status || 'DRAFT').toUpperCase();
  const config = {
    DRAFT: { label: 'Draft', class: 'badge-info', icon: FileText },
    SUBMITTED: { label: 'Submitted', class: 'badge-info', icon: Clock },
    DOCUMENT_PROCESSING: { label: 'OCR Processing', class: 'badge-medium', icon: Clock },
    VALIDATION_REQUIRED: { label: 'Verification Needed', class: 'badge-medium', icon: AlertTriangle },
    RISK_ASSESSED: { label: 'Risk Evaluated', class: 'badge-low', icon: CheckCircle },
    ENHANCED_REVIEW: { label: 'Enhanced Review', class: 'badge-high', icon: Search },
    APPROVED: { label: 'Approved', class: 'badge-low', icon: Check },
    REJECTED: { label: 'Declined', class: 'badge-critical', icon: XCircle },
    INVESTIGATION: { label: 'Investigation', class: 'badge-high', icon: Search },
    CLOSED: { label: 'Closed', class: 'badge-info', icon: CheckCircle }
  }[s] || { label: s, class: 'badge-info', icon: Clock };

  const Icon = config.icon;
  return (
    <span className={`badge ${config.class}`}>
      <Icon size={13} />
      {config.label}
    </span>
  );
};
