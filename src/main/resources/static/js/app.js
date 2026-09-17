/**
 * Digital Printing Queue Management System - Core Frontend App JS
 */

const API_BASE = '/api';

// Authentication helpers
function getToken() {
    return localStorage.getItem('dpq_token');
}

function getUser() {
    const userStr = localStorage.getItem('dpq_user');
    try {
        return userStr ? JSON.parse(userStr) : null;
    } catch (e) {
        return null;
    }
}

function setAuth(token, user) {
    localStorage.setItem('dpq_token', token);
    localStorage.setItem('dpq_user', JSON.stringify(user));
}

function clearAuth() {
    localStorage.removeItem('dpq_token');
    localStorage.removeItem('dpq_user');
}

function isLoggedIn() {
    return !!getToken();
}

function isAdmin() {
    const user = getUser();
    return user && user.role === 'ADMIN';
}

function requireAuth(adminOnly = false) {
    if (!isLoggedIn()) {
        window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname);
        return false;
    }
    if (adminOnly && !isAdmin()) {
        window.location.href = '/dashboard.html';
        return false;
    }
    return true;
}

// API Request Wrapper
async function apiFetch(endpoint, options = {}) {
    const headers = options.headers || {};
    const token = getToken();

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    // If body is JSON and not FormData
    if (options.body && !(options.body instanceof FormData) && !headers['Content-Type']) {
        headers['Content-Type'] = 'application/json';
    }

    const config = {
        ...options,
        headers
    };

    try {
        const res = await fetch(`${API_BASE}${endpoint}`, config);
        
        if (res.status === 401) {
            clearAuth();
            if (!window.location.pathname.includes('login.html')) {
                window.location.href = '/login.html';
            }
            throw new Error('Session expired. Please log in again.');
        }

        if (res.status === 204) {
            return null;
        }

        const data = await res.json().catch(() => null);

        if (!res.ok) {
            const errorMsg = (data && (data.message || data.error)) || `Request failed with status ${res.status}`;
            throw new Error(errorMsg);
        }

        return data;
    } catch (err) {
        console.error('API Error:', err);
        throw err;
    }
}

// Toast Notifications
function showToast(message, type = 'info') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const bgClass = {
        success: 'bg-success text-white',
        danger: 'bg-danger text-white',
        warning: 'bg-warning text-dark',
        info: 'bg-primary text-white'
    }[type] || 'bg-secondary text-white';

    const toastId = 'toast-' + Date.now();
    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center ${bgClass} border-0 mb-2" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;
    container.insertAdjacentHTML('beforeend', toastHtml);
    const toastEl = document.getElementById(toastId);
    if (window.bootstrap && window.bootstrap.Toast) {
        const toast = new bootstrap.Toast(toastEl, { delay: 4000 });
        toast.show();
        toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
    } else {
        setTimeout(() => toastEl.remove(), 4000);
    }
}

// Global Navbar updater
function updateNavbar() {
    const user = getUser();
    const navAuth = document.getElementById('nav-auth-section');
    if (!navAuth) return;

    if (user) {
        navAuth.innerHTML = `
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle active text-white" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                    <i class="bi bi-person-circle me-1"></i> ${user.name} <span class="badge bg-secondary ms-1">${user.role}</span>
                </a>
                <ul class="dropdown-menu dropdown-menu-end shadow">
                    <li><a class="dropdown-item" href="${user.role === 'ADMIN' ? '/admin-dashboard.html' : '/dashboard.html'}"><i class="bi bi-speedometer2 me-2"></i>Dashboard</a></li>
                    <li><a class="dropdown-item" href="/my-jobs.html"><i class="bi bi-file-earmark-text me-2"></i>My Jobs</a></li>
                    <li><a class="dropdown-item" href="/submit-job.html"><i class="bi bi-cloud-upload me-2"></i>Submit Job</a></li>
                    ${user.role === 'ADMIN' ? `
                    <li><hr class="dropdown-divider"></li>
                    <li><h6 class="dropdown-header">Admin Suite</h6></li>
                    <li><a class="dropdown-item" href="/admin-queue.html"><i class="bi bi-list-check me-2"></i>Admin Queue</a></li>
                    <li><a class="dropdown-item" href="/printer.html"><i class="bi bi-printer me-2"></i>Printer Control</a></li>
                    <li><a class="dropdown-item" href="/users.html"><i class="bi bi-people me-2"></i>User Directory</a></li>
                    ` : ''}
                    <li><hr class="dropdown-divider"></li>
                    <li><a class="dropdown-item text-danger" href="#" onclick="logout(); return false;"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
                </ul>
            </li>
        `;
    } else {
        navAuth.innerHTML = `
            <li class="nav-item"><a class="nav-link" href="/login.html"><i class="bi bi-box-arrow-in-right me-1"></i>Login</a></li>
            <li class="nav-item"><a class="btn btn-outline-light btn-sm ms-2" href="/register.html">Sign Up</a></li>
        `;
    }
}

async function logout() {
    try {
        await apiFetch('/auth/logout', { method: 'POST' });
    } catch (e) {
        // Ignore logout network failure
    } finally {
        clearAuth();
        showToast('You have been logged out.', 'info');
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 500);
    }
}

// Formatters
function formatDate(dateStr) {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    return d.toLocaleString('en-IN', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function formatCost(cost) {
    if (cost === undefined || cost === null) return '₹0.00';
    return `₹${parseFloat(cost).toFixed(2)}`;
}

function formatBytes(bytes) {
    if (!bytes) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

document.addEventListener('DOMContentLoaded', () => {
    updateNavbar();
});
