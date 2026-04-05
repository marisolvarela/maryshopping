<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { providersService } from '~/services/providersService';
import type {
  Provider,
  CreateProviderRequest,
} from '~/models/products/Provider';

definePageMeta({ middleware: ['auth', 'admin'] });

const { t } = useI18n();

const providers = ref<Provider[]>([]);
const loading = ref(false);
const loadingAction = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

const dialog = ref(false);
const dialogDelete = ref(false);
const editMode = ref(false);
const selectedProvider = ref<Provider | null>(null);

const formData = ref<CreateProviderRequest>({
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
  await loadProviders();
});

async function loadProviders() {
  loading.value = true;
  errorMessage.value = '';
  try {
    providers.value = await providersService.getAll();
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.providers.failedToLoad');
  } finally {
    loading.value = false;
  }
}

function openCreateDialog() {
  editMode.value = false;
  formData.value = { name: '' };
  dialog.value = true;
}

function openEditDialog(provider: Provider) {
  editMode.value = true;
  selectedProvider.value = provider;
  formData.value = { name: provider.name };
  dialog.value = true;
}

function openDeleteDialog(provider: Provider) {
  selectedProvider.value = provider;
  dialogDelete.value = true;
}

async function saveProvider() {
  loadingAction.value = true;
  errorMessage.value = '';
  try {
    if (editMode.value && selectedProvider.value) {
      await providersService.update(selectedProvider.value.id, formData.value);
      successMessage.value = t('admin.providers.updateSuccess');
    } else {
      await providersService.create(formData.value);
      successMessage.value = t('admin.providers.createSuccess');
    }
    dialog.value = false;
    await loadProviders();
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.providers.failedToSave');
  } finally {
    loadingAction.value = false;
  }
}

async function deleteProvider() {
  if (!selectedProvider.value) return;
  loadingAction.value = true;
  errorMessage.value = '';
  try {
    await providersService.delete(selectedProvider.value.id);
    successMessage.value = t('admin.providers.deleteSuccess');
    dialogDelete.value = false;
    await loadProviders();
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.providers.failedToDelete');
  } finally {
    loadingAction.value = false;
  }
}
</script>

<template>
  <v-container>
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-truck-delivery-outline</v-icon>
        <span class="text-h5">{{ $t('admin.providers.title') }}</span>
        <v-spacer />
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          class="text-none"
          @click="openCreateDialog"
        >
          {{ $t('admin.providers.newProvider') }}
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
        :items="providers"
        :headers="headers"
        :loading="loading"
        :items-per-page="10"
        :items-per-page-text="$t('admin.providers.perPage')"
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
          {{ editMode ? $t('admin.providers.editProvider') : $t('admin.providers.newProvider') }}
        </v-card-title>
        <v-text-field
          v-model="formData.name"
          :label="$t('admin.providers.providerName')"
          variant="outlined"
          class="mb-2"
          autofocus
        />
        <v-card-actions class="pa-0 mt-2">
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">{{ $t('common.cancel') }}</v-btn>
          <v-btn
            color="primary"
            :loading="loadingAction"
            :disabled="!formData.name.trim()"
            @click="saveProvider"
          >
            {{ editMode ? $t('common.update') : $t('common.create') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Confirmación de borrado -->
    <v-dialog v-model="dialogDelete" max-width="400">
      <v-card class="pa-6">
        <v-card-title class="pa-0 mb-2">{{ $t('admin.providers.deleteProvider') }}</v-card-title>
        <v-card-text class="pa-0 mb-4">
          {{ $t('admin.providers.confirmDelete', { name: selectedProvider?.name }) }}
        </v-card-text>
        <v-card-actions class="pa-0">
          <v-spacer />
          <v-btn variant="text" @click="dialogDelete = false">{{ $t('common.cancel') }}</v-btn>
          <v-btn color="error" :loading="loadingAction" @click="deleteProvider"
            >{{ $t('common.delete') }}</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
