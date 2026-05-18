<template>
  <div class="card" style="padding: 24px">
    <h3 style="margin-top: 0">立案信息</h3>

    <el-descriptions v-if="detail" :column="2" border>
      <el-descriptions-item label="案件号">{{ detail.caseNo }}</el-descriptions-item>
      <el-descriptions-item label="案件标题">{{ detail.caseTitle }}</el-descriptions-item>
      <el-descriptions-item label="案件类型">{{ detail.caseType }}</el-descriptions-item>
      <el-descriptions-item label="程序类型">{{ detail.procedureType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="案由">{{ detail.causeAction }}</el-descriptions-item>
      <el-descriptions-item label="受理法院">{{ detail.courtName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="标的金额">{{ detail.claimAmount ? `${detail.claimAmount} 元` : '-' }}</el-descriptions-item>
      <el-descriptions-item label="案件状态">
        <el-tag :type="statusType(detail.status)">{{ getCaseStatusName(detail.status) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="诉讼请求" :span="2">{{ detail.claimRequest || '-' }}</el-descriptions-item>
    </el-descriptions>

    <h3 style="margin: 24px 0 12px">当事人信息</h3>
    <div v-if="detail?.parties && detail.parties.length > 0">
      <div v-for="(party, index) in detail.parties" :key="index" style="padding: 12px; border: 1px solid var(--border-color); border-radius: 8px; margin-bottom: 8px">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="角色">{{ partyTypeName(String(party.partyType || '')) }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ party.partyName }}</el-descriptions-item>
          <el-descriptions-item label="证件类型">{{ party.identityType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="证件号">{{ party.identityNo || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
    <el-empty v-else description="暂无当事人信息" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

import { useCaseStore } from '@/stores/case'
import { getCaseStatusName, statusType } from '@/utils/status'

const caseStore = useCaseStore()
const detail = computed(() => caseStore.currentCase)

function partyTypeName(type?: string) {
  const map: Record<string, string> = { PLAINTIFF: '原告', DEFENDANT: '被告', THIRD_PARTY: '第三人' }
  return map[type || ''] || type || '-'
}
</script>
