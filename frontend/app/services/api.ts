// 🔌 CLIENTE HTTP (Axios configurado)
// Conecta con el backend. Gestiona:
// - URL base: http://localhost:8089/maryshopping
// - Autorización automática (agrega JWT token)
// - Interceptores (errores, reintentos)
// - Endpoints públicos (no necesitan token)

import axios from 'axios';
import type {
  AxiosInstance,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from 'axios';
import { API_CONFIG } from '~/config/api';

// Endpoints públicos que no requieren autenticación
const PUBLIC_ENDPOINTS = ['customers/iam/'];

function isPublicEndpoint(url: string | undefined, method?: string): boolean {
  if (!url) return false;
  // POST /customers (registro) es público, pero GET /customers (admin list) NO
  if ((url === 'customers' || url === '/customers') && method?.toLowerCase() === 'post') return true;
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

class ApiService {
  private readonly api: AxiosInstance;

  constructor() {
    let baseURL = `${API_CONFIG.baseUrl}${API_CONFIG.contextPath}`;

    if (globalThis.window !== undefined) {
      try {
        const config = useRuntimeConfig();
        baseURL = config.public.apiBase as string;
      } catch {
        // valor por defecto si falla
      }
    }

    this.api = axios.create({
      baseURL,
      timeout: API_CONFIG.timeout,
      headers: { 'Content-Type': 'application/json' },
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    this.api.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        if (globalThis.window !== undefined && !isPublicEndpoint(config.url, config.method)) {
          const token = getCookie('auth-token');
          if (token) {
            config.headers.Authorization = `Bearer ${token}`;
          }
        }
        return config;
      },
      error => Promise.reject(error),
    );

    this.api.interceptors.response.use(
      response => response,
      error => Promise.reject(error),
    );
  }

  async get<T>(url: string): Promise<AxiosResponse<T>> {
    return this.api.get<T>(url);
  }

  async post<T>(url: string, data?: unknown): Promise<AxiosResponse<T>> {
    return this.api.post<T>(url, data);
  }

  async put<T>(url: string, data?: unknown): Promise<AxiosResponse<T>> {
    return this.api.put<T>(url, data);
  }

  async delete<T>(url: string): Promise<AxiosResponse<T>> {
    return this.api.delete<T>(url);
  }

  async upload<T>(url: string, formData: FormData): Promise<AxiosResponse<T>> {
    return this.api.post<T>(url, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  }

  getAxiosInstance(): AxiosInstance {
    return this.api;
  }
}

export const apiService = new ApiService();
