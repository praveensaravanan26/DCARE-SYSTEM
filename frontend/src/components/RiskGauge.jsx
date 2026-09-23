import React from 'react';

export const RiskGauge = ({ score = 0, level = 'LOW', recommendation = '' }) => {
  const numScore = Math.max(0, Math.min(100, Number(score) || 0));
  
  const getColor = (lvl) => {
    switch (lvl?.toUpperCase()) {
      case 'CRITICAL': return '#ef4444';
      case 'HIGH': return '#f97316';
      case 'MEDIUM': return '#f59e0b';
      default: return '#10b981';
    }
  };

  const color = getColor(level);
  const circumference = 2 * Math.PI * 48;
  const strokeDashoffset = circumference - (numScore / 100) * circumference;

  return (
    <div className="dcare-card flex flex-col items-center justify-center p-6 text-center">
      <h3 className="text-sm font-semibold tracking-wider text-slate-400 uppercase mb-4">
        Evidence-Aware Risk Score
      </h3>
      
      <div className="relative flex items-center justify-center w-36 h-36">
        <svg className="w-full h-full transform -rotate-90" viewBox="0 0 120 120">
          <circle
            cx="60"
            cy="60"
            r="48"
            stroke="#1e293b"
            strokeWidth="10"
            fill="transparent"
          />
          <circle
            cx="60"
            cy="60"
            r="48"
            stroke={color}
            strokeWidth="10"
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
            strokeLinecap="round"
            fill="transparent"
            style={{ transition: 'stroke-dashoffset 0.8s ease-in-out' }}
          />
        </svg>

        <div className="absolute flex flex-col items-center justify-center text-center">
          <span className="text-3xl font-extrabold text-white font-heading">
            {numScore.toFixed(1)}
          </span>
          <span className="text-xs font-semibold uppercase tracking-wider" style={{ color }}>
            {level}
          </span>
        </div>
      </div>

      <div className="mt-4 pt-3 border-t border-slate-800 w-full text-center">
        <div className="text-xs text-slate-400 font-medium">Recommendation:</div>
        <div className="text-xs font-semibold text-slate-200 mt-0.5">
          {recommendation || 'Standard Processing Protocol'}
        </div>
      </div>
    </div>
  );
};
