<!-- 🔓 PÁGINA DE LOGIN (Inicio de sesión)
  Formulario para que los usuarios se logueen:
  - Email e input de contraseña
  - Botón de login que llama a authService
  - Valida credenciales contra el backend (/customers/iam/token)
  - Guarda tokens en userStore
  - Si es exitoso, redirige a /products
  - Si hay error, muestra mensaje
-->

<script setup lang="ts">
import { ref } from 'vue';
import { useUserStore } from '~/stores/userStore';

definePageMeta({ middleware: [] }); // Sin autenticación requerida

const route = useRoute();
const { login } = useAuth();

const email = ref('');
const password = ref('');
const showPassword = ref(false);
const loading = ref(false);
const error = ref('');
const sessionExpired = ref(route.query.sessionExpired === 'true');

async function handleLogin() {
  error.value = '';
  loading.value = true;

  const result = await login({ email: email.value, password: password.value });

  if (result.success) {
    const userStore = useUserStore();
    if (userStore.isAdministrator) {
      await navigateTo('/admin');
    } else {
      const redirect = (route.query.redirect as string) || '/products';
      await navigateTo(redirect);
    }
  } else {
    error.value = result.error?.message || 'Login failed';
  }

  loading.value = false;
}
</script>

<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="5" lg="4">
        <v-card class="pa-6">
          <div class="text-center mb-6">
            <v-icon size="64" color="primary" class="mb-4">mdi-shopping</v-icon>
            <h1 class="text-h5 font-weight-bold">{{ $t('login.title') }}</h1>
          </div>

          <v-alert
            v-if="sessionExpired"
            type="warning"
            variant="tonal"
            class="mb-4"
            closable
            @click:close="sessionExpired = false"
          >
            {{ $t('login.sessionExpired') }}
          </v-alert>

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

          <v-form @submit.prevent="handleLogin">
            <v-text-field
              v-model="email"
              :label="$t('login.email')"
              type="email"
              prepend-inner-icon="mdi-email-outline"
              required
              autofocus
              class="mb-3"
            />
            <v-text-field
              v-model="password"
              :label="$t('login.password')"
              :type="showPassword ? 'text' : 'password'"
              prepend-inner-icon="mdi-lock-outline"
              :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
              required
              class="mb-3"
              @click:append-inner="showPassword = !showPassword"
            />
            <v-btn
              type="submit"
              color="primary"
              block
              size="large"
              class="text-none mb-4"
              :loading="loading"
              :disabled="!email || !password"
            >
              {{ $t('login.signIn') }}
            </v-btn>
          </v-form>

          <div class="text-center">
            <span class="text-medium-emphasis">{{ $t('login.noAccount') }}</span>
            <NuxtLink
              to="/register"
              class="text-primary font-weight-medium ml-1"
            >
              {{ $t('login.register') }}
            </NuxtLink>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
