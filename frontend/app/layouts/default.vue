<script setup lang="ts">
import { ref, computed } from 'vue';
import { useUserStore } from '~/stores/userStore';
import { useCartStore } from '~/stores/cartStore';
import { useTheme } from 'vuetify';

const drawer = ref(true);
const userStore = useUserStore();
const cartStore = useCartStore();
const theme = useTheme();
const { t } = useI18n();

const isDark = computed(() => theme.global.current.value.dark);
function toggleTheme() {
  theme.global.name.value = isDark.value ? 'light' : 'dark';
}

const homeRoute = computed(() =>
  userStore.isAdministrator ? '/admin' : '/products',
);

const customerMenuItems = computed(() => [
  {
    title: t('nav.shopTitle'),
    icon: 'mdi-storefront-outline',
    to: '/products',
    description: t('nav.browseProducts'),
  },
  {
    title: t('nav.myCart'),
    icon: 'mdi-cart-outline',
    to: '/cart',
    description: t('nav.viewCart'),
  },
  {
    title: t('nav.myOrders'),
    icon: 'mdi-package-variant-closed',
    to: '/orders',
    description: t('nav.orderHistory'),
  },
]);

const adminCatalogItems = computed(() => [
  {
    title: t('nav.providers'),
    icon: 'mdi-truck-delivery-outline',
    to: '/admin/providers',
  },
  { title: t('nav.categories'), icon: 'mdi-shape-outline', to: '/admin/categories' },
  { title: t('nav.products'), icon: 'mdi-package-variant', to: '/admin/products' },
]);

const adminOrdersItems = computed(() => [
  {
    title: t('nav.allOrders'),
    icon: 'mdi-clipboard-list-outline',
    to: '/admin/orders',
  },
]);

const adminCustomersItems = computed(() => [
  {
    title: t('nav.allCustomers'),
    icon: 'mdi-account-group-outline',
    to: '/admin/customers',
  },
]);

const adminReportsItems = computed(() => [
  {
    title: t('nav.viewReports'),
    icon: 'mdi-chart-box-outline',
    to: '/admin/reports',
  },
]);

const visibleCustomerItems = computed(() => {
  if (userStore.isAdministrator) return [];
  if (!userStore.isLoggedIn) {
    return customerMenuItems.value.filter(item => item.to === '/products');
  }
  return customerMenuItems.value;
});

const showCustomerSection = computed(() => !userStore.isAdministrator);
const showManagementSection = computed(() => userStore.isAdministrator);

function handleLogout() {
  userStore.logout();
  navigateTo('/products');
}
</script>

