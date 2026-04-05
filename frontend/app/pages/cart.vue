<!-- 🛒 PÁGINA DE CARRITO (Resumen del carro)
  Muestra los productos en el carrito (Order con status=CREATED):
  - Lista de items (OrderLines: cantidad, precio, subtotal)
  - Modificar cantidad o eliminar items (PUT/DELETE /orders/orderLine)
  - Cálculo automático de total (totalAmountDue del backend)
  - Botón "Confirmar compra" → PUT /orders/order/status/confirmed/{id}
  - Requiere estar logueado (middleware: auth)
  - Después de confirmar, el carrito queda vacío (no hay Order CREATED)
-->

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useCartStore } from '~/stores/cartStore';

definePageMeta({ middleware: ['auth'] });

const cartStore = useCartStore();

const orderConfirmed = ref(false);
const confirmedOrderId = ref<string | null>(null);

const cartItems = computed(() => cartStore.items);
const totalPrice = computed(() => cartStore.totalPrice);
const totalItems = computed(() => cartStore.totalItems);
const loading = computed(() => cartStore.loading);
const error = computed(() => cartStore.error);

onMounted(async () => {
  if (cartStore.activeOrderId) {
    await cartStore.loadCart();
  }
});

async function confirmPurchase() {
  if (cartItems.value.length === 0) return;
  try {
    const orderId = await cartStore.confirmOrder();
    confirmedOrderId.value = orderId;
    orderConfirmed.value = true;
  } catch {
    // El error ya se gestiona en cartStore.error
  }
}

function formatPrice(price: number): string {
  return new Intl.NumberFormat('es-ES', {
    style: 'currency',
    currency: 'EUR',
  }).format(price);
}
</script>

