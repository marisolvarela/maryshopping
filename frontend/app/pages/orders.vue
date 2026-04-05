<!-- 🛒 PÁGINA DE PEDIDOS (Mis compras / Todos los pedidos)
  - Admin: GET /orders (lista todos los pedidos)
  - Customer: carga sus pedidos confirmados por ID (GET /orders/{id})
  - Lista todos los pedidos con fecha, estado, total
  - Clic en un pedido = modal con detalles
  - Muestra cada línea del pedido (producto, cantidad, precio)
  - Requiere estar logueado (middleware: auth)
-->

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ordersService } from '~/services/ordersService';
import { useUserStore } from '~/stores/userStore';
import { useCartStore } from '~/stores/cartStore';
import type { OrderSummary, OrderDetail } from '~/models/orders/Order';

definePageMeta({ middleware: ['auth'] });

const { t } = useI18n();
const userStore = useUserStore();
const cartStore = useCartStore();

const orders = ref<OrderSummary[]>([]);
const loading = ref(false);
const errorMessage = ref('');
const detailDialog = ref(false);
const selectedOrder = ref<OrderDetail | null>(null);

onMounted(async () => {
  await loadOrders();
});

async function loadOrders() {
  loading.value = true;
  errorMessage.value = '';
  try {
    if (userStore.isAdministrator) {
      // Admin: usa GET /orders para obtener todos los pedidos
      orders.value = await ordersService.getAll();
    } else {
      // Customer: carga cada pedido confirmado por su ID
      const loadedOrders: OrderSummary[] = [];
      for (const orderId of cartStore.confirmedOrderIds) {
        try {
          const detail = await ordersService.getById(orderId);
          // Mapear OrderDetail → OrderSummary para la lista
          loadedOrders.push({
            orderId: detail.orderId,
            orderDate: detail.orderDate,
            customerId: detail.customer.customerId,
            firstName: detail.customer.firstName,
            lastName: detail.customer.lastName,
            dni: detail.customer.dni,
            shippingAddress: detail.shippingAddress,
            orderLines: detail.orderLines,
            totalAmountDue: detail.totalAmountDue,
            status: detail.status,
          });
        } catch {
          // El pedido puede haber sido eliminado; ignorar
        }
      }
      orders.value = loadedOrders;
    }
  } catch (error: unknown) {
    const err = error as {
      response?: { status?: number; data?: { message?: string } };
    };
    errorMessage.value =
      err.response?.data?.message || t('orders.failedToLoad');
  } finally {
    loading.value = false;
  }
}

async function viewOrderDetails(order: OrderSummary) {
  try {
    selectedOrder.value = await ordersService.getById(order.orderId);
    detailDialog.value = true;
  } catch {
    errorMessage.value = 'Failed to load order details';
  }
}

async function confirmOrder(orderId: string) {
  try {
    await ordersService.confirm(orderId);
    await loadOrders();
    if (selectedOrder.value?.orderId === orderId) {
      selectedOrder.value = await ordersService.getById(orderId);
    }
  } catch {
    errorMessage.value = t('orders.failedToConfirm');
  }
}

function formatPrice(price: number): string {
  return new Intl.NumberFormat('es-ES', {
    style: 'currency',
    currency: 'EUR',
  }).format(price);
}

function formatDate(timestamp: number): string {
  return new Date(timestamp).toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
}

function statusColor(status: string): string {
  return status === 'CONFIRMED' ? 'success' : 'warning';
}
</script>

