/**
 * ManuscriptVault - API Utility & Auth Helper
 * Shared JavaScript module used by all frontend pages.
 */

// ============================================================
// Configuration
// ============================================================
const API_BASE = 'http://localhost:8081/api';

// ============================================================
// Token / Session Helpers
// ============================================================

/** Store auth data after login */
function saveAuthData(token, username, role) {
    sessionStorage.setItem('mv_token', token);
    sessionStorage.setItem('mv_username', username);
    sessionStorage.setItem('mv_role', role);
}

/** Retrieve stored JWT token */
function getToken() {
    return sessionStorage.getItem('mv_token');
}

/** Retrieve stored username */
function getUsername() {
    return sessionStorage.getItem('mv_username');
}

/** Retrieve stored role */
function getRole() {
    return sessionStorage.getItem('mv_role');
}

/** Check if user is logged in */
function isLoggedIn() {
    return !!getToken();
}

/** Check if current user is ADMIN */
function isAdmin() {
    return getRole() === 'ADMIN';
}

/** Clear session and redirect to login */
function logout() {
    sessionStorage.clear();
    window.location.href = 'login.html';
}

/** Redirect if not authenticated */
function requireAuth() {
    if (!isLoggedIn()) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

/** Redirect if not admin */
function requireAdmin() {
    if (!isLoggedIn()) { window.location.href = 'login.html'; return; }
    if (!isAdmin())    { window.location.href = 'user-dashboard.html'; return; }
}

// ============================================================
// HTTP Request Wrapper
// ============================================================

/**
 * Make an authenticated API request.
 * Automatically attaches JWT Bearer token.
 */
async function apiRequest(method, endpoint, body = null) {
    const headers = {
        'Content-Type': 'application/json',
    };

    const token = getToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const options = { method, headers };

    if (body) {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(`${API_BASE}${endpoint}`, options);

    // If unauthorized, force logout
    if (response.status === 401 || response.status === 403) {
        logout();
        throw new Error('Session expired. Please log in again.');
    }

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || `Request failed: ${response.status}`);
    }

    return data;
}

// Shorthand methods
const api = {
    get:    (endpoint)       => apiRequest('GET',    endpoint),
    post:   (endpoint, body) => apiRequest('POST',   endpoint, body),
    put:    (endpoint, body) => apiRequest('PUT',    endpoint, body),
    delete: (endpoint)       => apiRequest('DELETE', endpoint),
};

// ============================================================
// Auth API
// ============================================================

async function loginUser(username, password) {
    const response = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || 'Login failed');
    return data;
}

async function signupUser(username, password, role) {
    const response = await fetch(`${API_BASE}/auth/signup`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password, role }),
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || 'Signup failed');
    return data;
}

// ============================================================
// Manuscript API
// ============================================================

const ManuscriptAPI = {
    getAll:     ()               => api.get('/manuscripts'),
    getById:    (id)             => api.get(`/manuscripts/${id}`),
    search:     (query)          => api.get(`/manuscripts/search?query=${encodeURIComponent(query)}`),
    searchTitle:(title)          => api.get(`/manuscripts/search?title=${encodeURIComponent(title)}`),
    searchAuthor:(author)        => api.get(`/manuscripts/search?author=${encodeURIComponent(author)}`),
    filterDate: (start, end)     => api.get(`/manuscripts/filter?startDate=${start}&endDate=${end}`),
    filterGenre:(genre)          => api.get(`/manuscripts/filter?genre=${encodeURIComponent(genre)}`),
    create:     (data)           => api.post('/manuscripts', data),
    update:     (id, data)       => api.put(`/manuscripts/${id}`, data),
    delete:     (id)             => api.delete(`/manuscripts/${id}`),
};

// ============================================================
// User API (Admin only)
// ============================================================

const UserAPI = {
    getAll:        ()          => api.get('/users'),
    getById:       (id)        => api.get(`/users/${id}`),
    delete:        (id)        => api.delete(`/users/${id}`),
    updateRole:    (id, role)  => api.put(`/users/${id}/role`, { role }),
};

// ============================================================
// UI Helpers
// ============================================================

/** Show an alert message in a container */
function showAlert(containerId, message, type = 'error') {
    const el = document.getElementById(containerId);
    if (!el) return;
    const icons = { error: '⚠', success: '✓', info: 'ℹ' };
    el.innerHTML = `
        <div class="alert alert-${type}">
            <span>${icons[type] || '!'}</span>
            <span>${message}</span>
        </div>`;
    el.style.display = 'block';
    // Auto-clear success messages
    if (type === 'success') setTimeout(() => { el.innerHTML = ''; }, 4000);
}

/** Clear an alert container */
function clearAlert(containerId) {
    const el = document.getElementById(containerId);
    if (el) el.innerHTML = '';
}

/** Show/hide loading state on a button */
function setButtonLoading(btn, loading, originalText = 'Submit') {
    if (loading) {
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner"></span> Loading…';
    } else {
        btn.disabled = false;
        btn.innerHTML = originalText;
    }
}

/** Format a date string to locale format */
function formatDate(dateStr) {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-IN', {
        year: 'numeric', month: 'long', day: 'numeric'
    });
}

/** Truncate long text */
function truncate(text, length = 100) {
    if (!text) return '—';
    return text.length > length ? text.substring(0, length) + '…' : text;
}

/** Open/close a modal */
function openModal(modalId) {
    document.getElementById(modalId)?.classList.add('open');
    document.body.style.overflow = 'hidden';
}
function closeModal(modalId) {
    document.getElementById(modalId)?.classList.remove('open');
    document.body.style.overflow = '';
}

// Close modal on overlay click
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.classList.remove('open');
        document.body.style.overflow = '';
    }
});

// ============================================================
// Populate the topbar with user info
// ============================================================
function initTopbar() {
    const usernameEl = document.getElementById('topbar-username');
    const roleEl     = document.getElementById('topbar-role');
    const logoutBtn  = document.getElementById('btn-logout');

    if (usernameEl) usernameEl.textContent = getUsername() || 'User';
    if (roleEl) {
        roleEl.textContent = getRole() || '';
        if (isAdmin()) roleEl.classList.add('admin');
    }
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            if (confirm('Are you sure you want to log out?')) logout();
        });
    }
}
