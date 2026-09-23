import React, { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../api/client';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    const token = localStorage.getItem('dcare_token');
    if (!token) {
      setLoading(false);
      return;
    }

    try {
      const userData = await api.getCurrentUser();
      setUser(userData);
    } catch (err) {
      console.error('Session expired or invalid:', err);
      localStorage.removeItem('dcare_token');
      localStorage.removeItem('dcare_user');
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const login = async (email, password) => {
    const res = await api.login(email, password);
    if (res.token) {
      localStorage.setItem('dcare_token', res.token);
      localStorage.setItem('dcare_user', JSON.stringify(res));
      setUser(res);
    }
    return res;
  };

  const register = async (payload) => {
    const res = await api.register(payload);
    if (res.token) {
      localStorage.setItem('dcare_token', res.token);
      localStorage.setItem('dcare_user', JSON.stringify(res));
      setUser(res);
    }
    return res;
  };

  const logout = () => {
    localStorage.removeItem('dcare_token');
    localStorage.removeItem('dcare_user');
    setUser(null);
  };

  const value = {
    user,
    loading,
    login,
    register,
    logout,
    isAuthenticated: !!user,
    role: user?.role,
    isCustomer: user?.role === 'ROLE_CUSTOMER',
    isOfficer: user?.role === 'ROLE_CLAIM_OFFICER',
    isInvestigator: user?.role === 'ROLE_FRAUD_INVESTIGATOR',
    isAdmin: user?.role === 'ROLE_ADMIN',
    canReview: user?.role === 'ROLE_CLAIM_OFFICER' || user?.role === 'ROLE_FRAUD_INVESTIGATOR' || user?.role === 'ROLE_ADMIN',
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
