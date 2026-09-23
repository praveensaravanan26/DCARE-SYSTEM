import React from 'react';
import { CheckCircle, AlertTriangle, XCircle, ArrowRight } from 'lucide-react';

export const ConsistencyMatrix = ({ validations = [] }) => {
  if (!validations || validations.length === 0) {
    return (
      <div className="dcare-card p-6 text-center text-slate-400 text-sm">
        No cross-document validations recorded yet. Upload supporting documents and run evaluation.
      </div>
    );
  }

  return (
    <div className="dcare-card overflow-hidden">
      <div className="p-4 border-b border-slate-800 flex justify-between items-center">
        <div>
          <h3 className="font-heading font-semibold text-base text-slate-100">
            Cross-Document Consistency Matrix
          </h3>
          <p className="text-xs text-slate-400">
            Automated verification of dates, amounts, vehicle registrations & duplicate hashes
          </p>
        </div>
        <span className="text-xs font-medium px-2.5 py-1 rounded bg-slate-800 text-slate-300">
          {validations.filter(v => v.status === 'PASSED').length} / {validations.length} Passed
        </span>
      </div>

      <div className="overflow-x-auto">
        <table className="dcare-table">
          <thead>
            <tr>
              <th>Rule & Category</th>
              <th>Status</th>
              <th>Severity</th>
              <th>Source / Target</th>
              <th>Detected Value</th>
              <th>Expected Value</th>
            </tr>
          </thead>
          <tbody>
            {validations.map((val, idx) => {
              const isPassed = val.status === 'PASSED';
              const isWarning = val.status === 'WARNING';
              const isFailed = val.status === 'FAILED';

              return (
                <tr key={val.id || idx}>
                  <td>
                    <div className="font-semibold text-slate-200">{val.ruleName}</div>
                    <div className="text-xs text-slate-400 mt-0.5">{val.description}</div>
                  </td>
                  <td>
                    {isPassed && (
                      <span className="badge badge-low">
                        <CheckCircle size={12} /> Passed
                      </span>
                    )}
                    {isWarning && (
                      <span className="badge badge-medium">
                        <AlertTriangle size={12} /> Warning
                      </span>
                    )}
                    {isFailed && (
                      <span className="badge badge-critical">
                        <XCircle size={12} /> Mismatch
                      </span>
                    )}
                  </td>
                  <td>
                    <span className={`text-xs font-semibold ${
                      val.severity === 'CRITICAL' ? 'text-red-400' :
                      val.severity === 'HIGH' ? 'text-orange-400' :
                      val.severity === 'MEDIUM' ? 'text-amber-400' : 'text-emerald-400'
                    }`}>
                      {val.severity}
                    </span>
                  </td>
                  <td>
                    <div className="text-xs font-mono text-slate-300 flex items-center gap-1">
                      <span>{val.sourceDocumentType || 'DOC'}</span>
                      <ArrowRight size={10} className="text-slate-500" />
                      <span>{val.targetDocumentType || 'CLAIM'}</span>
                    </div>
                  </td>
                  <td className="font-mono text-xs text-amber-300">
                    {val.detectedValue || '—'}
                  </td>
                  <td className="font-mono text-xs text-slate-300">
                    {val.expectedValue || '—'}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};
