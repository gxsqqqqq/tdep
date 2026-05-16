<template>
  <section class="placeholder-panel">
    <el-empty :description="`${route.meta.title}模块骨架已就绪`" />
  </section>
</template>

<script setup>
import { useRoute } from 'vue-router'

// 占位视图用于保持前端工程可运行，不包含具体业务逻辑。
const route = useRoute()
</script>
