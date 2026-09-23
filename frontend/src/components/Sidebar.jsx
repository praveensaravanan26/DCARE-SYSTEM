import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  FileSpreadsheet,
  PlusCircle,
  ShieldAlert,
  BarChart3,
  ScrollText,
  ShieldCheck,
  FolderOpen
} from 'lucide-react';

export const Sidebar = () => {
  const { user, isCustomer, canReview, isAdmin } = useAuth();

  const navItemClass = ({ isActive }) =>
    `flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-xs font-semibold transition ${
      isActive
        ? 'bg-blue-600 text-white shadow-lg shadow-blue-600/30'
        : 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/60'
    }`;

  return (
    <aside className="w-64 bg-slate-900 border-r border-slate-800 flex flex-col justify-between shrink-0 p-4">
      <div className="space-y-6">
        <div>
          <div className="text-[11px] font-bold uppercase tracking-wider text-slate-500 px-3 mb-2">
            Navigation
          </div>
          <nav className="space-y-1">
            <NavLink to="/dashboard" className={navItemClass}>
              <LayoutDashboard size={16} />
              <span>Dashboard</span>
            </NavLink>

            <NavLink to="/claims" className={navItemClass}>
              <FileSpreadsheet size={16} />
              <span>{isCustomer ? 'My Claims' : 'All Claims'}</span>
            </NavLink>

            <NavLink to="/claims/new" className={navItemClass}>
              <PlusCircle size={16} />
              <span>Submit Claim</span>
            </NavLink>

            {canReview && (
              <NavLink to="/investigations" className={navItemClass}>
                <ShieldAlert size={16} />
                <span>Risk & Investigations</span>
              </NavLink>
            )}

            <NavLink to="/analytics" className={navItemClass}>
              <BarChart3 size={16} />
              <span>Analytics & Metrics</span>
            </NavLink>

            {canReview && (
              <NavLink to="/audit-logs" className={navItemClass}>
                <ScrollText size={16} />
                <span>Audit Trail</span>
              </NavLink>
            )}

            {isAdmin && (
              <NavLink to="/admin" className={navItemClass}>
                <ShieldCheck size={16} />
                <span>Admin Settings</span>
              </NavLink>
            )}
          </nav>
        </div>
      </div>

      <div className="pt-4 border-t border-slate-800">
        <div className="bg-slate-950 p-3 rounded-xl border border-slate-800/60">
          <div className="text-[11px] text-slate-400">Current Authorization:</div>
          <div className="text-xs font-bold text-slate-200 mt-0.5">
            {user?.role?.replace('ROLE_', '')}
          </div>
          <div className="text-[10px] text-emerald-400 font-mono mt-1 flex items-center gap-1">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
            PostgreSQL & AI Online
          </div>
        </div>
      </div>
    </aside>
  );
};
