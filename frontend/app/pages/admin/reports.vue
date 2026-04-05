<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import {
  reportsService,
  type TopProductReport,
  type TopCustomerReport,
} from '~/services/reportsService';

definePageMeta({ middleware: ['auth', 'admin'] });

const { t } = useI18n();

const topProducts = ref<TopProductReport[]>([]);
const topCustomers = ref<TopCustomerReport[]>([]);
const loading = ref(false);
const errorMessage = ref('');

const searchProducts = ref('');
const searchCustomers = ref('');

const productHeaders = computed(() => [
  { title: '#', key: 'rank', sortable: false, width: '60px' },
  { title: t('admin.reports.productName'), key: 'productName', sortable: true },
  { title: t('admin.reports.category'), key: 'categoryName', sortable: true },
  { title: t('admin.reports.provider'), key: 'providerName', sortable: true },
  { title: t('admin.reports.providerRef'), key: 'productProviderReference', sortable: true },
  { title: t('admin.reports.unitsSold'), key: 'totalUnitsSold', sortable: true },
  { title: t('admin.reports.revenue'), key: 'totalRevenue', sortable: true },
]);

const customerHeaders = computed(() => [
  { title: '#', key: 'rank', sortable: false, width: '60px' },
  { title: t('common.firstName'), key: 'firstName', sortable: true },
  { title: t('common.lastName'), key: 'lastName', sortable: true },
  { title: t('common.dni'), key: 'dni', sortable: true },
  { title: t('admin.reports.totalSpent'), key: 'totalSpent', sortable: true },
]);

const filteredProducts = computed(() => {
  if (!searchProducts.value) return topProducts.value;
  const q = searchProducts.value.toLowerCase();
  return topProducts.value.filter(
    p =>
      p.productName.toLowerCase().includes(q) ||
      p.categoryName.toLowerCase().includes(q) ||
      p.providerName.toLowerCase().includes(q) ||
      p.productProviderReference.toLowerCase().includes(q),
  );
});

const filteredCustomers = computed(() => {
  if (!searchCustomers.value) return topCustomers.value;
  const q = searchCustomers.value.toLowerCase();
  return topCustomers.value.filter(
    c =>
      c.firstName.toLowerCase().includes(q) ||
      c.lastName.toLowerCase().includes(q) ||
      c.dni.toLowerCase().includes(q),
  );
});

function formatPrice(value: number): string {
  return new Intl.NumberFormat('es-ES', {
    style: 'currency',
    currency: 'EUR',
  }).format(value);
}

const totalRevenue = computed(() =>
  filteredProducts.value.reduce((sum, p) => sum + p.totalRevenue, 0),
);

const totalUnitsSold = computed(() =>
  filteredProducts.value.reduce((sum, p) => sum + p.totalUnitsSold, 0),
);

const totalSpent = computed(() =>
  filteredCustomers.value.reduce((sum, c) => sum + c.totalSpent, 0),
);

onMounted(async () => {
  await loadReports();
});

