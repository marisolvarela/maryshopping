<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { customersService } from '~/services/customersService';
import type { UpdateCustomerRequest } from '~/services/customersService';
import type { Customer, Address } from '~/models/customers/Customer';

definePageMeta({ middleware: ['auth', 'admin'] });

const { t } = useI18n();

const customers = ref<Customer[]>([]);
const loading = ref(false);
const loadingAction = ref(false);
const errorMessage = ref('');
const successMessage = ref('');
const searchQuery = ref('');

const detailDialog = ref(false);
const editDialog = ref(false);
const deleteDialog = ref(false);
const selectedCustomer = ref<Customer | null>(null);

const emptyAddress: Address = {
  street: '',
  city: '',
  province: '',
  postalCode: '',
  country: '',
};

const formData = ref<UpdateCustomerRequest & { id: string }>({
  id: '',
  firstName: '',
  lastName: '',
  dni: '',
  emailAddress: '',
  phoneNumber: '',
  shippingAddress: { ...emptyAddress },
  billingAddress: { ...emptyAddress },
});

const headers = computed(() => [
  { title: t('common.name'), key: 'fullName', sortable: true },
  { title: t('common.email'), key: 'emailAddress', sortable: true },
  { title: t('common.dni'), key: 'dni', sortable: true },
  { title: t('common.phone'), key: 'phoneNumber', sortable: false },
  { title: t('common.city'), key: 'city', sortable: true },
  {
    title: t('common.actions'),
    key: 'actions',
    sortable: false,
    align: 'center' as const,
  },
]);

const filteredCustomers = computed(() => {
  if (!searchQuery.value) return customers.value;
  const q = searchQuery.value.toLowerCase();
  return customers.value.filter(
    c =>
      `${c.firstName} ${c.lastName}`.toLowerCase().includes(q) ||
      c.emailAddress?.toLowerCase().includes(q) ||
      c.dni?.toLowerCase().includes(q),
  );
});

onMounted(async () => {
  await loadCustomers();
});

async function loadCustomers() {
  loading.value = true;
  errorMessage.value = '';
  try {
    customers.value = await customersService.getAll();
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.customers.failedToLoad');
  } finally {
    loading.value = false;
  }
}

function viewCustomerDetails(customer: Customer) {
  selectedCustomer.value = customer;
  detailDialog.value = true;
}

function openEditDialog(customer: Customer) {
  selectedCustomer.value = customer;
  formData.value = {
    id: customer.id,
    firstName: customer.firstName,
    lastName: customer.lastName,
    dni: customer.dni,
    emailAddress: customer.emailAddress,
    phoneNumber: customer.phoneNumber,
    shippingAddress: customer.shippingAddress
      ? { ...customer.shippingAddress }
      : { ...emptyAddress },
    billingAddress: customer.billingAddress
      ? { ...customer.billingAddress }
      : { ...emptyAddress },
  };
  editDialog.value = true;
}

function openDeleteDialog(customer: Customer) {
  selectedCustomer.value = customer;
  deleteDialog.value = true;
}

async function updateCustomer() {
  loadingAction.value = true;
  errorMessage.value = '';
  try {
    const { id, ...updateData } = formData.value;
    await customersService.update(id, updateData);
    successMessage.value = t('admin.customers.updateSuccess');
    editDialog.value = false;
    await loadCustomers();
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.customers.failedToUpdate');
  } finally {
    loadingAction.value = false;
  }
}

async function deleteCustomer() {
  if (!selectedCustomer.value) return;
  loadingAction.value = true;
  errorMessage.value = '';
  try {
    await customersService.delete(selectedCustomer.value.id);
    successMessage.value = t('admin.customers.deleteSuccess');
    deleteDialog.value = false;
    await loadCustomers();
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('admin.customers.failedToDelete');
  } finally {
    loadingAction.value = false;
  }
}
</script>