<template>
  <v-container class="py-6">
    <!-- Encabezado de página -->
    <div class="d-flex align-center ga-4 mb-6">
      <v-avatar color="primary" variant="tonal" size="56" rounded="lg">
        <v-icon size="28">mdi-package-variant-closed</v-icon>
      </v-avatar>
      <div>
        <h1 class="text-h4 font-weight-bold">{{ $t('orders.title') }}</h1>
        <p class="text-body-2 text-medium-emphasis mb-0">
          {{ $t('orders.subtitle') }}
        </p>
      </div>
    </div>

    <v-alert
      v-if="errorMessage"
      type="error"
      variant="tonal"
      closable
      class="mb-4"
      @click:close="errorMessage = ''"
    >
      {{ errorMessage }}
    </v-alert>

    <!-- Cargando -->
    <div v-if="loading" class="text-center py-12">
      <v-progress-circular indeterminate color="primary" size="60" />
      <p class="mt-4 text-medium-emphasis">{{ $t('orders.loading') }}</p>
    </div>

    <!-- Estado vacío -->
    <v-card
      v-else-if="orders.length === 0"
      class="text-center pa-12"
      elevation="0"
      variant="outlined"
    >
      <v-icon size="64" color="primary" class="mb-4"
        >mdi-package-variant-closed-remove</v-icon
      >
      <h2 class="text-h5 font-weight-bold mb-2">{{ $t('orders.empty') }}</h2>
      <p class="text-body-1 text-medium-emphasis mb-6">
        {{ $t('orders.emptyHint') }}
      </p>
      <v-btn
        color="primary"
        size="large"
        to="/products"
        prepend-icon="mdi-storefront"
      >
        {{ $t('orders.browseProducts') }}
      </v-btn>
    </v-card>

    <!-- Lista de pedidos -->
    <div v-else>
      <v-card
        v-for="order in orders"
        :key="order.orderId"
        class="mb-4"
        variant="outlined"
      >
        <v-card-text class="pa-5">
          <v-row align="center">
            <v-col cols="12" md="3">
              <div class="text-caption text-medium-emphasis">{{ $t('orders.orderPlaced') }}</div>
              <div class="text-body-1 font-weight-bold">
                {{ formatDate(order.orderDate) }}
              </div>
              <div class="text-caption text-medium-emphasis mt-2">{{ $t('orders.orderNumber') }}</div>
              <div class="text-body-2 font-weight-medium">
                {{ order.orderId.slice(0, 8) }}...
              </div>
            </v-col>
            <v-col cols="6" md="2">
              <div class="text-caption text-medium-emphasis">Total</div>
              <div class="text-h6 font-weight-bold text-primary">
                {{ formatPrice(order.totalAmountDue || 0) }}
              </div>
            </v-col>
            <v-col cols="6" md="2">
              <div class="text-caption text-medium-emphasis">Status</div>
              <v-chip
                size="small"
                :color="statusColor(order.status)"
                variant="tonal"
              >
                {{ order.status }}
              </v-chip>
            </v-col>
            <v-col cols="12" md="3">
              <div class="text-caption text-medium-emphasis">Shipping to</div>
              <div class="text-body-1 d-flex align-center">
                <v-icon size="small" color="primary" class="mr-1"
                  >mdi-map-marker</v-icon
                >
                {{ order.shippingAddress?.city }},
                {{ order.shippingAddress?.country }}
              </div>
            </v-col>
            <v-col cols="12" md="2" class="text-md-right">
              <v-btn
                color="primary"
                variant="tonal"
                size="small"
                rounded="pill"
                @click="viewOrderDetails(order)"
              >
                {{ $t('orders.viewDetails') }}
              </v-btn>
              <v-btn
                v-if="order.status === 'CREATED'"
                color="success"
                variant="tonal"
                size="small"
                rounded="pill"
                class="ml-1"
                @click="confirmOrder(order.orderId)"
              >
                {{ $t('orders.confirmOrder') }}
              </v-btn>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </div>

    <!-- Diálogo de detalle del pedido -->
    <v-dialog v-model="detailDialog" max-width="700">
      <v-card v-if="selectedOrder" class="pa-0">
        <v-card-title class="d-flex align-center pa-5">
          <div>
            <div class="text-h6 font-weight-bold">{{ $t('orders.orderDetails') }}</div>
            <div class="text-caption text-medium-emphasis">
              Order #{{ selectedOrder.orderId.slice(0, 8) }}...
            </div>
          </div>
          <v-spacer />
          <v-chip
            :color="statusColor(selectedOrder.status)"
            variant="tonal"
            size="small"
            class="mr-2"
          >
            {{ selectedOrder.status }}
          </v-chip>
          <v-btn icon variant="text" @click="detailDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>

        <v-divider />

        <v-card-text class="pa-5">
          <v-chip
            color="info"
            variant="tonal"
            size="small"
            prepend-icon="mdi-calendar"
            class="mb-4"
          >
            {{ $t('orders.placedOn') }} {{ formatDate(selectedOrder.orderDate) }}
          </v-chip>

          <!-- Dirección de envío -->
          <div class="d-flex align-center mb-2">
            <v-icon size="18" color="primary" class="mr-2"
              >mdi-truck-delivery</v-icon
            >
            <span class="text-subtitle-2 font-weight-bold"
              >{{ $t('orders.shippingAddress') }}</span
            >
          </div>
          <div class="text-body-2 mb-5 pl-6">
            {{ selectedOrder.shippingAddress.street }}<br >
            {{ selectedOrder.shippingAddress.city }},
            {{ selectedOrder.shippingAddress.province }}
            {{ selectedOrder.shippingAddress.postalCode }}<br >
            {{ selectedOrder.shippingAddress.country }}
          </div>

          <!-- Artículos del pedido -->
          <div class="d-flex align-center mb-2">
            <v-icon size="18" color="primary" class="mr-2"
              >mdi-package-variant</v-icon
            >
            <span class="text-subtitle-2 font-weight-bold">Items</span>
          </div>
          <v-list lines="two" class="bg-transparent">
            <v-list-item
              v-for="line in selectedOrder.orderLines"
              :key="line.orderLineId"
              class="px-4 py-3 mb-2"
              rounded="lg"
            >
              <template #prepend>
                <v-avatar rounded="lg" size="50" color="surface-variant">
                  <v-icon color="primary">mdi-package-variant</v-icon>
                </v-avatar>
              </template>

              <v-list-item-title class="font-weight-medium">{{
                line.productName
              }}</v-list-item-title>
              <v-list-item-subtitle>
                <v-chip
                  size="x-small"
                  color="primary"
                  variant="tonal"
                  class="mr-1"
                  >{{ line.categoryName }}</v-chip
                >
                <v-chip size="x-small" variant="outlined">{{
                  line.providerName
                }}</v-chip>
                <span class="ml-2"
                  >&bull; {{ $t('orders.qty') }} {{ line.quantity }} &times;
                  {{ formatPrice(line.unitPrice) }}</span
                >
              </v-list-item-subtitle>

              <template #append>
                <div class="text-body-1 font-weight-bold">
                  {{ formatPrice(line.subTotal) }}
                </div>
              </template>
            </v-list-item>
          </v-list>

          <v-divider class="my-4" />

          <div class="d-flex justify-space-between align-center">
            <span class="text-h6 font-weight-bold">{{ $t('common.total') }}</span>
            <span class="text-h6 font-weight-bold text-primary">{{
              formatPrice(selectedOrder.totalAmountDue || 0)
            }}</span>
          </div>
        </v-card-text>

        <v-divider />

        <v-card-actions class="pa-4">
          <v-btn
            v-if="selectedOrder.status === 'CREATED'"
            color="success"
            variant="tonal"
            @click="confirmOrder(selectedOrder.orderId)"
          >
            {{ $t('orders.confirmOrder') }}
          </v-btn>
          <v-spacer />
          <v-btn variant="text" @click="detailDialog = false">{{ $t('common.close') }}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
