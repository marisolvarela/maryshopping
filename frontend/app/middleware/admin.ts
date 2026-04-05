// 🚩 MIDDLEWARE ADMIN (Protege rutas de administrador)
// Valida que el usuario sea administrador:
// - Si NO está logueado = redirige a login
// - Si No es admin = redirige a productos
// Se ejecuta ANTES de entrar a rutas /admin/*
// Uso: Añade "definePageMeta({ middleware: 'admin' })" en páginas admin

export default defineNuxtRouteMiddleware(to => {
  const userStore = useUserStore();

  if (!userStore.isLoggedIn) {
    return navigateTo({ path: '/login', query: { redirect: to.fullPath } });
  }

  if (!userStore.isAdministrator) {
    return navigateTo('/products');
  }
});
