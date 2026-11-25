// SAJGOJ FASHION - PWA Offline Service Worker with IndexedDB Catalog Cache & Order Background Sync
const CACHE_NAME = 'sajgoj-offline-v3';
const CATALOG_MEDIA_CACHE = 'sajgoj-catalog-media-v2';
const IDB_NAME = 'sajgoj_fashion_db';
const IDB_VERSION = 2;
const STORE_DATA = 'store_data';
const STORE_CATALOG = 'product_catalog_cache';
const STORE_ORDER_QUEUE = 'offline_orders_queue';
const SYNC_TAG_ORDERS = 'sync-offline-orders';

const FIRESTORE_PROJECT_ID = 'gen-lang-client-0562799796';
const FIRESTORE_DATABASE_ID = 'ai-studio-android-sajgojfa-3e596357-5132-4fa3-9784-b12eabde40bf';

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

// =========================================================
// INDEXEDDB HELPERS INSIDE SERVICE WORKER
// =========================================================
function openSajgojIdb() {
  return new Promise((resolve, reject) => {
    try {
      const req = indexedDB.open(IDB_NAME, IDB_VERSION);
      req.onupgradeneeded = (event) => {
        const db = event.target.result;
        if (!db.objectStoreNames.contains(STORE_DATA)) {
          db.createObjectStore(STORE_DATA);
        }
        if (!db.objectStoreNames.contains(STORE_CATALOG)) {
          db.createObjectStore(STORE_CATALOG, { keyPath: 'id' });
        }
        if (!db.objectStoreNames.contains(STORE_ORDER_QUEUE)) {
          db.createObjectStore(STORE_ORDER_QUEUE, { keyPath: 'id' });
        }
      };
      req.onsuccess = () => resolve(req.result);
      req.onerror = () => reject(req.error || new Error('Failed to open IndexedDB'));
    } catch (err) {
      reject(err);
    }
  });
}

async function idbGet(storeName, key) {
  const db = await openSajgojIdb();
  return new Promise((resolve) => {
    try {
      const tx = db.transaction(storeName, 'readonly');
      const req = tx.objectStore(storeName).get(key);
      req.onsuccess = () => resolve(req.result || null);
      req.onerror = () => resolve(null);
    } catch (e) {
      resolve(null);
    }
  });
}

async function idbGetAll(storeName) {
  const db = await openSajgojIdb();
  return new Promise((resolve) => {
    try {
      const tx = db.transaction(storeName, 'readonly');
      const req = tx.objectStore(storeName).getAll();
      req.onsuccess = () => resolve(Array.isArray(req.result) ? req.result : []);
      req.onerror = () => resolve([]);
    } catch (e) {
      resolve([]);
    }
  });
}

async function idbPut(storeName, value, explicitKey) {
  const db = await openSajgojIdb();
  return new Promise((resolve) => {
    try {
      const tx = db.transaction(storeName, 'readwrite');
      const store = tx.objectStore(storeName);
      const req = explicitKey !== undefined ? store.put(value, explicitKey) : store.put(value);
      req.onsuccess = () => resolve(true);
      req.onerror = () => resolve(false);
    } catch (e) {
      resolve(false);
    }
  });
}

async function idbDelete(storeName, key) {
  const db = await openSajgojIdb();
  return new Promise((resolve) => {
    try {
      const tx = db.transaction(storeName, 'readwrite');
      const req = tx.objectStore(storeName).delete(key);
      req.onsuccess = () => resolve(true);
      req.onerror = () => resolve(false);
    } catch (e) {
      resolve(false);
    }
  });
}

