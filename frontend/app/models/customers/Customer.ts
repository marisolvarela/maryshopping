// 👤 CLIENTE (Estructura de datos)
// Define cómo es un cliente:
// - id, nombre, email, teléfono, dni
// - dirección de envío y facturación
// Usado para registros y perfiles

export interface Address {
  street: string;
  city: string;
  province: string;
  postalCode: string;
  country: string;
}

export interface Customer {
  id: string;
  firstName: string;
  lastName: string;
  dni: string;
  phoneNumber: string;
  emailAddress: string;
  billingAddress: Address;
  shippingAddress: Address;
}
