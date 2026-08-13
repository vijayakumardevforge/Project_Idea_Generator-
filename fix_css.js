const fs = require('fs');
const file = 'd:/AI Project Idea Generator/src/main/resources/static/css/style.css';
let content = fs.readFileSync(file, 'utf8');

const replacement = `    transition: opacity 0.2s ease, transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.tour-body.tour-step-transitioning {
    opacity: 0;
    transform: translateY(12px) scale(0.98);
}

.tour-body.tour-step-animate-in {
    animation: tourStepSpringIn 0.42s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes tourStepSpringIn {
    0% { opacity: 0; transform: translateY(14px) scale(0.97); }
    100% { opacity: 1; transform: translateY(0) scale(1); }
}

.tour-title {
    font-size: 1.95rem; font-weight: 800; color: #ffffff;
    line-height: 1.3; letter-spacing: -0.01em; text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
}

.tour-description {
    color: #e2e8f0; font-size: 1.12rem; line-height: 1.7; font-weight: 400;
}

.tour-description strong {
    color: #38bdf8; font-weight: 700;
}

.tour-visual {
    margin-top: 0.6rem;
    background: linear-gradient(135deg, rgba(30, 41, 59, 0.85) 0%, rgba(15, 23, 42, 0.92) 100%);
    border: 1px solid rgba(56, 189, 248, 0.35); border-radius: 1rem; padding: 1.15rem 1.4rem;
    color: #38bdf8; font-weight: 600; font-size: 1rem;
    display: flex; align-items: center; gap: 1rem;
    box-shadow: inset 0 0 20px rgba(56, 189, 248, 0.12), 0 8px 25px rgba(0, 0, 0, 0.4);
    animation: tourVisualFloat 4s ease-in-out infinite alternate;
}

@keyframes tourVisualFloat {
    0% { transform: translateY(0); border-color: rgba(56, 189, 248, 0.35); }
    100% { transform: translateY(-4px); border-color: rgba(236, 72, 153, 0.45); }
}

.tour-visual i {
    font-size: 1.5rem; color: #ec4899; filter: drop-shadow(0 0 8px rgba(236, 72, 153, 0.6)); flex-shrink: 0;
}

.tour-footer {
    display: flex; justify-content: space-between; align-items: center;
    margin-top: 0.5rem; padding-top: 1.25rem;`;

const index = content.indexOf('    transition: opacity 0.2s ease, transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);');
if (index !== -1) {
    const endFooter = content.indexOf('    border-top: 1px solid rgba(255, 255, 255, 0.12);', index);
    if (endFooter !== -1) {
        content = content.substring(0, index) + replacement + '\n' + content.substring(endFooter);
        fs.writeFileSync(file, content, 'utf8');
        console.log('Fixed');
    }
}