async function cacheCatalogIntoIndexedDb(productsList, categoriesList) {
  if (!Array.isArray(productsList)) return 0;
  try {
    const db = await openSajgojIdb();
    await new Promise((resolve) => {
      try {
        const tx = db.transaction([STORE_CATALOG, STORE_DATA], 'readwrite');
        const catalogStore = tx.objectStore(STORE_CATALOG);
        const dataStore = tx.objectStore(STORE_DATA);
        catalogStore.clear();
        const now = Date.now();
        productsList.forEach((p, idx) => {
          if (p && (p.id || p.sku)) {
            catalogStore.put({
              ...p,
              id: String(p.id || p.sku || `prod_${idx}`),
              cachedAt: now
            });
          }
        });
        dataStore.put(productsList, 'products');
        if (Array.isArray(categoriesList) && categoriesList.length > 0) {
          dataStore.put(categoriesList, 'categories');
        }
        tx.oncomplete = () => resolve(true);
        tx.onerror = () => resolve(false);
      } catch (err) {
        resolve(false);
      }
    });

    // Warm CacheStorage with product images so thumbnails render smoothly offline
    const mediaCache = await caches.open(CATALOG_MEDIA_CACHE);
    const imageUrls = new Set();
    productsList.forEach((p) => {
      if (!p) return;
      const imgs = Array.isArray(p.images) ? p.images : (p.image ? [p.image] : []);
      imgs.forEach((u) => {
        if (typeof u === 'string' && u.startsWith('http') && imageUrls.size < 120) {
          imageUrls.add(u);
        }
      });
      if (typeof p.videoThumbUrl === 'string' && p.videoThumbUrl.startsWith('http') && imageUrls.size < 120) {
        imageUrls.add(p.videoThumbUrl);
      }
    });

    await Promise.allSettled(
      Array.from(imageUrls).map(async (url) => {
        const existing = await mediaCache.match(url);
        if (!existing) {
          const req = new Request(url, { mode: 'no-cors' });
          const res = await fetch(req).catch(() => null);
          if (res && (res.ok || res.type === 'opaque')) {
            await mediaCache.put(url, res);
          }
        }
      })
    );
    return productsList.length;
  } catch (err) {
    console.warn('[ServiceWorker] Failed caching catalog into IndexedDB:', err);
    return 0;
  }
}

// Convert JS object into Firestore REST v1 Value format for headless background sync fallback
function toFirestoreValue(val) {
  if (val === null || val === undefined) return { nullValue: null };
  if (typeof val === 'boolean') return { booleanValue: val };
  if (typeof val === 'number') {
    return Number.isInteger(val) ? { integerValue: String(val) } : { doubleValue: val };
  }
  if (typeof val === 'string') return { stringValue: val };
  if (Array.isArray(val)) {
    return { arrayValue: { values: val.map((item) => toFirestoreValue(item)) } };
  }
  if (typeof val === 'object') {
    const fields = {};
    Object.keys(val).forEach((k) => {
      if (val[k] !== undefined) fields[k] = toFirestoreValue(val[k]);
    });
    return { mapValue: { fields } };
  }
  return { stringValue: String(val) };
}

async function flushOfflineOrdersInSw() {
  const queue = await idbGetAll(STORE_ORDER_QUEUE);
  if (!queue || queue.length === 0) return { synced: 0, remaining: 0 };

  const clientsList = await self.clients.matchAll({ type: 'window', includeUncontrolled: true });
  if (clientsList && clientsList.length > 0) {
    // Active window client has initialized Firebase SDK & UI state — delegate sync and refresh UI
    clientsList.forEach((client) => {
      client.postMessage({
        type: 'SAJGOJ_SW_SYNC_OFFLINE_ORDERS',
        pendingCount: queue.length,
        timestamp: Date.now()
      });
    });
    return { delegatedToClient: true, pendingCount: queue.length };
  }

  // Headless background fallback when all tabs are closed: commit via Firestore v1 REST API
  let synced = 0;
  let remaining = 0;
  for (const entry of queue) {
    if (!entry || !entry.id || !entry.orderData) continue;
    const payload = {
      ...entry.orderData,
      syncedFromOfflineQueue: true,
      offlineQueuedAt: entry.queuedAt || Date.now(),
      syncedAt: Date.now()
    };
    const fields = {};
    Object.keys(payload).forEach((k) => {
      if (payload[k] !== undefined) fields[k] = toFirestoreValue(payload[k]);
    });

    const endpoint = `https://firestore.googleapis.com/v1/projects/${FIRESTORE_PROJECT_ID}/databases/${FIRESTORE_DATABASE_ID}/documents/orders/${encodeURIComponent(entry.id)}`;
    try {
      const res = await fetch(endpoint, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fields })
      });
      if (res.ok) {
        await idbDelete(STORE_ORDER_QUEUE, entry.id);
        synced++;
      } else {
        remaining++;
        await idbPut(STORE_ORDER_QUEUE, {
          ...entry,
          attempts: (entry.attempts || 0) + 1,
          syncStatus: 'pending',
          lastError: `HTTP ${res.status}`
        });
      }
    } catch (err) {
      remaining++;
      await idbPut(STORE_ORDER_QUEUE, {
        ...entry,
        attempts: (entry.attempts || 0) + 1,
        syncStatus: 'pending',
        lastError: err && err.message ? err.message : 'Network unavailable'
      });
    }
  }
  return { synced, remaining };
}

// =========================================================
// 1. INSTALL EVENT: Pre-cache core shell & CDN libraries
// =========================================================
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
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

