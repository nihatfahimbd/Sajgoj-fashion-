export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    if (url.pathname === '/pos') {
      return Response.redirect(`${url.origin}/pos/`, 301);
    }
    if (url.pathname === '/admin' || url.pathname === '/admin/' || url.pathname === '/login' || url.pathname === '/backend') {
      return Response.redirect(`${url.origin}/#admin`, 302);
    }
    if (env.ASSETS) {
      return env.ASSETS.fetch(request);
    }
    return new Response("SAJGOJ FASHION is running", {
      headers: { "content-type": "text/html;charset=UTF-8" }
    });
  }
};
