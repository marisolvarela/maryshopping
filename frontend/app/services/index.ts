// 📥 EXPORTS (Punto de entrada de servicios)
// Este archivo re-exporta todos los servicios
// para que se importe fácilmente en otros archivos:
// import { authService, productsService } from '~/services'

export { apiService } from './api';
export { authService } from './authService';
export { productsService } from './productsService';
export { categoriesService } from './categoriesService';
export { providersService } from './providersService';
export { customersService } from './customersService';
export { ordersService } from './ordersService';
export { reportsService } from './reportsService';

export type { LoginRequest, LoginResponse } from './authService';
export type {
  CreateCustomerRequest,
  UpdateCustomerRequest,
} from './customersService';
export type {
  CreateOrderRequest,
  CreateOrderLineRequest,
  UpdateOrderRequest,
  UpdateOrderLineRequest,
} from './ordersService';
export type { TopProductReport, TopCustomerReport } from './reportsService';
