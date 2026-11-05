const fs = require('fs');
const path = require('path');
const esbuild = require('esbuild');

console.log('⚡ Starting high-performance production build for Cloudflare...');

if (!fs.existsSync('public')) {
  fs.mkdirSync('public', { recursive: true });
}

// 1. Read index.html
const rawHtml = fs.readFileSync('index.html', 'utf8');

const startTag = '<script type="text/babel">';
const endTag = '</script>';
const startIndex = rawHtml.indexOf(startTag);
const endIndex = rawHtml.lastIndexOf(endTag);

if (startIndex === -1 || endIndex === -1) {
  console.error('❌ Could not locate <script type="text/babel"> block in index.html');
  process.exit(1);
}

const beforeScript = rawHtml.substring(0, startIndex);
const jsxCode = rawHtml.substring(startIndex + startTag.length, endIndex);
const afterScript = rawHtml.substring(endIndex + endTag.length);

console.log(`📦 Transpiling and minifying ${jsxCode.length} bytes of React JSX with esbuild...`);
const t0 = Date.now();

// 2. Pre-compile JSX -> Minified JS AOT (Ahead-of-Time)
const result = esbuild.transformSync(jsxCode, {
  loader: 'jsx',
  minify: true,
  target: 'es2020',
  jsxFactory: 'React.createElement',
  jsxFragment: 'React.Fragment'
});

const t1 = Date.now();
console.log(`⚡ Pre-compilation completed in ${t1 - t0}ms. Output: ${result.code.length} bytes.`);

// Write compiled JS
fs.writeFileSync(path.join('public', 'app.min.js'), result.code);

// 3. Transform HTML for ultra-fast loading:
// - Remove Babel-standalone entirely (saving ~4.5 MB!)
// - Replace inline JSX script with <script src="./app.min.js" defer></script>
// - Add defer to heavy secondary scripts (qrcodejs, jsQR, jspdf)
let optimizedHead = beforeScript
  .replace(/<script\s+src="[^"]*babel-standalone[^"]*"><\/script>/gi, '')
  .replace(/<script\s+src="([^"]*qrcode[^"]*)"><\/script>/gi, '<script src="$1" defer></script>')
  .replace(/<script\s+src="([^"]*jsqr[^"]*)"><\/script>/gi, '<script src="$1" defer></script>')
  .replace(/<script\s+src="([^"]*jspdf[^"]*)"><\/script>/gi, '<script src="$1" defer></script>');

const optimizedHtml = optimizedHead + '<script src="./app.min.js" defer></script>' + afterScript;

// Write public/index.html and public/200.html
fs.writeFileSync(path.join('public', 'index.html'), optimizedHtml);
fs.writeFileSync(path.join('public', '200.html'), optimizedHtml);

// 4. Sync PWA files
if (fs.existsSync('manifest.webmanifest')) {
  fs.copyFileSync('manifest.webmanifest', path.join('public', 'manifest.webmanifest'));
}

// 5. Update and sync Service Worker to precache app.min.js
if (fs.existsSync('public/sw.js')) {
  let swContent = fs.readFileSync('public/sw.js', 'utf8');
  if (!swContent.includes('./app.min.js')) {
    swContent = swContent.replace("'./200.html',", "'./200.html',\n  './app.min.js',");
    fs.writeFileSync('public/sw.js', swContent);
  }
}

// 6. Ensure no invalid _redirects file
if (fs.existsSync('public/_redirects')) fs.unlinkSync('public/_redirects');
if (fs.existsSync('_redirects')) fs.unlinkSync('_redirects');

console.log('✅ Ultra-fast production build complete! Babel removed, JS pre-compiled & minified.');