async function loadReports() {
  loading.value = true;
  errorMessage.value = '';
  try {
    const [products, customers] = await Promise.all([
      reportsService.getTopProducts(),
      reportsService.getTopCustomers(),
    ]);
    topProducts.value = products;
    topCustomers.value = customers;
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.reports.failedToLoad');
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <v-container>
    <!-- Encabezado de página -->
    <v-row class="mb-4">
      <v-col>
        <div class="d-flex align-center justify-space-between">
          <div class="d-flex align-center">
            <v-icon size="32" color="primary" class="mr-3">mdi-chart-bar</v-icon>
            <div>
              <h1 class="text-h5 font-weight-bold">{{ $t('admin.reports.title') }}</h1>
              <p class="text-body-2 text-medium-emphasis mb-0">
                {{ $t('admin.reports.subtitle') }}
              </p>
            </div>
          </div>
          <v-btn
            color="primary"
            prepend-icon="mdi-refresh"
            variant="outlined"
            :loading="loading"
            @click="loadReports"
          >
            {{ $t('common.refresh') }}
          </v-btn>
        </div>
      </v-col>
    </v-row>

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

    <!-- Tabla de productos más vendidos -->
    <v-card class="mb-6">
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2" color="primary">mdi-trophy</v-icon>
        <span class="text-h6">{{ $t('admin.reports.topProducts') }}</span>
      </v-card-title>

      <v-card-text>
        <v-row>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="searchProducts"
              :label="$t('admin.reports.searchProducts')"
              prepend-inner-icon="mdi-magnify"
              variant="outlined"
              density="compact"
              clearable
              hide-details
            />
          </v-col>
        </v-row>
      </v-card-text>

      <v-data-table
        :headers="productHeaders"
        :items="filteredProducts"
        :loading="loading"
        :items-per-page="10"
        :items-per-page-options="[5, 10, 25, 50]"
        :items-per-page-text="$t('admin.reports.perPage')"
        hover
      >
        <template #item.rank="{ index }">
          <v-chip
            v-if="index < 3"
            :color="['amber', 'grey', 'brown'][index]"
            size="small"
            variant="flat"
          >
            {{ index + 1 }}
          </v-chip>
          <span v-else class="text-medium-emphasis">{{ index + 1 }}</span>
        </template>

        <template #item.productName="{ item }">
          <span class="font-weight-medium">{{ item.productName }}</span>
        </template>

        <template #item.categoryName="{ item }">
          <v-chip size="small" variant="tonal" color="primary">
            {{ item.categoryName }}
          </v-chip>
        </template>

        <template #item.totalUnitsSold="{ item }">
          <span class="font-weight-bold">{{ item.totalUnitsSold }}</span>
        </template>

        <template #item.totalRevenue="{ item }">
          <span class="font-weight-bold text-success">
            {{ formatPrice(item.totalRevenue) }}
          </span>
        </template>

        <template #tfoot>
          <tr v-if="filteredProducts.length" class="bg-surface-light">
            <td />
            <td :colspan="4" class="text-body-2 font-weight-bold pa-3">
              {{ $t('common.total') }}
            </td>
            <td class="text-body-2 font-weight-bold pa-3">{{ totalUnitsSold }}</td>
            <td class="text-body-2 font-weight-bold text-success pa-3">{{ formatPrice(totalRevenue) }}</td>
          </tr>
        </template>

        <template #no-data>
          <div class="text-center pa-4 text-medium-emphasis">
            {{ $t('admin.reports.noData') }}
          </div>
        </template>
      </v-data-table>
    </v-card>

    <!-- Tabla de mejores clientes -->
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2" color="primary">mdi-account-star</v-icon>
        <span class="text-h6">{{ $t('admin.reports.topCustomers') }}</span>
      </v-card-title>

      <v-card-text>
        <v-row>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="searchCustomers"
              :label="$t('admin.reports.searchCustomers')"
              prepend-inner-icon="mdi-magnify"
              variant="outlined"
              density="compact"
              clearable
              hide-details
            />
          </v-col>
        </v-row>
      </v-card-text>

      <v-data-table
        :headers="customerHeaders"
        :items="filteredCustomers"
        :loading="loading"
        :items-per-page="10"
        :items-per-page-options="[5, 10, 25, 50]"
        :items-per-page-text="$t('admin.reports.perPage')"
        hover
      >
        <template #item.rank="{ index }">
          <v-chip
            v-if="index < 3"
            :color="['amber', 'grey', 'brown'][index]"
            size="small"
            variant="flat"
          >
            {{ index + 1 }}
          </v-chip>
          <span v-else class="text-medium-emphasis">{{ index + 1 }}</span>
        </template>

        <template #item.firstName="{ item }">
          <span class="font-weight-medium">{{ item.firstName }}</span>
        </template>

        <template #item.totalSpent="{ item }">
          <span class="font-weight-bold text-success">
            {{ formatPrice(item.totalSpent) }}
          </span>
        </template>

        <template #tfoot>
          <tr v-if="filteredCustomers.length" class="bg-surface-light">
            <td />
            <td :colspan="3" class="text-body-2 font-weight-bold pa-3">
              {{ $t('common.total') }}
            </td>
            <td class="text-body-2 font-weight-bold text-success pa-3">{{ formatPrice(totalSpent) }}</td>
          </tr>
        </template>

        <template #no-data>
          <div class="text-center pa-4 text-medium-emphasis">
            {{ $t('admin.reports.noData') }}
          </div>
        </template>
      </v-data-table>
    </v-card>
  </v-container>
</template>
