<template>
  <section class="page-card upload-page">
    <el-form :model="form" label-width="96px" class="upload-form">
      <el-form-item label="案件ID">
        <el-input v-model="form.caseId" placeholder="请输入案件ID" />
      </el-form-item>
      <el-form-item label="证据说明">
        <el-input v-model="form.description" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="文件">
        <el-upload
          drag
          :auto-upload="false"
          :limit="1"
          :on-change="onFileChange"
          :on-remove="onFileRemove"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">拖拽文件到此处，或点击选择</div>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="uploading" @click="submit">上传证据</el-button>
      </el-form-item>
    </el-form>

    <el-descriptions v-if="uploaded" title="上传结果" :column="1" border>
      <el-descriptions-item label="证据编号">{{ uploaded.evidenceNo }}</el-descriptions-item>
      <el-descriptions-item label="文件名">{{ uploaded.fileName }}</el-descriptions-item>
      <el-descriptions-item label="Hash">{{ uploaded.hash?.hashValue || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ uploaded.status }}</el-descriptions-item>
    </el-descriptions>
  </section>
</template>

<script setup lang="ts">
import { UploadFilled } from '@element-plus/icons-vue'
import { ElMessage, type UploadFile } from 'element-plus'
import { reactive, ref } from 'vue'

import { uploadEvidence } from '@/api/evidence'
import type { EvidenceItem } from '@/api/models'

const form = reactive({ caseId: '', description: '' })
const file = ref<File>()
const uploading = ref(false)
const uploaded = ref<EvidenceItem>()

function onFileChange(uploadFile: UploadFile) {
  file.value = uploadFile.raw
}

function onFileRemove() {
  file.value = undefined
}

async function submit() {
  if (!file.value) {
    ElMessage.warning('请选择文件')
    return
  }
  uploading.value = true
  try {
    uploaded.value = await uploadEvidence(file.value, { caseId: form.caseId, description: form.description })
    ElMessage.success('证据上传成功')
  } finally {
    uploading.value = false
  }
}
</script>
