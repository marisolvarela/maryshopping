// 📦 PRODUCTOS
// CRUD de productos:
// - GET /catalog/products/{id} (ver uno)
// - GET /catalog/categories/{id}/products (por categoría)
// - POST, PUT, DELETE (solo admin)
// - Soporta carga de imágenes

import { apiService } from './api';
import { EndpointBuilder } from '~/config/api';
import type {
  Product,
  CreateProductRequest,
  UpdateProductRequest,
} from '~/models/products/Product';
import type {
  ProductImage,
  UploadImageRequest,
  UpdateImageRequest,
} from '~/models/products/Image';

export class ProductsService {
  async getById(id: string): Promise<Product> {
    const response = await apiService.get<Product>(
      EndpointBuilder.catalog.product(id),
    );
    return response.data;
  }

  async getByCategory(categoryId: string): Promise<Product[]> {
    const response = await apiService.get<Product[]>(
      EndpointBuilder.catalog.categoryProducts(categoryId),
    );
    return response.data;
  }

  async create(data: CreateProductRequest): Promise<{ productId: string }> {
    const response = await apiService.post<{ productId: string }>(
      EndpointBuilder.catalog.products(),
      data,
    );
    return response.data;
  }

  async update(id: string, data: UpdateProductRequest): Promise<{ id: string }> {
    const response = await apiService.put<{ id: string }>(
      EndpointBuilder.catalog.product(id),
      data,
    );
    return response.data;
  }

  async delete(id: string): Promise<void> {
    await apiService.delete(EndpointBuilder.catalog.product(id));
  }

  // Imágenes
  async uploadImage(data: UploadImageRequest): Promise<{ imageId: string }> {
    const formData = new FormData();
    formData.append('imageFile', data.imageFile);
    formData.append('productId', data.productId);
    formData.append('order', data.order.toString());
    formData.append('primary', data.primary.toString());

    const response = await apiService.upload<{ imageId: string }>(
      EndpointBuilder.catalog.images.upload(),
      formData,
    );
    return response.data;
  }

  async getAllImages(): Promise<ProductImage[]> {
    const response = await apiService.get<ProductImage[]>(
      EndpointBuilder.catalog.images.list(),
    );
    return response.data;
  }

  async deleteImage(imageId: string): Promise<void> {
    await apiService.delete(EndpointBuilder.catalog.images.delete(imageId));
  }

  async updateImage(
    imageId: string,
    data: UpdateImageRequest,
  ): Promise<void> {
    await apiService.put(
      EndpointBuilder.catalog.images.update(imageId),
      data,
    );
  }
}

export const productsService = new ProductsService();
