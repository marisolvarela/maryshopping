import type { Category } from './Category';
import type { Provider } from './Provider';

// 📦 PRODUCTO (Estructura de datos)
// Define cómo es un producto en TypeScript:
// - id, nombre, descripción
// - precio, categoría, proveedor, referencia del proveedor
// Usado para validar y mostrar productos

export interface Product {
  id: string;
  name: string;
  description: string;
  category: Category | null;
  provider: Provider | null;
  providerReference: string;
  price: number;
}

export interface CreateProductRequest {
  name: string;
  categoryId: string;
  description: string;
  providerId: string;
  providerReference: string;
  price: number;
}

export interface UpdateProductRequest {
  name: string;
  categoryId: string;
  description: string;
  provider: string;
  providerReference: string;
  price: number;
}
