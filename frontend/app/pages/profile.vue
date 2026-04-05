<!-- 👤 PÁGINA DE PERFIL (Mi cuenta)
  Edición del perfil del usuario:
  - GET /customers/{id} (carga datos actuales)
  - Formulario editable: nombre, email, teléfono, dirección
  - PUT /customers/{id} (guarda cambios)
  - DELETE /customers/{id} (elimina la cuenta)
  - Requiere estar logueado (middleware: auth)
-->

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { customersService } from '~/services/customersService';
import type { UpdateCustomerRequest } from '~/services/customersService';
import type { Customer, Address } from '~/models/customers/Customer';
import { useUserStore } from '~/stores/userStore';

definePageMeta({ middleware: ['auth'] });

const { t } = useI18n();
const userStore = useUserStore();

const formData = ref<Customer>({
  id: '',
  firstName: '',
  lastName: '',
  dni: '',
  emailAddress: '',
  phoneNumber: '',
  shippingAddress: {
    street: '',
    city: '',
    province: '',
    postalCode: '',
    country: '',
  },
  billingAddress: {
    street: '',
    city: '',
    province: '',
    postalCode: '',
    country: '',
  },
});

const newPassword = ref('');
const confirmPassword = ref('');
const showPassword = ref(false);
const changePassword = ref(false);
const loading = ref(false);
const loadingProfile = ref(true);
const sameAsShipping = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

onMounted(async () => {
  await loadProfile();
});

async function loadProfile() {
  loadingProfile.value = true;
  errorMessage.value = '';
  try {
    const customerId = userStore.customerId;
    if (!customerId) {
      errorMessage.value = t('profile.noCustomerId');
      return;
    }
    const customer = await customersService.getById(customerId);
    formData.value = { ...customer };
    sameAsShipping.value = areAddressesEqual(
      customer.shippingAddress,
      customer.billingAddress,
    );
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('profile.failedToLoad');
  } finally {
    loadingProfile.value = false;
  }
}

function areAddressesEqual(a: Address, b: Address): boolean {
  return (
    a.street === b.street &&
    a.city === b.city &&
    a.province === b.province &&
    a.postalCode === b.postalCode &&
    a.country === b.country
  );
}

function copyShippingToBilling() {
  if (sameAsShipping.value) {
    formData.value.billingAddress = { ...formData.value.shippingAddress };
  }
}

async function handleUpdate() {
  errorMessage.value = '';
  successMessage.value = '';

  if (changePassword.value) {
    if (newPassword.value.length < 8) {
      errorMessage.value = t('profile.passwordMinLength');
      return;
    }
    if (newPassword.value !== confirmPassword.value) {
      errorMessage.value = t('profile.passwordsMismatch');
      return;
    }
  }

  if (sameAsShipping.value) {
    formData.value.billingAddress = { ...formData.value.shippingAddress };
  }

  loading.value = true;
  try {
    const updateRequest: UpdateCustomerRequest = {
      firstName: formData.value.firstName,
      lastName: formData.value.lastName,
      dni: formData.value.dni,
      emailAddress: formData.value.emailAddress,
      phoneNumber: formData.value.phoneNumber,
      shippingAddress: formData.value.shippingAddress,
      billingAddress: formData.value.billingAddress,
    };
    if (changePassword.value && newPassword.value) {
      updateRequest.password = newPassword.value;
    }

    await customersService.update(formData.value.id, updateRequest);
    await loadProfile();
    newPassword.value = '';
    confirmPassword.value = '';
    changePassword.value = false;
    successMessage.value = t('profile.updateSuccess');
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('profile.failedToUpdate');
  } finally {
    loading.value = false;
  }
}

