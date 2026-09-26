const fs = require('fs');

const neumorphismCss = `
/* --- MIXED NEUMORPHISM OVERRIDES --- */
body {
    background: #f0f4f8 !important;
    color: #5e6677 !important;
}
.background-animation, .blob {
    display: none !important;
}
.glass-panel, .admin-pro-bg {
    background: #5e6677 !important;
    border: none !important;
    box-shadow: 15px 15px 30px #d1d9e6, -15px -15px 30px #ffffff !important;
    backdrop-filter: none !important;
    -webkit-backdrop-filter: none !important;
    border-radius: 24px !important;
}
.glass-panel label, .admin-pro-bg label, .glass-panel h2, .admin-pro-bg h2, .glass-panel p, .admin-pro-bg p, .text-muted, .text-light {
    color: #cbd5e1 !important;
}
input, select, textarea {
    background: #f0f4f8 !important;
    border: none !important;
    box-shadow: inset 6px 6px 12px #4a5160, inset -6px -6px 12px #727b8e !important;
    color: #5e6677 !important;
    border-radius: 12px !important;
    padding-left: 50px !important;
}
input:focus, select:focus, textarea:focus {
    box-shadow: inset 8px 8px 16px #4a5160, inset -8px -8px 16px #727b8e !important;
    border: none !important;
}
.btn-primary {
    background: #f0f4f8 !important;
    color: #6366f1 !important;
    box-shadow: 0 0 20px rgba(255, 255, 255, 0.5), 8px 8px 16px #4a5160, -8px -8px 16px #727b8e !important;
    border: none !important;
    text-shadow: none !important;
    font-weight: 700 !important;
    border-radius: 12px !important;
}
.btn-primary:hover, .btn-primary:active {
    box-shadow: inset 4px 4px 8px #d1d9e6, inset -4px -4px 8px #ffffff !important;
    transform: none !important;
}
.btn-secondary {
    background: #f0f4f8 !important;
    color: #5e6677 !important;
    box-shadow: 6px 6px 12px #4a5160, -6px -6px 12px #727b8e !important;
    border: none !important;
}
.btn-secondary:hover, .btn-secondary:active {
    box-shadow: inset 4px 4px 8px #d1d9e6, inset -4px -4px 8px #ffffff !important;
    transform: none !important;
}
.feature-card {
    background: #5e6677 !important;
    border: none !important;
    box-shadow: 10px 10px 20px #d1d9e6, -10px -10px 20px #ffffff !important;
}
.feature-card:hover {
    transform: translateY(-2px) !important;
    box-shadow: 12px 12px 24px #d1d9e6, -12px -12px 24px #ffffff !important;
}
.feature-card h3, .feature-card p {
    color: #cbd5e1 !important;
}
.feature-icon-wrapper {
    background: #f0f4f8 !important;
    box-shadow: inset 4px 4px 8px #d1d9e6, inset -4px -4px 8px #ffffff !important;
    border: none !important;
}
.feature-icon-wrapper i {
    color: #6366f1 !important;
    text-shadow: none !important;
    filter: none !important;
}
.project-card {
    background: #5e6677 !important;
    border: none !important;
    box-shadow: 10px 10px 20px #d1d9e6, -10px -10px 20px #ffffff !important;
    padding: 1.5rem !important;
}
.project-card:hover {
    box-shadow: 15px 15px 30px #d1d9e6, -15px -15px 30px #ffffff !important;
    transform: translateY(-2px) !important;
    border: none !important;
}
.project-card h3, .project-card p, .project-card div {
    color: #cbd5e1 !important;
}
.tag {
    background: #f0f4f8 !important;
    border: none !important;
    box-shadow: 3px 3px 6px #4a5160, -3px -3px 6px #727b8e !important;
    color: #6366f1 !important;
}
.modal-content {
    background: #5e6677 !important;
    border: none !important;
    box-shadow: 20px 20px 40px #d1d9e6, -20px -20px 40px #ffffff !important;
    color: #cbd5e1 !important;
}
.close-modal {
    background: #f0f4f8 !important;
    color: #5e6677 !important;
    box-shadow: 4px 4px 8px #4a5160, -4px -4px 8px #727b8e !important;
    border: none !important;
}
.close-modal:hover {
    box-shadow: inset 2px 2px 5px #d1d9e6, inset -2px -2px 5px #ffffff !important;
}
.history-item {
    background: #5e6677 !important;
    border: none !important;
    box-shadow: inset 4px 4px 8px #4a5160, inset -4px -4px 8px #727b8e !important;
    color: #cbd5e1 !important;
}
.history-item:hover {
    background: #4a5160 !important;
}
.nav-link {
    color: #5e6677 !important;
}
.nav-link:hover {
    color: #6366f1 !important;
    text-shadow: none !important;
}
.nav-btn {
    box-shadow: 4px 4px 8px #d1d9e6, -4px -4px 8px #ffffff !important;
    background: #f0f4f8 !important;
    border: none !important;
    color: #6366f1 !important;
}
.nav-btn:hover {
    box-shadow: inset 3px 3px 6px #d1d9e6, inset -3px -3px 6px #ffffff !important;
}
h1, h2, h3, h4, h5, h6 {
    color: #6366f1 !important;
}
.gradient-text {
    background: none !important;
    -webkit-text-fill-color: #6366f1 !important;
    color: #6366f1 !important;
    text-shadow: none !important;
}
.auth-box {
    background: #5e6677 !important;
    border: none !important;
    box-shadow: 15px 15px 30px #d1d9e6, -15px -15px 30px #ffffff !important;
    color: #cbd5e1 !important;
}
.intro-portal {
    background: #f0f4f8 !important;
    backdrop-filter: none !important;
}
.portal-ring {
    border: none !important;
    box-shadow: 20px 20px 40px #d1d9e6, -20px -20px 40px #ffffff !important;
}
.portal-ring-inner {
    border: none !important;
    box-shadow: inset 10px 10px 20px #d1d9e6, inset -10px -10px 20px #ffffff !important;
}
.portal-core {
    background: #f0f4f8 !important;
    box-shadow: 8px 8px 16px #d1d9e6, -8px -8px 16px #ffffff !important;
    color: #6366f1 !important;
    border: none !important;
    text-shadow: none !important;
}
.intro-particle {
    display: none !important;
}
.loading-spinner {
    border-color: #d1d9e6 !important;
    border-top-color: #6366f1 !important;
}
.admin-table th, .admin-table td {
    border-bottom: 1px solid #4a5160 !important;
    color: #cbd5e1 !important;
}
.stat-card {
    background: #5e6677 !important;
    border: none !important;
    box-shadow: 10px 10px 20px #d1d9e6, -10px -10px 20px #ffffff !important;
    color: #cbd5e1 !important;
}
.stat-value {
    color: #6366f1 !important;
    text-shadow: none !important;
}
nav {
    background: #5e6677 !important;
    border-bottom-left-radius: 24px;
    border-bottom-right-radius: 24px;
    box-shadow: 0 15px 30px #d1d9e6 !important;
}
.logo-icon {
    color: #6366f1 !important;
    filter: drop-shadow(0 0 5px rgba(99, 102, 241, 0.5)) !important;
}
.logo-text {
    color: #6366f1 !important;
}
`;

