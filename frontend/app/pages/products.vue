<!-- 📦 PÁGINA DE CATÁLOGO (Lista de productos)
  Muestra productos filtrados por categoría:
  - GET /catalog/categories (categorías para filtrar)
  - GET /catalog/categories/{id}/products (productos por categoría)
  - Búsqueda y filtro por categoría
  - Botón "Añadir al carrito" (usa cartStore)
  - Ruta pública (no requiere login)
-->

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { categoriesService } from '~/services/categoriesService';
import { productsService } from '~/services/productsService';
import type { Category } from '~/models/products/Category';
import type { Product } from '~/models/products/Product';
import { getImageUrl } from '~/models/products/Image';
import type { ProductImage } from '~/models/products/Image';
import { useCartStore } from '~/stores/cartStore';
import { useUserStore } from '~/stores/userStore';

const route = useRoute();
const cartStore = useCartStore();
const userStore = useUserStore();

const categories = ref<Category[]>([]);
const products = ref<Product[]>([]);
const allImages = ref<ProductImage[]>([]);
const loading = ref(true);
const selectedCategory = ref<string | null>(
  (route.query.category as string) || null,
);
const searchQuery = ref('');
const sortBy = ref('name');
const showLoginDialog = ref(false);
const selectedProduct = ref<Product | null>(null);
const showDetailDialog = ref(false);
const carouselIndex = ref(0);
const currentPage = ref(1);
const itemsPerPage = ref(9);

// Estado del lightbox
const lightboxDialog = ref(false);
const lightboxImages = ref<string[]>([]);
const lightboxIndex = ref(0);

onMounted(async () => {
  try {
    categories.value = await categoriesService.getAll();
    await loadProducts();
    try {
      allImages.value = await productsService.getAllImages();
    } catch {
      // Las imágenes no son críticas
    }
  } catch (e) {
    console.error('Failed to load products:', e);
  } finally {
    loading.value = false;
  }
});

watch(
  () => route.query.category,
  newCat => {
    selectedCategory.value = (newCat as string) || null;
  },
);

watch(selectedCategory, async () => {
  loading.value = true;
  try {
    await loadProducts();
  } finally {
    loading.value = false;
  }
});

async function loadProducts() {
  if (selectedCategory.value) {
    products.value = await productsService.getByCategory(
      selectedCategory.value,
    );
  } else if (categories.value.length > 0) {
    // No hay endpoint "obtener todos" — cargar de todas las categorías
    const allProducts: Product[] = [];
    for (const cat of categories.value) {
      try {
        const catProducts = await productsService.getByCategory(cat.id);
        allProducts.push(...catProducts);
      } catch {
        // omitir categoría con error
      }
    }
    products.value = allProducts;
  }
}

const filteredProducts = computed(() => {
  let result = products.value;

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    result = result.filter(
      p =>
        p.name.toLowerCase().includes(q) ||
        p.description?.toLowerCase().includes(q),
    );
  }

  if (sortBy.value === 'name') {
    result = [...result].sort((a, b) => a.name.localeCompare(b.name));
  } else if (sortBy.value === 'price-asc') {
    result = [...result].sort((a, b) => a.price - b.price);
  } else if (sortBy.value === 'price-desc') {
    result = [...result].sort((a, b) => b.price - a.price);
  }

  return result;
});

const totalPages = computed(() =>
  Math.ceil(filteredProducts.value.length / itemsPerPage.value),
);

const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  return filteredProducts.value.slice(start, start + itemsPerPage.value);
});

// Volver a la página 1 cuando cambian los filtros
watch([searchQuery, selectedCategory, sortBy], () => {
  currentPage.value = 1;
});

function getProductImageUrl(product: Product): string {
  const img = allImages.value.find(
    i => i.productId === product.id && i.isPrimary,
  );
  if (img) return getImageUrl(img.id);
  const anyImg = allImages.value.find(i => i.productId === product.id);
  return anyImg ? getImageUrl(anyImg.id) : '';
}

function getProductImages(product: Product): string[] {
  const imgs = allImages.value
    .filter(i => i.productId === product.id)
    .sort((a, b) => {
      if (a.isPrimary && !b.isPrimary) return -1;
      if (!a.isPrimary && b.isPrimary) return 1;
      return a.order - b.order;
    });
  return imgs.map(i => getImageUrl(i.id));
}

function addToCart(product: Product) {
  if (!userStore.isLoggedIn) {
    showLoginDialog.value = true;
    return;
  }
  const customerId = userStore.customerId;
  if (!customerId) return;
  cartStore.addToCart(product, 1, customerId);
}

function openDetail(product: Product) {
  selectedProduct.value = product;
  carouselIndex.value = 0;
  showDetailDialog.value = true;
}

function openLightbox(images: string[], index: number) {
  lightboxImages.value = images;
  lightboxIndex.value = index;
  lightboxDialog.value = true;
}
</script>

