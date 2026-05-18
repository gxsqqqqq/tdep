<template>
  <el-result icon="warning" title="403" sub-title="当前账号无权访问该页面">
    <template #extra>
      <el-button type="primary" @click="$router.replace('/my-cases')">返回我的案件</el-button>
    </template>
  </el-result>
</template>
