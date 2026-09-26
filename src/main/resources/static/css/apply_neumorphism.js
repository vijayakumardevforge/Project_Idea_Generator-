const fs = require('fs');

const neumorphismCss = `
/* --- NEUMORPHISM OVERRIDES --- */
body {
    background: #f0f2f5 !important;
    color: #334155 !important;
}
.background-animation, .blob {
    display: none !important;
}
.glass-panel, .admin-pro-bg {
    background: #f0f2f5 !important;
    border: none !important;
    box-shadow: 10px 10px 20px #d1d9e6, -10px -10px 20px #ffffff !important;
    backdrop-filter: none !important;
    -webkit-backdrop-filter: none !important;
}
input, select, textarea {
    background: #f0f2f5 !important;
    border: none !important;
    box-shadow: inset 5px 5px 10px #d1d9e6, inset -5px -5px 10px #ffffff !important;
    color: #334155 !important;
}
input:focus, select:focus, textarea:focus {
    box-shadow: inset 6px 6px 12px #c8d0e0, inset -6px -6px 12px #ffffff !important;
    border: none !important;
}
.btn-primary {
    background: #f0f2f5 !important;
    color: #6366f1 !important;
    box-shadow: 6px 6px 12px #d1d9e6, -6px -6px 12px #ffffff !important;
    border: none !important;
    text-shadow: none !important;
}
.btn-primary:hover, .btn-primary:active {
    box-shadow: inset 4px 4px 8px #d1d9e6, inset -4px -4px 8px #ffffff !important;
    transform: none !important;
}
.btn-secondary {
    background: #f0f2f5 !important;
    color: #64748b !important;
    box-shadow: 6px 6px 12px #d1d9e6, -6px -6px 12px #ffffff !important;
    border: none !important;
}
.btn-secondary:hover, .btn-secondary:active {
    box-shadow: inset 4px 4px 8px #d1d9e6, inset -4px -4px 8px #ffffff !important;
    transform: none !important;
}
.feature-card {
    background: #f0f2f5 !important;
    border: none !important;
    box-shadow: 8px 8px 16px #d1d9e6, -8px -8px 16px #ffffff !important;
}
.feature-card:hover {
    transform: translateY(-2px) !important;
    box-shadow: 12px 12px 20px #d1d9e6, -12px -12px 20px #ffffff !important;
}
.feature-icon-wrapper {
    background: #f0f2f5 !important;
    box-shadow: inset 4px 4px 8px #d1d9e6, inset -4px -4px 8px #ffffff !important;
    border: none !important;
}
.feature-icon-wrapper i {
    color: #6366f1 !important;
    text-shadow: none !important;
    filter: none !important;
}
.project-card {
    background: #f0f2f5 !important;
    border: none !important;
    box-shadow: 8px 8px 16px #d1d9e6, -8px -8px 16px #ffffff !important;
    padding: 1.5rem !important;
}
.project-card:hover {
    box-shadow: 12px 12px 20px #d1d9e6, -12px -12px 20px #ffffff !important;
    transform: translateY(-2px) !important;
    border: none !important;
}
.tag {
    background: #f0f2f5 !important;
    border: none !important;
    box-shadow: 3px 3px 6px #d1d9e6, -3px -3px 6px #ffffff !important;
    color: #6366f1 !important;
}
.modal-content {
    background: #f0f2f5 !important;
    border: none !important;
    box-shadow: 15px 15px 30px #d1d9e6, -15px -15px 30px #ffffff !important;
}
.close-modal {
    background: #f0f2f5 !important;
    color: #64748b !important;
    box-shadow: 4px 4px 8px #d1d9e6, -4px -4px 8px #ffffff !important;
    border: none !important;
}
.close-modal:hover {
    box-shadow: inset 2px 2px 5px #d1d9e6, inset -2px -2px 5px #ffffff !important;
}
.history-item {
    background: #f0f2f5 !important;
    border: none !important;
    box-shadow: inset 4px 4px 8px #d1d9e6, inset -4px -4px 8px #ffffff !important;
}
.history-item:hover {
    background: #e8ecf1 !important;
}
.nav-link {
    color: #64748b !important;
}
.nav-link:hover {
    color: #6366f1 !important;
    text-shadow: none !important;
}
.nav-btn {
    box-shadow: 4px 4px 8px #d1d9e6, -4px -4px 8px #ffffff !important;
    background: #f0f2f5 !important;
    border: none !important;
    color: #6366f1 !important;
}
.nav-btn:hover {
    box-shadow: inset 3px 3px 6px #d1d9e6, inset -3px -3px 6px #ffffff !important;
}
h1, h2, h3, h4, h5, h6 {
    color: #334155 !important;
}
.gradient-text {
    background: none !important;
    -webkit-text-fill-color: #6366f1 !important;
    color: #6366f1 !important;
    text-shadow: none !important;
}
.auth-box {
    background: #f0f2f5 !important;
    border: none !important;
    box-shadow: 10px 10px 20px #d1d9e6, -10px -10px 20px #ffffff !important;
}
.intro-portal {
    background: #f0f2f5 !important;
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
    background: #f0f2f5 !important;
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
    border-bottom: 1px solid #d1d9e6 !important;
    color: #334155 !important;
}
.stat-card {
    background: #f0f2f5 !important;
    border: none !important;
    box-shadow: 8px 8px 16px #d1d9e6, -8px -8px 16px #ffffff !important;
}
.stat-value {
    color: #6366f1 !important;
    text-shadow: none !important;
}
.text-muted {
    color: #64748b !important;
}
.text-light {
    color: #475569 !important;
}
`;

// Append CSS
let styleCss = fs.readFileSync('style.css', 'utf8');
if (!styleCss.includes('NEUMORPHISM OVERRIDES')) {
    fs.writeFileSync('style.css', styleCss + '\n' + neumorphismCss);
    console.log('Appended Neumorphism CSS to style.css');
}

// Modify HTML files
['../index.html', '../admin.html'].forEach(file => {
    if (fs.existsSync(file)) {
        let html = fs.readFileSync(file, 'utf8');
        
        // Remove hardcoded neon colors in style tags
        html = html.replace(/rgba\(\s*239,\s*68,\s*68[^)]*\)/gi, '#f0f2f5'); // red backgrounds
        html = html.replace(/#ef4444/gi, '#ef4444'); // keep red text for errors, just ensure background is soft
        
        // Remove white text and borders
        html = html.replace(/rgba\(\s*255,\s*255,\s*255[^)]*\)/gi, 'transparent'); 
        html = html.replace(/rgba\(\s*0,\s*0,\s*0[^)]*\)/gi, 'transparent');
        
        // We will just let the !important CSS overrides handle most of it.
        fs.writeFileSync(file, html);
    }
});

console.log('Neumorphism theme applied successfully!');
