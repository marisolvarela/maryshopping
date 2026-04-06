// ⚙️ CONFIGURACIÓN PRINCIPAL DE NUXT
// Este archivo configura cómo se comporta la aplicación:
// - ssr: false = Modo SPA (necesario para Vuetify)
// - Módulos instalados: Vuetify, Pinia, Persistencia de estado
// - URL del backend: http://localhost:8089/maryshopping

import { defineNuxtConfig } from 'nuxt/config';

export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },

  // SPA mode — required for Vuetify compatibility
  ssr: false,

  runtimeConfig: {
    public: {
      apiBase:
        process.env.NUXT_PUBLIC_API_BASE ||
        '/maryshopping',
    },
  },

  build: {
    transpile: ['vuetify'],
  },

  modules: [
    'vuetify-nuxt-module',
    '@pinia/nuxt',
    'pinia-plugin-persistedstate/nuxt',
    '@nuxt/fonts',
    '@nuxt/icon',
    '@nuxt/image',
    '@nuxt/eslint',
    '@nuxtjs/i18n',
  ],

  i18n: {
    locales: [
      { code: 'en', name: 'English', file: 'en.json' },
      { code: 'es', name: 'Español', file: 'es.json' },
    ],
    defaultLocale: 'en',
    langDir: '../app/locales/',
    strategy: 'no_prefix',
  },

  vuetify: {
    moduleOptions: {},
    vuetifyOptions: './vuetify.config.ts',
  },

  css: ['vuetify/styles', '@mdi/font/css/materialdesignicons.css'],

  fonts: {
    families: [
      { name: 'Montserrat', provider: 'google', weights: [300, 400, 500, 700] },
    ],
    defaults: {
      fallbacks: {
        'sans-serif': ['Arial', 'Helvetica'],
      },
    },
  },

  // Proxy para evitar problemas de CORS en desarrollo.
  // El frontend envía las peticiones a sí mismo (mismo origen)
  // y Vite las reenvía al backend en localhost:8089.
  vite: {
    server: {
      proxy: {
        '/maryshopping': {
          target: 'http://localhost:8089',
          changeOrigin: true,
        },
      },
    },
  },
});
