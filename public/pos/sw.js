/**
 * Copyright 2018 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// If the loader is already loaded, just stop.
if (!self.define) {
  let registry = {};

  // Used for `eval` and `importScripts` where we can't get script URL by other means.
  // In both cases, it's safe to use a global var because those functions are synchronous.
  let nextDefineUri;

  const singleRequire = (uri, parentUri) => {
    uri = new URL(uri + ".js", parentUri).href;
    return registry[uri] || (
      
        new Promise(resolve => {
          if ("document" in self) {
            const script = document.createElement("script");
            script.src = uri;
            script.onload = resolve;
            document.head.appendChild(script);
          } else {
            nextDefineUri = uri;
            importScripts(uri);
            resolve();
          }
        })
      
      .then(() => {
        let promise = registry[uri];
        if (!promise) {
          throw new Error(`Module ${uri} didn’t register its module`);
        }
        return promise;
      })
    );
  };

  self.define = (depsNames, factory) => {
    const uri = nextDefineUri || ("document" in self ? document.currentScript.src : "") || location.href;
    if (registry[uri]) {
      // Module is already loading or loaded.
      return;
    }
    let exports = {};
    const require = depUri => singleRequire(depUri, uri);
    const specialDeps = {
      module: { uri },
      exports,
      require
    };
    registry[uri] = Promise.all(depsNames.map(
      depName => specialDeps[depName] || require(depName)
    )).then(deps => {
      factory(...deps);
      return exports;
    });
  };
}
define(['./workbox-b1bafff1'], (function (workbox) { 'use strict';

  self.skipWaiting();
  workbox.clientsClaim();
  /**
   * The precacheAndRoute() method efficiently caches and responds to
   * requests for URLs in the manifest.
   * See https://goo.gl/S9QRab
   */
  workbox.precacheAndRoute([{
    "url": "pwa-maskable-512x512.png",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "pwa-512x512.png",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "pwa-192x192.png",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "logo.png",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "logo.jpg",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "index.html",
    "revision": "490a4808a01f93666406744811979427"
  }, {
    "url": "icon.svg",
    "revision": "e3a2c45531a45dd3bdb4f401139ca874"
  }, {
    "url": "favicon.svg",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "favicon.ico",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "apple-touch-icon.png",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "assets/workbox-window.prod.es5-Bd17z0YL.js",
    "revision": null
  }, {
    "url": "assets/purify.es-Dqk5qvad.js",
    "revision": null
  }, {
    "url": "assets/index.es-cU0OZ2Tz.js",
    "revision": null
  }, {
    "url": "assets/index-D4cNJ1rw.css",
    "revision": null
  }, {
    "url": "assets/index-CppyY8PZ.js",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261104-034550.Chrome_659677ea.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261104-033631.Chrome_d2deaff5.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261102-225624.Chrome_9c3dd50b.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261102-222823.Chrome_ae378086.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261102-204237.Chrome_28c7623b.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261102-011419.Chrome_354ddbc4.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261102-004445.Chrome_4cac6f2a.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261102-002746.Chrome_7318d9b9.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261101-233556.Chrome_766fbe83.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261101-233459.Chrome_b8630e33.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261101-022752.Chrome_715a1fba.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261101-022350.Chrome_9c940cac.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261101-022253.Chrome_5aff8d60.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261101-022100.Chrome_2acc912b.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261101-015259.Chrome_03c52dc7.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261101-012851.Chrome_83631918.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261030-230223.Chrome_e0a0c6cb.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261030-223618.Chrome_79efd6a6.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261030-221712.Chrome_a3eaa4e6.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261029-151824.Chrome_270bdef6.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261029-145241.Chrome_0900dcf2.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261027-052015.Chrome_af6f621c.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261027-013902.Chrome_f2976e57.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261027-013807.Chrome_2f75362f.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261027-013621.Chrome_a62ac384.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261026-034347.Chrome_8577f7a4.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261026-034330.Chrome_8ea86b9a.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261026-034313.Chrome_30b1652f.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261026-031527.Chrome_dce635b6.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261026-015725.Chrome_e3e036c1.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261026-015651.Chrome_a8400691.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261026-004841.Chrome_e4cb65ef.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261026-003822.Chrome_f401c327.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261025-235808.Chrome_f46f6d43.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261025-235619.Chrome_2c23b5bc.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261025-233303.Chrome_252ee480.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261025-231426.Chrome_9c84c87f.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261023-224608.Chrome_094c50df.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261023-222322.Chrome_fafc81dd.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261022-232132.Chrome_093a8a29.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261021-142621.Chrome_d16a1d2e.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261020-213914.Chrome_1d379bf0.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261020-194039.Chrome_846818fd.png",
    "revision": null
  }, {
    "url": "assets/aistudio/Screenshot_20261020-112315.Chrome_acc469d6.png",
    "revision": null
  }, {
    "url": "apple-touch-icon.png",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "favicon.ico",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "favicon.svg",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "icon.svg",
    "revision": "e3a2c45531a45dd3bdb4f401139ca874"
  }, {
    "url": "logo.png",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "pwa-192x192.png",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "pwa-512x512.png",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "pwa-maskable-512x512.png",
    "revision": "11a18b15575e063cc8cbf32237720d0d"
  }, {
    "url": "manifest.webmanifest",
    "revision": "31ec49b95f27eddbec2b5e34d0f09cfb"
  }], {});
  workbox.cleanupOutdatedCaches();
  workbox.registerRoute(new workbox.NavigationRoute(workbox.createHandlerBoundToURL("/index.html"), {
    denylist: [/^\/api\//, /^\/netlify-health-check/, /^\/healthz/]
  }));
  workbox.registerRoute(/^https:\/\/fonts\.googleapis\.com\/.*/i, new workbox.CacheFirst({
    "cacheName": "google-fonts-cache",
    plugins: [new workbox.ExpirationPlugin({
      maxEntries: 10,
      maxAgeSeconds: 31536000
    }), new workbox.CacheableResponsePlugin({
      statuses: [0, 200]
    })]
  }), 'GET');
  workbox.registerRoute(/^https:\/\/fonts\.gstatic\.com\/.*/i, new workbox.CacheFirst({
    "cacheName": "gstatic-fonts-cache",
    plugins: [new workbox.ExpirationPlugin({
      maxEntries: 15,
      maxAgeSeconds: 31536000
    }), new workbox.CacheableResponsePlugin({
      statuses: [0, 200]
    })]
  }), 'GET');
  workbox.registerRoute(/\.(?:png|jpg|jpeg|svg|webp|gif|ico)$/i, new workbox.StaleWhileRevalidate({
    "cacheName": "toyverse-local-images-cache",
    plugins: [new workbox.ExpirationPlugin({
      maxEntries: 120,
      maxAgeSeconds: 2592000
    }), new workbox.CacheableResponsePlugin({
      statuses: [0, 200]
    })]
  }), 'GET');
  workbox.registerRoute(/^https:\/\/images\.unsplash\.com\/.*/i, new workbox.StaleWhileRevalidate({
    "cacheName": "unsplash-images-cache",
    plugins: [new workbox.ExpirationPlugin({
      maxEntries: 50,
      maxAgeSeconds: 2592000
    }), new workbox.CacheableResponsePlugin({
      statuses: [0, 200]
    })]
  }), 'GET');

}));