async function handleDeleteAccount() {
  if (
    !confirm(t('profile.confirmDelete'))
  )
    return;
  loading.value = true;
  errorMessage.value = '';
  try {
    await customersService.delete(formData.value.id);
    userStore.logout();
    navigateTo('/login');
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } };
    errorMessage.value =
      err.response?.data?.message || t('profile.failedToDelete');
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <v-container class="py-6">
    <v-row justify="center">
      <v-col cols="12" md="10" lg="8">
        <v-card class="pa-6" elevation="0" variant="outlined">
          <div class="text-center mb-6">
            <v-avatar color="primary" variant="tonal" size="80" class="mb-4">
              <v-icon size="40">mdi-account-circle</v-icon>
            </v-avatar>
            <h1 class="text-h4 font-weight-bold mb-2">{{ $t('profile.title') }}</h1>
            <p class="text-body-2 text-medium-emphasis">
              {{ $t('profile.subtitle') }}
            </p>
          </div>

          <div v-if="loadingProfile" class="text-center py-12">
            <v-progress-circular indeterminate color="primary" size="64" />
            <p class="mt-4 text-medium-emphasis">{{ $t('profile.loading') }}</p>
          </div>

          <v-alert
            v-if="errorMessage && !loadingProfile"
            type="error"
            variant="tonal"
            closable
            class="mb-4"
            @click:close="errorMessage = ''"
          >
            {{ errorMessage }}
          </v-alert>
          <v-alert
            v-if="successMessage"
            type="success"
            variant="tonal"
            class="mb-4"
          >
            {{ successMessage }}
          </v-alert>

          <v-form
            v-if="!loadingProfile && formData.id"
            @submit.prevent="handleUpdate"
          >
            <!-- Información de la cuenta -->
            <div class="d-flex align-center mb-4">
              <v-icon size="20" color="primary" class="mr-2"
                >mdi-account-circle</v-icon
              >
              <span class="text-subtitle-1 font-weight-bold"
                >{{ $t('profile.accountInfo') }}</span
              >
            </div>
            <v-row>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.emailAddress"
                  :label="$t('profile.emailAddress')"
                  type="email"
                  prepend-inner-icon="mdi-email-outline"
                  variant="outlined"
                  required
                />
              </v-col>
            </v-row>

            <!-- Cambio de contraseña -->
            <v-checkbox
              v-model="changePassword"
              :label="$t('profile.changePassword')"
              color="primary"
              hide-details
              class="mb-2"
            />
            <v-row v-if="changePassword" class="mt-2">
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="newPassword"
                  :label="$t('profile.newPassword')"
                  :type="showPassword ? 'text' : 'password'"
                  prepend-inner-icon="mdi-lock-outline"
                  :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                  variant="outlined"
                  :hint="$t('profile.passwordHint')"
                  @click:append-inner="showPassword = !showPassword"
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="confirmPassword"
                  :label="$t('profile.confirmPassword')"
                  :type="showPassword ? 'text' : 'password'"
                  prepend-inner-icon="mdi-lock-check-outline"
                  variant="outlined"
                />
              </v-col>
            </v-row>

            <v-divider class="my-6" />

            <!-- Datos personales -->
            <div class="d-flex align-center mb-4">
              <v-icon size="20" color="primary" class="mr-2"
                >mdi-card-account-details</v-icon
              >
              <span class="text-subtitle-1 font-weight-bold"
                >{{ $t('profile.personalDetails') }}</span
              >
            </div>
            <v-row>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.firstName"
                  :label="$t('profile.firstName')"
                  prepend-inner-icon="mdi-account"
                  variant="outlined"
                  required
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.lastName"
                  :label="$t('profile.lastName')"
                  prepend-inner-icon="mdi-account"
                  variant="outlined"
                  required
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.dni"
                  :label="$t('profile.dni')"
                  prepend-inner-icon="mdi-card-account-details-outline"
                  variant="outlined"
                  required
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.phoneNumber"
                  :label="$t('profile.phoneNumber')"
                  prepend-inner-icon="mdi-phone-outline"
                  variant="outlined"
                  required
                />
              </v-col>
            </v-row>

            <v-divider class="my-6" />

            <!-- Dirección de envío -->
            <div class="d-flex align-center mb-4">
              <v-icon size="20" color="primary" class="mr-2"
                >mdi-truck-delivery-outline</v-icon
              >
              <span class="text-subtitle-1 font-weight-bold"
                >{{ $t('profile.shippingAddress') }}</span
              >
            </div>
            <v-row>
              <v-col cols="12">
                <v-text-field
                  v-model="formData.shippingAddress.street"
                  :label="$t('profile.street')"
                  variant="outlined"
                  @input="copyShippingToBilling"
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="formData.shippingAddress.city"
                  :label="$t('profile.city')"
                  variant="outlined"
                  @input="copyShippingToBilling"
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="formData.shippingAddress.province"
                  :label="$t('profile.province')"
                  variant="outlined"
                  @input="copyShippingToBilling"
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="formData.shippingAddress.postalCode"
                  :label="$t('profile.postalCode')"
                  variant="outlined"
                  @input="copyShippingToBilling"
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.shippingAddress.country"
                  :label="$t('profile.country')"
                  variant="outlined"
                  @input="copyShippingToBilling"
                />
              </v-col>
            </v-row>

            <v-divider class="my-6" />

            <!-- Dirección de facturación -->
            <div class="d-flex align-center mb-4">
              <v-icon size="20" color="primary" class="mr-2"
                >mdi-credit-card-outline</v-icon
              >
              <span class="text-subtitle-1 font-weight-bold"
                >{{ $t('profile.billingAddress') }}</span
              >
            </div>
            <v-checkbox
              v-model="sameAsShipping"
              :label="$t('profile.sameAsShipping')"
              color="primary"
              hide-details
              class="mb-4"
              @update:model-value="copyShippingToBilling"
            />
            <v-row v-if="!sameAsShipping">
              <v-col cols="12">
                <v-text-field
                  v-model="formData.billingAddress.street"
                  :label="$t('profile.street')"
                  variant="outlined"
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="formData.billingAddress.city"
                  :label="$t('profile.city')"
                  variant="outlined"
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="formData.billingAddress.province"
                  :label="$t('profile.province')"
                  variant="outlined"
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="formData.billingAddress.postalCode"
                  :label="$t('profile.postalCode')"
                  variant="outlined"
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.billingAddress.country"
                  :label="$t('profile.country')"
                  variant="outlined"
                />
              </v-col>
            </v-row>

            <v-divider class="my-6" />

            <!-- Acciones -->
            <div class="d-flex justify-space-between flex-wrap ga-4">
              <v-btn
                color="error"
                variant="outlined"
                class="text-none"
                @click="handleDeleteAccount"
              >
                <v-icon start>mdi-delete-outline</v-icon>
                {{ $t('profile.deleteAccount') }}
              </v-btn>
              <v-btn
                color="primary"
                type="submit"
                size="large"
                class="text-none"
                :loading="loading"
              >
                <v-icon start>mdi-content-save</v-icon>
                {{ $t('profile.saveChanges') }}
              </v-btn>
            </div>
          </v-form>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
