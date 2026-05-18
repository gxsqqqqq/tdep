<template>
  <section class="page-card">
    <div class="filter-bar">
      <el-input v-model="query.keyword" clearable placeholder="搜索案件标题" />
      <el-input v-model="query.caseNo" clearable placeholder="案件号" />
      <el-select v-model="query.status" clearable placeholder="案件状态">
        <el-option v-for="status in caseStatuses" :key="status" :label="status" :value="status" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="loadCases">查询</el-button>
      <PermissionButton permission="case:create" type="primary" :icon="Plus" @click="createVisible = true">
        创建案件
      </PermissionButton>
    </div>

    <el-table v-loading="loading" :data="cases" stripe>
      <el-table-column prop="caseNo" label="案件号" min-width="170" />
      <el-table-column prop="caseTitle" label="案件标题" min-width="220" show-overflow-tooltip />
      <el-table-column prop="caseType" label="类型" width="110" />
      <el-table-column prop="causeAction" label="案由" min-width="140" />
      <el-table-column prop="claimAmount" label="标的金额" width="120" />
      <el-table-column prop="status" label="状态" width="150">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="210" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <PermissionButton permission="case:workflow" link type="primary" @click="openTransition(row)">
            流转
          </PermissionButton>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.pageNo"
      v-model:page-size="pagination.pageSize"
      class="table-pagination"
      layout="total, sizes, prev, pager, next"
      :total="pagination.total"
      @change="loadCases"
    />

    <el-dialog v-model="createVisible" title="创建案件" width="720px">
      <el-form :model="caseForm" label-width="96px">
        <el-form-item label="案件标题"><el-input v-model="caseForm.caseTitle" /></el-form-item>
        <el-form-item label="案件类型"><el-input v-model="caseForm.caseType" /></el-form-item>
        <el-form-item label="案由"><el-input v-model="caseForm.causeAction" /></el-form-item>
        <el-form-item label="程序类型"><el-input v-model="caseForm.procedureType" /></el-form-item>
        <el-form-item label="受理法院"><el-input v-model="caseForm.courtName" /></el-form-item>
        <el-form-item label="诉讼请求"><el-input v-model="caseForm.claimRequest" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCase">提交</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="案件详情" size="520px">
      <el-descriptions v-if="detail" :column="1" border>
        <el-descriptions-item label="案件号">{{ detail.caseNo }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ detail.caseTitle }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ detail.caseType }}</el-descriptions-item>
        <el-descriptions-item label="案由">{{ detail.causeAction }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
        <el-descriptions-item label="法院">{{ detail.courtName || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>

    <el-dialog v-model="transitionVisible" title="案件状态流转" width="480px">
      <el-form :model="transitionForm" label-width="80px">
        <el-form-item label="动作">
          <el-select v-model="transitionForm.action" placeholder="选择流转动作">
            <el-option v-for="action in caseActions" :key="action" :label="action" :value="action" />
          </el-select>
        </el-form-item>
        <el-form-item label="意见"><el-input v-model="transitionForm.comment" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transitionVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTransition">确认</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

import { createCase, getCaseDetail, getCaseList, transitionCase } from '@/api/case'
import type { CaseDetail, CaseItem } from '@/api/models'
import PermissionButton from '@/components/PermissionButton/index.vue'
import { formatDateTime } from '@/utils/formatter'
import { statusType } from '@/utils/status'

const caseStatuses = ['DRAFT', 'SUBMITTED', 'FILING_REVIEW', 'ACCEPTED', 'EVIDENCE_SUBMITTING', 'SCHEDULED', 'IN_TRIAL', 'JUDGED', 'EFFECTIVE', 'CLOSED']
const caseActions = ['SUBMIT', 'START_REVIEW', 'ACCEPT', 'REJECT', 'OPEN_EVIDENCE', 'SCHEDULE_TRIAL', 'START_TRIAL', 'JUDGEMENT_PENDING', 'PUBLISH_JUDGEMENT', 'CONFIRM_EFFECTIVE', 'CLOSE']

const loading = ref(false)
const cases = ref<CaseItem[]>([])
const detail = ref<CaseDetail>()
const detailVisible = ref(false)
const createVisible = ref(false)
const transitionVisible = ref(false)
const currentCaseId = ref<number>()
const query = reactive({ keyword: '', caseNo: '', status: '' })
const pagination = reactive({ pageNo: 1, pageSize: 10, total: 0 })
const caseForm = reactive({
  caseTitle: '',
  caseType: '民事',
  causeAction: '',
  procedureType: '一审',
  courtName: '',
  claimRequest: '',
  parties: [{ partyType: 'PLAINTIFF', partyName: '原告', identityType: 'OTHER', identityNo: 'N/A' }]
})
const transitionForm = reactive({ action: '', comment: '' })

onMounted(loadCases)

async function loadCases() {
  loading.value = true
  try {
    const page = await getCaseList({ ...query, pageNo: pagination.pageNo, pageSize: pagination.pageSize })
    cases.value = page.records || []
    pagination.total = page.total || 0
  } finally {
    loading.value = false
  }
}

async function submitCase() {
  await createCase(caseForm)
  ElMessage.success('案件已创建')
  createVisible.value = false
  loadCases()
}

async function openDetail(id: number) {
  detail.value = await getCaseDetail(id)
  detailVisible.value = true
}

function openTransition(row: CaseItem) {
  currentCaseId.value = row.id
  transitionForm.action = ''
  transitionForm.comment = ''
  transitionVisible.value = true
}

async function submitTransition() {
  if (!currentCaseId.value) return
  await transitionCase(currentCaseId.value, transitionForm)
  ElMessage.success('状态已流转')
  transitionVisible.value = false
  loadCases()
}
</script>
