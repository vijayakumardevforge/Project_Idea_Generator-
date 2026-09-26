const fs = require('fs');
const path = require('path');

const cssPath = path.join(__dirname, 'style.css');
let styleCss = fs.readFileSync(cssPath, 'utf8');

// Remove existing overrides
const markers = [
    '/* --- PREMIUM NEUMORPHISM OVERRIDES --- */',
    '/* --- MIXED NEUMORPHISM OVERRIDES --- */',
    '/* --- NEUMORPHISM OVERRIDES --- */',
    '/* --- M3 THEME OVERRIDES --- */'
];

for (const marker of markers) {
    if (styleCss.includes(marker)) {
        styleCss = styleCss.substring(0, styleCss.indexOf(marker));
    }
}

const premiumNeumorphism = `
/* --- PREMIUM NEUMORPHISM OVERRIDES --- */
:root {
    --bg-dark: #e0e0e0;
    --bg-darker: #e0e0e0;
    --primary: #2B303A;
    --primary-hover: #1A1D23;
    --secondary: #ec4899;
    --text-main: #333333;
    --text-muted: #666666;
    
    --neumorph-convex: 9px 9px 16px #bebebe, -9px -9px 16px #ffffff;
    --neumorph-concave: inset 9px 9px 16px #bebebe, inset -9px -9px 16px #ffffff;
    --neumorph-radius-sm: 12px;
    --neumorph-radius-md: 16px;
    --neumorph-radius-lg: 24px;
}

body {
    background-color: #e0e0e0 !important;
    color: var(--text-main) !important;
}

/* Hide old effects */
.background-animation, .blob, .intro-core-glow, .intro-bg-grid, .intro-radar-sweep, .pro-particle {
    display: none !important;
}

/* Convex/Extruded Elements */
.glass, .glass-inner, .glass-panel, .form-container, .result-container, .modal-content, .neon-card, .tour-card, .detail-card, .history-card, .intro-portal, .admin-pro-bg {
    background: #e0e0e0 !important;
    border: none !important;
    box-shadow: var(--neumorph-convex) !important;
    backdrop-filter: none !important;
    -webkit-backdrop-filter: none !important;
    border-radius: var(--neumorph-radius-lg) !important;
}

/* Specific Interactive Elements: Buttons */
.btn, .btn-primary, .btn-secondary, .floating-btn, .neon-btn, .enter-portal-btn, .tour-btn, .action-btn {
    background-color: #e0e0e0 !important;
    border-radius: 50px !important;
    box-shadow: inset 4px 4px 10px #bcbcbc, inset -4px -4px 10px #ffffff !important;
    color: #4d4d4d !important;
    cursor: pointer !important;
    transition: all 0.2s ease-in-out !important;
    border: 2px solid rgb(206, 206, 206) !important;
    font-weight: 600 !important;
    text-shadow: none !important;
}

.btn::before, .btn-primary::before, .btn-secondary::before {
    display: none !important; /* Remove gradient sweeps */
}

.btn:hover, .btn-primary:hover, .btn-secondary:hover, .floating-btn:hover, .neon-btn:hover, .enter-portal-btn:hover, .tour-btn:hover, .action-btn:hover,
.btn:focus, .btn-primary:focus, .btn-secondary:focus, .floating-btn:focus, .neon-btn:focus, .enter-portal-btn:focus, .tour-btn:focus, .action-btn:focus,
.btn:active, .btn-primary:active, .btn-secondary:active, .floating-btn:active, .neon-btn:active, .enter-portal-btn:active, .tour-btn:active, .action-btn:active {
    box-shadow: inset 2px 2px 5px #bcbcbc, inset -2px -2px 5px #ffffff, 2px 2px 5px #bcbcbc, -2px -2px 5px #ffffff !important;
    outline: none !important;
    color: #4d4d4d !important;
}

/* Text Box Design (Inputs, Selects, Textareas) */
input:not([type="radio"]):not([type="checkbox"]), select, textarea {
    min-height: 40px !important;
    padding: 10px !important;
    font-family: 'Courier New', Courier, monospace !important;
    outline: none !important;
    background: #e8e8e8 !important;
    box-shadow: 5px 5px 17px #c8c8c8, -5px -5px 17px #ffffff !important;
    border: none !important;
    border-radius: 10px !important;
    transition: all .5s !important;
    color: var(--text-main) !important;
}

input:not([type="radio"]):not([type="checkbox"]):focus, select:focus, textarea:focus {
    background: #e8e8e8 !important;
    box-shadow: inset 5px 5px 17px #c8c8c8, inset -5px -5px 17px #ffffff !important;
}

/* Remove default select arrows for custom styling if needed, or just let them be */

/* Tags (Convex smaller) */
.tag, .intro-tag, .tour-badge {
    background: #e0e0e0 !important;
    border: none !important;
    box-shadow: 4px 4px 8px #bebebe, -4px -4px 8px #ffffff !important;
    color: var(--primary) !important;
    border-radius: var(--neumorph-radius-sm) !important;
}

/* Navbar */
.navbar {
    background: #e0e0e0 !important;
    border-bottom: none !important;
    box-shadow: 0 4px 8px -4px #bebebe !important;
    backdrop-filter: none !important;
}

.nav-links a {
    color: var(--text-muted) !important;
    transition: all 0.3s ease !important;
    padding: 0.5rem 1rem;
    border-radius: var(--neumorph-radius-sm);
}
.nav-links a:hover, .nav-links a.active {
    color: var(--primary) !important;
    box-shadow: var(--neumorph-concave) !important;
    background: #e0e0e0 !important;
}
.nav-links a.active::after {
    display: none !important;
}

/* Typography Overrides */
h1, h2, h3, h4, h5, h6, .gradient-text, .neon-title, .logo span {
    color: var(--text-main) !important;
    background: none !important;
    -webkit-text-fill-color: var(--text-main) !important;
    text-shadow: none !important;
}

p, .text-muted, label, .intro-subtext, .intro-tagline {
    color: var(--text-muted) !important;
}

.logo-icon, i, .orb-icon, .dot-pulse {
    color: var(--primary) !important;
    filter: none !important;
}

/* Special elements like portal rings */
.intro-ring {
    border: none !important;
    box-shadow: var(--neumorph-convex) !important;
}

.intro-orb {
    background: #e0e0e0 !important;
    box-shadow: var(--neumorph-convex) !important;
}

.intro-progress-bar-container {
    background: #e0e0e0 !important;
    box-shadow: var(--neumorph-concave) !important;
    border: none !important;
}

.intro-progress-bar {
    background: var(--primary) !important;
    border-radius: var(--neumorph-radius-sm) !important;
}

/* Tables for Admin */
.admin-table {
    color: var(--text-main) !important;
}
.admin-table th {
    color: var(--text-main) !important;
    border-bottom: 2px solid #bebebe !important;
}
.admin-table td {
    border-bottom: 1px solid #bebebe !important;
}
.admin-table tbody tr:hover {
    background: transparent !important;
    box-shadow: var(--neumorph-concave) !important;
}

/* History Card Adjustments */
.history-card:hover {
    transform: none !important;
    box-shadow: var(--neumorph-concave) !important;
    border-color: transparent !important;
}

/* Detail Card specific */
.detail-card h3 {
    border-bottom: 1px solid #bebebe !important;
    color: var(--text-main) !important;
}
.detail-card li {
    color: var(--text-muted) !important;
}

/* Feedback Modal fixes */
.modal {
    background: rgba(224, 224, 224, 0.8) !important;
    backdrop-filter: blur(4px) !important;
}
.close-modal {
    color: var(--text-muted) !important;
    text-shadow: none !important;
}
.close-modal:hover {
    color: var(--primary) !important;
}

/* Status dots or labels */
.tour-dot {
    background: #bebebe !important;
    box-shadow: var(--neumorph-concave) !important;
}
.tour-dot.active {
    background: var(--primary) !important;
    box-shadow: var(--neumorph-convex) !important;
}

/* From Uiverse.io by adamgiebl */ 
.checkbox {
  display: flex;
  align-items: center;
  margin: 10px;
  font-family: Arial, sans-serif;
  color: var(--text-main) !important;
}

.checkbox input {
  display: none !important;
}

.checkbox .checkmark {
  width: 28px;
  height: 28px;
  border-radius: 10px;
  background-color: #ffffff2b;
  box-shadow: rgba(0, 0, 0, 0.62) 0px 0px 5px inset, rgba(0, 0, 0, 0.21) 0px 0px 0px 24px inset,
        #22cc3f 0px 0px 0px 0px inset, rgba(224, 224, 224, 0.45) 0px 1px 0px 0px;
  cursor: pointer;
  position: relative;
}

.checkbox .checkmark::after {
  content: "";
  width: 18px;
  height: 18px;
  border-radius: 5px;
  background-color: #e3e3e3;
  box-shadow: transparent 0px 0px 0px 2px, rgba(0, 0, 0, 0.3) 0px 6px 6px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  transition: background-color 0.3s ease-in-out;
}

.checkbox input:checked + .checkmark {
  background-color: #22cc3f;
  box-shadow: rgba(0, 0, 0, 0.62) 0px 0px 5px inset, #22cc3f 0px 0px 0px 2px inset, #22cc3f 0px 0px 0px 24px inset,
        rgba(224, 224, 224, 0.45) 0px 1px 0px 0px;
}

.checkbox input:checked + .checkmark::after {
  background-color: white;
}

.checkbox .label {
  margin-right: 10px;
  user-select: none;
  font-weight: 700;
  cursor: pointer;
}

/* Make sure text within inputs is legible */
input::placeholder, textarea::placeholder {
    color: #999999 !important;
}

/* Fix inline styles in some elements */
div[style*="background: rgba(0, 0, 0, 0.2)"] {
    background: #e0e0e0 !important;
    box-shadow: var(--neumorph-concave) !important;
    border: none !important;
}
div[style*="border: 1px solid rgba(255, 255, 255, 0.1)"],
div[style*="border: 1px solid rgba(239, 68, 68, 0.2)"] {
    border: none !important;
}


/* From Uiverse.io by Harsha2lucky */ 
.content {
  width: 330px;
  padding: 40px 30px;
  background: #dde1e7;
  border-radius: 10px;
  box-shadow: -3px -3px 7px #ffffff73,
               2px 2px 5px rgba(94,104,121,0.288);
}

.content .text {
  font-size: 33px;
  font-weight: 600;
  margin-bottom: 35px;
  color: #595959 !important;
}

.field {
  height: 50px;
  width: 100%;
  display: flex;
  position: relative;
  margin-bottom: 20px;
}

.field:nth-child(2) {
  margin-top: 20px;
}

.field .input {
  height: 100%;
  width: 100%;
  padding-left: 45px !important;
  outline: none;
  border: none;
  font-size: 18px;
  background: #dde1e7 !important;
  color: #595959 !important;
  border-radius: 25px !important;
  box-shadow: inset 2px 2px 5px #BABECC,
              inset -5px -5px 10px #ffffff73 !important;
}

.field .input:focus {
  box-shadow: inset 1px 1px 2px #BABECC,
              inset -1px -1px 2px #ffffff73 !important;
}

.field .span {
  position: absolute;
  color: #595959;
  width: 50px;
  line-height: 50px;
  text-align: center;
}

.field .label {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  left: 45px;
  pointer-events: none;
  color: #666666;
  transition: opacity 0.3s ease;
}

.field .input:valid ~ .label,
.field .input:focus ~ .label {
  opacity: 0;
}

.forgot-pass {
  text-align: left;
  margin: 10px 0 10px 5px;
}

.forgot-pass a {
  font-size: 16px;
  color: #666666;
  text-decoration: none;
}

.forgot-pass:hover a {
  text-decoration: underline;
}

.content .button {
  margin: 15px 0;
  width: 100%;
  height: 50px;
  font-size: 18px;
  line-height: 50px;
  font-weight: 600;
  background: #dde1e7 !important;
  border-radius: 25px !important;
  border: none;
  outline: none;
  cursor: pointer;
  color: #595959 !important;
  box-shadow: 2px 2px 5px #BABECC,
             -5px -5px 10px #ffffff73 !important;
}

.content .button:focus, .content .button:hover {
  color: #2B303A !important;
  box-shadow: inset 2px 2px 5px #BABECC,
             inset -5px -5px 10px #ffffff73 !important;
}

.sign-up {
  margin: 10px 0;
  color: #595959;
  font-size: 16px;
}

.sign-up a {
  color: #2B303A;
  text-decoration: none;
}

.sign-up a:hover {
  text-decoration: underline;
}

/* 1. Inter Font Integration & Colors */
body {
    font-family: 'Inter', sans-serif !important;
}

/* Style the dropdown text and placeholders */
select {
    font-family: 'Inter', sans-serif !important;
    font-size: 14px !important;
    color: #2B303A !important;
    cursor: pointer !important;
}

select option {
    background-color: #e8e8e8 !important;
    color: #2B303A !important;
    font-family: 'Inter', sans-serif !important;
    font-size: 14px !important;
    padding: 12px !important;
}

select option:first-child {
    color: #A0AAB5 !important;
}

select option:checked, select option:hover {
    background-color: #c8c8c8 !important; /* Darker gray for selection */
    color: #1A1D23 !important;
    /* Inset shadow trick to override native blue highlight in some browsers */
    box-shadow: 0 0 10px 100px #c8c8c8 inset !important;
}

/* Style the Form Labels */
label, .label-text {
    font-family: 'Inter', sans-serif !important;
    font-size: 13px !important;
    font-weight: 500 !important;
    color: #5A626A !important;
    margin-bottom: 6px !important;
}

/* Custom Select Dropdown UI */
.custom-select-wrapper {
    position: relative;
    width: 100%;
}

.custom-select-display {
    min-height: 40px !important;
    padding: 10px 15px !important;
    background: #e8e8e8 !important;
    box-shadow: 5px 5px 17px #c8c8c8, -5px -5px 17px #ffffff !important;
    border-radius: 10px !important;
    font-family: 'Inter', sans-serif !important;
    font-size: 14px !important;
    color: #2B303A !important;
    cursor: pointer;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: all .5s !important;
}

.custom-select-display.open {
    box-shadow: inset 5px 5px 17px #c8c8c8, inset -5px -5px 17px #ffffff !important;
}

.custom-select-display i {
    transition: transform 0.3s ease;
    color: #5A626A !important;
}
.custom-select-display.open i {
    transform: rotate(180deg);
}

.custom-select-options {
    position: absolute;
    top: calc(100% + 10px);
    left: 0;
    right: 0;
    background: #e8e8e8 !important;
    border-radius: 10px !important;
    box-shadow: 5px 5px 17px #c8c8c8, -5px -5px 17px #ffffff !important;
    z-index: 100;
    overflow: hidden;
    opacity: 0;
    visibility: hidden;
    transform: translateY(-10px);
    transition: all 0.3s ease;
    max-height: 250px;
    overflow-y: auto;
}

.custom-select-options.open {
    opacity: 1;
    visibility: visible;
    transform: translateY(0);
}

.custom-option {
    padding: 12px 15px !important;
    font-family: 'Inter', sans-serif !important;
    font-size: 14px !important;
    color: #2B303A !important;
    cursor: pointer;
    transition: background 0.2s ease !important;
}

.custom-option:hover {
    background: #c8c8c8 !important;
}

.custom-option.selected {
    background: #d1d5db !important;
    font-weight: 600 !important;
}

.custom-option.disabled {
    color: #A0AAB5 !important;
    cursor: not-allowed;
    background: transparent !important;
}
.custom-option.disabled:hover {
    background: transparent !important;
}

/* Custom Scrollbar for dropdown */
.custom-select-options::-webkit-scrollbar {
    width: 8px;
}
.custom-select-options::-webkit-scrollbar-track {
    background: #e8e8e8;
    border-radius: 10px;
}
.custom-select-options::-webkit-scrollbar-thumb {
    background: #bcbcbc;
    border-radius: 10px;
}

`;

fs.writeFileSync(cssPath, styleCss.trim() + '\n\n' + premiumNeumorphism);
console.log('Successfully appended Premium Neumorphism Overrides to style.css');
