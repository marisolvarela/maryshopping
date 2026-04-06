// Configuración de la API del backend MaryShopping
// URL base: http://localhost:8089/maryshopping
// Sin prefijo de versión — los endpoints están directamente bajo la ruta raíz

export interface ApiConfig {
  baseUrl: string;
  contextPath: string;
  timeout: number;
}

export const API_CONFIG: ApiConfig = {
  baseUrl: '',
  contextPath: '/maryshopping',
  timeout: 10000,
};

/**
 * Constructor de endpoints adaptado al backend MaryShopping.
 * Todas las rutas son relativas a la URL base de la API (ej. http://localhost:8089/maryshopping).
 */
export class EndpointBuilder {
  static build(endpoint: string): string {
    return endpoint.startsWith('/') ? endpoint.slice(1) : endpoint;
  }

  /**
   * Endpoints de autenticación — /customers/iam/...
   * NOTA: NO existe un endpoint dedicado para refrescar el token.
   */
  static readonly auth = {
    login: () => EndpointBuilder.build('customers/iam/token'),
    logout: () => EndpointBuilder.build('customers/iam/logout'),
  };

  /**
   * Endpoints del catálogo — /catalog/...
   * Productos: /catalog/products
   * Categorías: /catalog/categories
   * Proveedores: /catalog/products/providers  (NO /catalog/providers)
   * Imágenes: /catalog/products/images
   */
  static readonly catalog = {
    products: () => EndpointBuilder.build('catalog/products'),
    product: (id: string) => EndpointBuilder.build(`catalog/products/${id}`),
    categories: () => EndpointBuilder.build('catalog/categories'),
    category: (id: string) => EndpointBuilder.build(`catalog/categories/${id}`),
    categoryProducts: (categoryId: string) =>
      EndpointBuilder.build(`catalog/categories/${categoryId}/products`),
    providers: () => EndpointBuilder.build('catalog/products/providers'),
    provider: (id: string) =>
      EndpointBuilder.build(`catalog/products/providers/${id}`),
    images: {
      upload: () => EndpointBuilder.build('catalog/products/images'),
      list: () => EndpointBuilder.build('catalog/products/images'),
      get: (id: string) =>
        EndpointBuilder.build(`catalog/products/images/${id}`),
      update: (id: string) =>
        EndpointBuilder.build(`catalog/products/images/${id}`),
      delete: (id: string) =>
        EndpointBuilder.build(`catalog/images/${id}`),
    },
  };

  /**
   * Endpoints de pedidos — /orders/...
   * Listar/Ver: /orders, /orders/{id}
   * Crear/Editar/Borrar: /orders/order, /orders/order/{id}
   * Confirmar: /orders/order/status/confirmed/{id}
   */
  static readonly orders = {
    list: () => EndpointBuilder.build('orders'),
    get: (id: string) => EndpointBuilder.build(`orders/${id}`),
    create: () => EndpointBuilder.build('orders/order'),
    update: (id: string) => EndpointBuilder.build(`orders/order/${id}`),
    delete: (id: string) => EndpointBuilder.build(`orders/order/${id}`),
    confirm: (id: string) =>
      EndpointBuilder.build(`orders/order/status/confirmed/${id}`),
    lines: {
      create: () => EndpointBuilder.build('orders/orderLine'),
      update: (id: string) => EndpointBuilder.build(`orders/orderLine/${id}`),
      delete: (id: string) => EndpointBuilder.build(`orders/orderLine/${id}`),
    },
  };

  /**
   * Endpoints de clientes — /customers/...
   */
  static readonly customers = {
    list: () => EndpointBuilder.build('customers'),
    get: (id: string) => EndpointBuilder.build(`customers/${id}`),
    create: () => EndpointBuilder.build('customers'),
    update: (id: string) => EndpointBuilder.build(`customers/${id}`),
    delete: (id: string) => EndpointBuilder.build(`customers/${id}`),
  };

  /**
   * Endpoints de informes — /reports/...
   */
  static readonly reports = {
    topProducts: (limit?: number) =>
      EndpointBuilder.build(
        `reports/top-products${limit ? `?limit=${limit}` : ''}`,
      ),
    topCustomers: (limit?: number) =>
      EndpointBuilder.build(
        `reports/top-customers${limit ? `?limit=${limit}` : ''}`,
      ),
  };
}
