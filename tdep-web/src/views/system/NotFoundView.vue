<template>
  <el-result icon="info" title="404" sub-title="页面不存在">
    <template #extra>
      <el-button type="primary" @click="$router.replace('/dashboard')">返回工作台</el-button>
    </template>
  </el-result>
</template>
