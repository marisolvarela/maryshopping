// 🔐 COMPOSABLE AUTH (Lógica de autenticación compartida)
// Hook de Vue que proporciona funciones de login/logout:
// - login(email, password): autentica contra el backend
// - logout(): cierra sesión y limpia tokens
// - Comparte estado con userStore (tienda global)
// Se puede usar en cualquier componente

import { authService } from '~/services/authService';
import type { LoginRequest } from '~/services/authService';

export const useAuth = () => {
  const userStore = useUserStore();
  const router = useRouter();

  const isLoggedIn = computed(() => userStore.isLoggedIn);
  const currentUser = computed(() => userStore.currentUser);
  const authUser = computed(() => userStore.authUser);
  const hasValidToken = computed(() => userStore.hasValidToken);

  const login = async (credentials: LoginRequest) => {
    try {
      const response = await authService.login(credentials);
      return { success: true, data: response };
    } catch (error: any) {
      return {
        success: false,
        error: {
          message:
            error.response?.data?.message ||
            error.message ||
            'Login failed. Please check your credentials.',
          status: error.response?.status,
        },
      };
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch {
      userStore.logout();
      await router.push('/login');
    }
  };

  const initializeAuth = () => {
    userStore.initializeFromCookie();
  };

  return {
    isLoggedIn,
    currentUser,
    authUser,
    hasValidToken,
    login,
    logout,
    initializeAuth,
  };
};
