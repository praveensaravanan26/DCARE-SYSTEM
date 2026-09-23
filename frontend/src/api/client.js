const BASE_URL = '/api';

const getHeaders = (isMultipart = false) => {
  const token = localStorage.getItem('dcare_token');
  const headers = {};
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  if (!isMultipart) {
    headers['Content-Type'] = 'application/json';
  }
  return headers;
};

const handleResponse = async (res) => {
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    const message = errorData.message || `Request failed with status ${res.status}`;
    throw new Error(message);
  }
  return res.json();
};

export const api = {
  // Auth
  login: async (email, password) => {
    const res = await fetch(`${BASE_URL}/auth/login`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify({ email, password }),
    });
    return handleResponse(res);
  },
  register: async (payload) => {
    const res = await fetch(`${BASE_URL}/auth/register`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(payload),
    });
    return handleResponse(res);
  },
  getCurrentUser: async () => {
    const res = await fetch(`${BASE_URL}/auth/me`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  // Policies
  getPolicies: async () => {
    const res = await fetch(`${BASE_URL}/policies`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  // Claims
  getClaims: async () => {
    const res = await fetch(`${BASE_URL}/claims`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
  getClaimById: async (id) => {
    const res = await fetch(`${BASE_URL}/claims/${id}`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
  createClaim: async (claimData) => {
    const res = await fetch(`${BASE_URL}/claims`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(claimData),
    });
    return handleResponse(res);
  },
  evaluateClaimRisk: async (id) => {
    const res = await fetch(`${BASE_URL}/claims/${id}/evaluate`, {
      method: 'POST',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
  getRiskDetails: async (id) => {
    const res = await fetch(`${BASE_URL}/claims/${id}/risk`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
  correctField: async (claimId, fieldName, correctedValue, note = '') => {
    const res = await fetch(`${BASE_URL}/claims/${claimId}/fields/correct`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify({ fieldName, correctedValue, note }),
    });
    return handleResponse(res);
  },

  // Documents
  uploadDocument: async (claimId, documentType, file) => {
    const formData = new FormData();
    formData.append('documentType', documentType);
    formData.append('file', file);
    const res = await fetch(`${BASE_URL}/claims/${claimId}/documents`, {
      method: 'POST',
      headers: getHeaders(true),
      body: formData,
    });
    return handleResponse(res);
  },
  getDocumentsForClaim: async (claimId) => {
    const res = await fetch(`${BASE_URL}/claims/${claimId}/documents`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
  getExtractedFields: async (claimId) => {
    const res = await fetch(`${BASE_URL}/claims/${claimId}/extraction`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
  processDocument: async (docId) => {
    const res = await fetch(`${BASE_URL}/documents/${docId}/process`, {
      method: 'POST',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
  getDocumentDownloadUrl: (docId) => `${BASE_URL}/documents/download/${docId}`,

  // Reviews
  submitReview: async (claimId, decision, notes) => {
    const res = await fetch(`${BASE_URL}/claims/${claimId}/review`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify({ decision, notes }),
    });
    return handleResponse(res);
  },
  getReviews: async (claimId) => {
    const res = await fetch(`${BASE_URL}/claims/${claimId}/reviews`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  // Analytics
  getAnalytics: async () => {
    const res = await fetch(`${BASE_URL}/analytics/dashboard`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  // Audit Logs
  getAuditLogs: async () => {
    const res = await fetch(`${BASE_URL}/audit-logs`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  // Admin Management & Approvals
  getUsers: async () => {
    const res = await fetch(`${BASE_URL}/admin/users`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
  getPendingApprovals: async () => {
    const res = await fetch(`${BASE_URL}/admin/pending-approvals`, {
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
  approveUser: async (userId) => {
    const res = await fetch(`${BASE_URL}/admin/users/${userId}/approve`, {
      method: 'POST',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
  rejectUser: async (userId) => {
    const res = await fetch(`${BASE_URL}/admin/users/${userId}/reject`, {
      method: 'POST',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },
  toggleUserStatus: async (userId) => {
    const res = await fetch(`${BASE_URL}/admin/users/${userId}/toggle-status`, {
      method: 'PATCH',
      headers: getHeaders(),
    });
    return handleResponse(res);
  }
};
