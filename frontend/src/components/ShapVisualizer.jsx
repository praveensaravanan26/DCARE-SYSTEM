import React from 'react';
import { TrendingUp, TrendingDown, HelpCircle } from 'lucide-react';

export const ShapVisualizer = ({ shapSummary }) => {
  if (!shapSummary || !shapSummary.top_contributors) {
    return (
      <div className="dcare-card p-6 text-center text-slate-400 text-sm">
        SHAP Explainability feature weights are computed upon risk evaluation.
      </div>
    );
  }

  const contributors = shapSummary.top_contributors || [];

  return (
    <div className="dcare-card">
      <div className="p-4 border-b border-slate-800 flex justify-between items-center">
        <div>
          <h3 className="font-heading font-semibold text-base text-slate-100 flex items-center gap-2">
            Explainable AI (SHAP Feature Attribution)
          </h3>
          <p className="text-xs text-slate-400">
            Shapley Additive exPlanations for supervised model predictions (Contribution to Risk)
          </p>
        </div>
        <div className="flex items-center gap-3 text-xs">
          <span className="flex items-center gap-1 text-orange-400 font-medium">
            <TrendingUp size={13} /> Increases Risk
          </span>
          <span className="flex items-center gap-1 text-emerald-400 font-medium">
            <TrendingDown size={13} /> Lowers Risk
          </span>
        </div>
      </div>

      <div className="p-4 space-y-4">
        {contributors.map((c, idx) => {
          const isPositive = c.shap_value > 0;
          const pct = Math.min(100, Math.round(c.abs_shap * 100 * 2.5));

          return (
            <div key={idx} className="bg-slate-900/60 p-3 rounded-lg border border-slate-800">
              <div className="flex justify-between items-center mb-1">
                <span className="text-xs font-semibold text-slate-200">
                  {c.feature_name}
                </span>
                <span className={`text-xs font-mono font-bold ${isPositive ? 'text-orange-400' : 'text-emerald-400'}`}>
                  {isPositive ? '+' : ''}{c.shap_value.toFixed(3)}
                </span>
              </div>

              {/* Bar Graph */}
              <div className="w-full bg-slate-800 h-2 rounded-full overflow-hidden flex">
                <div
                  className={`h-full rounded-full transition-all duration-500 ${isPositive ? 'bg-orange-500' : 'bg-emerald-500'}`}
                  style={{ width: `${Math.max(8, pct)}%` }}
                />
              </div>

              <div className="text-xs text-slate-400 mt-2 flex items-start gap-1.5">
                <span className="text-slate-500 font-mono">Interpretation:</span>
                <span>{c.interpretation}</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
