<!-- ✍️ PÁGINA DE REGISTRO (Crear cuenta)
  Formulario para nuevos usuarios:
  - Email, contraseña, nombre, apellido, documento de identidad
  - Valida que las contraseñas coincidan
  - POST /customers (endpoint público, sin auth)
  - Si es exitoso, muestra mensaje y redirige a login
  - Si hay error, muestra mensaje
-->

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { customersService } from '~/services/customersService';
import type { CreateCustomerRequest } from '~/services/customersService';

definePageMeta({ middleware: [] });

const loading = ref(false);
const error = ref('');
const success = ref(false);
const showPassword = ref(false);

const form = reactive<CreateCustomerRequest>({
  firstName: '',
  lastName: '',
  dni: '',
  phoneNumber: '',
  emailAddress: '',
  password: '',
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

const sameAddress = ref(true);

async function handleRegister() {
  error.value = '';
  loading.value = true;

  try {
    if (sameAddress.value) {
      form.billingAddress = { ...form.shippingAddress };
    }
    await customersService.create(form);
    success.value = true;
  } catch (e: any) {
    error.value =
      e.response?.data?.message || 'Registration failed. Please try again.';
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <v-container class="py-8" fluid>
    <v-row justify="center">
      <v-col cols="12" md="8" lg="6">
        <!-- Éxito -->
        <v-card v-if="success" class="pa-8 text-center">
          <v-icon size="80" color="success" class="mb-4"
            >mdi-check-circle-outline</v-icon
          >
          <h2 class="text-h5 font-weight-bold mb-4">
            {{ $t('register.success') }}
          </h2>
          <p class="text-body-1 mb-6">
            {{ $t('register.successMessage') }}
          </p>
          <v-btn color="primary" size="large" class="text-none" to="/login">
            {{ $t('register.goToLogin') }}
          </v-btn>
        </v-card>

        <!-- Formulario -->
        <v-card v-else class="pa-6">
          <div class="text-center mb-6">
            <v-icon size="64" color="primary" class="mb-4"
              >mdi-account-plus</v-icon
            >
            <h1 class="text-h5 font-weight-bold">{{ $t('register.title') }}</h1>
          </div>

          <v-alert
            v-if="error"
            type="error"
            variant="tonal"
            class="mb-4"
            closable
            @click:close="error = ''"
          >
            {{ error }}
          </v-alert>

          <v-form @submit.prevent="handleRegister">
            <!-- Cuenta -->
            <h3 class="text-subtitle-1 font-weight-bold mb-3">
              {{ $t('register.accountDetails') }}
            </h3>
            <v-row dense>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.emailAddress"
                  :label="$t('register.email')"
                  type="email"
                  required
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.password"
                  :label="$t('register.password')"
                  :type="showPassword ? 'text' : 'password'"
                  :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                  required
                  @click:append-inner="showPassword = !showPassword"
                />
              </v-col>
            </v-row>

            <!-- Información personal -->
            <h3 class="text-subtitle-1 font-weight-bold mb-3 mt-4">
              {{ $t('register.personalInfo') }}
            </h3>
            <v-row dense>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.firstName"
                  :label="$t('register.firstName')"
                  required
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.lastName"
                  :label="$t('register.lastName')"
                  required
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="form.phoneNumber" :label="$t('register.phone')" />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.dni"
                  :label="$t('register.dni')"
                  required
                />
              </v-col>
            </v-row>

            <!-- Dirección de envío -->
            <h3 class="text-subtitle-1 font-weight-bold mb-3 mt-4">
              {{ $t('register.shippingAddress') }}
            </h3>
            <v-row dense>
              <v-col cols="12">
                <v-text-field
                  v-model="form.shippingAddress.street"
                  :label="$t('register.street')"
                  required
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.shippingAddress.city"
                  :label="$t('register.city')"
                  required
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.shippingAddress.province"
                  :label="$t('register.province')"
                  required
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.shippingAddress.postalCode"
                  :label="$t('register.postalCode')"
                  required
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.shippingAddress.country"
                  :label="$t('register.country')"
                  required
                />
              </v-col>
            </v-row>

            <v-checkbox
              v-model="sameAddress"
              :label="$t('register.billingSameAsShipping')"
              class="mt-2"
            />

            <!-- Dirección de facturación (si es diferente) -->
            <template v-if="!sameAddress">
              <h3 class="text-subtitle-1 font-weight-bold mb-3">
                {{ $t('register.billingAddress') }}
              </h3>
              <v-row dense>
                <v-col cols="12">
                  <v-text-field
                    v-model="form.billingAddress.street"
                    :label="$t('register.street')"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="form.billingAddress.city"
                    :label="$t('register.city')"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="form.billingAddress.province"
                    :label="$t('register.province')"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="form.billingAddress.postalCode"
                    :label="$t('register.postalCode')"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="form.billingAddress.country"
                    :label="$t('register.country')"
                    required
                  />
                </v-col>
              </v-row>
            </template>

            <v-btn
              type="submit"
              color="primary"
              block
              size="large"
              class="text-none mt-6"
              :loading="loading"
            >
              {{ $t('register.createAccount') }}
            </v-btn>
          </v-form>

          <div class="text-center mt-4">
            <span class="text-medium-emphasis">{{ $t('register.hasAccount') }}</span>
            <NuxtLink to="/login" class="text-primary font-weight-medium ml-1">
              {{ $t('register.signIn') }}
            </NuxtLink>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
