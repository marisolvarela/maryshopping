<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { productsService } from '~/services/productsService';
import { categoriesService } from '~/services/categoriesService';
import { providersService } from '~/services/providersService';
import type {
  Product,
  CreateProductRequest,
  UpdateProductRequest,
} from '~/models/products/Product';
import type { ProductImage } from '~/models/products/Image';
import { getImageUrl } from '~/models/products/Image';
import type { Category } from '~/models/products/Category';
import type { Provider } from '~/models/products/Provider';

definePageMeta({ middleware: ['auth', 'admin'] });

const { t } = useI18n();

const products = ref<Product[]>([]);
const categories = ref<Category[]>([]);
const providers = ref<Provider[]>([]);
const allImages = ref<ProductImage[]>([]);
const loading = ref(false);
const loadingAction = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

const dialog = ref(false);
const dialogDelete = ref(false);
const dialogImages = ref(false);
const editMode = ref(false);
const selectedProduct = ref<Product | null>(null);
const productImages = ref<ProductImage[]>([]);

// Carga de imágenes
const uploadingImage = ref(false);
const pendingImages = ref<File[]>([]);
const pendingImagePreviews = ref<string[]>([]);

// Estado de la galería de imágenes
const carouselModel = ref(0);
const lightboxOpen = ref(false);
const lightboxIndex = ref(0);

// Arrastrar y soltar
const draggedImageIndex = ref<number | null>(null);

// El formulario de edición guarda el id del producto que se está editando de manera separada
const editProductId = ref('');

const formData = ref<CreateProductRequest>({
  name: '',
  providerReference: '',
  categoryId: '',
  providerId: '',
  description: '',
  price: 0,
});

const headers = computed(() => [
  { title: t('admin.products.image'), key: 'image', sortable: false, width: '80px' },
  { title: t('common.name'), key: 'name', sortable: true },
  { title: t('admin.products.ref'), key: 'providerReference', sortable: true },
  { title: t('nav.categories'), key: 'category', sortable: false },
  { title: t('nav.providers'), key: 'provider', sortable: false },
  { title: t('common.price'), key: 'price', sortable: true },
  {
    title: t('common.actions'),
    key: 'actions',
    sortable: false,
    align: 'center' as const,
  },
]);

const canCreateProduct = computed(
  () => categories.value.length > 0 && providers.value.length > 0,
);

const currentCarouselImage = computed(() => productImages.value[carouselModel.value]);
const currentLightboxImage = computed(() => productImages.value[lightboxIndex.value]);

function getPrimaryImageUrl(product: Product): string | null {
  const img = allImages.value.find(
    i => i.productId === product.id && i.isPrimary,
  );
  if (img) return getImageUrl(img.id);
  const anyImg = allImages.value.find(i => i.productId === product.id);
  return anyImg ? getImageUrl(anyImg.id) : null;
}

onMounted(async () => {
  await Promise.all([loadCategories(), loadProviders()]);
  await loadProducts();
});

async function loadProducts() {
  loading.value = true;
  errorMessage.value = '';
  try {
    // No hay GET /catalog/products — cargar de todas las categorías
    const allProducts: Product[] = [];
    for (const cat of categories.value) {
      try {
        const catProducts = await productsService.getByCategory(cat.id);
        allProducts.push(...catProducts);
      } catch {
        // omitir
      }
    }
    products.value = allProducts;
    // Cargar metadatos de las imágenes
    try {
      allImages.value = await productsService.getAllImages();
    } catch {
      // las imágenes no son críticas
    }
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.products.failedToSave');
  } finally {
    loading.value = false;
  }
}

async function loadCategories() {
  try {
    categories.value = await categoriesService.getAll();
  } catch (e) {
    console.error('Failed to load categories:', e);
  }
}

async function loadProviders() {
  try {
    providers.value = await providersService.getAll();
  } catch (e) {
    console.error('Failed to load providers:', e);
  }
}

