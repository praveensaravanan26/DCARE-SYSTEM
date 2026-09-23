import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Shield, Lock, Mail, User, Phone, ArrowRight, CheckCircle2, AlertCircle, ShieldAlert } from 'lucide-react';

export const LoginPage = () => {
  const navigate = useNavigate();
  const { login, register } = useAuth();

  const [isRegister, setIsRegister] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [role, setRole] = useState('ROLE_CUSTOMER');

  const [error, setError] = useState('');
  const [successNotice, setSuccessNotice] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccessNotice('');
    setLoading(true);

    try {
      if (isRegister) {
        const payload = {
          email: email.trim(),
          password,
          fullName: fullName.trim(),
          phoneNumber: phoneNumber.trim(),
          role
        };
        const res = await register(payload);
        if (res.isApproved === false || !res.token) {
          setSuccessNotice(
            res.message ||
            'Staff application received! Your account is queued for System Administrator approval. You will be granted access once approved.'
          );
          setIsRegister(false);
          setPassword('');
        } else {
          navigate('/dashboard');
        }
      } else {
        await login(email.trim(), password);
        navigate('/dashboard');
      }
    } catch (err) {
      setError(err.message || 'Authentication failed. Please verify credentials.');
    } finally {
      setLoading(false);
    }
  };

  const isStaffRole = role === 'ROLE_CLAIM_OFFICER' || role === 'ROLE_FRAUD_INVESTIGATOR';

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col justify-center py-12 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
      {/* Background Decorative Gradients */}
      <div className="absolute top-0 left-1/4 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-0 right-1/4 w-96 h-96 bg-indigo-600/10 rounded-full blur-3xl pointer-events-none" />

      <div className="sm:mx-auto sm:w-full sm:max-w-md text-center z-10 mb-8">
        <div className="w-14 h-14 mx-auto rounded-2xl bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center shadow-xl shadow-blue-600/30 mb-4">
          <Shield size={28} className="text-white" />
        </div>
        <h1 className="text-3xl font-heading font-extrabold text-white tracking-tight">
          DCARE
        </h1>
        <p className="mt-1 text-xs text-slate-400 font-medium max-w-sm mx-auto">
          Document-Centric Automated Risk Evaluation & Fraud Intelligence Platform
        </p>
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-md z-10">
        <div className="bg-slate-900/95 border border-slate-800 rounded-2xl p-8 shadow-2xl backdrop-blur-md">
          {/* Tabs */}
          <div className="flex border-b border-slate-800 mb-6">
            <button
              type="button"
              onClick={() => {
                setIsRegister(false);
                setError('');
              }}
              className={`pb-3 text-xs font-bold uppercase tracking-wider border-b-2 mr-6 transition ${
                !isRegister
                  ? 'border-blue-500 text-blue-400'
                  : 'border-transparent text-slate-400 hover:text-slate-200'
              }`}
            >
              Sign In
            </button>
            <button
              type="button"
              onClick={() => {
                setIsRegister(true);
                setError('');
                setSuccessNotice('');
              }}
              className={`pb-3 text-xs font-bold uppercase tracking-wider border-b-2 transition ${
                isRegister
                  ? 'border-blue-500 text-blue-400'
                  : 'border-transparent text-slate-400 hover:text-slate-200'
              }`}
            >
              Register Account
            </button>
          </div>

          {/* Success Notice */}
          {successNotice && (
            <div className="mb-5 p-3.5 rounded-xl bg-blue-950/60 border border-blue-800/50 text-blue-200 text-xs flex items-start gap-2.5">
              <CheckCircle2 size={16} className="text-blue-400 shrink-0 mt-0.5" />
              <div>
                <div className="font-bold text-blue-300">Registration Submitted</div>
                <div className="mt-0.5 leading-relaxed">{successNotice}</div>
              </div>
            </div>
          )}

          {/* Error Message */}
          {error && (
            <div className="mb-5 p-3.5 rounded-xl bg-red-950/50 border border-red-800/50 text-red-200 text-xs flex items-start gap-2.5">
              <AlertCircle size={16} className="text-red-400 shrink-0 mt-0.5" />
              <div>
                <div className="font-bold text-red-300">Authentication Error</div>
                <div className="mt-0.5 leading-relaxed">{error}</div>
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {isRegister && (
              <>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">
                    Full Name *
                  </label>
                  <div className="relative">
                    <User size={15} className="absolute left-3 top-3 text-slate-500" />
                    <input
                      type="text"
                      required
                      value={fullName}
                      onChange={(e) => setFullName(e.target.value)}
                      placeholder="e.g. Johnathan Miller"
                      className="dcare-input pl-9"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">
                    Account Role Type *
                  </label>
                  <select
                    value={role}
                    onChange={(e) => setRole(e.target.value)}
                    className="dcare-select"
                  >
                    <option value="ROLE_CUSTOMER">Policyholder (Customer)</option>
                    <option value="ROLE_CLAIM_OFFICER">Claim Review Officer (Staff)</option>
                    <option value="ROLE_FRAUD_INVESTIGATOR">Fraud Risk Investigator (SIU)</option>
                  </select>
                </div>

                {isStaffRole && (
                  <div className="p-3 rounded-lg bg-amber-950/40 border border-amber-800/40 text-amber-300 text-[11px] leading-relaxed flex items-start gap-2">
                    <ShieldAlert size={14} className="text-amber-400 shrink-0 mt-0.5" />
                    <span>
                      <strong>Enterprise Policy:</strong> Staff accounts ({role === 'ROLE_CLAIM_OFFICER' ? 'Claim Officer' : 'Fraud Investigator'}) require mandatory administrator identity verification before access is activated.
                    </span>
                  </div>
                )}

                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">
                    Phone Number
                  </label>
                  <div className="relative">
                    <Phone size={15} className="absolute left-3 top-3 text-slate-500" />
                    <input
                      type="text"
                      value={phoneNumber}
                      onChange={(e) => setPhoneNumber(e.target.value)}
                      placeholder="+1-800-555-0199"
                      className="dcare-input pl-9 font-mono"
                    />
                  </div>
                </div>
              </>
            )}

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                Corporate Email Address *
              </label>
              <div className="relative">
                <Mail size={15} className="absolute left-3 top-3 text-slate-500" />
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="user@dcare.local"
                  className="dcare-input pl-9 font-mono"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                Password *
              </label>
              <div className="relative">
                <Lock size={15} className="absolute left-3 top-3 text-slate-500" />
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="dcare-input pl-9"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full dcare-btn-primary justify-center py-2.5 mt-4 text-sm font-bold shadow-lg shadow-blue-600/30"
            >
              {loading ? 'Processing...' : isRegister ? 'Submit Registration' : 'Sign In'}
              <ArrowRight size={14} />
            </button>
          </form>

          <div className="mt-6 pt-4 border-t border-slate-800 text-center">
            <p className="text-[11px] text-slate-500">
              Protected by Enterprise Role-Based Access Control (RBAC) & Immutable Compliance Audit.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
