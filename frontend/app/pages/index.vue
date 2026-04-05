<!-- 🏠 PÁGINA DE INICIO (Home)
  Landing page de la tienda:
  - Hero section con botón "Explorar"
  - Muestra todas las categorías disponibles
  - Muestra productos destacados (de la primera categoría)
  - GET /catalog/categories (todas las categorías)
  - GET /catalog/categories/{id}/products (productos por categoría)
  - Página pública (no requiere login)
  - carga datos en onMounted
-->

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { categoriesService } from '~/services/categoriesService';
import { productsService } from '~/services/productsService';
import type { Category } from '~/models/products/Category';
import type { Product } from '~/models/products/Product';
import { getImageUrl } from '~/models/products/Image';
import type { ProductImage } from '~/models/products/Image';

const categories = ref<Category[]>([]);
const featuredProducts = ref<Product[]>([]);
const allImages = ref<ProductImage[]>([]);
const loading = ref(true);

onMounted(async () => {
  try {
    const cats = await categoriesService.getAll();
    categories.value = cats;

    // Cargar productos de la primera categoría como destacados
    if (cats.length > 0) {
      const prods = await productsService.getByCategory(cats[0].id);
      featuredProducts.value = prods.slice(0, 8);
    }

    // Cargar todos los metadatos de imágenes para las miniaturas
    try {
      allImages.value = await productsService.getAllImages();
    } catch {
      // Las imágenes no son críticas
    }
  } catch (e) {
    console.error('Failed to load home data:', e);
  } finally {
    loading.value = false;
  }
});

function getProductImageUrl(product: Product): string {
  const img = allImages.value.find(
    i => i.productId === product.id && i.isPrimary,
  );
  if (img) return getImageUrl(img.id);
  const anyImg = allImages.value.find(i => i.productId === product.id);
  return anyImg ? getImageUrl(anyImg.id) : '';
}
</script>

<template>
  <v-container fluid class="pa-0">
    <!-- Sección hero -->
    <v-sheet
      color="primary"
      class="d-flex align-center justify-center"
      height="400"
    >
      <div class="text-center text-white">
        <h1 class="text-h2 font-weight-bold mb-4">MaryShopping</h1>
        <p class="text-h6 font-weight-light mb-8">
          {{ $t('home.hero') }}
        </p>
        <v-btn
          size="x-large"
          variant="flat"
          color="white"
          class="text-primary text-none"
          to="/products"
        >
          <v-icon start>mdi-storefront-outline</v-icon>
          {{ $t('home.browseProducts') }}
        </v-btn>
      </div>
    </v-sheet>

    <v-container class="py-12">
      <!-- Categorías -->
      <div v-if="categories.length > 0" class="mb-12">
        <h2 class="text-h4 font-weight-bold mb-6">{{ $t('home.categories') }}</h2>
        <v-row>
          <v-col
            v-for="cat in categories"
            :key="cat.id"
            cols="6"
            sm="4"
            md="3"
            lg="2"
          >
            <v-card
              variant="tonal"
              hover
              :to="`/products?category=${cat.id}`"
              class="text-center pa-4"
            >
              <v-icon size="40" color="primary" class="mb-2"
                >mdi-shape-outline</v-icon
              >
              <v-card-title class="text-body-1">{{ cat.name }}</v-card-title>
            </v-card>
          </v-col>
        </v-row>
      </div>

      <!-- Productos destacados -->
      <div v-if="featuredProducts.length > 0">
        <h2 class="text-h4 font-weight-bold mb-6">{{ $t('home.featuredProducts') }}</h2>
        <v-row>
          <v-col
            v-for="product in featuredProducts"
            :key="product.id"
            cols="12"
            sm="6"
            md="4"
            lg="3"
          >
            <v-card hover to="/products" class="h-100 d-flex flex-column">
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
              <v-card-title class="text-body-1 font-weight-medium">{{
                product.name
              }}</v-card-title>
              <v-card-subtitle>{{ product.category?.name }}</v-card-subtitle>
              <v-spacer />
              <v-card-actions>
                <v-chip color="primary" variant="flat" size="small">
                  {{ product.price.toFixed(2) }} &euro;
                </v-chip>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </div>

      <!-- Cargando -->
      <div v-if="loading" class="text-center py-12">
        <v-progress-circular indeterminate color="primary" size="64" />
      </div>
    </v-container>
  </v-container>
</template>
