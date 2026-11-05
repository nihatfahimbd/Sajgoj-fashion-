export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    if (url.pathname === '/pos') {
      return Response.redirect(`${url.origin}/pos/`, 301);
    }
    if (env.ASSETS) {
      return env.ASSETS.fetch(request);
    }
    return new Response("SAJGOJ FASHION is running", {
      headers: { "content-type": "text/html;charset=UTF-8" }
    });
  }
};
