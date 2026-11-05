// SAJGOJ FASHION - Progressive Web App (PWA) Offline Service Worker
const CACHE_NAME = 'sajgoj-offline-v1';

const STATIC_ASSETS = [
  './',
  './index.html',
  './200.html',
  './app.min.js',
  './manifest.webmanifest',
  './pwa-192x192.png',
  './pwa-512x512.png',
  './apple-touch-icon.png',
  './favicon.svg',
  './pos/',
  './pos/index.html',
  'https://cdn.tailwindcss.com',
  'https://cdnjs.cloudflare.com/ajax/libs/react/18.2.0/umd/react.production.min.js',
  'https://cdnjs.cloudflare.com/ajax/libs/react-dom/18.2.0/umd/react-dom.production.min.js',
  'https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js',
  'https://cdn.jsdelivr.net/npm/jsqr@1.4.0/dist/jsQR.min.js',
  'https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js',
  'https://www.gstatic.com/firebasejs/10.14.1/firebase-app-compat.js',
  'https://www.gstatic.com/firebasejs/10.14.1/firebase-auth-compat.js',
  'https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore-compat.js',
  'https://fonts.googleapis.com/css2?family=Hind+Siliguri:wght@400;500;600;700&display=swap'
];

// 1. Install Event: Pre-cache core shell & CDN libraries
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      // Try to cache critical assets gracefully
      return Promise.allSettled(
        STATIC_ASSETS.map((url) =>
          cache.add(url).catch((err) => {
            console.warn(`[ServiceWorker] Failed to cache ${url}:`, err);
          })
        )
      );
    }).then(() => self.skipWaiting())
  );
});

// 2. Activate Event: Clean up outdated caches
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) => {
      return Promise.all(
        keys.map((key) => {
          if (key !== CACHE_NAME) {
            return caches.delete(key);
          }
        })
      );
    }).then(() => self.clients.claim())
  );
});

// 3. Fetch Event: Stale-While-Revalidate with Offline Navigation Fallback
self.addEventListener('fetch', (event) => {
  if (event.request.method !== 'GET') return;

  const url = new URL(event.request.url);

  // Skip chrome-extension, internal protocols
  if (!url.protocol.startsWith('http')) return;

  // Navigation requests (HTML documents)
  if (event.request.mode === 'navigate') {
    event.respondWith(
      fetch(event.request)
        .then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200) {
            const responseClone = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(event.request, responseClone);
            });
          }
          return networkResponse;
        })
        .catch(async () => {
          // Offline fallback
          const cache = await caches.open(CACHE_NAME);
          if (url.pathname.startsWith('/pos')) {
            const posCached = await cache.match('./pos/index.html') || await cache.match('/pos/index.html');
            if (posCached) return posCached;
          }
          const cachedIndex = await cache.match('./index.html') || await cache.match('/index.html') || await cache.match('./200.html');
          return cachedIndex || new Response("SAJGOJ FASHION is running in offline mode.", {
            headers: { 'Content-Type': 'text/html;charset=utf-8' }
          });
        })
    );
    return;
  }

  // Assets, Scripts, Styles & Images
  event.respondWith(
    caches.match(event.request).then((cachedResponse) => {
      const fetchPromise = fetch(event.request)
        .then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200) {
            const responseClone = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(event.request, responseClone);
            });
          }
          return networkResponse;
        })
        .catch(() => cachedResponse);

      // Return cached version immediately if available, otherwise wait for network
      return cachedResponse || fetchPromise;
    })
  );
});
