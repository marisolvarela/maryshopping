// 🏭 PROVEEDOR (Estructura de datos)
// Define cómo es un proveedor:
// - id, nombre
// Usado en admin para gestionar proveedores

export interface Provider {
  id: string;
  name: string;
}

export interface CreateProviderRequest {
  name: string;
}

export interface UpdateProviderRequest {
  name: string;
}