<template>
  <v-container>
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-account-group</v-icon>
        <span class="text-h5">{{ $t('admin.customers.title') }}</span>
        <v-spacer />
        <v-btn
          color="primary"
          prepend-icon="mdi-refresh"
          variant="outlined"
          @click="loadCustomers"
        >
          {{ $t('common.refresh') }}
        </v-btn>
      </v-card-title>

      <v-card-text>
        <v-row>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="searchQuery"
              :label="$t('admin.customers.searchPlaceholder')"
              prepend-inner-icon="mdi-magnify"
              variant="outlined"
              density="compact"
              clearable
              hide-details
            />
          </v-col>
        </v-row>
      </v-card-text>

      <v-alert
        v-if="errorMessage"
        type="error"
        variant="tonal"
        closable
        class="mx-4 mb-2"
        @click:close="errorMessage = ''"
      >
        {{ errorMessage }}
      </v-alert>
      <v-alert
        v-if="successMessage"
        type="success"
        variant="tonal"
        class="mx-4 mb-2"
      >
        {{ successMessage }}
      </v-alert>

      <v-data-table
        :items="filteredCustomers"
        :headers="headers"
        :loading="loading"
        :items-per-page="10"
        :items-per-page-text="$t('admin.customers.perPage')"
        item-value="id"
        class="elevation-0"
      >
        <template #item.fullName="{ item }">
          {{ item.firstName }} {{ item.lastName }}
        </template>
        <template #item.city="{ item }">
          {{ item.shippingAddress?.city || '-' }}
        </template>
        <template #item.actions="{ item }">
          <v-btn
            icon
            size="small"
            variant="text"
            color="info"
            @click="viewCustomerDetails(item)"
          >
            <v-icon>mdi-eye</v-icon>
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

    <!-- Diálogo de detalle del cliente -->
    <v-dialog v-model="detailDialog" max-width="600">
      <v-card v-if="selectedCustomer" class="pa-6">
        <v-card-title class="pa-0 d-flex align-center mb-4">
          <span class="text-h6"
            >{{ selectedCustomer.firstName }}
            {{ selectedCustomer.lastName }}</span
          >
          <v-spacer />
          <v-btn icon variant="text" @click="detailDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>

        <v-row>
          <v-col cols="12" md="6">
            <div class="text-caption text-medium-emphasis">{{ $t('common.email') }}</div>
            <div class="text-body-1">{{ selectedCustomer.emailAddress }}</div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="text-caption text-medium-emphasis">{{ $t('common.phone') }}</div>
            <div class="text-body-1">
              {{ selectedCustomer.phoneNumber || '-' }}
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="text-caption text-medium-emphasis">{{ $t('common.dni') }}</div>
            <div class="text-body-1">
              {{ selectedCustomer.dni || '-' }}
            </div>
          </v-col>
        </v-row>

        <v-divider class="my-4" />

        <v-row>
          <v-col cols="12" md="6">
            <div class="d-flex align-center mb-2">
              <v-icon size="18" color="primary" class="mr-2"
                >mdi-truck-delivery</v-icon
              >
              <span class="text-subtitle-2 font-weight-bold"
                >{{ $t('common.shippingAddress') }}</span
              >
            </div>
            <div class="text-body-2 pl-6">
              {{ selectedCustomer.shippingAddress?.street || '-' }}<br />
              {{ selectedCustomer.shippingAddress?.city }},
              {{ selectedCustomer.shippingAddress?.province }}
              {{ selectedCustomer.shippingAddress?.postalCode }}<br />
              {{ selectedCustomer.shippingAddress?.country }}
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="d-flex align-center mb-2">
              <v-icon size="18" color="primary" class="mr-2"
                >mdi-credit-card-outline</v-icon
              >
              <span class="text-subtitle-2 font-weight-bold"
                >{{ $t('common.billingAddress') }}</span
              >
            </div>
            <div class="text-body-2 pl-6">
              {{ selectedCustomer.billingAddress?.street || '-' }}<br />
              {{ selectedCustomer.billingAddress?.city }},
              {{ selectedCustomer.billingAddress?.province }}
              {{ selectedCustomer.billingAddress?.postalCode }}<br />
              {{ selectedCustomer.billingAddress?.country }}
            </div>
          </v-col>
        </v-row>
      </v-card>
    </v-dialog>

    <!-- Diálogo de edición -->
    <v-dialog v-model="editDialog" max-width="700">
      <v-card class="pa-6">
        <v-card-title class="pa-0 mb-4">{{ $t('admin.customers.editTitle') }}</v-card-title>
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.firstName"
              :label="$t('common.firstName')"
              variant="outlined"
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.lastName"
              :label="$t('common.lastName')"
              variant="outlined"
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.emailAddress"
              :label="$t('common.email')"
              variant="outlined"
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.phoneNumber"
              :label="$t('common.phone')"
              variant="outlined"
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="formData.dni"
              :label="$t('common.dni')"
              variant="outlined"
            />
          </v-col>
        </v-row>

        <v-divider class="my-4" />
        <div class="text-subtitle-2 font-weight-bold mb-3">
          {{ $t('common.shippingAddress') }}
        </div>
        <v-row>
          <v-col cols="12">
            <v-text-field
              v-model="formData.shippingAddress.street"
              :label="$t('common.street')"
              variant="outlined"
              density="compact"
            />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field
              v-model="formData.shippingAddress.city"
              :label="$t('common.city')"
              variant="outlined"
              density="compact"
            />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field
              v-model="formData.shippingAddress.province"
              :label="$t('common.province')"
              variant="outlined"
              density="compact"
            />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field
              v-model="formData.shippingAddress.postalCode"
              :label="$t('common.postalCode')"
              variant="outlined"
              density="compact"
            />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field
              v-model="formData.shippingAddress.country"
              :label="$t('common.country')"
              variant="outlined"
              density="compact"
            />
          </v-col>
        </v-row>

        <v-divider class="my-4" />
        <div class="text-subtitle-2 font-weight-bold mb-3">{{ $t('common.billingAddress') }}</div>
        <v-row>
          <v-col cols="12">
            <v-text-field
              v-model="formData.billingAddress.street"
              :label="$t('common.street')"
              variant="outlined"
              density="compact"
            />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field
              v-model="formData.billingAddress.city"
              :label="$t('common.city')"
              variant="outlined"
              density="compact"
            />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field
              v-model="formData.billingAddress.province"
              :label="$t('common.province')"
              variant="outlined"
              density="compact"
            />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field
              v-model="formData.billingAddress.postalCode"
              :label="$t('common.postalCode')"
              variant="outlined"
              density="compact"
            />
          </v-col>
          <v-col cols="6" md="3">
            <v-text-field
              v-model="formData.billingAddress.country"
              :label="$t('common.country')"
              variant="outlined"
              density="compact"
            />
          </v-col>
        </v-row>

        <v-card-actions class="pa-0 mt-4">
          <v-spacer />
          <v-btn variant="text" @click="editDialog = false">{{ $t('common.cancel') }}</v-btn>
          <v-btn
            color="primary"
            :loading="loadingAction"
            @click="updateCustomer"
            >{{ $t('common.save') }}</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Confirmación de borrado -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card class="pa-6">
        <v-card-title class="pa-0 mb-2">{{ $t('admin.customers.deleteTitle') }}</v-card-title>
        <v-card-text class="pa-0 mb-4">
          {{ $t('admin.customers.confirmDelete') }}
          <strong
            >{{ selectedCustomer?.firstName }}
            {{ selectedCustomer?.lastName }}</strong
          >?
        </v-card-text>
        <v-card-actions class="pa-0">
          <v-spacer />
          <v-btn variant="text" @click="deleteDialog = false">{{ $t('common.cancel') }}</v-btn>
          <v-btn color="error" :loading="loadingAction" @click="deleteCustomer"
            >{{ $t('common.delete') }}</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
