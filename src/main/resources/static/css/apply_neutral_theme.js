const fs = require('fs');

let css = fs.readFileSync('style.css', 'utf8');

// Root variables
css = css.replace(/--bg-dark:\s*#[0-9a-fA-F]+;/gi, '--bg-dark: #CCBEB1;');
css = css.replace(/--bg-darker:\s*#[0-9a-fA-F]+;/gi, '--bg-darker: #FFDBBB;');
css = css.replace(/--primary:\s*#[0-9a-fA-F]+;/gi, '--primary: #997E67;');
css = css.replace(/--primary-hover:\s*#[0-9a-fA-F]+;/gi, '--primary-hover: #664930;');
css = css.replace(/--secondary:\s*#[0-9a-fA-F]+;/gi, '--secondary: #997E67;');
css = css.replace(/--text-main:\s*#[0-9a-fA-F]+;/gi, '--text-main: #664930;');
css = css.replace(/--text-muted:\s*#[0-9a-fA-F]+;/gi, '--text-muted: #997E67;');

// Hex colors
const hexMap = {
    '#6366f1': '#997E67',
    '#ec4899': '#664930',
    '#38bdf8': '#997E67',
    '#a855f7': '#664930',
    '#f43f5e': '#997E67',
    '#10b981': '#664930',
    '#eab308': '#997E67',
    '#8b5cf6': '#664930',
    '#0f172a': '#CCBEB1',
    '#020617': '#FFDBBB',
    '#1e1b4b': '#CCBEB1',
    '#090d16': '#FFDBBB',
    '#111827': '#CCBEB1',
    '#1f2937': '#FFDBBB',
    '#ffffff': '#FFDBBB',
    '#f8fafc': '#FFDBBB',
    '#c7d2fe': '#997E67',
    '#94a3b8': '#997E67',
    '#e2e8f0': '#664930',
    '#9ca3af': '#997E67',
    '#fbbf24': '#664930',
    '#818cf8': '#997E67',
    '#f472b6': '#664930',
    '#34d399': '#997E67'
};

for (const [oldHex, newHex] of Object.entries(hexMap)) {
    css = css.replace(new RegExp(oldHex, 'gi'), newHex);
}

// RGBA colors
const rgbaMap = {
    'rgba\\(\\s*99,\\s*102,\\s*241': 'rgba(153, 126, 103',
    'rgba\\(\\s*236,\\s*72,\\s*153': 'rgba(102, 73, 48',
    'rgba\\(\\s*56,\\s*189,\\s*248': 'rgba(153, 126, 103',
    'rgba\\(\\s*139,\\s*92,\\s*246': 'rgba(102, 73, 48',
    'rgba\\(\\s*168,\\s*85,\\s*247': 'rgba(102, 73, 48',
    'rgba\\(\\s*239,\\s*68,\\s*68': 'rgba(153, 126, 103',
    'rgba\\(\\s*16,\\s*185,\\s*129': 'rgba(102, 73, 48',
    'rgba\\(\\s*30,\\s*41,\\s*59': 'rgba(255, 219, 187',
    'rgba\\(\\s*15,\\s*23,\\s*42': 'rgba(204, 190, 177',
    'rgba\\(\\s*2,\\s*6,\\s*23': 'rgba(255, 219, 187',
    'rgba\\(\\s*255,\\s*255,\\s*255': 'rgba(102, 73, 48',
    'rgba\\(\\s*0,\\s*0,\\s*0': 'rgba(102, 73, 48'
};

for (const [oldRgba, newRgba] of Object.entries(rgbaMap)) {
    css = css.replace(new RegExp(oldRgba, 'gi'), newRgba);
}

// Fix root glassmorphism since the loop might have replaced its components weirdly if not careful,
// but the regexes above will cleanly replace the `rgba(30, 41, 59` to `rgba(255, 219, 187`.

// Fix text colors which might have been converted to background colors if #ffffff was replaced with #FFDBBB
// Actually, `color: #FFDBBB` would be invisible on `#FFDBBB` background.
// So let's replace `color: #FFDBBB` with `color: #664930`
css = css.replace(/color:\s*#FFDBBB/gi, 'color: #664930');
// Also if any text-shadow was using rgba(102, 73, 48) instead of white, it's actually good for light theme.

fs.writeFileSync('style.css', css);
console.log('Replaced colors successfully!');
