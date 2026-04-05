// 🏭 PROVEEDORES
// CRUD de proveedores:
// - GET /catalog/products/providers (listar todos)
// - GET /catalog/products/providers/{id} (ver uno)
// - POST, PUT, DELETE (solo admin)

import { apiService } from './api';
import { EndpointBuilder } from '~/config/api';
import type {
  Provider,
  CreateProviderRequest,
  UpdateProviderRequest,
} from '~/models/products/Provider';

export class ProvidersService {
  async getAll(): Promise<Provider[]> {
    const response = await apiService.get<Provider[]>(
      EndpointBuilder.catalog.providers(),
    );
    return response.data;
  }

  async getById(id: string): Promise<Provider> {
    const response = await apiService.get<Provider>(
      EndpointBuilder.catalog.provider(id),
    );
    return response.data;
  }

  async create(data: CreateProviderRequest): Promise<{ id: string }> {
    const response = await apiService.post<{ id: string }>(
      EndpointBuilder.catalog.providers(),
      data,
    );
    return response.data;
  }

  async update(id: string, data: UpdateProviderRequest): Promise<{ id: string }> {
    const response = await apiService.put<{ id: string }>(
      EndpointBuilder.catalog.provider(id),
      data,
    );
    return response.data;
  }

  async delete(id: string): Promise<void> {
    await apiService.delete(EndpointBuilder.catalog.provider(id));
  }
}

export const providersService = new ProvidersService();
