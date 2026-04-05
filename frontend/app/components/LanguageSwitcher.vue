<template>
  <v-menu>
    <template #activator="{ props }">
      <v-btn v-bind="props" icon variant="text" :aria-label="$t('common.language')">
        <v-icon>mdi-translate</v-icon>
      </v-btn>
    </template>
    <v-list density="compact">
      <v-list-item
        v-for="loc in locales"
        :key="loc.code"
        :active="loc.code === locale"
        @click="setLocale(loc.code)"
      >
        <v-list-item-title>{{ loc.name }}</v-list-item-title>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script setup lang="ts">
const { locale, locales, setLocale } = useI18n()
</script>
