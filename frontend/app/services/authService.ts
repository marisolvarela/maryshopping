// 🔐 AUTENTICACIÓN (Login/Logout)
// Maneja credenciales con el backend:
// - POST /customers/iam/token (login con email y contraseña)
// - POST /customers/iam/logout (cierre de sesión)
// - Guarda tokens en la tienda (token + refreshToken)
// - Extrae información del usuario desde el JWT

import { apiService } from './api';
import { useUserStore } from '~/stores/userStore';
import { EndpointBuilder } from '~/config/api';
import {
  getSubjectFromToken,
  getEmailFromToken,
  getRolesFromToken,
} from '~/utils/jwt';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export class AuthService {
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await apiService.post<LoginResponse>(
      EndpointBuilder.auth.login(),
      credentials,
    );

    if (response.data.accessToken) {
      const userStore = useUserStore();

      const customerId = getSubjectFromToken(response.data.accessToken) || '';
      const emailFromToken =
        getEmailFromToken(response.data.accessToken) || credentials.email;
      const roles = getRolesFromToken(response.data.accessToken);

      userStore.login(
        {
          id: customerId,
          email: emailFromToken,
          name: emailFromToken.split('@')[0],
          roles,
          token: response.data.accessToken,
          refreshToken: response.data.refreshToken,
        },
        response.data.expiresIn,
      );
    }

    return response.data;
  }

  /**
   * Cierra sesión — envía ambos tokens al backend y luego limpia el estado local.
   */
  async logout(): Promise<void> {
    const userStore = useUserStore();
    const refreshTokenValue = userStore.getRefreshToken();
    const accessTokenValue = userStore.getToken();

    try {
      if (refreshTokenValue && accessTokenValue) {
        await apiService.post(EndpointBuilder.auth.logout(), {
          refreshToken: refreshTokenValue,
          accessToken: accessTokenValue,
        });
      }
    } finally {
      userStore.logout();
      await navigateTo('/login');
    }
  }

  isAuthenticated(): boolean {
    return useUserStore().isLoggedIn;
  }

  getToken(): string | null {
    return useUserStore().getToken();
  }
}

export const authService = new AuthService();
