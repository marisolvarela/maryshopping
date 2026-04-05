<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useUserStore } from '~/stores/userStore';
import { ordersService } from '~/services/ordersService';
import { categoriesService } from '~/services/categoriesService';
import { productsService } from '~/services/productsService';
import { providersService } from '~/services/providersService';
import { customersService } from '~/services/customersService';
import type { OrderSummary } from '~/models/orders/Order';
import type { Product } from '~/models/products/Product';

definePageMeta({ middleware: ['auth', 'admin'] });

const { t } = useI18n();
const userStore = useUserStore();
const loading = ref(true);

const stats = ref({
  totalOrders: 0,
  totalProducts: 0,
  totalCategories: 0,
  totalProviders: 0,
  totalCustomers: 0,
  recentOrders: [] as OrderSummary[],
});

const quickActions = computed(() => [
  {
    title: t('admin.dashboard.manageProducts'),
    subtitle: t('admin.dashboard.manageProductsDesc'),
    icon: 'mdi-package-variant',
    color: 'primary',
    to: '/admin/products',
  },
  {
    title: t('admin.dashboard.manageCategories'),
    subtitle: t('admin.dashboard.manageCategoriesDesc'),
    icon: 'mdi-shape-outline',
    color: 'secondary',
    to: '/admin/categories',
  },
  {
    title: t('admin.dashboard.manageProviders'),
    subtitle: t('admin.dashboard.manageProvidersDesc'),
    icon: 'mdi-truck-delivery-outline',
    color: 'info',
    to: '/admin/providers',
  },
  {
    title: t('admin.dashboard.viewOrders'),
    subtitle: t('admin.dashboard.viewOrdersDesc'),
    icon: 'mdi-clipboard-list-outline',
    color: 'success',
    to: '/admin/orders',
  },
  {
    title: t('admin.dashboard.viewCustomers'),
    subtitle: t('admin.dashboard.viewCustomersDesc'),
    icon: 'mdi-account-group-outline',
    color: 'warning',
    to: '/admin/customers',
  },
]);

const currentDate = computed(() =>
  new Date().toLocaleDateString('es-ES', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  }),
);

const greeting = computed(() => {
  const hour = new Date().getHours();
  if (hour < 12) return t('admin.dashboard.goodMorning');
  if (hour < 18) return t('admin.dashboard.goodAfternoon');
  return t('admin.dashboard.goodEvening');
});

onMounted(async () => {
  await loadDashboardData();
});

