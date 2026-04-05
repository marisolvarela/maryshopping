// 🎨 TEMA VISUAL (Colores y estilos de Vuetify)
// Define el diseño oscuro de la aplicación:
// - Colores de fondo, botones, errores, etc.
// - Material Design 3 (md3)
// - Tipografía: Montserrat

import { defineVuetifyConfiguration } from 'vuetify-nuxt-module/custom-configuration';
import { md3 } from 'vuetify/blueprints';

export default defineVuetifyConfiguration({
  blueprint: md3,
  theme: {
    defaultTheme: 'dark',
    themes: {
      dark: {
        dark: true,
        colors: {
          background: '#0a0a0f',
          surface: '#12121a',
          'surface-light': '#1a1a24',
          'surface-variant': '#22222e',
          primary: '#6366f1',
          'primary-darken-1': '#4f46e5',
          secondary: '#8b5cf6',
          'secondary-darken-1': '#7c3aed',
          accent: '#06b6d4',
          error: '#ef4444',
          warning: '#f59e0b',
          info: '#3b82f6',
          success: '#10b981',
          'on-background': '#f1f5f9',
          'on-surface': '#e2e8f0',
          'on-primary': '#ffffff',
          'on-secondary': '#ffffff',
          'tooltip-background': '#1e1e2e',
          'inverse-surface': '#e2e8f0',
          'inverse-on-surface': '#1e1e2e',
        },
      },
      light: {
        dark: false,
        colors: {
          background: '#f8fafc',
          surface: '#ffffff',
          'surface-light': '#f1f5f9',
          'surface-variant': '#e2e8f0',
          primary: '#6366f1',
          'primary-darken-1': '#4f46e5',
          secondary: '#8b5cf6',
          'secondary-darken-1': '#7c3aed',
          accent: '#06b6d4',
          error: '#ef4444',
          warning: '#f59e0b',
          info: '#3b82f6',
          success: '#10b981',
          'on-background': '#0f172a',
          'on-surface': '#1e293b',
          'on-primary': '#ffffff',
          'on-secondary': '#ffffff',
          'tooltip-background': '#1e293b',
          'inverse-surface': '#1e293b',
          'inverse-on-surface': '#f1f5f9',
        },
      },
    },
  },
  defaults: {
    VBtn: {
      rounded: 'lg',
      elevation: 0,
    },
    VCard: {
      rounded: 'xl',
      elevation: 0,
    },
    VTextField: {
      variant: 'outlined',
      rounded: 'lg',
    },
    VSelect: {
      variant: 'outlined',
      rounded: 'lg',
    },
    VChip: {
      rounded: 'lg',
    },
    VAlert: {
      rounded: 'lg',
    },
  },
});
