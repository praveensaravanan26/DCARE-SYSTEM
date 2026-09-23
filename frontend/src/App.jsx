import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';

import { Navbar } from './components/Navbar';
import { Sidebar } from './components/Sidebar';

import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { ClaimsListPage } from './pages/ClaimsListPage';
import { ClaimCreatePage } from './pages/ClaimCreatePage';
import { ClaimDetailPage } from './pages/ClaimDetailPage';
import { ClaimDocumentsPage } from './pages/ClaimDocumentsPage';
import { ClaimRiskPage } from './pages/ClaimRiskPage';
import { InvestigationsPage } from './pages/InvestigationsPage';
import { AnalyticsPage } from './pages/AnalyticsPage';
import { AuditLogsPage } from './pages/AuditLogsPage';
import { AdminPage } from './pages/AdminPage';

const ProtectedLayout = ({ children, requiredRole = null }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center text-slate-400 text-sm">
        Initializing DCARE Secure Session...
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && user.role !== requiredRole && user.role !== 'ROLE_ADMIN') {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="layout-container bg-slate-950">
      <Sidebar />
      <div className="main-content">
        <Navbar />
        <main className="flex-1 overflow-y-auto">
          {children}
        </main>
      </div>
    </div>
  );
};

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route
            path="/dashboard"
            element={
              <ProtectedLayout>
                <DashboardPage />
              </ProtectedLayout>
            }
          />

          <Route
            path="/claims"
            element={
              <ProtectedLayout>
                <ClaimsListPage />
              </ProtectedLayout>
            }
          />

          <Route
            path="/claims/new"
            element={
              <ProtectedLayout>
                <ClaimCreatePage />
              </ProtectedLayout>
            }
          />

          <Route
            path="/claims/:id"
            element={
              <ProtectedLayout>
                <ClaimDetailPage />
              </ProtectedLayout>
            }
          />

          <Route
            path="/claims/:id/documents"
            element={
              <ProtectedLayout>
                <ClaimDocumentsPage />
              </ProtectedLayout>
            }
          />

          <Route
            path="/claims/:id/risk"
            element={
              <ProtectedLayout>
                <ClaimRiskPage />
              </ProtectedLayout>
            }
          />

          <Route
            path="/investigations"
            element={
              <ProtectedLayout>
                <InvestigationsPage />
              </ProtectedLayout>
            }
          />

          <Route
            path="/analytics"
            element={
              <ProtectedLayout>
                <AnalyticsPage />
              </ProtectedLayout>
            }
          />

          <Route
            path="/audit-logs"
            element={
              <ProtectedLayout>
                <AuditLogsPage />
              </ProtectedLayout>
            }
          />

          <Route
            path="/admin"
            element={
              <ProtectedLayout requiredRole="ROLE_ADMIN">
                <AdminPage />
              </ProtectedLayout>
            }
          />

          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
