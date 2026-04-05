// 🔐 PLUGIN TOKEN MANAGER (Vigila que el token sea válido)
// Monitorea la expiración del JWT:
// - Cada 5 minutos verifica si el token expiró
// - Si expiró = redirige a login automáticamente
// - NOTA: No hay endpoint de refresco en este backend
// Se ejecuta en el cliente (solo navegador)

/**
 * Plugin Token Manager
 *
 * Como este backend NO tiene endpoint de refresco, la estrategia es:
 * - Monitorear la expiración del token
 * - Cuando el token expira, redirigir al login
 * - Avisar al usuario 5 minutos antes de que expire (opcional, por consola)
 */
export default defineNuxtPlugin({
  name: 'token-manager',
  enforce: 'post',
  async setup(nuxtApp) {
    if (!import.meta.client) return;

    let checkInterval: ReturnType<typeof setInterval> | null = null;

    const stopMonitoring = () => {
      if (checkInterval) {
        clearInterval(checkInterval);
        checkInterval = null;
      }
    };

    const checkTokenExpiry = async () => {
      try {
        const userStore = useUserStore();

        if (!userStore.isLoggedIn) {
          stopMonitoring();
          return;
        }

        if (!userStore.hasValidToken) {
          console.warn(
            '[TokenManager] Access token expired. Redirecting to login.',
          );
          userStore.logout();
          await navigateTo('/login?sessionExpired=true');
          stopMonitoring();
        }
      } catch (e) {
        console.error('[TokenManager] Error checking token:', e);
      }
    };

    const startMonitoring = () => {
      stopMonitoring();
      checkInterval = setInterval(checkTokenExpiry, 60 * 1000);
      checkTokenExpiry();
    };

    nuxtApp.hook('app:suspense:resolve', () => {
      try {
        const userStore = useUserStore();
        userStore.initializeFromCookie();

        if (userStore.isLoggedIn) {
          if (!userStore.hasValidToken) {
            userStore.logout();
          } else {
            startMonitoring();
          }
        }

        watch(
          () => userStore.isLoggedIn,
          isLoggedIn => {
            if (isLoggedIn) startMonitoring();
            else stopMonitoring();
          },
        );
      } catch (e) {
        console.error('[TokenManager] Initialization error:', e);
      }
    });
  },
});
