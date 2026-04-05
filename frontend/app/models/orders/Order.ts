// 🛒 PEDIDO (Estructura de datos)
// Define cómo es un pedido:
// - orderId, cliente (nested), fecha, dirección de envío (nested)
// - estado: "CREATED" o "CONFIRMED"
// - líneas del pedido (productos en el pedido)
// Usado en historial y detalles de pedidos

import type { OrderLine } from '~/models/orders/OrderLine';
import type { Address } from '~/models/customers/Customer';

export interface OrderCustomer {
  customerId: string;
  firstName: string;
  lastName: string;
  dni: string;
}

export type OrderStatus = 'CREATED' | 'CONFIRMED';

// Respuesta de GET /orders/{id}
export interface OrderDetail {
  orderId: string;
  orderDate: number;
  customer: OrderCustomer;
  shippingAddress: Address;
  orderLines: OrderLine[];
  totalAmountDue: number;
  status: OrderStatus;
}

// Cada elemento dentro de GET /orders (lista admin)
export interface OrderSummary {
  orderId: string;
  orderDate: number;
  customerId: string;
  firstName: string;
  lastName: string;
  dni: string;
  shippingAddress: Address;
  orderLines: OrderLine[];
  totalAmountDue: number;
  status: OrderStatus;
}

// Respuesta envolvente de GET /orders
export interface GetAllOrdersResponse {
  orders: OrderSummary[];
}

export function getCustomerFullName(
  order: OrderDetail | OrderSummary,
): string {
  if ('customer' in order) {
    return `${order.customer.firstName || ''} ${order.customer.lastName || ''}`.trim() || 'Unknown';
  }
  return `${order.firstName || ''} ${order.lastName || ''}`.trim() || 'Unknown';
}

export function getFullShippingAddress(
  order: OrderDetail | OrderSummary,
): string {
  const addr = order.shippingAddress;
  return [addr.street, addr.city, addr.province, addr.postalCode, addr.country]
    .filter(Boolean)
    .join(', ');
}