// =========================================================
// 2. ACTIVATE EVENT: Clean up outdated caches & initialize IDB
// =========================================================
self.addEventListener('activate', (event) => {
  event.waitUntil(
    Promise.all([
      caches.keys().then((keys) => {
        return Promise.all(
          keys.map((key) => {
            if (key !== CACHE_NAME && key !== CATALOG_MEDIA_CACHE) {
              return caches.delete(key);
            }
          })
        );
      }),
      openSajgojIdb().catch(() => null)
    ]).then(() => self.clients.claim())
  );
});

// =========================================================
// 3. MESSAGE BRIDGE: Catalog Caching & Order Queue Commands
// =========================================================
self.addEventListener('message', (event) => {
  const data = event.data || {};
  if (!data || !data.type) return;

  if (data.type === 'CACHE_PRODUCT_CATALOG') {
    event.waitUntil(
      cacheCatalogIntoIndexedDb(data.products || [], data.categories || []).then((count) => {
        if (event.ports && event.ports[0]) {
          event.ports[0].postMessage({ ok: true, count, updatedAt: Date.now() });
        }
      })
    );
  } else if (data.type === 'QUEUE_OFFLINE_ORDER' && data.queueEntry) {
    event.waitUntil(
      idbPut(STORE_ORDER_QUEUE, data.queueEntry).then(async () => {
        if (self.registration && self.registration.sync) {
          try {
            await self.registration.sync.register(SYNC_TAG_ORDERS);
          } catch (e) {}
        }
      })
    );
  } else if (data.type === 'REMOVE_SYNCED_OFFLINE_ORDER' && data.orderId) {
    event.waitUntil(idbDelete(STORE_ORDER_QUEUE, data.orderId));
  } else if (data.type === 'TRIGGER_OFFLINE_ORDER_SYNC') {
    event.waitUntil(flushOfflineOrdersInSw());
  }
});

// =========================================================
// 4. BACKGROUND SYNC EVENT: Auto-sync queued offline orders
// =========================================================
self.addEventListener('sync', (event) => {
  if (event.tag === SYNC_TAG_ORDERS) {
    event.waitUntil(flushOfflineOrdersInSw());
  }
});

// =========================================================
// 5. FETCH EVENT: Serve Shell, IndexedDB Catalog & Cached Images
// =========================================================
self.addEventListener('fetch', (event) => {
  if (event.request.method !== 'GET') return;

  const url = new URL(event.request.url);
  if (!url.protocol.startsWith('http')) return;

  // Serve cached product catalog directly from IndexedDB when queried
  if (url.pathname.endsWith('/offline-catalog.json') || url.searchParams.get('offline-catalog') === '1') {
    event.respondWith(
      (async () => {
        let products = await idbGetAll(STORE_CATALOG);
        if (!products || products.length === 0) {
          const fallback = await idbGet(STORE_DATA, 'products');
          products = Array.isArray(fallback) ? fallback : [];
        }
        const categories = (await idbGet(STORE_DATA, 'categories')) || [];
        return new Response(
          JSON.stringify({ source: 'indexeddb', cachedAt: Date.now(), products, categories }),
          {
            status: 200,
            headers: {
              'Content-Type': 'application/json;charset=utf-8',
              'X-Sajgoj-Catalog-Source': 'IndexedDB'
            }
          }
        );
      })()
    );
    return;
  }

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
          const cache = await caches.open(CACHE_NAME);
          if (url.pathname.startsWith('/pos')) {
            const posCached = await cache.match('./pos/index.html') || await cache.match('/pos/index.html');
            if (posCached) return posCached;
          }
          const cachedIndex = await cache.match('./index.html') || await cache.match('/index.html') || await cache.match('./200.html');
          return cachedIndex || new Response('SAJGOJ FASHION is running in offline mode.', {
            headers: { 'Content-Type': 'text/html;charset=utf-8' }
          });
        })
    );
    return;
  }

  // Product images & media: cache-first / stale-while-revalidate into CATALOG_MEDIA_CACHE
  const isImageReq =
    event.request.destination === 'image' ||
    /\.(png|jpe?g|webp|gif|svg|avif)(\?.*)?$/i.test(url.pathname);

  if (isImageReq) {
    event.respondWith(
      caches.open(CATALOG_MEDIA_CACHE).then(async (mediaCache) => {
        const cached = (await mediaCache.match(event.request)) || (await caches.match(event.request));
        const networkFetch = fetch(event.request)
          .then((networkResponse) => {
            if (networkResponse && (networkResponse.status === 200 || networkResponse.type === 'opaque')) {
              mediaCache.put(event.request, networkResponse.clone());
            }
            return networkResponse;
          })
          .catch(() => cached);
        return cached || networkFetch;
      })
    );
    return;
  }

  // Static scripts, stylesheets & fonts: cache-first with background revalidation
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

      return cachedResponse || fetchPromise;
    })
  );
});
