// 👨 TIENDA DE USUARIO (Estado global de sesión)
// Guarda:
// - Token JWT (acceso y refresco)
// - Datos del usuario (email, rol, ID)
// - Estado: logueado, tiene validación de token
// Funciones: login(), logout(), setCurrentUser()
// Se persiste automáticamente en localStorage

import { ref, computed } from 'vue';
import {
  getSubjectFromToken,
  getEmailFromToken,
  getRolesFromToken,
} from '~/utils/jwt';

export interface AuthUser {
  id: string;
  email: string;
  name?: string;
  roles: string[];
  token: string;
  refreshToken?: string;
}

export const ROLE_CUSTOMER = 'customer';
export const ROLE_ADMINISTRATOR = 'administrator';

export const useUserStore = defineStore(
  'user',
  () => {
    const authToken = ref<string | null>(null);
    const refreshToken = ref<string | null>(null);
    const currentUser = ref<
      import('~/models/customers/Customer').Customer | null
    >(null);
    const authUser = ref<AuthUser | null>(null);
    const accessTokenExpiresAt = ref<number | null>(null);

    // Propiedades calculadas (computed)
    const isLoggedIn = computed(() => !!authToken.value);
    const hasValidToken = computed(() => {
      if (!authToken.value) return false;
      if (accessTokenExpiresAt.value)
        return Date.now() < accessTokenExpiresAt.value;
      return true;
    });
    const userRoles = computed(() => authUser.value?.roles || []);
    const isCustomer = computed(() => userRoles.value.includes(ROLE_CUSTOMER));
    const isAdministrator = computed(() =>
      userRoles.value.includes(ROLE_ADMINISTRATOR),
    );
    const customerId = computed(() => authUser.value?.id || null);

    const hasRole = (role: string) =>
      userRoles.value.includes(role.toLowerCase());
    const canManageCustomerData = (targetCustomerId: string) => {
      if (!authUser.value) return false;
      if (isAdministrator.value) return true;
      return authUser.value.id === targetCustomerId;
    };

    // Acciones (funciones que modifican el estado)
    function setAuthToken(token: string, expiresIn?: number) {
      authToken.value = token;
      if (expiresIn) {
        accessTokenExpiresAt.value = Date.now() + expiresIn * 1000;
      }

      const maxAge = expiresIn || 60 * 60 * 24 * 7;
      const isSecure = globalThis.location?.protocol === 'https:';
      const tokenCookie = useCookie('auth-token', {
        httpOnly: false,
        secure: isSecure,
        sameSite: 'strict',
        maxAge,
      });
      tokenCookie.value = token;
    }

    function setRefreshToken(token: string) {
      refreshToken.value = token;
      const isSecure = globalThis.location?.protocol === 'https:';
      const tokenCookie = useCookie('refresh-token', {
        httpOnly: false,
        secure: isSecure,
        sameSite: 'strict',
        maxAge: 60 * 60 * 24 * 30,
      });
      tokenCookie.value = token;
    }

    function login(user: AuthUser, expiresIn?: number) {
      authUser.value = user;
      setAuthToken(user.token, expiresIn);
      if (user.refreshToken) {
        setRefreshToken(user.refreshToken);
      }
    }

    function setCustomerData(
      customer: import('~/models/customers/Customer').Customer,
    ) {
      currentUser.value = customer;
    }

    function logout() {
      authToken.value = null;
      refreshToken.value = null;
      currentUser.value = null;
      authUser.value = null;
      accessTokenExpiresAt.value = null;

      const tokenCookie = useCookie('auth-token');
      const refreshCookie = useCookie('refresh-token');
      tokenCookie.value = null;
      refreshCookie.value = null;
    }

    function initializeFromCookie() {
      const tokenCookie = useCookie('auth-token');
      const refreshCookie = useCookie('refresh-token');

      if (tokenCookie.value) {
        authToken.value = tokenCookie.value;

        const id = getSubjectFromToken(tokenCookie.value);
        const email = getEmailFromToken(tokenCookie.value);
        const roles = getRolesFromToken(tokenCookie.value);

        if (id || email) {
          authUser.value = {
            id: id || '',
            email: email || '',
            name: email ? email.split('@')[0] : '',
            roles,
            token: tokenCookie.value,
            refreshToken: refreshCookie.value || undefined,
          };
        }
      }
      if (refreshCookie.value) {
        refreshToken.value = refreshCookie.value;
      }
    }

    function getToken(): string | null {
      return authToken.value;
    }

    function getRefreshToken(): string | null {
      return refreshToken.value;
    }

    return {
      authToken,
      refreshToken,
      currentUser,
      authUser,
      accessTokenExpiresAt,
      isLoggedIn,
      hasValidToken,
      userRoles,
      isCustomer,
      isAdministrator,
      customerId,
      setAuthToken,
      setRefreshToken,
      login,
      setCustomerData,
      logout,
      initializeFromCookie,
      getToken,
      getRefreshToken,
      hasRole,
      canManageCustomerData,
    };
  },
  {
    persist: {
      pick: ['currentUser', 'authUser'],
    },
  },
);
