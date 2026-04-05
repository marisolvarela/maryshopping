// 🛍 PLUGIN CART MANAGER (Carga el carrito desde el backend)
// Al iniciar la app, si el usuario está logueado y tiene un activeOrderId,
// carga el carrito (Order con status=CREATED) desde el backend.
// También observa cambios de sesión para limpiar el carrito al hacer logout.
// Se ejecuta en el cliente (solo navegador)

import { watch } from 'vue';

export default defineNuxtPlugin({
  name: 'cart-manager',
  enforce: 'post',
  async setup(nuxtApp) {
    if (typeof window === 'undefined') return;

    nuxtApp.hook('app:suspense:resolve', async () => {
      try {
        const userStore = useUserStore();
        const cartStore = useCartStore();

        // Si el usuario está logueado y hay un activeOrderId persistido, cargar el carrito
        if (userStore.isLoggedIn && cartStore.activeOrderId) {
          await cartStore.loadCart();
        }

        // Observar cambios de sesión
        watch(
          () => userStore.isLoggedIn,
          async (isLoggedIn, wasLoggedIn) => {
            if (isLoggedIn && !wasLoggedIn) {
              // Al hacer login, cargar el carrito si hay un activeOrderId
              if (cartStore.activeOrderId) {
                await cartStore.loadCart();
              }
            } else if (!isLoggedIn && wasLoggedIn) {
              // Al hacer logout, limpiar el estado local del carrito
              cartStore.onLogout();
            }
          },
        );
      } catch (e) {
        console.error('[CartManager] Initialization error:', e);
      }
    });
  },
});
