<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { ordersService } from '~/services/ordersService';
import type { OrderSummary, OrderDetail } from '~/models/orders/Order';
import {
  getCustomerFullName,
  getFullShippingAddress,
} from '~/models/orders/Order';

definePageMeta({ middleware: ['auth', 'admin'] });

const { t } = useI18n();

const orders = ref<OrderSummary[]>([]);
const loading = ref(false);
const errorMessage = ref('');
const searchQuery = ref('');
const detailDialog = ref(false);
const selectedOrder = ref<OrderDetail | null>(null);

const headers = computed(() => [
  { title: t('admin.orders.customer'), key: 'customerName', sortable: true },
  { title: t('common.dni'), key: 'dni', sortable: true },
  { title: t('admin.orders.date'), key: 'orderDate', sortable: true },
  { title: t('common.shippingAddress'), key: 'shippingAddress', sortable: false },
  { title: t('admin.orders.status'), key: 'status', sortable: true },
  { title: t('common.total'), key: 'totalAmountDue', sortable: true },
  {
    title: t('common.actions'),
    key: 'actions',
    sortable: false,
    align: 'center' as const,
  },
]);

const filteredOrders = computed(() => {
  if (!searchQuery.value) return orders.value;
  const q = searchQuery.value.toLowerCase();
  return orders.value.filter(
    o =>
      getCustomerFullName(o).toLowerCase().includes(q) ||
      o.dni?.toLowerCase().includes(q) ||
      o.shippingAddress?.city?.toLowerCase().includes(q) ||
      o.orderId.toLowerCase().includes(q),
  );
});

onMounted(async () => {
  await loadOrders();
});

async function loadOrders() {
  loading.value = true;
  errorMessage.value = '';
  try {
    orders.value = await ordersService.getAll();
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value = err.response?.data?.message || t('admin.orders.failedToLoad');
  } finally {
    loading.value = false;
  }
}

async function viewOrderDetails(order: OrderSummary) {
  try {
    selectedOrder.value = await ordersService.getById(order.orderId);
  } catch {
    errorMessage.value = t('admin.orders.failedToLoadDetails');
    return;
  }
  detailDialog.value = true;
}

async function confirmOrder(orderId: string) {
  try {
    await ordersService.confirm(orderId);
    await loadOrders();
    if (selectedOrder.value?.orderId === orderId) {
      selectedOrder.value = await ordersService.getById(orderId);
    }
  } catch {
    errorMessage.value = t('admin.orders.failedToConfirm');
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
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function statusColor(status: string): string {
  return status === 'CONFIRMED' ? 'success' : 'warning';
}
</script>

<template>
  <v-container>
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-clipboard-list</v-icon>
        <span class="text-h5">{{ $t('admin.orders.title') }}</span>
        <v-spacer />
        <v-btn
          color="primary"
          prepend-icon="mdi-refresh"
          variant="outlined"
          @click="loadOrders"
        >
          {{ $t('common.refresh') }}
        </v-btn>
      </v-card-title>

      <v-card-text>
        <v-row>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="searchQuery"
              :label="$t('admin.orders.searchPlaceholder')"
              prepend-inner-icon="mdi-magnify"
              variant="outlined"
              density="compact"
              clearable
              hide-details
            />
          </v-col>
        </v-row>
      </v-card-text>

      <v-alert
        v-if="errorMessage"
        type="error"
        variant="tonal"
        closable
        class="mx-4 mb-2"
        @click:close="errorMessage = ''"
      >
        {{ errorMessage }}
      </v-alert>

      <v-data-table
        :items="filteredOrders"
        :headers="headers"
        :loading="loading"
        :items-per-page="10"
        :items-per-page-text="$t('admin.orders.perPage')"
        item-value="orderId"
        class="elevation-0"
      >
        <template #item.customerName="{ item }">
          {{ getCustomerFullName(item) }}
        </template>
        <template #item.orderDate="{ item }">
          {{ formatDate(item.orderDate) }}
        </template>
        <template #item.shippingAddress="{ item }">
          {{ getFullShippingAddress(item) }}
        </template>
        <template #item.status="{ item }">
          <v-chip
            :color="statusColor(item.status)"
            variant="tonal"
            size="small"
          >
            {{ item.status }}
          </v-chip>
        </template>
        <template #item.totalAmountDue="{ item }">
          <span class="font-weight-bold">{{
            formatPrice(item.totalAmountDue || 0)
          }}</span>
        </template>
        <template #item.actions="{ item }">
          <v-btn
            icon
            size="small"
            variant="text"
            color="primary"
            @click="viewOrderDetails(item)"
          >
            <v-icon>mdi-eye</v-icon>
          </v-btn>
          <v-btn
            v-if="item.status === 'CREATED'"
            icon
            size="small"
            variant="text"
            color="success"
            @click="confirmOrder(item.orderId)"
          >
            <v-icon>mdi-check-circle</v-icon>
          </v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- Diálogo de detalle del pedido -->
    <v-dialog v-model="detailDialog" max-width="700">
      <v-card v-if="selectedOrder" class="pa-0">
        <v-card-title class="d-flex align-center pa-5">
          <div>
            <div class="text-h6 font-weight-bold">{{ $t('admin.orders.orderDetails') }}</div>
            <div class="text-caption text-medium-emphasis">
              Order #{{ selectedOrder.orderId }}
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
          <v-row class="mb-4">
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">{{ $t('admin.orders.customer') }}</div>
              <div class="text-body-1 font-weight-medium">
                {{ selectedOrder.customer.firstName }}
                {{ selectedOrder.customer.lastName }}
              </div>
              <div class="text-caption">
                DNI: {{ selectedOrder.customer.dni }}
              </div>
            </v-col>
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">{{ $t('admin.orders.date') }}</div>
              <div class="text-body-1">
                {{ formatDate(selectedOrder.orderDate) }}
              </div>
            </v-col>
          </v-row>

          <div class="d-flex align-center mb-2">
            <v-icon size="18" color="primary" class="mr-2"
              >mdi-truck-delivery</v-icon
            >
            <span class="text-subtitle-2 font-weight-bold"
              >{{ $t('common.shippingAddress') }}</span
            >
          </div>
          <div class="text-body-2 mb-5 pl-6">
            {{ selectedOrder.shippingAddress.street }}<br />
            {{ selectedOrder.shippingAddress.city }},
            {{ selectedOrder.shippingAddress.province }}
            {{ selectedOrder.shippingAddress.postalCode }}<br />
            {{ selectedOrder.shippingAddress.country }}
          </div>

          <div class="d-flex align-center mb-2">
            <v-icon size="18" color="primary" class="mr-2"
              >mdi-package-variant</v-icon
            >
            <span class="text-subtitle-2 font-weight-bold">{{ $t('admin.orders.orderLines') }}</span>
          </div>
          <v-list lines="two" class="bg-transparent">
            <v-list-item
              v-for="line in selectedOrder.orderLines"
              :key="line.orderLineId"
              class="px-4 py-3 mb-2"
              rounded="lg"
            >
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
                  >&bull; {{ $t('orders.qty') }}: {{ line.quantity }} &times;
                  {{ formatPrice(line.unitPrice) }}</span
                >
              </v-list-item-subtitle>
              <template #append>
                <span class="text-body-1 font-weight-bold">{{
                  formatPrice(line.subTotal)
                }}</span>
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