// Make sure to clean out any previously injected overrides first if they exist
let styleCss = fs.readFileSync('style.css', 'utf8');
const overrideMarker = '/* --- MIXED NEUMORPHISM OVERRIDES --- */';
const previousOverrideMarker = '/* --- NEUMORPHISM OVERRIDES --- */';

if (styleCss.includes(previousOverrideMarker)) {
    styleCss = styleCss.substring(0, styleCss.indexOf(previousOverrideMarker));
}
if (styleCss.includes(overrideMarker)) {
    styleCss = styleCss.substring(0, styleCss.indexOf(overrideMarker));
}

fs.writeFileSync('style.css', styleCss + '\n' + neumorphismCss);
console.log('Appended Mixed Neumorphism CSS to style.css');

// Modify HTML files
['../index.html', '../admin.html'].forEach(file => {
    if (fs.existsSync(file)) {
        let html = fs.readFileSync(file, 'utf8');
        
        // Remove hardcoded neon colors in style tags
        html = html.replace(/rgba\(\s*239,\s*68,\s*68[^)]*\)/gi, '#5e6677'); // red backgrounds inside panels
        html = html.replace(/#ef4444/gi, '#ff8a8a'); // lighter red text for errors against dark panel
        html = html.replace(/#10b981/gi, '#a7f3d0'); // lighter green text for success against dark panel
        
        // Ensure proper button backgrounds
        html = html.replace(/rgba\(\s*255,\s*255,\s*255[^)]*\)/gi, 'transparent'); 
        html = html.replace(/rgba\(\s*0,\s*0,\s*0[^)]*\)/gi, 'transparent');
        
        fs.writeFileSync(file, html);
    }
});

console.log('Mixed Neumorphism theme applied successfully!');
