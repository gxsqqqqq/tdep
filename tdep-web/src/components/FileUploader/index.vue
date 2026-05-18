<template>
  <div>
    <el-upload
      drag
      :auto-upload="false"
      :limit="multiple ? undefined : 1"
      :on-change="onFileChange"
      :on-remove="onFileRemove"
      :accept="accept"
    >
      <div class="file-uploader">
        <div class="file-uploader__icon">📁</div>
        <div class="file-uploader__text">拖拽文件到此处，或点击选择</div>
        <div class="file-uploader__hint">
          支持 PDF、图片、音视频、Word 等格式
          <template v-if="maxSize">，单个文件不超过 {{ formatFileSize(maxSize) }}</template>
        </div>
      </div>
    </el-upload>
    <div v-if="file" style="margin-top: 12px; display: flex; align-items: center; gap: 12px">
      <span>{{ file.name }} ({{ formatFileSize(file.size) }})</span>
      <el-button type="primary" :loading="uploading" @click="submit">上传证据</el-button>
    </div>
    <el-progress v-if="uploading" :percentage="100" :indeterminate="true" style="margin-top: 8px" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { UploadFile } from 'element-plus'

import { formatFileSize } from '@/utils/status'

const props = defineProps<{
  caseId: number | string
  accept?: string
  maxSize?: number
  multiple?: boolean
  uploading?: boolean
}>()

const emit = defineEmits<{
  upload: [file: File, description: string]
}>()

const file = ref<File>()
const description = ref('')

function onFileChange(uploadFile: UploadFile) {
  file.value = uploadFile.raw
}

function onFileRemove() {
  file.value = undefined
}

function submit() {
  if (!file.value) return
  emit('upload', file.value, description.value)
}
</script>
