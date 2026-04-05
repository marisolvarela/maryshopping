// 🖼 IMAGEN PRODUCTO (Estructura de datos)
// Define cómo es una imagen de producto:
// - id, productId, mimeType, order, isPrimary
// - El backend devuelve solo metadatos; los bytes se obtienen por GET /catalog/products/images/{id}
// Usado en tarjetas de productos y galería

import { API_CONFIG } from '~/config/api';

export interface ProductImage {
  id: string;
  productId: string;
  mimeType: string;
  order: number;
  isPrimary: boolean;
}

export interface UploadImageRequest {
  imageFile: File;
  productId: string;
  order: number;
  primary: boolean;
}

export interface UpdateImageRequest {
  order: number;
  primary: boolean;
  productId: string;
}

/**
 * Construye la URL completa de la imagen a partir de su ID.
 * El backend sirve los bytes de la imagen en GET /catalog/products/images/{imageId}.
 */
export function getImageUrl(imageId: string): string {
  return `${API_CONFIG.contextPath}/catalog/products/images/${imageId}`;
}
