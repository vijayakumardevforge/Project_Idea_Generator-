document.addEventListener('DOMContentLoaded', () => {
    const loginSection = document.getElementById('login-section');
    const dashboardSection = document.getElementById('dashboard-section');
    const loginForm = document.getElementById('admin-login-form');
    const loginError = document.getElementById('login-error');
    const logoutBtn = document.getElementById('logout-btn');
    const feedbackBody = document.getElementById('feedback-body');

    const API_BASE_URL = window.location.protocol === 'file:' 
        ? 'http://localhost:8080/api/admin' 
        : '/api/admin';

    // Check if already logged in
    const storedAuth = sessionStorage.getItem('adminAuth');
    if (storedAuth) {
        verifyAndLoadDashboard(storedAuth);
    }

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const authString = 'Basic ' + btoa(username + ':' + password);
        
        verifyAndLoadDashboard(authString);
    });

    logoutBtn.addEventListener('click', (e) => {
        e.preventDefault();
        sessionStorage.removeItem('adminAuth');
        dashboardSection.classList.add('hidden');
        loginSection.classList.remove('hidden');
        logoutBtn.classList.add('hidden');
        loginForm.reset();
    });

    // Sidebar Navigation Logic
    const sidebarLinks = document.querySelectorAll('.sidebar-link');
    const dashboardTabs = document.querySelectorAll('.dashboard-tab');

    sidebarLinks.forEach(link => {
        link.addEventListener('click', () => {
            sidebarLinks.forEach(l => l.classList.remove('active'));
            dashboardTabs.forEach(t => t.classList.remove('active'));

            link.classList.add('active');
            const targetId = link.getAttribute('data-tab');
            document.getElementById(targetId).classList.add('active');
        });
    });

    async function verifyAndLoadDashboard(authString) {
        try {
            // Test credentials
            const verifyResponse = await fetch(`${API_BASE_URL}/verify`, {
                headers: { 'Authorization': authString }
            });

            if (verifyResponse.ok) {
                // Success
                sessionStorage.setItem('adminAuth', authString);
                loginSection.classList.add('hidden');
                dashboardSection.classList.remove('hidden');
                logoutBtn.classList.remove('hidden');
                loginError.classList.add('hidden');
                
                loadFeedback(authString);
                loadFailedLogins(authString);
                loadStats(authString);
            } else {
                // Failure
                loginError.classList.remove('hidden');
                sessionStorage.removeItem('adminAuth');
                loginSection.classList.remove('hidden');
                dashboardSection.classList.add('hidden');
            }
        } catch (error) {
            console.error('Error verifying admin:', error);
            loginError.textContent = 'Connection error';
            loginError.classList.remove('hidden');
        }
    }

    async function loadFeedback(authString) {
        try {
            const response = await fetch(`${API_BASE_URL}/feedback`, {
                headers: { 'Authorization': authString }
            });

            if (response.ok) {
                const feedbacks = await response.json();
                renderFeedback(feedbacks);
            } else {
                if (response.status === 401) {
                    sessionStorage.removeItem('adminAuth');
                    window.location.reload();
                }
            }
        } catch (error) {
            console.error('Error loading feedback:', error);
            feedbackBody.innerHTML = '<tr class="empty-row"><td colspan="4" style="color: #ef4444; text-align: center;">Failed to load feedback.</td></tr>';
        }
    }

    function renderFeedback(feedbacks) {
        feedbackBody.innerHTML = '';
        const summaryText = document.getElementById('recent-feedback-summary');
        
        if (feedbacks.length === 0) {
            feedbackBody.innerHTML = '<tr class="empty-row"><td colspan="4" style="text-align: center;">No feedback received yet.</td></tr>';
            if(summaryText) summaryText.textContent = '';
            return;
        }

        const now = new Date();
        let recentCount = 0;

        feedbacks.forEach(fb => {
            const tr = document.createElement('tr');
            
            const fbDate = new Date(fb.createdAt);
            const dateStr = fbDate.toLocaleDateString('en-GB', { day: 'numeric', month: 'short' });
            const yearStr = fbDate.toLocaleDateString('en-GB', { year: 'numeric' });
            const timeStr = fbDate.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });
            
            // Check if recent (last 24 hours)
            const isRecent = (now - fbDate) < (24 * 60 * 60 * 1000);
            let badgeHtml = '';
            if (isRecent) {
                recentCount++;
                badgeHtml = '<div style="margin-top: 0.5rem;"><span class="neon-badge" style="margin-left: 0;">NEW</span></div>';
            }

            let starsHtml = '<div class="stars-display">';
            for(let i=0; i<fb.stars; i++) {
                starsHtml += '<i class="fa-solid fa-star"></i>';
            }
            starsHtml += '</div>';

            tr.innerHTML = `
                <td data-label="Date">
                    <div style="line-height: 1.5; color: #9ca3af;">
                        ${dateStr}<br>${yearStr},<br>${timeStr}
                    </div>
                    ${badgeHtml}
                </td>
                <td data-label="Email"><a href="mailto:${fb.email}" style="color: #60a5fa; text-decoration: underline; text-underline-offset: 4px; word-break: break-all;">${fb.email}</a></td>
                <td data-label="Rating">${starsHtml}</td>
                <td data-label="Message" style="color: #9ca3af; line-height: 1.6; word-break: break-word;">${fb.message}</td>
            `;
            
            feedbackBody.appendChild(tr);
        });

        if(summaryText) {
            summaryText.textContent = recentCount > 0 ? `(${recentCount} new in the last 24 hours)` : '';
        }
    }

    async function loadFailedLogins(authString) {
        const failedLoginBody = document.getElementById('failed-login-body');
        try {
            const response = await fetch(`${API_BASE_URL}/failed-logins`, {
                headers: { 'Authorization': authString }
            });

            if (response.ok) {
                const failedLogins = await response.json();
                renderFailedLogins(failedLogins, failedLoginBody);
            }
        } catch (error) {
            console.error('Error loading failed logins:', error);
            failedLoginBody.innerHTML = '<tr class="empty-row"><td colspan="3" style="color: #ef4444; text-align: center;">Failed to load data.</td></tr>';
        }
    }

    function renderFailedLogins(failedLogins, container) {
        container.innerHTML = '';
        
        if (failedLogins.length === 0) {
            container.innerHTML = '<tr class="empty-row"><td colspan="3" style="text-align: center; color: var(--text-muted);">No failed login attempts recorded. Secure!</td></tr>';
            return;
        }

        failedLogins.forEach(attempt => {
            const tr = document.createElement('tr');
            const date = new Date(attempt.attemptTime).toLocaleDateString(undefined, { 
                year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit'
            });

            tr.innerHTML = `
                <td data-label="Date & Time">${date}</td>
                <td data-label="Username Attempted" style="color: #ef4444; font-weight: 500; word-break: break-all;">${attempt.username}</td>
                <td data-label="IP Address" style="font-family: monospace;">${attempt.ipAddress || 'Unknown'}</td>
            `;
            container.appendChild(tr);
        });
    }

    async function loadStats(authString) {
        try {
            const response = await fetch(`${API_BASE_URL}/stats`, {
                headers: { 'Authorization': authString }
            });

            if (response.ok) {
                const stats = await response.json();
                document.getElementById('stat-users').textContent = stats.totalUsersToday;
                document.getElementById('stat-ideas').textContent = stats.totalIdeasToday;
            }
        } catch (error) {
            console.error('Error loading stats:', error);
            document.getElementById('stat-users').textContent = '-';
            document.getElementById('stat-ideas').textContent = '-';
        }
    }
});
