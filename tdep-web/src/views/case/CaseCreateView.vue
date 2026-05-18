<template>
  <div class="case-create">
    <!-- Back -->
    <button class="back-btn" @click="$router.back()">← 返回</button>

    <h1 class="page-title">创建新案件</h1>

    <!-- Step indicator -->
    <div class="step-bar">
      <div
        v-for="(step, idx) in stepLabels"
        :key="idx"
        class="step-item"
        :class="{
          'step-item--done': idx < currentStep,
          'step-item--active': idx === currentStep
        }"
        @click="goToStep(idx)"
      >
        <div class="step-item__dot">
          <span v-if="idx < currentStep">✓</span>
          <span v-else>{{ idx + 1 }}</span>
        </div>
        <span class="step-item__label">{{ step }}</span>
      </div>
    </div>

    <!-- Form Guide (Agent) -->
    <FormGuide
      v-if="currentStep < 3"
      :fields="currentStepFields"
      :form-data="formDataForGuide"
    />

    <!-- Agent field hints -->
    <InlineHint
      v-if="fieldHint"
      type="hint"
      :title="fieldHint.title"
      :content="fieldHint.content"
    />

    <!-- Step 0: Basic Info -->
    <div v-if="currentStep === 0" class="form-card">
      <div class="form-group">
        <label class="form-label required">案件标题</label>
        <input
          v-model="form.caseTitle"
          class="form-input"
          placeholder="例：张三诉李四借款合同纠纷"
        />
        <div v-if="form.caseTitle" class="form-hint form-hint--success">✓ 已填写</div>
        <div v-else class="form-hint">建议包含原被告姓名和纠纷类型</div>
      </div>

      <div class="form-group">
        <label class="form-label required">案件类型</label>
        <select v-model="form.caseType" class="form-select">
          <option value="民事">民事</option>
          <option value="刑事">刑事</option>
          <option value="行政">行政</option>
        </select>
        <div class="form-hint form-hint--success">✓ 已选择</div>
      </div>

      <div class="form-group">
        <label class="form-label">程序类型</label>
        <select v-model="form.procedureType" class="form-select">
          <option value="一审">一审</option>
          <option value="二审">二审</option>
          <option value="再审">再审</option>
        </select>
      </div>

      <div class="form-group">
        <label class="form-label required">案由</label>
        <input
          v-model="form.causeAction"
          class="form-input"
          placeholder="例：借款合同纠纷"
        />
        <div v-if="form.causeAction" class="form-hint form-hint--success">✓ 已填写</div>
        <div v-else class="form-hint">
          常见案由：
          <button class="hint-link" @click="form.causeAction = '借款合同纠纷'">借款合同纠纷</button>、
          <button class="hint-link" @click="form.causeAction = '买卖合同纠纷'">买卖合同纠纷</button>、
          <button class="hint-link" @click="form.causeAction = '劳动争议'">劳动争议</button>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">受理法院</label>
        <input
          v-model="form.courtName"
          class="form-input"
          placeholder="例：XX 市中级人民法院"
        />
      </div>

      <div class="form-group">
        <label class="form-label">标的金额 (元)</label>
        <input
          v-model.number="form.claimAmount"
          class="form-input"
          type="number"
          min="0"
          placeholder="请输入金额"
        />
      </div>
    </div>

    <!-- Step 1: Parties -->
    <div v-if="currentStep === 1" class="form-card">
      <div
        v-for="(party, index) in form.parties"
        :key="index"
        class="party-card"
      >
        <div class="party-card__header">
          <span class="party-card__title">
            {{ party.partyType === 'PLAINTIFF' ? '👤 原告' : party.partyType === 'DEFENDANT' ? '👤 被告' : '👤 第三人' }}
          </span>
          <button
            v-if="form.parties.length > 1"
            class="party-card__delete"
            @click="form.parties.splice(index, 1)"
          >删除</button>
        </div>

        <div class="form-group">
          <label class="form-label">角色</label>
          <select v-model="party.partyType" class="form-select">
            <option value="PLAINTIFF">原告</option>
            <option value="DEFENDANT">被告</option>
            <option value="THIRD_PARTY">第三人</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label required">姓名/名称</label>
          <input
            v-model="party.partyName"
            class="form-input"
            placeholder="姓名或单位名称"
          />
        </div>

        <div class="form-row">
          <div class="form-group" style="flex: 1">
            <label class="form-label">证件类型</label>
            <select v-model="party.identityType" class="form-select">
              <option value="ID_CARD">身份证</option>
              <option value="BUSINESS_LICENSE">营业执照</option>
              <option value="OTHER">其他</option>
            </select>
          </div>
          <div class="form-group" style="flex: 2">
            <label class="form-label">证件号</label>
            <input
              v-model="party.identityNo"
              class="form-input"
              placeholder="证件号码"
            />
          </div>
        </div>
      </div>

      <button class="btn-add-party" @click="addParty">+ 添加当事人</button>

      <InlineHint
        v-if="form.parties.length < 2"
        type="hint"
        title="建议添加被告信息"
        content="一个完整的案件通常需要包含原告和被告双方信息。"
      />
    </div>

    <!-- Step 2: Claim Request -->
    <div v-if="currentStep === 2" class="form-card">
      <div class="form-group">
        <label class="form-label">诉讼请求</label>
        <textarea
          v-model="form.claimRequest"
          class="form-textarea"
          rows="6"
          placeholder="请详细描述您的诉讼请求...