<template>
  <v-container class="py-6" style="max-width: 900px">
    <!-- Pedido confirmado con éxito -->
    <template v-if="orderConfirmed">
      <v-card class="pa-8 text-center">
        <v-icon size="80" color="success" class="mb-4"
          >mdi-check-circle-outline</v-icon
        >
        <h2 class="text-h5 font-weight-bold mb-2">{{ $t('cart.orderConfirmed') }}</h2>
        <p class="text-body-1 text-medium-emphasis mb-6">
          {{ $t('cart.orderConfirmedMessage', { id: confirmedOrderId?.substring(0, 8) }) }}
        </p>
        <div class="d-flex ga-2 justify-center">
          <v-btn color="primary" to="/orders" class="text-none"
            >{{ $t('cart.viewOrders') }}</v-btn
          >
          <v-btn variant="outlined" to="/products" class="text-none"
            >{{ $t('cart.continueShopping') }}</v-btn
          >
        </div>
      </v-card>
    </template>

    <!-- Carrito vacío -->
    <template v-else-if="!loading && cartItems.length === 0">
      <v-card class="pa-8 text-center">
        <v-icon size="80" color="grey" class="mb-4">mdi-cart-off</v-icon>
        <h2 class="text-h5 font-weight-bold mb-2">{{ $t('cart.empty') }}</h2>
        <p class="text-body-1 text-medium-emphasis mb-6">
          {{ $t('cart.emptyHint') }}
        </p>
        <v-btn color="primary" to="/products" class="text-none"
          >{{ $t('cart.browseProducts') }}</v-btn
        >
      </v-card>
    </template>

    <!-- Estado de carga -->
    <template v-else-if="loading && cartItems.length === 0">
      <div class="text-center py-12">
        <v-progress-circular indeterminate color="primary" size="60" />
        <p class="mt-4 text-medium-emphasis">{{ $t('cart.loading') }}</p>
      </div>
    </template>

    <!-- Carrito con artículos -->
    <template v-else>
      <h2 class="text-h5 font-weight-bold mb-4">{{ $t('cart.title') }}</h2>

      <v-alert
        v-if="error"
        type="error"
        variant="tonal"
        class="mb-4"
      >
        {{ error }}
      </v-alert>

      <v-row>
        <v-col cols="12" md="8">
          <v-card>
            <v-list>
              <v-list-item
                v-for="item in cartItems"
                :key="item.orderLineId"
                class="py-4"
              >
                <template #prepend>
                  <v-sheet
                    width="80"
                    height="80"
                    color="surface-light"
                    rounded="lg"
                    class="d-flex align-center justify-center mr-4"
                  >
                    <v-icon color="primary">mdi-package-variant</v-icon>
                  </v-sheet>
                </template>

                <v-list-item-title class="font-weight-medium">{{
                  item.productName
                }}</v-list-item-title>
                <v-list-item-subtitle>
                  <v-chip
                    size="x-small"
                    color="primary"
                    variant="tonal"
                    class="mr-1"
                    >{{ item.categoryName }}</v-chip
                  >
                  <v-chip size="x-small" variant="outlined">{{
                    item.providerName
                  }}</v-chip>
                  <span class="ml-2">{{ formatPrice(item.unitPrice) }} {{ $t('products.each') }}</span>
                </v-list-item-subtitle>

                <template #append>
                  <div class="d-flex align-center ga-2">
                    <v-btn
                      icon
                      size="small"
                      variant="text"
                      :disabled="loading"
                      @click="
                        cartStore.updateQuantity(
                          item.orderLineId,
                          item.quantity - 1,
                        )
                      "
                    >
                      <v-icon>mdi-minus</v-icon>
                    </v-btn>
                    <span
                      class="text-body-1 font-weight-medium"
                      style="min-width: 24px; text-align: center"
                    >
                      {{ item.quantity }}
                    </span>
                    <v-btn
                      icon
                      size="small"
                      variant="text"
                      :disabled="loading"
                      @click="
                        cartStore.updateQuantity(
                          item.orderLineId,
                          item.quantity + 1,
                        )
                      "
                    >
                      <v-icon>mdi-plus</v-icon>
                    </v-btn>
                    <span
                      class="text-body-1 font-weight-bold ml-4"
                      style="min-width: 70px; text-align: right"
                    >
                      {{ formatPrice(item.subTotal) }}
                    </span>
                    <v-btn
                      icon
                      size="small"
                      variant="text"
                      color="error"
                      :disabled="loading"
                      @click="cartStore.removeFromCart(item.orderLineId)"
                    >
                      <v-icon>mdi-delete-outline</v-icon>
                    </v-btn>
                  </div>
                </template>
              </v-list-item>
            </v-list>
          </v-card>
        </v-col>

        <v-col cols="12" md="4">
          <v-card class="pa-4">
            <h3 class="text-h6 font-weight-bold mb-4">{{ $t('cart.orderSummary') }}</h3>
            <div class="d-flex justify-space-between mb-2">
              <span class="text-medium-emphasis">{{ $t('cart.items') }}</span>
              <span>{{ totalItems }}</span>
            </div>
            <v-divider class="my-3" />
            <div class="d-flex justify-space-between mb-4">
              <span class="text-body-1 font-weight-bold">{{ $t('common.total') }}</span>
              <span class="text-body-1 font-weight-bold text-primary"
                >{{ formatPrice(totalPrice) }}</span
              >
            </div>
            <v-btn
              color="primary"
              block
              size="large"
              class="text-none"
              :loading="loading"
              @click="confirmPurchase"
            >
              <v-icon start>mdi-check</v-icon>
              {{ $t('cart.confirmPurchase') }}
            </v-btn>
            <v-btn
              variant="outlined"
              block
              size="small"
              class="text-none mt-2"
              color="error"
              :loading="loading"
              @click="cartStore.clearCart()"
            >
              {{ $t('cart.clearCart') }}
            </v-btn>
          </v-card>
        </v-col>
      </v-row>
    </template>
  </v-container>
</template>
