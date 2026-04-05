// 👤 CLIENTES (Perfiles de usuario)
// CRUD de perfiles:
// - GET /customers (listar todos, solo admin)
// - GET /customers/{id} (ver perfil)
// - POST /customers (registrarse, público)
// - PUT /customers/{id} (actualizar perfil)
// - DELETE /customers/{id} (eliminar cuenta)

import { apiService } from './api';
import { EndpointBuilder } from '~/config/api';
import type { Customer, Address } from '~/models/customers/Customer';

export interface CreateCustomerRequest {
  firstName: string;
  lastName: string;
  dni: string;
  phoneNumber: string;
  emailAddress: string;
  password: string;
  billingAddress: Address;
  shippingAddress: Address;
}

export interface UpdateCustomerRequest {
  firstName: string;
  lastName: string;
  dni: string;
  phoneNumber: string;
  emailAddress: string;
  password?: string;
  billingAddress: Address;
  shippingAddress: Address;
}

export class CustomersService {
  async create(data: CreateCustomerRequest): Promise<{ id: string }> {
    const response = await apiService.post<{ id: string }>(
      EndpointBuilder.customers.create(),
      data,
    );
    return response.data;
  }

  async getById(id: string): Promise<Customer> {
    const response = await apiService.get<Customer>(
      EndpointBuilder.customers.get(id),
    );
    return response.data;
  }

  async getAll(): Promise<Customer[]> {
    const response = await apiService.get<Customer[]>(
      EndpointBuilder.customers.list(),
    );
    return response.data;
  }

  async update(id: string, data: UpdateCustomerRequest): Promise<{ id: string }> {
    const response = await apiService.put<{ id: string }>(
      EndpointBuilder.customers.update(id),
      data,
    );
    return response.data;
  }

  async delete(id: string): Promise<void> {
    await apiService.delete(EndpointBuilder.customers.delete(id));
  }
}

export const customersService = new CustomersService();
