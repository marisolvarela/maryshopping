// 📦 LÍNEA DE PEDIDO (Estructura de datos)
// Define cómo es cada producto dentro de un pedido:
// - orderLineId, orderId, productId, cantidad, precio unitario
// - subtotal (cantidad × precio)
// - información del producto (nombre, categoría, proveedor)
// Usado para mostrar detalles de cada producto en un pedido

export interface OrderLine {
  orderLineId: string;
  orderId: string;
  productId: string;
  productName: string;
  productProviderReference: string;
  categoryName: string;
  providerName: string;
  quantity: number;
  unitPrice: number;
  subTotal: number;
}
