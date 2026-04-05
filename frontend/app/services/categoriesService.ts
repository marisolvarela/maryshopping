// 🏷️  CATEGORÍAS (Productos)
// CRUD de categorías del catálogo:
// - GET /catalog/categories (listar todas)
// - GET /catalog/categories/{id} (ver una)
// - POST, PUT, DELETE (solo admin)

import { apiService } from './api';
import { EndpointBuilder } from '~/config/api';
import type {
  Category,
  CreateCategoryRequest,
  UpdateCategoryRequest,
} from '~/models/products/Category';

export class CategoriesService {
  async getAll(): Promise<Category[]> {
    const response = await apiService.get<Category[]>(
      EndpointBuilder.catalog.categories(),
    );
    return response.data;
  }

  async getById(id: string): Promise<Category> {
    const response = await apiService.get<Category>(
      EndpointBuilder.catalog.category(id),
    );
    return response.data;
  }

  async create(data: CreateCategoryRequest): Promise<{ id: string }> {
    const response = await apiService.post<{ id: string }>(
      EndpointBuilder.catalog.categories(),
      data,
    );
    return response.data;
  }

  async update(id: string, data: UpdateCategoryRequest): Promise<{ id: string }> {
    const response = await apiService.put<{ id: string }>(
      EndpointBuilder.catalog.category(id),
      data,
    );
    return response.data;
  }

  async delete(id: string): Promise<void> {
    await apiService.delete(EndpointBuilder.catalog.category(id));
  }
}

export const categoriesService = new CategoriesService();
