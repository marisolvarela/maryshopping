<template>
  <NuxtLayout>
    <v-app>
      <NuxtPage />
    </v-app>
  </NuxtLayout>
</template>

<style>
/* Estilos globales del tooltip para mejor contraste */
.v-tooltip > .v-overlay__content {
  background: rgb(var(--v-theme-surface)) !important;
  color: rgb(var(--v-theme-on-surface)) !important;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.12);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

/* Estilos del contenido del menú */
.v-menu > .v-overlay__content {
  background: rgb(var(--v-theme-surface)) !important;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.12);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
}

.v-list {
  background: transparent !important;
}

.v-list-item-title {
  color: rgb(var(--v-theme-on-surface)) !important;
}

.v-list-item-subtitle {
  color: rgba(var(--v-theme-on-surface), 0.7) !important;
}

.v-menu .v-card {
  background: rgb(var(--v-theme-surface)) !important;
}

.v-menu .v-card-text {
  color: rgb(var(--v-theme-on-surface)) !important;
}
</style>
