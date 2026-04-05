<!-- 🎨 COMPONENTE LOGO (Logo de MaryShopping)
  Muestra el logo de la tienda en diferentes tamaños:
  - size: 'small', 'medium', 'large' (tamaño del logo)
  - showText: si mostrar el texto "MaryShopping" junto al logo
  Se reutiliza en header, landing page, etc.
  Usa el tema actual (claro/oscuro) de Vuetify
-->

<script setup lang="ts">
import { computed } from 'vue';
import { useTheme } from 'vuetify';

defineProps<{
  size?: 'small' | 'medium' | 'large';
  showText?: boolean;
}>();

const theme = useTheme();
const isDark = computed(() => theme.global.current.value.dark);
const textColor = computed(() => (isDark.value ? '#e2e8f0' : '#1e293b'));
</script>

<template>
  <div class="app-logo" :class="[`app-logo--${size || 'medium'}`]">
    <div class="logo-icon">
      <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <linearGradient id="logoGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" style="stop-color: #8b5cf6" />
            <stop offset="50%" style="stop-color: #6366f1" />
            <stop offset="100%" style="stop-color: #06b6d4" />
          </linearGradient>
          <linearGradient
            id="logoGradient2"
            x1="0%"
            y1="100%"
            x2="100%"
            y2="0%"
          >
            <stop offset="0%" style="stop-color: #6366f1" />
            <stop offset="100%" style="stop-color: #8b5cf6" />
          </linearGradient>
        </defs>
        <path
          d="M8 16C8 14.8954 8.89543 14 10 14H38C39.1046 14 40 14.8954 40 16V40C40 42.2091 38.2091 44 36 44H12C9.79086 44 8 42.2091 8 40V16Z"
          fill="url(#logoGradient)"
          opacity="0.9"
        />
        <path
          d="M8 16C8 14.8954 8.89543 14 10 14H38C39.1046 14 40 14.8954 40 16V20H8V16Z"
          fill="url(#logoGradient2)"
          opacity="0.6"
        />
        <path
          d="M16 14V12C16 7.58172 19.5817 4 24 4C28.4183 4 32 7.58172 32 12V14"
          stroke="url(#logoGradient)"
          stroke-width="3"
          stroke-linecap="round"
          fill="none"
        />
        <circle cx="30" cy="28" r="2" fill="white" opacity="0.8" />
        <circle cx="34" cy="24" r="1" fill="white" opacity="0.5" />
        <path d="M18 26L22 30L26 26L22 34L18 26Z" fill="white" opacity="0.3" />
      </svg>
    </div>
    <div v-if="showText !== false" class="logo-text">
      <span class="logo-text-primary" :style="{ color: textColor }">Mary</span
      ><span class="logo-text-accent">Shopping</span>
    </div>
  </div>
</template>

<style scoped>
.app-logo {
  display: flex;
  align-items: center;
  gap: 8px;
}
.logo-icon svg {
  display: block;
}
.app-logo--small .logo-icon {
  width: 32px;
  height: 32px;
}
.app-logo--medium .logo-icon {
  width: 40px;
  height: 40px;
}
.app-logo--large .logo-icon {
  width: 56px;
  height: 56px;
}
.logo-text-primary {
  font-size: 1.15rem;
  font-weight: 700;
  letter-spacing: -0.5px;
}
.logo-text-accent {
  font-size: 1.15rem;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: #6366f1;
}
</style>