例如：
1. 请求判令被告偿还借款本金人民币 50,000 元；
2. 请求判令被告支付逾期利息；
3. 本案诉讼费用由被告承担。"
        />
        <div class="form-hint">
          请清晰、具体地列出您的诉求，法院将据此审理案件。
        </div>
      </div>
    </div>

    <!-- Step 3: Confirm -->
    <div v-if="currentStep === 3" class="form-card">
      <h3 class="confirm-title">请确认案件信息</h3>
      <InlineHint
        type="info"
        title="提交前请仔细核对"
        content="信息提交后将自动递交法院审核，请确保所有信息准确无误。"
      />

      <div class="confirm-section">
        <div class="confirm-item">
          <span class="confirm-label">案件标题</span>
          <span class="confirm-value">{{ form.caseTitle }}</span>
        </div>
        <div class="confirm-item">
          <span class="confirm-label">案件类型</span>
          <span class="confirm-value">{{ form.caseType }}</span>
        </div>
        <div class="confirm-item">
          <span class="confirm-label">程序类型</span>
          <span class="confirm-value">{{ form.procedureType }}</span>
        </div>
        <div class="confirm-item">
          <span class="confirm-label">案由</span>
          <span class="confirm-value">{{ form.causeAction }}</span>
        </div>
        <div class="confirm-item">
          <span class="confirm-label">受理法院</span>
          <span class="confirm-value">{{ form.courtName || '-' }}</span>
        </div>
        <div class="confirm-item">
          <span class="confirm-label">标的金额</span>
          <span class="confirm-value">{{ form.claimAmount ? `${form.claimAmount} 元` : '-' }}</span>
        </div>
        <div class="confirm-item">
          <span class="confirm-label">当事人</span>
          <span class="confirm-value">
            <div v-for="(p, i) in form.parties" :key="i">
              {{ p.partyType === 'PLAINTIFF' ? '原告' : p.partyType === 'DEFENDANT' ? '被告' : '第三人' }}: {{ p.partyName }}
            </div>
          </span>
        </div>
        <div class="confirm-item">
          <span class="confirm-label">诉讼请求</span>
          <span class="confirm-value">{{ form.claimRequest || '-' }}</span>
        </div>
      </div>
    </div>

    <!-- Bottom Actions -->
    <div class="form-actions">
      <button v-if="currentStep > 0" class="btn-secondary" @click="currentStep--">
        上一步
      </button>
      <div v-else />

      <button
        v-if="currentStep < 3"
        class="btn-primary"
        :disabled="!canProceed"
        @click="nextStep"
      >
        下一步 →
      </button>
      <button
        v-else
        class="btn-primary btn-primary--submit"
        :disabled="submitting"
        @click="submitCase"
      >
        {{ submitting ? '提交中...' : '确认提交立案申请' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { reactive, ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'

import FormGuide from '@/components/FormGuide/index.vue'
import InlineHint from '@/components/InlineHint/index.vue'
import { useCaseStore } from '@/stores/case'
import { useAgentStore } from '@/stores/agent'

const router = useRouter()
const caseStore = useCaseStore()
const agentStore = useAgentStore()
const currentStep = ref(0)
const submitting = ref(false)

const stepLabels = ['基本信息', '当事人', '诉讼请求', '确认提交']

const form = reactive({
  caseTitle: '',
  caseType: '民事',
  causeAction: '',
  procedureType: '一审',
  courtName: '',
  claimAmount: undefined as number | undefined,
  claimRequest: '',
  parties: [
    { partyType: 'PLAINTIFF', partyName: '', identityType: 'ID_CARD', identityNo: '' }
  ]
})

// ── Form Guide fields per step ──

const step0Fields = [
  { key: 'caseTitle', label: '案件标题', required: true },
  { key: 'caseType', label: '案件类型', required: true },
  { key: 'causeAction', label: '案由', required: true },
  { key: 'procedureType', label: '程序类型', required: false },
]

const step1Fields = [
  { key: 'hasPlaintiff', label: '原告信息', required: true },
  { key: 'hasDefendant', label: '被告信息', required: true },
]

const step2Fields = [
  { key: 'claimRequest', label: '诉讼请求', required: false },
]

const currentStepFields = computed(() => {
  if (currentStep.value === 0) return step0Fields
  if (currentStep.value === 1) return step1Fields
  return step2Fields
})

const formDataForGuide = computed(() => {
  if (currentStep.value === 0) {
    return {
      caseTitle: form.caseTitle,
      caseType: form.caseType,
      causeAction: form.causeAction,
      procedureType: form.procedureType,
    }
  }
  if (currentStep.value === 1) {
    return {
      hasPlaintiff: form.parties.some((p) => p.partyType === 'PLAINTIFF' && p.partyName) ? 'yes' : '',
      hasDefendant: form.parties.some((p) => p.partyType === 'DEFENDANT' && p.partyName) ? 'yes' : '',
    }
  }
  return { claimRequest: form.claimRequest }
})

// ── Field hints (Agent) ──

const fieldHint = computed(() => {
  if (currentStep.value === 0 && !form.caseTitle && !form.causeAction) {
    return {
      title: '填写引导',
      content: '请先填写案件标题和案由，这两项是必填信息。标题建议格式："原告姓名诉被告姓名+纠纷类型"。'
    }
  }
  if (currentStep.value === 1 && !form.parties.some((p) => p.partyType === 'DEFENDANT' && p.partyName)) {
    return {
      title: '缺少被告信息',
      content: '建议添加至少一名被告的姓名和身份信息，以便法院受理。'
    }
  }
  return null
})

// ── Can proceed ──

const canProceed = computed(() => {
  if (currentStep.value === 0) {
    return form.caseTitle.trim() !== '' && form.causeAction.trim() !== ''
  }
  if (currentStep.value === 1) {
    return form.parties.some((p) => p.partyName.trim() !== '')
  }
  return true
})

// ── Actions ──

function addParty() {
  form.parties.push({ partyType: 'DEFENDANT', partyName: '', identityType: 'ID_CARD', identityNo: '' })
}

function goToStep(idx: number) {
  // Only allow going back to completed steps
  if (idx < currentStep.value) {
    currentStep.value = idx
  }
}

function nextStep() {
  if (!canProceed.value) return
  currentStep.value++
}

async function submitCase() {
  submitting.value = true
  try {
    await caseStore.createCase({
      caseTitle: form.caseTitle,
      caseType: form.caseType,
      causeAction: form.causeAction,
      procedureType: form.procedureType,
      courtName: form.courtName,
      claimAmount: form.claimAmount,
      claimRequest: form.claimRequest,
      parties: form.parties
    })
    ElMessage.success('立案申请已提交')
    agentStore.addMessage({
      type: 'success',
      title: '立案申请已提交',
      content: '您的立案申请已成功提交，法院将在 1-3 个工作日内完成审核。审核结果将通过消息通知您。',
      dismissible: true,
      priority: 10,
      source: 'system'
    })
    router.push('/my-cases')
  } catch {
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// ── Update agent context on form changes ──

watch(
  () => ({ ...form }),
  () => {
    const missing: string[] = []
    if (!form.caseTitle.trim()) missing.push('案件标题')
    if (!form.causeAction.trim()) missing.push('案由')
    if (!form.parties.some((p) => p.partyName.trim())) missing.push('当事人姓名')
    agentStore.updateFormContext(missing)
  },
  { deep: true, immediate: true }
)
</script>

<style scoped>
.case-create {
  max-width: 700px;
  margin: 0 auto;
  padding-bottom: 40px;
}

.back-btn {
  background: none;
  border: none;
  color: #5f6368;
  font-size: 14px;
  cursor: pointer;
  padding: 8px 0;
  margin-bottom: 8px;
}

.back-btn:hover { color: #1a73e8; }

.page-title {
  margin: 0 0 24px;
  font-size: 22px;
  font-weight: 700;
  color: #202124;
}

/* Step bar */
.step-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
  padding: 0 8px;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  flex: 1;
}

.step-item__dot {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  border: 2px solid #e0e4e8;
  background: white;
  color: #9aa0a6;
  transition: all 0.2s;
}

.step-item--done .step-item__dot {
  border-color: #34a853;
  background: #34a853;
  color: white;
}

.step-item--active .step-item__dot {
  border-color: #1a73e8;
  background: #1a73e8;
  color: white;
  box-shadow: 0 0 0 4px rgba(26, 115, 232, 0.15);
}

.step-item__label {
  font-size: 12px;
  color: #9aa0a6;
  font-weight: 500;
}

.step-item--active .step-item__label { color: #1a73e8; font-weight: 600; }
.step-item--done .step-item__label { color: #34a853; }

/* Form card */
.form-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e0e4e8;
  padding: 28px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

/* Form elements */
.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #202124;
  margin-bottom: 8px;
}

.form-label.required::after {
  content: ' *';
  color: #ea4335;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e0e4e8;
  border-radius: 8px;
  font-size: 14px;
  color: #202124;
  background: white;
  outline: none;
  transition: border-color 0.2s;
  font-family: inherit;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  border-color: #1a73e8;
  box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.1);
}

.form-textarea {
  resize: vertical;
  line-height: 1.6;
}

.form-hint {
  margin-top: 6px;
  font-size: 12px;
  color: #9aa0a6;
}

.form-hint--success {
  color: #34a853;
}

.hint-link {
  background: none;
  border: none;
  color: #1a73e8;
  cursor: pointer;
  font-size: 12px;
  padding: 0;
  text-decoration: underline;
}

.form-row {
  display: flex;
  gap: 16px;
}

/* Party card */
.party-card {
  padding: 20px;
  border: 1px solid #e0e4e8;
  border-radius: 12px;
  margin-bottom: 16px;
  background: #fafbfc;
}

.party-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.party-card__title {
  font-weight: 600;
  font-size: 15px;
  color: #202124;
}

.party-card__delete {
  background: none;
  border: none;
  color: #ea4335;
  font-size: 13px;
  cursor: pointer;
  padding: 4px 8px;
}

.party-card__delete:hover {
  background: #fce8e6;
  border-radius: 4px;
}

.btn-add-party {
  width: 100%;
  padding: 12px;
  border: 2px dashed #e0e4e8;
  border-radius: 12px;
  background: transparent;
  color: #5f6368;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-party:hover {
  border-color: #1a73e8;
  color: #1a73e8;
  background: #f0f7ff;
}

/* Confirm */
.confirm-title {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 700;
  color: #202124;
}

.confirm-section {
  border: 1px solid #e0e4e8;
  border-radius: 12px;
  overflow: hidden;
}

.confirm-item {
  display: flex;
  padding: 14px 20px;
  border-bottom: 1px solid #f1f3f4;
  font-size: 14px;
}

.confirm-item:last-child { border-bottom: none; }

.confirm-label {
  width: 100px;
  flex-shrink: 0;
  color: #5f6368;
  font-weight: 500;
}

.confirm-value {
  flex: 1;
  color: #202124;
}

/* Actions */
.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 24px;
}

.btn-primary {
  padding: 12px 32px;
  border-radius: 8px;
  border: none;
  background: #1a73e8;
  color: white;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background: #1557b0;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(26, 115, 232, 0.3);
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary--submit {
  padding: 14px 40px;
  font-size: 16px;
}

.btn-secondary {
  padding: 12px 24px;
  border-radius: 8px;
  border: 1px solid #e0e4e8;
  background: white;
  color: #5f6368;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  border-color: #1a73e8;
  color: #1a73e8;
}

/* Dark mode */
:root.dark .page-title { color: #e2e8f0; }
:root.dark .form-card { background: #1e293b; border-color: #334155; }
:root.dark .form-input,
:root.dark .form-select,
:root.dark .form-textarea {
  background: #0f172a;
  border-color: #334155;
  color: #e2e8f0;
}
:root.dark .form-label { color: #e2e8f0; }
:root.dark .party-card { background: #0f172a; border-color: #334155; }
:root.dark .party-card__title { color: #e2e8f0; }
:root.dark .confirm-section { border-color: #334155; }
:root.dark .confirm-item { border-color: #1e293b; }
:root.dark .confirm-value { color: #e2e8f0; }
:root.dark .back-btn { color: #94a3b8; }
:root.dark .step-item__dot { background: #1e293b; border-color: #334155; color: #64748b; }
</style>
