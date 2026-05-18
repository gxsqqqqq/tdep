<template>
  <div class="status-hero" :class="`status-hero--${statusCategory}`">
    <div class="status-hero__top">
      <div class="status-hero__case-no">{{ caseNo }}</div>
      <div class="status-hero__tag" :class="`status-hero__tag--${statusCategory}`">
        {{ statusName }}
      </div>
    </div>
    <h1 class="status-hero__title">{{ caseTitle }}</h1>
    <div class="status-hero__meta">
      <span v-if="courtName" class="status-hero__meta-item">🏛️ {{ courtName }}</span>
      <span v-if="judgeName" class="status-hero__meta-item">👨‍⚖️ {{ judgeName }}</span>
      <span v-if="createdAt" class="status-hero__meta-item">📅 {{ createdAt }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { caseStatusNames } from '@/utils/status'

const props = defineProps<{
  caseTitle: string
  caseNo?: string
  status: string
  courtName?: string
  judgeName?: string
  createdAt?: string
}>()

const statusName = computed(() => caseStatusNames[props.status] || props.status)

const statusCategory = computed(() => {
  if (['DRAFT', 'SUBMITTED', 'FILING_REVIEW'].includes(props.status)) return 'pending'
  if (['FILING_REJECTED'].includes(props.status)) return 'rejected'
  if (['ACCEPTED', 'EVIDENCE_SUBMITTING', 'SCHEDULED', 'IN_TRIAL'].includes(props.status)) return 'active'
  if (['JUDGED', 'EFFECTIVE', 'CLOSED'].includes(props.status)) return 'done'
  return 'pending'
})
</script>

<style scoped>
.status-hero {
  padding: 28px 32px;
  border-radius: 16px;
  background: linear-gradient(135deg, #1a73e8, #0d47a1);
  color: white;
  margin-bottom: 20px;
}

.status-hero--active  { background: linear-gradient(135deg, #1a73e8, #0d47a1); }
.status-hero--pending { background: linear-gradient(135deg, #5f6368, #3c4043); }
.status-hero--rejected { background: linear-gradient(135deg, #ea4335, #c5221f); }
.status-hero--done    { background: linear-gradient(135deg, #34a853, #1e8e3e); }

.status-hero__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.status-hero__case-no {
  font-size: 13px;
  opacity: 0.8;
  font-family: monospace;
}

.status-hero__tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(4px);
}

.status-hero__title {
  margin: 0 0 16px;
  font-size: 22px;
  font-weight: 700;
  line-height: 1.4;
}

.status-hero__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  opacity: 0.85;
}

.status-hero__meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style>
