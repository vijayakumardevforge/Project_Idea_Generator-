const fs = require('fs');
const file = 'd:/AI Project Idea Generator/src/main/resources/static/css/style.css';
let content = fs.readFileSync(file, 'utf8');

// Replace .tour-overlay transition with animation
content = content.replace(
    '    transition: opacity 0.4s cubic-bezier(0.16, 1, 0.3, 1), backdrop-filter 0.4s cubic-bezier(0.16, 1, 0.3, 1);\r\n}',
    '    animation: tourOverlayFadeIn 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55) forwards;\r\n}\r\n\r\n@keyframes tourOverlayFadeIn {\r\n    0% { opacity: 0; backdrop-filter: blur(0px); }\r\n    100% { opacity: 1; backdrop-filter: blur(14px); }\r\n}'
);
content = content.replace(
    '    transition: opacity 0.4s cubic-bezier(0.16, 1, 0.3, 1), backdrop-filter 0.4s cubic-bezier(0.16, 1, 0.3, 1);\n}',
    '    animation: tourOverlayFadeIn 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55) forwards;\n}\n\n@keyframes tourOverlayFadeIn {\n    0% { opacity: 0; backdrop-filter: blur(0px); }\n    100% { opacity: 1; backdrop-filter: blur(14px); }\n}'
);

// Replace .tour-card transition with animation
content = content.replace(
    '    transition: transform 0.4s cubic-bezier(0.16, 1, 0.3, 1);\r\n    overflow: hidden;\r\n}',
    '    animation: tourCardPopIn 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55) forwards;\r\n    overflow: hidden;\r\n}\r\n\r\n@keyframes tourCardPopIn {\r\n    0% { opacity: 0; transform: scale(0.6) translateY(40px); }\r\n    100% { opacity: 1; transform: scale(1) translateY(0); }\r\n}'
);
content = content.replace(
    '    transition: transform 0.4s cubic-bezier(0.16, 1, 0.3, 1);\n    overflow: hidden;\n}',
    '    animation: tourCardPopIn 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55) forwards;\n    overflow: hidden;\n}\n\n@keyframes tourCardPopIn {\n    0% { opacity: 0; transform: scale(0.6) translateY(40px); }\n    100% { opacity: 1; transform: scale(1) translateY(0); }\n}'
);


// Replace tourStepSpringIn
content = content.replace(
    '.tour-body.tour-step-animate-in {\r\n    animation: tourStepSpringIn 0.42s cubic-bezier(0.16, 1, 0.3, 1) forwards;\r\n}\r\n\r\n@keyframes tourStepSpringIn {\r\n    0% { opacity: 0; transform: translateY(14px) scale(0.97); }\r\n    100% { opacity: 1; transform: translateY(0) scale(1); }\r\n}',
    '.tour-body.tour-step-animate-in {\r\n    animation: tourStepSpringIn 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55) forwards;\r\n}\r\n\r\n@keyframes tourStepSpringIn {\r\n    0% { opacity: 0; transform: translateY(30px) scale(0.85); }\r\n    100% { opacity: 1; transform: translateY(0) scale(1); }\r\n}'
);
content = content.replace(
    '.tour-body.tour-step-animate-in {\n    animation: tourStepSpringIn 0.42s cubic-bezier(0.16, 1, 0.3, 1) forwards;\n}\n\n@keyframes tourStepSpringIn {\n    0% { opacity: 0; transform: translateY(14px) scale(0.97); }\n    100% { opacity: 1; transform: translateY(0) scale(1); }\n}',
    '.tour-body.tour-step-animate-in {\n    animation: tourStepSpringIn 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55) forwards;\n}\n\n@keyframes tourStepSpringIn {\n    0% { opacity: 0; transform: translateY(30px) scale(0.85); }\n    100% { opacity: 1; transform: translateY(0) scale(1); }\n}'
);

fs.writeFileSync(file, content, 'utf8');
console.log('Animations updated.');
