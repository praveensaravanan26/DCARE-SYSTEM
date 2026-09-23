import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Shield, User, LogOut } from 'lucide-react';

export const Navbar = () => {
  const { user, logout } = useAuth();

  const getRoleBadge = (role) => {
    switch (role) {
      case 'ROLE_ADMIN':
        return { label: 'System Admin', bg: 'bg-purple-950/80 text-purple-300 border-purple-850' };
      case 'ROLE_CLAIM_OFFICER':
        return { label: 'Claim Officer', bg: 'bg-blue-950/80 text-blue-300 border-blue-850' };
      case 'ROLE_FRAUD_INVESTIGATOR':
        return { label: 'Fraud Investigator', bg: 'bg-orange-950/80 text-orange-300 border-orange-850' };
      default:
        return { label: 'Policyholder', bg: 'bg-emerald-950/80 text-emerald-300 border-emerald-850' };
    }
  };

  const badge = getRoleBadge(user?.role);

  return (
    <header className="bg-slate-900/90 backdrop-blur-md border-b border-slate-800 px-6 py-3.5 flex items-center justify-between sticky top-0 z-30 shadow-md">
      <div className="flex items-center gap-3.5">
        <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center shadow-lg shadow-blue-500/25">
          <Shield size={20} className="text-white" />
        </div>
        <div>
          <div className="font-heading font-bold text-lg text-white tracking-tight flex items-center gap-2">
            DCARE
            <span className="text-[10px] px-2 py-0.5 rounded-full font-mono font-bold bg-blue-950 text-blue-400 border border-blue-800/40">
              Enterprise Suite
            </span>
          </div>
          <div className="text-[11px] text-slate-400 hidden sm:block">
            Document-Centric Automated Risk Evaluation Platform
          </div>
        </div>
      </div>

      <div className="flex items-center gap-4">
        {/* User Identity Info */}
        <div className="flex items-center gap-3">
          <div className="hidden sm:block text-right">
            <div className="text-xs font-bold text-slate-100 flex items-center justify-end gap-2">
              <span>{user?.fullName}</span>
              <span className={`text-[10px] px-2 py-0.5 rounded-full font-semibold border ${badge.bg}`}>
                {badge.label}
              </span>
            </div>
            <div className="text-[11px] text-slate-400 font-mono mt-0.5">{user?.email}</div>
          </div>

          <div className="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-slate-300 font-bold text-xs">
            {user?.fullName?.charAt(0) || 'U'}
          </div>

          <button
            onClick={logout}
            title="Sign Out"
            className="p-2 rounded-lg text-slate-400 hover:text-red-400 hover:bg-red-950/30 transition border border-transparent hover:border-red-900/40"
          >
            <LogOut size={16} />
          </button>
        </div>
      </div>
    </header>
  );
};
