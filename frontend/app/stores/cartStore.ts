// 🛍 TIENDA DEL CARRITO (Estado global del carro de compras)
// El carrito se gestiona a través del backend (bounded context "orders"):
// - Una Order con status=CREATED es el carrito activo del usuario
// - Cada producto añadido es una OrderLine dentro de esa Order
// - Al confirmar la compra, la Order pasa a status=CONFIRMED
// - Se persiste el activeOrderId en localStorage para recuperarlo al recargar

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { ordersService } from '~/services/ordersService';
import { customersService } from '~/services/customersService';
import type { OrderDetail } from '~/models/orders/Order';
import type { OrderLine } from '~/models/orders/OrderLine';
import type { Product } from '~/models/products/Product';
import type {
  CreateOrderRequest,
  CreateOrderLineRequest,
  UpdateOrderLineRequest,
} from '~/services/ordersService';

export const useCartStore = defineStore(
  'cart',
  () => {
    // Estado reactivo
    const activeOrderId = ref<string | null>(null);
    const confirmedOrderIds = ref<string[]>([]);
    const order = ref<OrderDetail | null>(null);
    const loading = ref(false);
    const error = ref('');

    // Propiedades calculadas
    const items = computed<OrderLine[]>(() =>
      [...(order.value?.orderLines ?? [])].sort((a, b) =>
        a.orderLineId.localeCompare(b.orderLineId),
      ),
    );

    const totalItems = computed(() =>
      items.value.reduce((sum, line) => sum + line.quantity, 0),
    );

    const totalPrice = computed(() => order.value?.totalAmountDue ?? 0);

    const isEmpty = computed(() => items.value.length === 0);

    // Cargar carrito desde el backend
    async function loadCart(): Promise<void> {
      if (!activeOrderId.value) {
        order.value = null;
        return;
      }

      loading.value = true;
      error.value = '';
      try {
        const fetched = await ordersService.getById(activeOrderId.value);
        // Si la order ya no está en estado CREATED, ya no es un carrito activo
        if (fetched.status !== 'CREATED') {
          activeOrderId.value = null;
          order.value = null;
        } else {
          order.value = fetched;
        }
      } catch (e: unknown) {
        // Si la order no existe (404), limpiar
        activeOrderId.value = null;
        order.value = null;
      } finally {
        loading.value = false;
      }
    }

    // Asegurar que existe una Order con status=CREATED (el carrito)
    async function ensureActiveOrder(customerId: string): Promise<string> {
      if (activeOrderId.value && order.value?.status === 'CREATED') {
        return activeOrderId.value;
      }

      // Cargar datos del cliente para obtener la dirección de envío
      const customer = await customersService.getById(customerId);

      const request: CreateOrderRequest = {
        customerId,
        orderDate: Date.now(),
        shippingAddress: customer.shippingAddress,
      };

      const created = await ordersService.create(request);
      activeOrderId.value = created.id;
      return created.id;
    }

    // Añadir un producto al carrito
    async function addToCart(
      product: Product,
      quantity: number,
      customerId: string,
    ): Promise<void> {
      loading.value = true;
      error.value = '';
      try {
        const orderId = await ensureActiveOrder(customerId);

        // Comprobar si ya existe una línea para este producto
        const existingLine = items.value.find(
          line => line.productId === product.id,
        );

        if (existingLine) {
          // Actualizar cantidad sumando la nueva
          const updateRequest: UpdateOrderLineRequest = {
            orderId,
            productId: product.id,
            quantity: existingLine.quantity + quantity,
            unitPrice: existingLine.unitPrice,
            categoryName: existingLine.categoryName,
            productName: existingLine.productName,
            providerName: existingLine.providerName,
            productProviderReference: existingLine.productProviderReference,
          };
          await ordersService.updateLine(existingLine.orderLineId, updateRequest);
        } else {
          // Crear nueva línea
          const lineRequest: CreateOrderLineRequest = {
            orderId,
            productId: product.id,
            quantity,
            unitPrice: product.price,
            categoryName: product.category?.name || '',
            productName: product.name,
            providerName: product.provider?.name || '',
            productProviderReference: product.providerReference,
          };
          await ordersService.createLine(lineRequest);
        }

        // Recargar la order completa desde el backend
        await loadCart();
      } catch (e: unknown) {
        error.value =
          e instanceof Error ? e.message : 'Error al añadir al carrito';
        throw e;
      } finally {
        loading.value = false;
      }
    }

    // Actualizar la cantidad de una línea
    async function updateQuantity(
      orderLineId: string,
      newQuantity: number,
    ): Promise<void> {
      if (!activeOrderId.value) return;

      const line = items.value.find(l => l.orderLineId === orderLineId);
      if (!line) return;

      loading.value = true;
      error.value = '';
      try {
        if (newQuantity <= 0) {
          await ordersService.deleteLine(orderLineId);
        } else {
          const updateRequest: UpdateOrderLineRequest = {
            orderId: activeOrderId.value,
            productId: line.productId,
            quantity: newQuantity,
            unitPrice: line.unitPrice,
            categoryName: line.categoryName,
            productName: line.productName,
            providerName: line.providerName,
            productProviderReference: line.productProviderReference,
          };
          await ordersService.updateLine(orderLineId, updateRequest);
        }
        await loadCart();
      } catch (e: unknown) {
        error.value =
          e instanceof Error ? e.message : 'Error al actualizar cantidad';
      } finally {
        loading.value = false;
      }
    }

    // Eliminar una línea del carrito
    async function removeFromCart(orderLineId: string): Promise<void> {
      if (!activeOrderId.value) return;

      loading.value = true;
      error.value = '';
      try {
        await ordersService.deleteLine(orderLineId);
        await loadCart();
      } catch (e: unknown) {
        error.value =
          e instanceof Error ? e.message : 'Error al eliminar del carrito';
      } finally {
        loading.value = false;
      }
    }

    // Vaciar el carrito (eliminar la order entera)
    async function clearCart(): Promise<void> {
      if (!activeOrderId.value) return;

      loading.value = true;
      error.value = '';
      try {
        await ordersService.delete(activeOrderId.value);
        activeOrderId.value = null;
        order.value = null;
      } catch (e: unknown) {
        error.value =
          e instanceof Error ? e.message : 'Error al vaciar el carrito';
      } finally {
        loading.value = false;
      }
    }

    // Confirmar la compra (pasar de CREATED a CONFIRMED)
    async function confirmOrder(): Promise<string> {
      if (!activeOrderId.value) {
        throw new Error('No hay carrito activo');
      }

      loading.value = true;
      error.value = '';
      try {
        const confirmedOrderId = activeOrderId.value;
        await ordersService.confirm(confirmedOrderId);
        // Guardar el ID en el historial de pedidos confirmados
        if (!confirmedOrderIds.value.includes(confirmedOrderId)) {
          confirmedOrderIds.value.push(confirmedOrderId);
        }
        activeOrderId.value = null;
        order.value = null;
        return confirmedOrderId;
      } catch (e: unknown) {
        error.value =
          e instanceof Error ? e.message : 'Error al confirmar el pedido';
        throw e;
      } finally {
        loading.value = false;
      }
    }

    function isInCart(productId: string): boolean {
      return items.value.some(line => line.productId === productId);
    }

    function getItemQuantity(productId: string): number {
      return (
        items.value.find(line => line.productId === productId)?.quantity ?? 0
      );
    }

    // Limpiar estado local al hacer logout
    function onLogout(): void {
      activeOrderId.value = null;
      order.value = null;
      error.value = '';
      // No limpiamos confirmedOrderIds: se pierden al hacer logout
      // porque ya no se puede acceder sin token
      confirmedOrderIds.value = [];
    }

    return {
      activeOrderId,
      confirmedOrderIds,
      order,
      items,
      loading,
      error,
      totalItems,
      totalPrice,
      isEmpty,
      loadCart,
      addToCart,
      updateQuantity,
      removeFromCart,
      clearCart,
      confirmOrder,
      isInCart,
      getItemQuantity,
      onLogout,
    };
  },
  {
    persist: {
      pick: ['activeOrderId', 'confirmedOrderIds'],
    },
  },
);
