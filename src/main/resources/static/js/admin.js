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
    const sidebarToggle = document.getElementById('sidebar-toggle');
    const adminSidebar = document.querySelector('.admin-sidebar');

    if (sidebarToggle && adminSidebar) {
        sidebarToggle.addEventListener('click', () => {
            adminSidebar.classList.toggle('show');
        });
    }

    sidebarLinks.forEach(link => {
        link.addEventListener('click', () => {
            sidebarLinks.forEach(l => l.classList.remove('active'));
            dashboardTabs.forEach(t => t.classList.remove('active'));

            link.classList.add('active');
            const targetId = link.getAttribute('data-tab');
            document.getElementById(targetId).classList.add('active');
            
            if (adminSidebar) {
                adminSidebar.classList.remove('show');
            }
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
                loadApiUsage(authString);
                loadRecentUsers(authString);
                loadRateLimitedUsers(authString);
                loadRecent24HourUsers(authString);
                loadBlockedIps(authString);
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
        const failedLoginBody = document.getElementById('failed-logins-body');
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
            container.innerHTML = '<tr class="empty-row"><td colspan="4" style="text-align: center; color: var(--text-muted);">No failed login attempts recorded. Secure!</td></tr>';
            return;
        }

        failedLogins.forEach(attempt => {
            const tr = document.createElement('tr');
            
            tr.innerHTML = `
                <td data-label="Date & Time">${formatServerTime(attempt.attemptTime)}</td>
                <td data-label="Username Attempted" style="color: #ef4444; font-weight: 500; word-break: break-all;">${attempt.username}</td>
                <td data-label="IP Address" style="font-family: monospace;">${attempt.ipAddress || 'Unknown'}</td>
                <td>
                    <button class="neon-btn neon-btn-danger" style="padding: 0.3rem 0.8rem; font-size: 0.8rem;" onclick="blockUser('${attempt.ipAddress}')">Block</button>
                </td>
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
                document.getElementById('stat-total-users').textContent = stats.totalUsers;
                document.getElementById('stat-total-ideas').textContent = stats.totalIdeas;
            }
        } catch (error) {
            console.error('Error loading stats:', error);
            document.getElementById('stat-users').textContent = '-';
            document.getElementById('stat-ideas').textContent = '-';
            document.getElementById('stat-total-users').textContent = '-';
            document.getElementById('stat-total-ideas').textContent = '-';
        }
    }
    async function loadApiUsage(authString) {
        try {
            const response = await fetch(`${API_BASE_URL}/api-usage`, { headers: { 'Authorization': authString } });
            if (response.ok) {
                const stats = await response.json();
                document.getElementById('stat-idea-api-today').textContent = stats.ideaCallsToday;
                document.getElementById('stat-idea-api-month').textContent = stats.ideaCallsMonth;
                document.getElementById('stat-roadmap-api-today').textContent = stats.roadmapCallsToday;
                document.getElementById('stat-roadmap-api-month').textContent = stats.roadmapCallsMonth;
            }
        } catch (error) { console.error('Error loading API usage:', error); }
    }

    function formatDeviceName(uaString) {
        if (!uaString) return 'Unknown';
        
        // Extract exact model if present (e.g. [Model: RMX3842])
        const modelMatch = uaString.match(/\[Model:\s*(.*?)\]/i);
        if (modelMatch && modelMatch[1]) {
            return modelMatch[1].trim();
        }
        
        // Fallbacks based on common substrings
        const uaLower = uaString.toLowerCase();
        if (uaLower.includes('windows')) return 'Windows';
        if (uaLower.includes('mac os') || uaLower.includes('macintosh')) return 'Mac';
        if (uaLower.includes('android')) return 'Android';
        if (uaLower.includes('iphone') || uaLower.includes('ipad')) return 'iOS';
        if (uaLower.includes('linux')) return 'Linux';
        
        // Final fallback: just return up to first 20 chars so it doesn't break layout
        return uaString.length > 20 ? uaString.substring(0, 20) + '...' : uaString;
    }

    function formatServerTime(timeString) {
        if (!timeString) return 'Unknown';
        // Strip out the 'T' and milliseconds to show exactly what the server sent
        return timeString.split('.')[0].replace('T', ' ');
    }

    async function loadRecentUsers(authString) {
        const body = document.getElementById('recent-users-body');
        try {
            const response = await fetch(`${API_BASE_URL}/recent-users`, { headers: { 'Authorization': authString } });
            if (response.ok) {
                const users = await response.json();
                body.innerHTML = '';
                if (users.length === 0) {
                    body.innerHTML = '<tr><td colspan="5" style="text-align:center;">No recent users found.</td></tr>';
                    return;
                }
                users.forEach(u => {
                    const tr = document.createElement('tr');
                    const isBlocked = u.isBlocked === 'true';
                    tr.innerHTML = `
                        <td>${u.ipAddress}</td>
                        <td style="max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="${u.userAgent}">${formatDeviceName(u.userAgent)}</td>
                        <td><span style="background: rgba(245, 158, 11, 0.2); color: #fbbf24; padding: 0.2rem 0.6rem; border-radius: 999px; font-weight: bold; border: 1px solid rgba(245, 158, 11, 0.3);">${u.ideaCount}</span></td>
                        <td>${formatServerTime(u.lastActive)}</td>
                        <td>
                            ${!isBlocked ? `<button class="neon-btn neon-btn-danger" style="padding: 0.3rem 0.8rem; font-size: 0.8rem;" onclick="blockUser('${u.ipAddress}')">Block</button>` 
                                         : '<span style="color: #ef4444; font-weight: bold;">Blocked</span>'}
                        </td>
                    `;
                    body.appendChild(tr);
                });
            }
        } catch (error) { console.error('Error loading recent users:', error); }
    }

    async function loadRateLimitedUsers(authString) {
        const body = document.getElementById('rate-limited-body');
        try {
            const response = await fetch(`${API_BASE_URL}/rate-limited-users`, { headers: { 'Authorization': authString } });
            if (response.ok) {
                const users = await response.json();
                body.innerHTML = '';
                if (users.length === 0) {
                    body.innerHTML = '<tr><td colspan="4" style="text-align:center;">No users hit the limit today.</td></tr>';
                    return;
                }
                users.forEach(u => {
                    const tr = document.createElement('tr');
                    const isBlocked = u.isBlocked === 'true';
                    tr.innerHTML = `
                        <td>${u.ipAddress}</td>
                        <td style="max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="${u.userAgent}">${formatDeviceName(u.userAgent)}</td>
                        <td>${formatServerTime(u.hitLimitAt)}</td>
                        <td>
                            ${!isBlocked ? `<button class="neon-btn neon-btn-danger" style="padding: 0.3rem 0.8rem; font-size: 0.8rem;" onclick="blockUser('${u.ipAddress}')">Block</button>` 
                                         : '<span style="color: #ef4444; font-weight: bold;">Blocked</span>'}
                        </td>
                    `;
                    body.appendChild(tr);
                });
            }
        } catch (error) { console.error('Error loading rate limited users:', error); }
    }

    async function loadRecent24HourUsers(authString) {
        const body = document.getElementById('recent-24h-body');
        try {
            const response = await fetch(`${API_BASE_URL}/recent-24h`, { headers: { 'Authorization': authString } });
            if (response.ok) {
                const users = await response.json();
                body.innerHTML = '';
                if (users.length === 0) {
                    body.innerHTML = '<tr><td colspan="4" style="text-align:center;">No users active in the last 24 hours.</td></tr>';
                    return;
                }
                users.forEach(u => {
                    const tr = document.createElement('tr');
                    const isBlocked = u.isBlocked === 'true';
                    tr.innerHTML = `
                        <td>${u.ipAddress}</td>
                        <td style="max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="${u.userAgent}">${formatDeviceName(u.userAgent)}</td>
                        <td>${formatServerTime(u.lastActive)}</td>
                        <td>
                            ${!isBlocked ? `<button class="neon-btn neon-btn-danger" style="padding: 0.3rem 0.8rem; font-size: 0.8rem;" onclick="blockUser('${u.ipAddress}')">Block</button>` 
                                         : '<span style="color: #ef4444; font-weight: bold;">Blocked</span>'}
                        </td>
                    `;
                    body.appendChild(tr);
                });
            }
        } catch (error) { console.error('Error loading recent 24h users:', error); }
    }

    async function loadBlockedIps(authString) {
        const body = document.getElementById('blocked-ips-body');
        try {
            const response = await fetch(`${API_BASE_URL}/blocked-ips`, { headers: { 'Authorization': authString } });
            if (response.ok) {
                const ips = await response.json();
                body.innerHTML = '';
                if (ips.length === 0) {
                    body.innerHTML = '<tr><td colspan="4" style="text-align:center;">No blocked IPs.</td></tr>';
                    return;
                }
                ips.forEach(ip => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>${ip.ipAddress}</td>
                        <td style="max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="${ip.userAgent}">${formatDeviceName(ip.userAgent)}</td>
                        <td>${ip.reason || 'Manual block by admin'}</td>
                        <td>${formatServerTime(ip.blockedAt)}</td>
                        <td>
                            <button class="neon-btn neon-btn-success" style="padding: 0.3rem 0.8rem; font-size: 0.8rem;" onclick="unblockUser('${ip.ipAddress}')">Unblock</button>
                        </td>
                    `;
                    body.appendChild(tr);
                });
            }
        } catch (error) { console.error('Error loading blocked IPs:', error); }
    }

    window.blockUser = async function(ipAddress) {
        const reason = prompt(`Enter reason for blocking ${ipAddress} (or leave blank):`, 'Suspicious activity');
        if (reason === null) return; // User cancelled

        const authString = sessionStorage.getItem('adminAuth');
        try {
            const res = await fetch(`${API_BASE_URL}/block-ip`, {
                method: 'POST',
                headers: { 'Authorization': authString, 'Content-Type': 'application/json' },
                body: JSON.stringify({ ipAddress: ipAddress, reason: reason })
            });
            if (res.ok) {
                loadRecentUsers(authString);
                loadRateLimitedUsers(authString);
                loadRecent24HourUsers(authString);
                loadBlockedIps(authString);
            }
        } catch (error) { console.error('Error blocking user:', error); }
    };

    window.unblockUser = async function(ipAddress) {
        if (!confirm(`Are you sure you want to unblock ${ipAddress}?`)) return;
        const authString = sessionStorage.getItem('adminAuth');
        try {
            const res = await fetch(`${API_BASE_URL}/unblock-ip`, {
                method: 'POST',
                headers: { 'Authorization': authString, 'Content-Type': 'application/json' },
                body: JSON.stringify({ ipAddress: ipAddress })
            });
            if (res.ok) {
                loadRecentUsers(authString);
                loadRateLimitedUsers(authString);
                loadRecent24HourUsers(authString);
                loadBlockedIps(authString);
            }
        } catch (error) { console.error('Error unblocking user:', error); }
    };
});