<template>
  <v-layout>
    <!-- Barra superior -->
    <v-app-bar class="app-header" :elevation="0" height="70">
      <template #prepend>
        <v-app-bar-nav-icon
          variant="tonal"
          color="primary"
          class="ml-1"
          @click="drawer = !drawer"
        />
      </template>

      <NuxtLink
        :to="homeRoute"
        class="d-flex align-center text-decoration-none ml-2"
      >
        <div class="logo-wrapper d-flex align-center">
          <v-icon size="32" color="primary" class="mr-2">mdi-shopping</v-icon>
          <span
            class="brand-text"
            :class="isDark ? 'text-white' : 'text-grey-darken-4'"
          >
            Mary<span class="text-primary">Shopping</span>
          </span>
        </div>
      </NuxtLink>

      <v-spacer />

      <div class="d-flex align-center ga-2 mr-2">
        <!-- Selector de idioma -->
        <LanguageSwitcher />

        <!-- Cambio de tema -->
        <v-btn
          icon
          variant="flat"
          size="small"
          color="primary"
          @click="toggleTheme"
        >
          <v-icon>{{
            isDark ? 'mdi-weather-sunny' : 'mdi-weather-night'
          }}</v-icon>
          <v-tooltip activator="parent" location="bottom">
            {{ isDark ? $t('common.lightMode') : $t('common.darkMode') }}
          </v-tooltip>
        </v-btn>

        <!-- No está logueado -->
        <template v-if="!userStore.isLoggedIn">
          <v-btn
            variant="outlined"
            color="primary"
            class="text-none"
            to="/login"
          >
            <v-icon start>mdi-login</v-icon>
            {{ $t('nav.login') }}
          </v-btn>
          <v-btn
            variant="flat"
            color="primary"
            class="text-none"
            to="/register"
          >
            <v-icon start>mdi-account-plus</v-icon>
            {{ $t('nav.register') }}
          </v-btn>
        </template>

        <!-- Logueado -->
        <template v-else>
          <!-- Carrito (solo clientes) -->
          <v-btn
            v-if="!userStore.isAdministrator"
            icon
            variant="tonal"
            to="/cart"
            color="primary"
            class="mx-1"
          >
            <v-badge
              :content="cartStore.totalItems"
              :model-value="cartStore.totalItems > 0"
              color="secondary"
              overlap
              offset-x="2"
              offset-y="2"
            >
              <v-icon>mdi-cart-outline</v-icon>
            </v-badge>
            <v-tooltip activator="parent" location="bottom">
              {{ $t('nav.shoppingCart') }}
            </v-tooltip>
          </v-btn>

          <!-- Menú de usuario -->
          <v-menu offset="8">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                variant="tonal"
                color="primary"
                class="ml-2 px-3"
                rounded="pill"
              >
                <v-avatar size="28" color="primary" class="mr-2">
                  <span class="text-caption font-weight-bold">
                    {{ userStore.currentUser?.firstName?.charAt(0)
                    }}{{ userStore.currentUser?.lastName?.charAt(0) }}
                  </span>
                </v-avatar>
                <span class="d-none d-sm-inline text-none">{{
                  userStore.currentUser?.firstName
                }}</span>
                <v-icon end size="small">mdi-chevron-down</v-icon>
              </v-btn>
            </template>
            <v-card min-width="200" class="mt-2">
              <v-card-text class="pb-0">
                <div class="text-subtitle-2 font-weight-bold">
                  {{ userStore.currentUser?.firstName }}
                  {{ userStore.currentUser?.lastName }}
                </div>
                <div class="text-caption text-medium-emphasis">
                  {{ userStore.currentUser?.emailAddress }}
                </div>
              </v-card-text>
              <v-divider class="my-2" />
              <v-list density="compact" nav>
                <v-list-item
                  prepend-icon="mdi-account-outline"
                  :title="$t('nav.myProfile')"
                  to="/profile"
                  rounded="lg"
                />
                <v-divider class="my-1" />
                <v-list-item
                  prepend-icon="mdi-logout"
                  :title="$t('nav.logout')"
                  rounded="lg"
                  base-color="error"
                  @click="handleLogout"
                />
              </v-list>
            </v-card>
          </v-menu>
        </template>
      </div>
    </v-app-bar>

    <!-- Sidebar -->
    <v-navigation-drawer v-model="drawer" class="nav-drawer" width="260">
      <div class="pa-4 pb-2">
        <NuxtLink
          :to="homeRoute"
          class="d-flex align-center text-decoration-none d-md-none"
        >
          <AppLogo size="small" />
        </NuxtLink>
      </div>

      <v-list nav class="px-2">
        <!-- Customer Section -->
        <template v-if="showCustomerSection">
          <div v-if="userStore.isLoggedIn" class="nav-section-title">{{ $t('nav.shop') }}</div>
          <v-list-item
            v-for="item in visibleCustomerItems"
            :key="item.title"
            :prepend-icon="item.icon"
            :title="item.title"
            :to="item.to"
            rounded="lg"
            class="mb-1"
            color="primary"
          />
        </template>

        <!-- Admin Section -->
        <template v-if="showManagementSection">
          <v-divider
            v-if="showCustomerSection && visibleCustomerItems.length > 0"
            class="my-4"
          />
          <div class="nav-section-title">{{ $t('nav.management') }}</div>

          <v-list-item
            prepend-icon="mdi-view-dashboard"
            :title="$t('nav.dashboard')"
            to="/admin"
            rounded="lg"
            class="mb-1"
            color="primary"
          />

          <v-list-group value="Catalog">
            <template #activator="{ props }">
              <v-list-item
                v-bind="props"
                prepend-icon="mdi-bookshelf"
                :title="$t('nav.catalog')"
                rounded="lg"
                class="mb-1"
              />
            </template>
            <v-list-item
              v-for="item in adminCatalogItems"
              :key="item.title"
              :prepend-icon="item.icon"
              :title="item.title"
              :to="item.to"
              rounded="lg"
              class="mb-1 ml-2"
              color="primary"
            />
          </v-list-group>

          <v-list-group value="Orders">
            <template #activator="{ props }">
              <v-list-item
                v-bind="props"
                prepend-icon="mdi-clipboard-text-outline"
                :title="$t('nav.orders')"
                rounded="lg"
                class="mb-1"
              />
            </template>
            <v-list-item
              v-for="item in adminOrdersItems"
              :key="item.title"
              :prepend-icon="item.icon"
              :title="item.title"
              :to="item.to"
              rounded="lg"
              class="mb-1 ml-2"
              color="primary"
            />
          </v-list-group>

          <v-list-group value="Customers">
            <template #activator="{ props }">
              <v-list-item
                v-bind="props"
                prepend-icon="mdi-account-supervisor-outline"
                :title="$t('nav.customers')"
                rounded="lg"
                class="mb-1"
              />
            </template>
            <v-list-item
              v-for="item in adminCustomersItems"
              :key="item.title"
              :prepend-icon="item.icon"
              :title="item.title"
              :to="item.to"
              rounded="lg"
              class="mb-1 ml-2"
              color="primary"
            />
          </v-list-group>

          <v-list-group value="Reports">
            <template #activator="{ props }">
              <v-list-item
                v-bind="props"
                prepend-icon="mdi-chart-bar"
                :title="$t('nav.reports')"
                rounded="lg"
                class="mb-1"
              />
            </template>
            <v-list-item
              v-for="item in adminReportsItems"
              :key="item.title"
              :prepend-icon="item.icon"
              :title="item.title"
              :to="item.to"
              rounded="lg"
              class="mb-1 ml-2"
              color="primary"
            />
          </v-list-group>
        </template>
      </v-list>

      <template #append>
        <div class="pa-4">
          <v-card variant="tonal" color="primary" class="pa-3">
            <div class="d-flex align-center">
              <v-icon icon="mdi-headset" class="mr-3" size="24" />
              <div>
                <div class="text-caption font-weight-medium">{{ $t('nav.needHelp') }}</div>
                <div class="text-caption text-medium-emphasis">
                  {{ $t('nav.contactSupport') }}
                </div>
              </div>
            </div>
          </v-card>
        </div>
      </template>
    </v-navigation-drawer>

    <!-- Main content -->
    <v-main class="main-content">
      <slot />
    </v-main>
  </v-layout>
</template>

<style scoped>
.app-header {
  backdrop-filter: blur(20px);
  background: rgba(var(--v-theme-surface), 0.8) !important;
  border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.08);
}

.nav-drawer {
  border-right: 1px solid rgba(var(--v-theme-on-surface), 0.08) !important;
  background: rgb(var(--v-theme-surface)) !important;
}

.nav-section-title {
  font-size: 0.7rem;
  font-weight: 600;
  letter-spacing: 0.1em;
  color: rgba(var(--v-theme-on-surface), 0.5);
  padding: 8px 16px 8px;
  margin-top: 8px;
}

.main-content {
  background: rgb(var(--v-theme-background));
  min-height: 100vh;
}

.brand-text {
  font-size: 1.25rem;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.logo-wrapper {
  transition: transform 0.2s ease;
}

.logo-wrapper:hover {
  transform: scale(1.02);
}

.v-list-item {
  transition: all 0.2s ease;
}

.v-list-item--active {
  background: linear-gradient(
    135deg,
    rgba(99, 102, 241, 0.15),
    rgba(139, 92, 246, 0.1)
  ) !important;
}
</style>