<template>
  <v-container fluid class="py-6">
    <v-row>
      <!-- Filtros laterales -->
      <v-col cols="12" md="3">
        <v-card class="pa-4 mb-4">
          <h3 class="text-subtitle-1 font-weight-bold mb-3">{{ $t('products.search') }}</h3>
          <v-text-field
            v-model="searchQuery"
            :placeholder="$t('products.searchPlaceholder')"
            prepend-inner-icon="mdi-magnify"
            density="compact"
            clearable
            hide-details
          />
        </v-card>

        <v-card class="pa-4 mb-4">
          <h3 class="text-subtitle-1 font-weight-bold mb-3">{{ $t('products.categories') }}</h3>
          <v-list density="compact" nav>
            <v-list-item
              :active="!selectedCategory"
              rounded="lg"
              color="primary"
              @click="selectedCategory = null"
            >
              <v-list-item-title>{{ $t('products.allCategories') }}</v-list-item-title>
            </v-list-item>
            <v-list-item
              v-for="cat in categories"
              :key="cat.id"
              :active="selectedCategory === cat.id"
              rounded="lg"
              color="primary"
              @click="selectedCategory = cat.id"
            >
              <v-list-item-title>{{ cat.name }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>

        <v-card class="pa-4">
          <h3 class="text-subtitle-1 font-weight-bold mb-3">{{ $t('products.sortBy') }}</h3>
          <v-select
            v-model="sortBy"
            :items="[
              { title: $t('products.sortName'), value: 'name' },
              { title: $t('products.sortPriceAsc'), value: 'price-asc' },
              { title: $t('products.sortPriceDesc'), value: 'price-desc' },
            ]"
            density="compact"
            hide-details
          />
        </v-card>
      </v-col>

      <!-- Grid de productos -->
      <v-col cols="12" md="9">
        <div class="d-flex align-center mb-4">
          <h2 class="text-h5 font-weight-bold">
            {{
              selectedCategory
                ? categories.find(c => c.id === selectedCategory)?.name
                : $t('products.allProducts')
            }}
          </h2>
          <v-spacer />
          <v-chip variant="tonal" color="primary"
            >{{ $t('products.productCount', { count: filteredProducts.length }) }}</v-chip
          >
        </div>

        <div v-if="loading" class="text-center py-12">
          <v-progress-circular indeterminate color="primary" size="64" />
        </div>

        <v-row v-else-if="paginatedProducts.length > 0">
          <v-col
            v-for="product in paginatedProducts"
            :key="product.id"
            cols="12"
            sm="6"
            lg="4"
          >
            <v-card
              hover
              class="h-100 d-flex flex-column"
              @click="openDetail(product)"
            >
              <div class="position-relative">
                <v-img
                  v-if="getProductImageUrl(product)"
                  :src="getProductImageUrl(product)"
                  height="200"
                  cover
                />
                <v-sheet
                  v-else
                  height="200"
                  color="surface-light"
                  class="d-flex align-center justify-center"
                >
                  <v-icon size="64" color="grey">mdi-image-off-outline</v-icon>
                </v-sheet>
                <v-chip
                  v-if="getProductImages(product).length > 1"
                  size="x-small"
                  color="black"
                  class="position-absolute"
                  style="top: 8px; right: 8px; opacity: 0.8"
                >
                  <v-icon size="12" class="mr-1">mdi-image-multiple</v-icon>
                  {{ getProductImages(product).length }}
                </v-chip>
              </div>
              <v-card-title class="text-body-1 font-weight-medium">{{
                product.name
              }}</v-card-title>
              <v-card-subtitle
                >{{ product.category?.name }} &bull;
                {{ product.provider?.name }}</v-card-subtitle
              >
              <v-card-text class="text-body-2 text-medium-emphasis flex-grow-1">
                {{ product.description?.substring(0, 100)
                }}{{ (product.description?.length || 0) > 100 ? '...' : '' }}
              </v-card-text>
              <v-card-actions class="px-4 pb-4">
                <v-chip color="primary" variant="flat"
                  >{{ product.price.toFixed(2) }} &euro;</v-chip
                >
                <v-spacer />
                <v-btn
                  variant="tonal"
                  color="primary"
                  size="small"
                  class="text-none"
                  @click.stop="addToCart(product)"
                >
                  <v-icon start>mdi-cart-plus</v-icon>
                  {{ $t('products.addToCart') }}
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>

        <div
          v-if="!loading && totalPages > 1"
          class="d-flex justify-center mt-6"
        >
          <v-pagination
            v-model="currentPage"
            :length="totalPages"
            :total-visible="5"
            rounded="circle"
          />
        </div>

        <v-sheet v-if="!loading && filteredProducts.length === 0" class="text-center py-12" color="transparent">
          <v-icon size="80" color="grey" class="mb-4">mdi-magnify-close</v-icon>
          <h3 class="text-h6">{{ $t('products.noProducts') }}</h3>
          <p class="text-medium-emphasis">
            {{ $t('products.noProductsHint') }}
          </p>
        </v-sheet>
      </v-col>
    </v-row>

    <!-- Diálogo de detalle del producto -->
    <v-dialog v-model="showDetailDialog" max-width="700">
      <v-card v-if="selectedProduct" class="pa-6">
        <v-card-title class="d-flex align-center pa-0 mb-4">
          <span class="text-h5 font-weight-bold">{{
            selectedProduct.name
          }}</span>
          <v-spacer />
          <v-btn icon variant="text" @click="showDetailDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-row>
          <v-col cols="12" sm="5">
            <!-- Carrusel para múltiples imágenes -->
            <template v-if="getProductImages(selectedProduct).length > 1">
              <v-carousel
                v-model="carouselIndex"
                height="250"
                show-arrows="hover"
                hide-delimiters
                class="rounded-lg"
              >
                <v-carousel-item
                  v-for="(imgUrl, index) in getProductImages(selectedProduct)"
                  :key="imgUrl"
                >
                  <v-img
                    :src="imgUrl"
                    height="250"
                    cover
                    class="cursor-pointer"
                    @click="openLightbox(getProductImages(selectedProduct), index)"
                  />
                </v-carousel-item>
              </v-carousel>
              <div class="d-flex ga-2 mt-2 overflow-x-auto">
                <v-img
                  v-for="(imgUrl, index) in getProductImages(selectedProduct)"
                  :key="imgUrl"
                  :src="imgUrl"
                  width="56"
                  height="56"
                  cover
                  rounded="lg"
                  class="flex-shrink-0 cursor-pointer"
                  :style="{
                    border: carouselIndex === index ? '2px solid rgb(var(--v-theme-primary))' : '2px solid transparent',
                    opacity: carouselIndex === index ? 1 : 0.6,
                  }"
                  @click="carouselIndex = index"
                />
              </div>
            </template>
            <!-- Imagen única -->
            <v-img
              v-else-if="getProductImageUrl(selectedProduct)"
              :src="getProductImageUrl(selectedProduct)"
              height="250"
              cover
              rounded="lg"
              class="cursor-pointer"
              @click="openLightbox([getProductImageUrl(selectedProduct)], 0)"
            />
            <!-- Sin imagen -->
            <v-sheet
              v-else
              height="250"
              color="surface-light"
              rounded="lg"
              class="d-flex align-center justify-center"
            >
              <v-icon size="80" color="grey">mdi-image-off-outline</v-icon>
            </v-sheet>
          </v-col>
          <v-col cols="12" sm="7">
            <v-chip color="primary" variant="flat" size="large" class="mb-4">
              {{ selectedProduct.price.toFixed(2) }} &euro;
            </v-chip>
            <p class="text-body-1 mb-3">
              {{ selectedProduct.description }}
            </p>
            <div class="text-body-2 text-medium-emphasis mb-2">
              <strong>{{ $t('products.category') }}</strong> {{ selectedProduct.category?.name }}
            </div>
            <div class="text-body-2 text-medium-emphasis mb-2">
              <strong>{{ $t('products.provider') }}</strong> {{ selectedProduct.provider?.name }}
            </div>
            <div class="text-body-2 text-medium-emphasis mb-4">
              <strong>{{ $t('products.ref') }}</strong> {{ selectedProduct.providerReference }}
            </div>
            <v-btn
              color="primary"
              class="text-none"
              size="large"
              @click="
                addToCart(selectedProduct);
                showDetailDialog = false;
              "
            >
              <v-icon start>mdi-cart-plus</v-icon>
              {{ $t('products.addToCart') }}
            </v-btn>
          </v-col>
        </v-row>
      </v-card>
    </v-dialog>

    <!-- Diálogo de inicio de sesión requerido -->
    <v-dialog v-model="showLoginDialog" max-width="400">
      <v-card class="pa-6 text-center">
        <v-icon size="64" color="warning" class="mb-4"
          >mdi-alert-circle-outline</v-icon
        >
        <h3 class="text-h6 mb-2">{{ $t('products.loginRequired') }}</h3>
        <p class="text-body-2 text-medium-emphasis mb-4">
          {{ $t('products.loginRequiredMessage') }}
        </p>
        <div class="d-flex ga-2 justify-center">
          <v-btn variant="outlined" @click="showLoginDialog = false"
            >{{ $t('common.cancel') }}</v-btn
          >
          <v-btn color="primary" to="/login" class="text-none">{{ $t('products.signIn') }}</v-btn>
        </div>
      </v-card>
    </v-dialog>

    <!-- Diálogo lightbox -->
    <v-dialog v-model="lightboxDialog" fullscreen>
      <v-card color="black">
        <v-toolbar color="transparent">
          <v-spacer />
          <v-btn icon color="white" @click="lightboxDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>

        <v-carousel
          v-model="lightboxIndex"
          height="calc(100vh - 64px)"
          hide-delimiters
          show-arrows
        >
          <v-carousel-item
            v-for="(image, index) in lightboxImages"
            :key="index"
            :src="image"
            contain
          />
        </v-carousel>

        <div class="lightbox-counter">
          {{ lightboxIndex + 1 }} / {{ lightboxImages.length }}
        </div>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
.lightbox-counter {
  position: absolute;
  bottom: 16px;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  font-size: 14px;
  background: rgba(0, 0, 0, 0.6);
  padding: 4px 12px;
  border-radius: 16px;
  backdrop-filter: blur(4px);
}
</style>
