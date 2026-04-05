// 🖥️ PLUGIN AXIOS (Inicializa HTTP)
// Configuración e interceptores de Axios:
// - Agrega automáticamente el token JWT en Headers
// - Maneja errores (401 = ir a login, 403 = sin permisos)
// - Define endpoints públicos (no necesitan token)
// Se ejecuta cuando la app carga

import axios from 'axios';

const PUBLIC_ENDPOINTS = ['customers/iam/', 'catalog/'];

function isPublicEndpoint(url: string | undefined): boolean {
  if (!url) return false;
  if (url === 'customers' || url === '/customers') return true;
  return PUBLIC_ENDPOINTS.some(prefix => url.includes(prefix));
}

function getCookie(name: string): string | null {
  if (typeof document === 'undefined') return null;
  const cookies = document.cookie.split(';');
  for (const cookie of cookies) {
    const [cookieName, ...cookieValue] = cookie.trim().split('=');
    if (cookieName === name) {
      return decodeURIComponent(cookieValue.join('='));
    }
  }
  return null;
}

function clearAuthCookies() {
  if (typeof document === 'undefined') return;
  document.cookie =
    'auth-token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
  document.cookie =
    'refresh-token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
}

export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig();

  const api = axios.create({
    baseURL: config.public.apiBase as string,
    timeout: 10000,
    headers: { 'Content-Type': 'application/json' },
  });

  // Interceptor de peticiones — agrega el token Bearer en endpoints protegidos
  api.interceptors.request.use(
    reqConfig => {
      if (!isPublicEndpoint(reqConfig.url)) {
        const token = getCookie('auth-token');
        if (token) {
          reqConfig.headers.Authorization = `Bearer ${token}`;
        }
      }
      return reqConfig;
    },
    error => Promise.reject(error),
  );

  let isRedirectingToLogin = false;

  // Interceptor de respuestas — si devuelve 401, limpia la sesión y redirige a login
  // (este backend no tiene endpoint de refresco de token)
  api.interceptors.response.use(
    response => response,
    async error => {
      if (isPublicEndpoint(error.config?.url)) {
        return Promise.reject(error);
      }

      if (error.response?.status === 401 && !isRedirectingToLogin) {
        isRedirectingToLogin = true;
        clearAuthCookies();

        try {
          const userStore = useUserStore();
          userStore.logout();
        } catch {
          // la tienda puede no estar disponible aún
        }

        navigateTo('/login?sessionExpired=true');
        setTimeout(() => {
          isRedirectingToLogin = false;
        }, 2000);
      }

      return Promise.reject(error);
    },
  );

  return {
    provide: {
      api,
    },
  };
});