function openCreateDialog() {
  editMode.value = false;
  editProductId.value = '';
  formData.value = {
    name: '',
    providerReference: '',
    categoryId: categories.value[0]?.id || '',
    providerId: providers.value[0]?.id || '',
    description: '',
    price: 0,
  };
  pendingImages.value = [];
  pendingImagePreviews.value = [];
  dialog.value = true;
}

function openEditDialog(product: Product) {
  editMode.value = true;
  selectedProduct.value = product;
  editProductId.value = product.id;
  formData.value = {
    name: product.name,
    providerReference: product.providerReference,
    categoryId: product.category?.id || '',
    providerId: product.provider?.id || '',
    description: product.description,
    price: product.price,
  };
  pendingImages.value = [];
  pendingImagePreviews.value = [];
  // Cargar imágenes existentes para este producto
  productImages.value = allImages.value
    .filter(i => i.productId === product.id)
    .sort((a, b) => a.order - b.order);
  dialog.value = true;
}

function openDeleteDialog(product: Product) {
  selectedProduct.value = product;
  dialogDelete.value = true;
}

async function openImagesDialog(product: Product) {
  selectedProduct.value = product;
  carouselModel.value = 0;
  dialogImages.value = true;
  productImages.value = allImages.value
    .filter(i => i.productId === product.id)
    .sort((a, b) => a.order - b.order);
}

function onPendingImageSelected(files: File | File[]) {
  const fileList = Array.isArray(files) ? files : [files];
  for (const file of fileList) {
    if (!file) continue;
    pendingImages.value.push(file);
    const reader = new FileReader();
    reader.onload = e => {
      pendingImagePreviews.value.push(e.target?.result as string);
    };
    reader.readAsDataURL(file);
  }
}

function removePendingImage(index: number) {
  pendingImages.value.splice(index, 1);
  pendingImagePreviews.value.splice(index, 1);
}

async function saveProduct() {
  loadingAction.value = true;
  errorMessage.value = '';
  try {
    let productId: string;

    if (editMode.value) {
      const updateData: UpdateProductRequest = {
        name: formData.value.name,
        categoryId: formData.value.categoryId,
        description: formData.value.description,
        provider: formData.value.providerId,
        providerReference: formData.value.providerReference,
        price: formData.value.price,
      };
      await productsService.update(editProductId.value, updateData);
      productId = editProductId.value;
      successMessage.value = t('admin.products.updateSuccess');
    } else {
      const result = await productsService.create(formData.value);
      productId = result.productId;
      successMessage.value = t('admin.products.createSuccess');
    }

    // Subir las imágenes pendientes
    for (let i = 0; i < pendingImages.value.length; i++) {
      await productsService.uploadImage({
        imageFile: pendingImages.value[i],
        productId,
        order: i,
        primary: i === 0 && !editMode.value,
      });
    }

    dialog.value = false;
    await loadProducts();
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.products.failedToSave');
  } finally {
    loadingAction.value = false;
  }
}

async function deleteProduct() {
  if (!selectedProduct.value) return;
  loadingAction.value = true;
  errorMessage.value = '';
  try {
    await productsService.delete(selectedProduct.value.id);
    successMessage.value = t('admin.products.deleteSuccess');
    dialogDelete.value = false;
    await loadProducts();
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.products.failedToDelete');
  } finally {
    loadingAction.value = false;
  }
}

async function uploadImageToProduct(file: File) {
  if (!selectedProduct.value) return;
  uploadingImage.value = true;
  try {
    await productsService.uploadImage({
      imageFile: file,
      productId: selectedProduct.value.id,
      order: productImages.value.length,
      primary: productImages.value.length === 0,
    });
    allImages.value = await productsService.getAllImages();
    productImages.value = allImages.value
      .filter(i => i.productId === selectedProduct.value!.id)
      .sort((a, b) => a.order - b.order);
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.products.failedToUpload');
  } finally {
    uploadingImage.value = false;
  }
}

