// 🚩 MIDDLEWARE AUTH (Protege rutas que necesitan login)
// Valida que el usuario esté autenticado:
// - Si NO está logueado = redirige a /login
// - Permite rutas públicas: /, /products, /login, /register
// - Si está logueado y va a /login = redirige a /products
// Se ejecuta ANTES de entrar a cualquier ruta

export default defineNuxtRouteMiddleware(to => {
  const { isLoggedIn, hasValidToken, initializeAuth } = useAuth();

  initializeAuth();

  const publicRoutes = ['/login', '/register', '/', '/products'];
  if (publicRoutes.includes(to.path)) {
    if (isLoggedIn.value && hasValidToken.value && to.path === '/login') {
      return navigateTo('/products');
    }
    return;
  }

  if (!isLoggedIn.value || !hasValidToken.value) {
    return navigateTo({
      path: '/login',
      query: { redirect: to.fullPath },
    });
  }
});
