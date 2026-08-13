const fs = require('fs');
const file = 'd:/AI Project Idea Generator/src/main/resources/static/css/style.css';
let content = fs.readFileSync(file, 'utf8');

const appendCss = `
/* Tour Overlay Closing Animation */
.tour-overlay.closing {
    animation: tourOverlayFadeOut 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55) forwards;
}

.tour-overlay.closing .tour-card {
    animation: tourCardPopOut 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55) forwards;
}

@keyframes tourOverlayFadeOut {
    0% { opacity: 1; backdrop-filter: blur(14px); -webkit-backdrop-filter: blur(14px); }
    100% { opacity: 0; backdrop-filter: blur(0px); -webkit-backdrop-filter: blur(0px); }
}

@keyframes tourCardPopOut {
    0% { opacity: 1; transform: scale(1) translateY(0); }
    100% { opacity: 0; transform: scale(0.6) translateY(40px); }
}
`;

fs.writeFileSync(file, content + appendCss, 'utf8');
console.log('Appended closing animation CSS.');
