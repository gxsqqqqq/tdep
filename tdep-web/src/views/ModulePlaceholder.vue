<template>
  <section class="module-placeholder">
    <div>
      <h2>{{ title }}</h2>
      <p>{{ description }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  description: string
}>()
</script>
