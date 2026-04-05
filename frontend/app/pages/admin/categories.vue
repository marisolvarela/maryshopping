<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { categoriesService } from '~/services/categoriesService';
import type {
  Category,
  CreateCategoryRequest,
} from '~/models/products/Category';

definePageMeta({ middleware: ['auth', 'admin'] });

const { t } = useI18n();

const categories = ref<Category[]>([]);
const loading = ref(false);
const loadingAction = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

const dialog = ref(false);
const dialogDelete = ref(false);
const editMode = ref(false);
const selectedCategory = ref<Category | null>(null);

const formData = ref<CreateCategoryRequest>({
  name: '',
});

const headers = computed(() => [
  { title: t('common.name'), key: 'name', sortable: true },
  {
    title: t('common.actions'),
    key: 'actions',
    sortable: false,
    align: 'center' as const,
  },
]);

onMounted(async () => {
  await loadCategories();
});

async function loadCategories() {
  loading.value = true;
  errorMessage.value = '';
  try {
    categories.value = await categoriesService.getAll();
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.categories.failedToLoad');
  } finally {
    loading.value = false;
  }
}

function openCreateDialog() {
  editMode.value = false;
  formData.value = { name: '' };
  dialog.value = true;
}

function openEditDialog(category: Category) {
  editMode.value = true;
  selectedCategory.value = category;
  formData.value = { name: category.name };
  dialog.value = true;
}

function openDeleteDialog(category: Category) {
  selectedCategory.value = category;
  dialogDelete.value = true;
}

async function saveCategory() {
  loadingAction.value = true;
  errorMessage.value = '';
  try {
    if (editMode.value && selectedCategory.value) {
      await categoriesService.update(selectedCategory.value.id, formData.value);
      successMessage.value = t('admin.categories.updateSuccess');
    } else {
      await categoriesService.create(formData.value);
      successMessage.value = t('admin.categories.createSuccess');
    }
    dialog.value = false;
    await loadCategories();
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.categories.failedToSave');
  } finally {
    loadingAction.value = false;
  }
}

async function deleteCategory() {
  if (!selectedCategory.value) return;
  loadingAction.value = true;
  errorMessage.value = '';
  try {
    await categoriesService.delete(selectedCategory.value.id);
    successMessage.value = t('admin.categories.deleteSuccess');
    dialogDelete.value = false;
    await loadCategories();
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.categories.failedToDelete');
  } finally {
    loadingAction.value = false;
  }
}
</script>

<template>
  <v-container>
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-shape-outline</v-icon>
        <span class="text-h5">{{ $t('admin.categories.title') }}</span>
        <v-spacer />
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          class="text-none"
          @click="openCreateDialog"
        >
          {{ $t('admin.categories.newCategory') }}
        </v-btn>
      </v-card-title>

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
        :items="categories"
        :headers="headers"
        :loading="loading"
        :items-per-page="10"
        :items-per-page-text="$t('admin.categories.perPage')"
        item-value="id"
        class="elevation-0"
      >
        <template #item.actions="{ item }">
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

    <!-- Diálogo Crear/Editar -->
    <v-dialog v-model="dialog" max-width="500">
      <v-card class="pa-6">
        <v-card-title class="pa-0 mb-4">
          {{ editMode ? $t('admin.categories.editCategory') : $t('admin.categories.newCategory') }}
        </v-card-title>
        <v-text-field
          v-model="formData.name"
          :label="$t('admin.categories.categoryName')"
          variant="outlined"
          autofocus
        />
        <v-card-actions class="pa-0 mt-2">
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">{{ $t('common.cancel') }}</v-btn>
          <v-btn
            color="primary"
            :loading="loadingAction"
            :disabled="!formData.name.trim()"
            @click="saveCategory"
          >
            {{ editMode ? $t('common.update') : $t('common.create') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Confirmación de borrado -->
    <v-dialog v-model="dialogDelete" max-width="400">
      <v-card class="pa-6">
        <v-card-title class="pa-0 mb-2">{{ $t('admin.categories.deleteCategory') }}</v-card-title>
        <v-card-text class="pa-0 mb-4">
          {{ $t('admin.categories.confirmDelete', { name: selectedCategory?.name }) }}
        </v-card-text>
        <v-card-actions class="pa-0">
          <v-spacer />
          <v-btn variant="text" @click="dialogDelete = false">{{ $t('common.cancel') }}</v-btn>
          <v-btn color="error" :loading="loadingAction" @click="deleteCategory"
            >{{ $t('common.delete') }}</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
