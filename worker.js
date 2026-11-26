const CANONICAL_ALIAS_MAP = {
  "/privacy": "/privacy-policy/",
  "/privacy.html": "/privacy-policy/",
  "/privacy-policy.html": "/privacy-policy/",
  "/terms": "/terms-and-conditions/",
  "/terms.html": "/terms-and-conditions/",
  "/terms-of-service": "/terms-and-conditions/",
  "/tos": "/terms-and-conditions/",
  "/terms-and-conditions.html": "/terms-and-conditions/",
  "/about": "/about-us/",
  "/about.html": "/about-us/",
  "/about-us.html": "/about-us/",
  "/contact": "/contact-us/",
  "/contact.html": "/contact-us/",
  "/contact-us.html": "/contact-us/",
  "/user-data-deletion": "/data-deletion/",
  "/data-deletion.html": "/data-deletion/",
  "/return-policy": "/refund-policy/",
  "/refund-policy.html": "/refund-policy/",
  "/delivery-policy": "/shipping-policy/",
  "/shipping-policy.html": "/shipping-policy/",
  "/exchange": "/exchange-policy/",
  "/exchange-policy.html": "/exchange-policy/",
  "/cancellation": "/cancellation-policy/",
  "/cancellation-policy.html": "/cancellation-policy/",
  "/cookies": "/cookie-policy/",
  "/cookie-policy.html": "/cookie-policy/",
  "/payment": "/payment-policy/",
  "/payment-policy.html": "/payment-policy/",
  "/faq.html": "/faq/",
  "/all-pages": "/pages/",
  "/links": "/pages/"
};

export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    const lowerPath = url.pathname.toLowerCase();
    const normalizedPath = lowerPath.length > 1 && lowerPath.endsWith("/")
      ? lowerPath.slice(0, -1)
      : lowerPath;

    // 1. Meta / Facebook Developer App Data Deletion Callback (POST /data-deletion)
    if (request.method === "POST" && (normalizedPath === "/data-deletion" || normalizedPath === "/user-data-deletion")) {
      const code = `SAJGOJ-DEL-${Date.now().toString(36).toUpperCase()}-${Math.random().toString(36).slice(2, 6).toUpperCase()}`;
      const statusUrl = `${url.origin}/data-deletion?code=${encodeURIComponent(code)}`;
      return new Response(
        JSON.stringify({ url: statusUrl, confirmation_code: code }),
        {
          status: 200,
          headers: {
            "content-type": "application/json;charset=UTF-8",
            "access-control-allow-origin": "*"
          }
        }
      );
    }

    // 2. POS and Admin routes
    if (normalizedPath === "/pos") {
      return Response.redirect(`${url.origin}/pos/`, 301);
    }
    if (normalizedPath === "/admin" || normalizedPath === "/login" || normalizedPath === "/backend") {
      return Response.redirect(`${url.origin}/#admin`, 302);
    }

    // 3. Clean direct 200 OK static serving for legal/policy aliases (no redirect loops for Facebook/Google bots)
    const canonicalAssetPath = CANONICAL_ALIAS_MAP[normalizedPath];
    if (canonicalAssetPath && env.ASSETS) {
      const assetReq = new Request(`${url.origin}${canonicalAssetPath}`, {
        method: request.method === "HEAD" ? "HEAD" : "GET",
        headers: request.headers
      });
      return env.ASSETS.fetch(assetReq);
    }

    // 4. Static assets & SPA fallback for dynamic routes (/categories, /category/*, /products, /product/*, /checkout, /cart, /order-tracking, /offers)
    if (env.ASSETS) {
      return env.ASSETS.fetch(request);
    }

    return new Response("SAJGOJ FASHION is running", {
      headers: { "content-type": "text/html;charset=UTF-8" }
    });
  }
};
