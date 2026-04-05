// 📊 REPORTES (Dashboard admin)
// Datos estadísticos de la tienda:
// - GET /reports/top-products (productos más vendidos)
// - GET /reports/top-customers (clientes más activos)
// Solo accesible por administradores

import { apiService } from './api';
import { EndpointBuilder } from '~/config/api';

export interface TopProductReport {
  productId: string;
  productName: string;
  productProviderReference: string;
  categoryName: string;
  providerName: string;
  totalUnitsSold: number;
  totalRevenue: number;
}

export interface TopCustomerReport {
  customerId: string;
  firstName: string;
  lastName: string;
  dni: string;
  totalSpent: number;
}

export class ReportsService {
  async getTopProducts(limit?: number): Promise<TopProductReport[]> {
    const response = await apiService.get<TopProductReport[]>(
      EndpointBuilder.reports.topProducts(limit),
    );
    return response.data;
  }

  async getTopCustomers(limit?: number): Promise<TopCustomerReport[]> {
    const response = await apiService.get<TopCustomerReport[]>(
      EndpointBuilder.reports.topCustomers(limit),
    );
    return response.data;
  }
}

export const reportsService = new ReportsService();