async function deleteImage(imageId: string) {
  if (!selectedProduct.value) return;
  try {
    await productsService.deleteImage(imageId);
    allImages.value = await productsService.getAllImages();
    productImages.value = allImages.value
      .filter(i => i.productId === selectedProduct.value!.id)
      .sort((a, b) => a.order - b.order);
    if (carouselModel.value >= productImages.value.length) {
      carouselModel.value = Math.max(0, productImages.value.length - 1);
    }
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.products.failedToDeleteImage');
  }
}

async function setImageAsPrimary(imageId: string) {
  if (!selectedProduct.value) return;
  const image = productImages.value.find(img => img.id === imageId);
  if (!image || image.isPrimary) return;

  // El backend NO desmarca automáticamente isPrimary en otras imágenes,
  // así que debemos desmarcar la primaria actual primero y luego establecer la nueva.
  const currentPrimary = productImages.value.find(img => img.isPrimary);

  try {
    const updates: Promise<void>[] = [];

    // Desmarcar la primaria actual
    if (currentPrimary) {
      updates.push(
        productsService.updateImage(currentPrimary.id, {
          productId: selectedProduct.value.id,
          primary: false,
          order: currentPrimary.order,
        }),
      );
    }

    // Establecer la nueva primaria
    updates.push(
      productsService.updateImage(imageId, {
        productId: selectedProduct.value.id,
        primary: true,
        order: image.order,
      }),
    );

    await Promise.all(updates);

    allImages.value = await productsService.getAllImages();
    productImages.value = allImages.value
      .filter(i => i.productId === selectedProduct.value!.id)
      .sort((a, b) => a.order - b.order);
    successMessage.value = t('admin.products.primarySuccess');
    setTimeout(() => { successMessage.value = ''; }, 3000);
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.products.failedToSetPrimary');
  }
}

async function moveImageOrder(imageId: string, direction: 'up' | 'down') {
  if (!selectedProduct.value) return;
  const currentIndex = productImages.value.findIndex(img => img.id === imageId);
  if (currentIndex === -1) return;
  const newIndex = direction === 'up' ? currentIndex - 1 : currentIndex + 1;
  if (newIndex < 0 || newIndex >= productImages.value.length) return;

  const currentImage = productImages.value[currentIndex];
  const swapImage = productImages.value[newIndex];
  if (!currentImage || !swapImage) return;

  try {
    await Promise.all([
      productsService.updateImage(currentImage.id, {
        productId: selectedProduct.value.id,
        primary: currentImage.isPrimary,
        order: swapImage.order,
      }),
      productsService.updateImage(swapImage.id, {
        productId: selectedProduct.value.id,
        primary: swapImage.isPrimary,
        order: currentImage.order,
      }),
    ]);
    allImages.value = await productsService.getAllImages();
    productImages.value = allImages.value
      .filter(i => i.productId === selectedProduct.value!.id)
      .sort((a, b) => a.order - b.order);
    carouselModel.value = newIndex;
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.products.failedToReorder');
  }
}

function onDragStart(index: number) {
  draggedImageIndex.value = index;
}

function onDragOver(event: DragEvent) {
  event.preventDefault();
}

async function onDrop(targetIndex: number) {
  if (draggedImageIndex.value === null || draggedImageIndex.value === targetIndex) {
    draggedImageIndex.value = null;
    return;
  }
  if (!selectedProduct.value) return;

  const sourceIndex = draggedImageIndex.value;
  draggedImageIndex.value = null;

  const images = [...productImages.value];
  const movedImage = images[sourceIndex];
  if (!movedImage) return;
  images.splice(sourceIndex, 1);
  images.splice(targetIndex, 0, movedImage);

  try {
    await Promise.all(
      images.map((image, index) =>
        productsService.updateImage(image.id, {
          productId: selectedProduct.value!.id,
          primary: image.isPrimary,
          order: index,
        }),
      ),
    );
    allImages.value = await productsService.getAllImages();
    productImages.value = allImages.value
      .filter(i => i.productId === selectedProduct.value!.id)
      .sort((a, b) => a.order - b.order);
    carouselModel.value = targetIndex;
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.products.failedToReorder');
  }
}

