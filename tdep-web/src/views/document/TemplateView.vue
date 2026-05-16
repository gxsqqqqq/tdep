<template>
  <section class="page-card">
    <div class="filter-bar template-filter">
      <el-select v-model="query.documentType" clearable placeholder="文书类型">
        <el-option label="判决书" value="JUDGEMENT" />
        <el-option label="起诉状" value="COMPLAINT" />
        <el-option label="开庭通知书" value="TRIAL_NOTICE" />
      </el-select>
      <el-button type="primary" @click="loadTemplates">查询</el-button>
      <PermissionButton permission="document:template:manage" type="primary" @click="uploadVisible = true">
        上传模板
      </PermissionButton>
    </div>

    <el-table v-loading="loading" :data="templates" stripe>
      <el-table-column prop="templateCode" label="模板编码" min-width="180" />
      <el-table-column prop="templateName" label="模板名称" min-width="180" />
      <el-table-column prop="documentType" label="类型" width="150" />
      <el-table-column prop="version" label="版本" width="90" />
      <el-table-column label="状态" width="140">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="占位符" min-width="220" show-overflow-tooltip>
        <template #default="{ row }">{{ row.placeholders?.join(', ') || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" :disabled="!row.previewUrl" @click="window.open(row.previewUrl, '_blank')">
            预览
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="uploadVisible" title="上传文书模板" width="520px">
      <el-form :model="uploadForm" label-width="96px">
        <el-form-item label="文书类型">
          <el-select v-model="uploadForm.documentType">
            <el-option label="判决书" value="JUDGEMENT" />
            <el-option label="起诉状" value="COMPLAINT" />
            <el-option label="开庭通知书" value="TRIAL_NOTICE" />
          </el-select>
        </el-form-item>
        <el-form-item label="模板名称"><el-input v-model="uploadForm.templateName" /></el-form-item>
        <el-form-item label="模板文件">
          <el-upload :auto-upload="false" :limit="1" :on-change="onTemplateChange">
            <el-button>选择 docx</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="submitUpload">上传</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { ElMessage, type UploadFile } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

import { getTemplateList, uploadTemplate } from '@/api/document'
import type { DocumentTemplateItem } from '@/api/models'
import PermissionButton from '@/components/PermissionButton/index.vue'

const window = globalThis.window
const loading = ref(false)
const uploading = ref(false)
const uploadVisible = ref(false)
const templates = ref<DocumentTemplateItem[]>([])
const templateFile = ref<File>()
const query = reactive({ documentType: '' })
const uploadForm = reactive({ documentType: 'JUDGEMENT', templateName: '' })

onMounted(loadTemplates)

async function loadTemplates() {
  loading.value = true
  try {
    templates.value = await getTemplateList(query.documentType ? query : undefined)
  } finally {
    loading.value = false
  }
}

function onTemplateChange(file: UploadFile) {
  templateFile.value = file.raw
}

async function submitUpload() {
  if (!templateFile.value) {
    ElMessage.warning('请选择模板文件')
    return
  }
  uploading.value = true
  try {
    await uploadTemplate(templateFile.value, { ...uploadForm, currentVersion: true })
    ElMessage.success('模板已上传')
    uploadVisible.value = false
    loadTemplates()
  } finally {
    uploading.value = false
  }
}
</script>