async function loadDashboardData() {
  loading.value = true;
  try {
    const [orders, categories, providers, customers] = await Promise.all([
      ordersService.getAll().catch(() => [] as OrderSummary[]),
      categoriesService.getAll().catch(() => []),
      providersService.getAll().catch(() => []),
      customersService.getAll().catch(() => []),
    ]);

    // Contar productos de todas las categorías (no hay endpoint GET de todos los productos)
    let productCount = 0;
    for (const cat of categories) {
      try {
        const catProducts = await productsService.getByCategory(cat.id);
        productCount += catProducts.length;
      } catch {
        // omitir
      }
    }

    stats.value.totalOrders = orders.length;
    stats.value.totalCategories = categories.length;
    stats.value.totalProducts = productCount;
    stats.value.totalProviders = providers.length;
    stats.value.totalCustomers = customers.length;
    stats.value.recentOrders = orders
      .sort((a, b) => b.orderDate - a.orderDate)
      .slice(0, 5);
  } catch (e) {
    console.error('Failed to load dashboard data:', e);
  } finally {
    loading.value = false;
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
    <!-- Bienvenida -->
    <v-card class="mb-6 pa-6" variant="tonal" color="primary">
      <div class="d-flex align-center flex-wrap">
        <div class="flex-grow-1">
          <p class="text-body-2 text-medium-emphasis mb-1">{{ currentDate }}</p>
          <h1 class="text-h4 font-weight-bold mb-2">
            {{ $t('admin.dashboard.hello') }},
            <span class="text-primary">{{
              userStore.currentUser?.firstName || userStore.authUser?.name || 'Administrator'
            }}</span>
          </h1>
          <p class="text-body-1 text-medium-emphasis">
            {{ $t('admin.dashboard.welcome') }}
          </p>
        </div>
        <v-icon size="80" color="primary" class="opacity-50 d-none d-md-flex"
          >mdi-view-dashboard</v-icon
        >
      </div>
    </v-card>

    <!-- Estadísticas -->
    <v-row class="mb-6">
      <v-col
        v-for="stat in [
          {
            label: $t('admin.dashboard.totalOrders'),
            value: stats.totalOrders,
            icon: 'mdi-clipboard-list',
            color: 'primary',
          },
          {
            label: $t('admin.dashboard.products'),
            value: stats.totalProducts,
            icon: 'mdi-package-variant',
            color: 'secondary',
          },
          {
            label: $t('admin.dashboard.categories'),
            value: stats.totalCategories,
            icon: 'mdi-shape',
            color: 'info',
          },
          {
            label: $t('admin.dashboard.providers'),
            value: stats.totalProviders,
            icon: 'mdi-truck-delivery',
            color: 'warning',
          },
          {
            label: $t('admin.dashboard.customers'),
            value: stats.totalCustomers,
            icon: 'mdi-account-group',
            color: 'success',
          },
        ]"
        :key="stat.label"
        cols="12"
        sm="6"
        md="4"
        lg
      >
        <v-card variant="outlined" class="h-100">
          <v-card-text class="text-center pa-4">
            <v-avatar
              :color="stat.color"
              variant="tonal"
              size="48"
              class="mb-3"
            >
              <v-icon size="24">{{ stat.icon }}</v-icon>
            </v-avatar>
            <div class="text-h4 font-weight-bold mb-1">
              <v-skeleton-loader
                v-if="loading"
                type="text"
                width="40"
                class="mx-auto"
              />
              <template v-else>{{ stat.value }}</template>
            </div>
            <div class="text-caption text-medium-emphasis">
              {{ stat.label }}
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <!-- Acciones rápidas -->
      <v-col cols="12" lg="7">
        <v-card variant="outlined" class="h-100">
          <v-card-title class="d-flex align-center pa-4">
            <v-icon class="mr-2" color="primary">mdi-lightning-bolt</v-icon>
            {{ $t('admin.dashboard.quickActions') }}
          </v-card-title>
          <v-divider />
          <v-card-text class="pa-4">
            <v-row>
              <v-col
                v-for="action in quickActions"
                :key="action.to"
                cols="12"
                sm="6"
              >
                <v-card
                  :to="action.to"
                  class="pa-4 h-100"
                  variant="tonal"
                  :color="action.color"
                >
                  <div class="d-flex align-center">
                    <v-avatar :color="action.color" size="48" class="mr-4">
                      <v-icon size="24" color="white">{{ action.icon }}</v-icon>
                    </v-avatar>
                    <div>
                      <div class="text-subtitle-1 font-weight-bold">
                        {{ action.title }}
                      </div>
                      <div class="text-caption text-medium-emphasis">
                        {{ action.subtitle }}
                      </div>
                    </div>
                    <v-spacer />
                    <v-icon color="grey">mdi-chevron-right</v-icon>
                  </div>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Pedidos recientes -->
      <v-col cols="12" lg="5">
        <v-card variant="outlined" class="h-100">
          <v-card-title class="d-flex align-center pa-4">
            <v-icon class="mr-2" color="secondary">mdi-clock-outline</v-icon>
            {{ $t('admin.dashboard.recentOrders') }}
            <v-spacer />
            <v-btn
              variant="text"
              color="primary"
              size="small"
              to="/admin/orders"
              >{{ $t('admin.dashboard.viewAll') }}</v-btn
            >
          </v-card-title>
          <v-divider />
          <v-card-text class="pa-0">
            <v-skeleton-loader v-if="loading" type="list-item-three-line@3" />
            <v-list
              v-else-if="stats.recentOrders.length > 0"
              lines="two"
              class="pa-0"
            >
              <template
                v-for="(order, index) in stats.recentOrders"
                :key="order.orderId"
              >
                <v-list-item to="/admin/orders">
                  <template #prepend>
                    <v-avatar color="primary" variant="tonal" size="40">
                      <v-icon size="20">mdi-receipt</v-icon>
                    </v-avatar>
                  </template>
                  <v-list-item-title class="font-weight-medium">
                    Order #{{ order.orderId?.substring(0, 8) }}...
                  </v-list-item-title>
                  <v-list-item-subtitle>
                    {{ order.shippingAddress?.city }} &bull;
                    {{ formatDate(order.orderDate) }}
                  </v-list-item-subtitle>
                  <template #append>
                    <div class="d-flex flex-column align-end">
                      <div class="text-body-2 font-weight-bold text-primary">
                        {{ formatPrice(order.totalAmountDue || 0) }}
                      </div>
                      <v-chip
                        :color="statusColor(order.status)"
                        variant="tonal"
                        size="x-small"
                      >
                        {{ order.status }}
                      </v-chip>
                    </div>
                  </template>
                </v-list-item>
                <v-divider v-if="index < stats.recentOrders.length - 1" />
              </template>
            </v-list>
            <div v-else class="text-center pa-8">
              <v-icon size="48" color="grey">mdi-clipboard-text-outline</v-icon>
              <p class="text-body-2 text-grey mt-2">No orders yet</p>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