function openLightbox(index: number) {
  lightboxIndex.value = index;
  lightboxOpen.value = true;
}

function nextLightboxImage() {
  if (lightboxIndex.value < productImages.value.length - 1) {
    lightboxIndex.value++;
  } else {
    lightboxIndex.value = 0;
  }
}

function prevLightboxImage() {
  if (lightboxIndex.value > 0) {
    lightboxIndex.value--;
  } else {
    lightboxIndex.value = productImages.value.length - 1;
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
  <v-container>
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-package-variant</v-icon>
        <span class="text-h5">{{ $t('admin.products.title') }}</span>
        <v-spacer />
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          class="text-none"
          :disabled="!canCreateProduct"
          @click="openCreateDialog"
        >
          {{ $t('admin.products.newProduct') }}
        </v-btn>
      </v-card-title>

      <v-alert
        v-if="!canCreateProduct && !loading"
        type="warning"
        variant="tonal"
        class="mx-4 mb-0"
      >
        {{ $t('admin.products.needCategoryProvider') }}
      </v-alert>
      <v-alert
        v-if="errorMessage"
        type="error"
        variant="tonal"
        closable
        class="mx-4 mb-0"
        @click:close="errorMessage = ''"
      >
        {{ errorMessage }}
      </v-alert>
      <v-alert
        v-if="successMessage"
        type="success"
        variant="tonal"
        class="mx-4 mb-0"
      >
        {{ successMessage }}
      </v-alert>

      <v-data-table
        :items="products"
        :headers="headers"
        :loading="loading"
        :items-per-page="10"
        :items-per-page-text="$t('admin.products.perPage')"
        item-value="id"
        class="elevation-0"
      >
        <template #item.image="{ item }">
          <v-avatar
            rounded="lg"
            size="50"
            class="my-1"
            @click="openImagesDialog(item)"
          >
            <v-img
              v-if="getPrimaryImageUrl(item)"
              :src="getPrimaryImageUrl(item)!"
              cover
            />
            <v-icon v-else color="grey">mdi-image-off-outline</v-icon>
          </v-avatar>
        </template>

        <template #item.category="{ item }">
          {{ item.category?.name || '-' }}
        </template>

        <template #item.provider="{ item }">
          {{ item.provider?.name || '-' }}
        </template>

        <template #item.price="{ item }">
          {{ formatPrice(item.price) }}
        </template>

        <template #item.actions="{ item }">
          <v-btn
            icon
            size="small"
            variant="text"
            color="info"
            @click="openImagesDialog(item)"
          >
            <v-icon>mdi-image-multiple</v-icon>
          </v-btn>
          <v-btn
            icon
            size="small"
            variant="text"
            color="primary"
            @click="openEditDialog(item)"
          >
            <v-icon>mdi-pencil</v-icon>
          </v-btn>
          <v-btn
            icon
            size="small"
            variant="text"
            color="error"
            @click="openDeleteDialog(item)"
          >
            <v-icon>mdi-delete</v-icon>
          </v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- Diálogo Crear/Editar Producto -->
    <v-dialog v-model="dialog" max-width="700">
      <v-card class="pa-6">
        <v-card-title class="pa-0 mb-4">
          {{ editMode ? $t('admin.products.editProduct') : $t('admin.products.newProduct') }}
        </v-card-title>
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.name"
              :label="$t('admin.products.productName')"
              variant="outlined"
              autofocus
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.providerReference"
              :label="$t('admin.products.providerReference')"
              variant="outlined"
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-select
              v-model="formData.categoryId"
              :items="categories"
              item-title="name"
              item-value="id"
              :label="$t('nav.categories')"
              variant="outlined"
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-select
              v-model="formData.providerId"
              :items="providers"
              item-title="name"
              item-value="id"
              :label="$t('nav.providers')"
              variant="outlined"
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model.number="formData.price"
              :label="$t('common.price')"
              type="number"
              prefix="€"
              variant="outlined"
            />
          </v-col>
          <v-col cols="12">
            <v-textarea
              v-model="formData.description"
              :label="$t('admin.products.description')"
              variant="outlined"
              rows="3"
            />
          </v-col>
          <v-col cols="12">
            <!-- Imágenes existentes (modo edición) -->
            <div v-if="editMode && productImages.length > 0" class="mb-3">
              <div class="text-subtitle-2 mb-2">{{ $t('admin.products.existingImages') }}</div>
              <div class="d-flex ga-2 flex-wrap">
                <div
                  v-for="img in productImages"
                  :key="img.id"
                  class="position-relative"
                >
                  <v-img
                    :src="getImageUrl(img.id)"
                    width="80"
                    height="80"
                    cover
                    rounded="lg"
                  />
                  <v-chip
                    v-if="img.isPrimary"
                    color="primary"
                    size="x-small"
                    class="position-absolute"
                    style="bottom: 2px; left: 2px"
                  >
                    <v-icon size="10">mdi-star</v-icon>
                  </v-chip>
                </div>
              </div>
            </div>
            <v-file-input
              :label="$t('admin.products.addImages')"
              variant="outlined"
              accept="image/*"
              multiple
              prepend-icon="mdi-camera"
              @update:model-value="onPendingImageSelected"
            />
            <div
              v-if="pendingImagePreviews.length > 0"
              class="d-flex ga-2 flex-wrap mt-2"
            >
              <div
                v-for="(preview, i) in pendingImagePreviews"
                :key="i"
                class="position-relative"
              >
                <v-img
                  :src="preview"
                  width="80"
                  height="80"
                  cover
                  rounded="lg"
                />
                <v-btn
                  icon
                  size="x-small"
                  color="error"
                  class="position-absolute"
                  style="top: -8px; right: -8px"
                  @click="removePendingImage(i)"
                >
                  <v-icon size="14">mdi-close</v-icon>
                </v-btn>
              </div>
            </div>
          </v-col>
        </v-row>
        <v-card-actions class="pa-0 mt-4">
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">{{ $t('common.cancel') }}</v-btn>
          <v-btn
            color="primary"
            :loading="loadingAction"
            :disabled="
              !formData.name.trim() ||
              !formData.categoryId ||
              !formData.providerId
            "
            @click="saveProduct"
          >
            {{ editMode ? $t('common.update') : $t('common.create') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Confirmación de borrado -->
    <v-dialog v-model="dialogDelete" max-width="400">
      <v-card class="pa-6">
        <v-card-title class="pa-0 mb-2">{{ $t('admin.products.deleteProduct') }}</v-card-title>
        <v-card-text class="pa-0 mb-4">
          {{ $t('admin.products.confirmDelete', { name: selectedProduct?.name }) }}
        </v-card-text>
        <v-card-actions class="pa-0">
          <v-spacer />
          <v-btn variant="text" @click="dialogDelete = false">{{ $t('common.cancel') }}</v-btn>
          <v-btn color="error" :loading="loadingAction" @click="deleteProduct"
            >{{ $t('common.delete') }}</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Diálogo de gestión de imágenes -->
    <v-dialog v-model="dialogImages" max-width="900" scrollable>
      <v-card>
        <v-card-title class="d-flex align-center pa-4">
          <v-icon class="mr-2" color="primary">mdi-image-multiple</v-icon>
          <div>
            <span class="text-h6">{{ selectedProduct?.name }}</span>
            <div class="text-caption text-medium-emphasis">
              {{ productImages.length }} {{ productImages.length === 1 ? 'imagen' : 'imágenes' }}
            </div>
          </div>
          <v-spacer />
          <v-btn icon variant="text" @click="dialogImages = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>

        <v-divider />

        <v-card-text class="pa-4">
          <!-- Sección de carga -->
          <v-card variant="outlined" class="mb-4 pa-3">
            <div class="d-flex align-center ga-3 flex-wrap">
              <v-icon color="primary">mdi-cloud-upload</v-icon>
              <span class="text-body-2">{{ $t('admin.products.uploadImage') }}</span>
              <v-spacer />
              <v-file-input
                :label="$t('admin.products.uploadImage')"
                variant="outlined"
                accept="image/*"
                prepend-icon=""
                prepend-inner-icon="mdi-camera"
                hide-details
                density="compact"
                style="max-width: 250px"
                :loading="uploadingImage"
                @update:model-value="(f: File) => f && uploadImageToProduct(f)"
              />
            </div>
          </v-card>

          <!-- Carrusel y galería de imágenes -->
          <template v-if="productImages.length > 0">
            <!-- Carrusel principal -->
            <v-card variant="outlined" class="mb-4 overflow-hidden">
              <v-carousel
                v-model="carouselModel"
                height="350"
                hide-delimiter-background
                show-arrows="hover"
                :continuous="false"
              >
                <v-carousel-item
                  v-for="(image, index) in productImages"
                  :key="image.id"
                >
                  <v-img
                    :src="getImageUrl(image.id)"
                    height="350"
                    contain
                    class="cursor-pointer"
                    style="background: #f5f5f5"
                    @click="openLightbox(index)"
                  >
                    <!-- Superposición de imagen con información -->
                    <div
                      class="position-absolute"
                      style="bottom: 0; left: 0; right: 0; background: linear-gradient(transparent, rgba(0,0,0,0.7)); padding: 12px"
                    >
                      <div class="d-flex align-center">
                        <v-chip v-if="image.isPrimary" size="small" color="primary" class="mr-2">
                          <v-icon start size="small">mdi-star</v-icon>
                          {{ $t('admin.products.primary') }}
                        </v-chip>
                        <v-spacer />
                        <v-btn
                          icon
                          size="small"
                          variant="text"
                          color="white"
                          @click.stop="openLightbox(index)"
                        >
                          <v-icon>mdi-fullscreen</v-icon>
                        </v-btn>
                      </div>
                    </div>
                  </v-img>
                </v-carousel-item>
              </v-carousel>
            </v-card>

            <!-- Tira de miniaturas con arrastrar y soltar -->
            <div class="mb-3">
              <div class="text-caption text-medium-emphasis mb-1">
                <v-icon size="12">mdi-drag</v-icon>
                {{ $t('admin.products.dragToReorder') }}
              </div>
              <div class="d-flex ga-2 overflow-x-auto pb-2">
                <v-card
                  v-for="(image, index) in productImages"
                  :key="image.id"
                  :variant="carouselModel === index ? 'elevated' : 'outlined'"
                  :class="{ 'border-primary': carouselModel === index }"
                  class="flex-shrink-0 cursor-grab"
                  :elevation="carouselModel === index ? 4 : 0"
                  width="80"
                  draggable="true"
                  @click="carouselModel = index"
                  @dragstart="onDragStart(index)"
                  @dragover="onDragOver"
                  @drop="onDrop(index)"
                >
                  <div class="position-relative">
                    <v-img
                      :src="getImageUrl(image.id)"
                      height="60"
                      cover
                    >
                      <v-icon
                        v-if="image.isPrimary"
                        icon="mdi-star"
                        color="warning"
                        size="16"
                        class="position-absolute"
                        style="top: 4px; right: 4px"
                      />
                    </v-img>
                    <div
                      class="position-absolute text-caption text-white px-1 rounded"
                      style="bottom: 2px; left: 2px; background: rgba(0,0,0,0.6); font-size: 10px"
                    >
                      {{ index + 1 }}
                    </div>
                  </div>
                </v-card>
              </div>
            </div>

            <!-- Acciones de imagen -->
            <v-card variant="tonal" class="pa-3">
              <div class="text-subtitle-2 mb-2">{{ $t('common.actions') }}</div>
              <div class="d-flex ga-2 flex-wrap">
                <v-btn
                  v-if="currentCarouselImage && carouselModel > 0"
                  variant="outlined"
                  color="secondary"
                  size="small"
                  prepend-icon="mdi-arrow-left"
                  @click="moveImageOrder(currentCarouselImage.id, 'up')"
                >
                  {{ $t('admin.products.moveLeft') }}
                </v-btn>
                <v-btn
                  v-if="currentCarouselImage && carouselModel < productImages.length - 1"
                  variant="outlined"
                  color="secondary"
                  size="small"
                  append-icon="mdi-arrow-right"
                  @click="moveImageOrder(currentCarouselImage.id, 'down')"
                >
                  {{ $t('admin.products.moveRight') }}
                </v-btn>
                <v-divider v-if="productImages.length > 1" vertical class="mx-1" />
                <v-btn
                  v-if="currentCarouselImage && !currentCarouselImage.isPrimary"
                  variant="outlined"
                  color="warning"
                  size="small"
                  prepend-icon="mdi-star"
                  @click="setImageAsPrimary(currentCarouselImage.id)"
                >
                  {{ $t('admin.products.setAsPrimary') }}
                </v-btn>
                <v-btn
                  v-if="currentCarouselImage"
                  variant="outlined"
                  color="error"
                  size="small"
                  prepend-icon="mdi-delete"
                  @click="deleteImage(currentCarouselImage.id)"
                >
                  {{ $t('common.delete') }}
                </v-btn>
              </div>
            </v-card>
          </template>

          <!-- Estado vacío -->
          <div v-else class="text-center py-8">
            <v-icon size="64" color="grey">mdi-image-plus</v-icon>
            <p class="text-body-2 text-medium-emphasis mt-3">
              {{ $t('admin.products.noImages') }}
            </p>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- Superposición lightbox -->
    <v-overlay
      v-model="lightboxOpen"
      class="align-center justify-center"
      scrim="black"
      :opacity="0.95"
    >
      <div class="d-flex align-center justify-center" style="width: 100vw; height: 100vh">
        <v-btn
          icon
          variant="text"
          color="white"
          size="large"
          class="position-absolute"
          style="top: 20px; right: 20px; z-index: 10"
          @click="lightboxOpen = false"
        >
          <v-icon>mdi-close</v-icon>
        </v-btn>

        <div
          class="position-absolute text-white text-body-1"
          style="top: 24px; left: 50%; transform: translateX(-50%)"
        >
          {{ lightboxIndex + 1 }} / {{ productImages.length }}
        </div>

        <v-btn
          v-if="productImages.length > 1"
          icon
          variant="text"
          color="white"
          size="x-large"
          class="position-absolute"
          style="left: 20px"
          @click="prevLightboxImage"
        >
          <v-icon>mdi-chevron-left</v-icon>
        </v-btn>

        <v-img
          v-if="currentLightboxImage"
          :src="getImageUrl(currentLightboxImage.id)"
          max-height="85vh"
          max-width="85vw"
          contain
        />

        <v-btn
          v-if="productImages.length > 1"
          icon
          variant="text"
          color="white"
          size="x-large"
          class="position-absolute"
          style="right: 20px"
          @click="nextLightboxImage"
        >
          <v-icon>mdi-chevron-right</v-icon>
        </v-btn>

        <div
          v-if="currentLightboxImage?.isPrimary"
          class="position-absolute text-white text-center"
          style="bottom: 30px; left: 50%; transform: translateX(-50%)"
        >
          <v-chip color="primary" size="small">
            <v-icon start size="small">mdi-star</v-icon>
            {{ $t('admin.products.primary') }}
          </v-chip>
        </div>
      </div>
    </v-overlay>
  </v-container>
</template>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
.cursor-grab {
  cursor: grab;
}
.cursor-grab:active {
  cursor: grabbing;
}
.border-primary {
  border-color: rgb(var(--v-theme-primary)) !important;
  border-width: 2px !important;
}
</style>
