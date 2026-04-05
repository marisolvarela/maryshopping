// 🛒 PEDIDOS (Shopping orders)
// CRUD de pedidos y líneas de pedido:
// - POST /orders/order (crear pedido)
// - GET /orders (listar, solo admin)
// - GET /orders/{id} (ver detalles)
// - PUT /orders/order/{id} (actualizar)
// - DELETE /orders/order/{id} (cancelar)
// - PUT /orders/order/status/confirmed/{id} (confirmar)
// - POST/PUT/DELETE /orders/orderLine (líneas)

import { apiService } from './api';
import { EndpointBuilder } from '~/config/api';
import type {
  OrderDetail,
  OrderSummary,
  GetAllOrdersResponse,
} from '~/models/orders/Order';
import type { Address } from '~/models/customers/Customer';

export interface CreateOrderRequest {
  customerId: string;
  orderDate: number;
  shippingAddress: Address;
}

export interface UpdateOrderRequest {
  orderDate: number;
  customerId: string;
  shippingAddress: Address;
}

export interface CreateOrderLineRequest {
  orderId: string;
  productId: string;
  quantity: number;
  unitPrice: number;
  categoryName: string;
  productName: string;
  providerName: string;
  productProviderReference: string;
}

export interface UpdateOrderLineRequest {
  orderId: string;
  productId: string;
  quantity: number;
  unitPrice: number;
  categoryName: string;
  productName: string;
  providerName: string;
  productProviderReference: string;
}

export class OrdersService {
  async getAll(): Promise<OrderSummary[]> {
    const response = await apiService.get<GetAllOrdersResponse>(
      EndpointBuilder.orders.list(),
    );
    return response.data.orders || [];
  }

  async getById(id: string): Promise<OrderDetail> {
    const response = await apiService.get<OrderDetail>(
      EndpointBuilder.orders.get(id),
    );
    return response.data;
  }

  async create(data: CreateOrderRequest): Promise<{ id: string }> {
    const response = await apiService.post<{ id: string }>(
      EndpointBuilder.orders.create(),
      data,
    );
    return response.data;
  }

  async update(id: string, data: UpdateOrderRequest): Promise<{ id: string }> {
    const response = await apiService.put<{ id: string }>(
      EndpointBuilder.orders.update(id),
      data,
    );
    return response.data;
  }

  async delete(id: string): Promise<void> {
    await apiService.delete(EndpointBuilder.orders.delete(id));
  }

  async confirm(id: string): Promise<{ id: string }> {
    const response = await apiService.put<{ id: string }>(
      EndpointBuilder.orders.confirm(id),
    );
    return response.data;
  }

  // Líneas de pedido
  async createLine(data: CreateOrderLineRequest): Promise<{ id: string }> {
    const response = await apiService.post<{ id: string }>(
      EndpointBuilder.orders.lines.create(),
      data,
    );
    return response.data;
  }

  async updateLine(
    id: string,
    data: UpdateOrderLineRequest,
  ): Promise<{ orderLineId: string }> {
    const response = await apiService.put<{ orderLineId: string }>(
      EndpointBuilder.orders.lines.update(id),
      data,
    );
    return response.data;
  }

  async deleteLine(id: string): Promise<void> {
    await apiService.delete(EndpointBuilder.orders.lines.delete(id));
  }
}

export const ordersService = new OrdersService();
