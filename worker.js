export default {
  async fetch(request, env) {
    if (env.ASSETS) {
      return env.ASSETS.fetch(request);
    }
    return new Response("SAJGOJ FASHION is running", {
      headers: { "content-type": "text/html;charset=UTF-8" }
    });
  }
};
