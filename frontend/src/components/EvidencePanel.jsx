import React from 'react';
import { AlertOctagon, FileWarning, AlertTriangle, ShieldCheck } from 'lucide-react';

export const EvidencePanel = ({ evidence = [] }) => {
  if (!evidence || evidence.length === 0) {
    return (
      <div className="dcare-card p-6 text-center text-slate-400 text-sm">
        <ShieldCheck size={28} className="mx-auto text-emerald-400 mb-2 opacity-80" />
        No high-risk evidence flags detected. Claim exhibits standard compliance signals.
      </div>
    );
  }

  return (
    <div className="dcare-card">
      <div className="p-4 border-b border-slate-800 flex justify-between items-center">
        <div>
          <h3 className="font-heading font-semibold text-base text-slate-100 flex items-center gap-2">
            <AlertOctagon size={16} className="text-orange-400" />
            Detected Risk Evidence & Provenance
          </h3>
          <p className="text-xs text-slate-400">
            Atomic evidence indicators contributing to the aggregate decision-support score
          </p>
        </div>
        <span className="text-xs font-bold text-orange-400 px-2 py-0.5 rounded bg-orange-950/60 border border-orange-800/40">
          {evidence.length} Evidence Items
        </span>
      </div>

      <div className="p-4 space-y-3">
        {evidence.map((item, idx) => {
          const isCritical = item.severity === 'CRITICAL';
          const isHigh = item.severity === 'HIGH';

          return (
            <div
              key={item.id || idx}
              className={`p-3.5 rounded-lg border flex flex-col gap-2 ${
                isCritical
                  ? 'bg-red-950/20 border-red-800/40'
                  : isHigh
                  ? 'bg-orange-950/20 border-orange-800/40'
                  : 'bg-amber-950/20 border-amber-800/40'
              }`}
            >
              <div className="flex justify-between items-start">
                <div className="flex items-center gap-2">
                  <FileWarning
                    size={15}
                    className={isCritical ? 'text-red-400' : isHigh ? 'text-orange-400' : 'text-amber-400'}
                  />
                  <span className="text-xs font-bold text-slate-100 uppercase tracking-wide">
                    {item.evidenceType || 'RISK_INDICATOR'}
                  </span>
                </div>
                <span className={`text-xs font-semibold px-2 py-0.5 rounded ${
                  isCritical ? 'bg-red-900/50 text-red-300' : isHigh ? 'bg-orange-900/50 text-orange-300' : 'bg-amber-900/50 text-amber-300'
                }`}>
                  {item.severity} Severity
                </span>
              </div>

              <p className="text-xs text-slate-300 leading-relaxed">
                {item.description}
              </p>

              <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 pt-2 border-t border-slate-800/60 text-xs">
                <div>
                  <span className="text-slate-500 block">Source Doc:</span>
                  <span className="font-semibold text-slate-300">{item.sourceDocument || 'Claim Form'}</span>
                </div>
                <div>
                  <span className="text-slate-500 block">Source Field:</span>
                  <span className="font-semibold text-slate-300">{item.sourceField || 'General'}</span>
                </div>
                <div>
                  <span className="text-slate-500 block">Detected:</span>
                  <span className="font-mono font-semibold text-amber-300">{item.detectedValue || '—'}</span>
                </div>
                <div>
                  <span className="text-slate-500 block">Contribution:</span>
                  <span className="font-mono font-semibold text-orange-400">+{item.contributionScore || 0} pts</span>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
