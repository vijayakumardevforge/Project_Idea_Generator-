const fs = require('fs');

let html = fs.readFileSync('../index.html', 'utf8');

// Hex colors
const hexMap = {
    '#ef4444': '#664930', // error text
    '#10b981': '#997E67', // success text
};

for (const [oldHex, newHex] of Object.entries(hexMap)) {
    html = html.replace(new RegExp(oldHex, 'gi'), newHex);
}

// RGBA colors
const rgbaMap = {
    'rgba\\(\\s*239,\\s*68,\\s*68': 'rgba(102, 73, 48', // error bg/border
    'rgba\\(\\s*255,\\s*255,\\s*255': 'rgba(102, 73, 48', // general border
    'rgba\\(\\s*0,\\s*0,\\s*0': 'rgba(255, 219, 187' // input bg
};

for (const [oldRgba, newRgba] of Object.entries(rgbaMap)) {
    html = html.replace(new RegExp(oldRgba, 'gi'), newRgba);
}

fs.writeFileSync('../index.html', html);
console.log('Replaced HTML colors successfully!');
