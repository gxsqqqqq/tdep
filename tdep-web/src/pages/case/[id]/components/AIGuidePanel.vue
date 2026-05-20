<template>
  <div class="ai-guide-panel relative rounded-2xl overflow-hidden">
    <div class="absolute inset-0 bg-gradient-to-br from-purple-50 via-white to-blue-50" />
    <div class="absolute inset-[1px] rounded-2xl bg-white/80 backdrop-blur-sm" />

    <div class="relative p-6 space-y-5">
      <!-- Header -->
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-violet-500 to-purple-600 flex items-center justify-center shadow-lg shadow-purple-200">
          <el-icon :size="22" color="#fff"><ChatDotRound /></el-icon>
        </div>
        <div class="flex-1">
          <h3 class="text-base font-bold text-gray-800">AI 案件协作助手</h3>
          <div class="flex items-center gap-1.5 mt-0.5">
            <span class="relative flex h-2 w-2">
              <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75" />
              <span class="relative inline-flex rounded-full h-2 w-2 bg-green-500" />
            </span>
            <span class="text-xs text-gray-500">在线</span>
          </div>
        </div>
        <el-button type="primary" size="small" round @click="handleAskAI">
          提问
        </el-button>
      </div>

      <!-- 1️⃣ 案件智能摘要 -->
      <div v-if="showAIFeatures" class="summary-section">
        <div class="section-header">
          <el-icon :size="16" color="#8b5cf6"><DataAnalysis /></el-icon>
          <span class="text-sm font-bold text-gray-800">智能摘要</span>
        </div>
        <div class="summary-card">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="案件类型">
              <el-tag size="small" type="info">{{ caseSummary.caseType }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="涉案金额">
              <span class="font-semibold text-red-600">{{ caseSummary.keyFacts[0]?.split(': ')[1] || '--' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="原告">
              <span class="text-blue-600">{{ caseSummary.parties.plaintiff }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="被告">
              <span class="text-orange-600">{{ caseSummary.parties.defendant }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="风险评估" :span="2">
              <el-alert
                :title="caseSummary.riskAssessment"
                :type="riskLevelType"
                :closable="false"
                show-icon
                class="!py-1"
              />
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <!-- 2️⃣ 争议焦点分析 -->
      <div v-if="showAIFeatures" class="disputed-section">
        <div class="section-header">
          <el-icon :size="16" color="#f59e0b"><Warning /></el-icon>
          <span class="text-sm font-bold text-gray-800">争议焦点</span>
          <el-badge :value="disputedIssues.length" :max="99" class="ml-2" />
        </div>
        <div class="space-y-3">
          <div
            v-for="(issue, index) in disputedIssues"
            :key="index"
            class="dispute-card"
          >
            <div class="dispute-header">
              <span class="dispute-title">{{ issue.issue }}</span>
              <el-tag
                :type="priorityTagType(issue.priority)"
                size="small"
                round
                effect="dark"
              >
                {{ priorityLabel(issue.priority) }}
              </el-tag>
            </div>
            <div class="dispute-content">
              <div class="position-row">
                <span class="position-label plaintiff">原告立场</span>
                <span class="position-text">{{ issue.plaintiffPosition }}</span>
              </div>
              <div class="position-row">
                <span class="position-label defendant">被告立场</span>
                <span class="position-text">{{ issue.defendantPosition }}</span>
              </div>
              <div v-if="issue.evidenceGap" class="evidence-gap">
                <el-icon :size="14" class="text-red-500"><WarningFilled /></el-icon>
                <span>缺少: {{ issue.evidenceGap }}</span>
              </div>
              <div v-if="issue.suggestion" class="suggestion">
                <el-icon :size="14" class="text-green-500"><CircleCheck /></el-icon>
                <span>{{ issue.suggestion }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 3️⃣ 缺失证据提示 -->
      <div v-if="showAIFeatures" class="missing-evidence-section">
        <div class="section-header">
          <el-icon :size="16" color="#ef4444"><DocumentDelete /></el-icon>
          <span class="text-sm font-bold text-gray-800">缺失证据</span>
          <el-badge :value="missingEvidences.length" :max="99" class="ml-2" type="danger" />
        </div>
        <div class="space-y-2">
          <div
            v-for="(evidence, index) in missingEvidences"
            :key="index"
            class="evidence-item"
            :class="{ 'urgency-high': evidence.urgency === 'high', 'urgency-medium': evidence.urgency === 'medium' }"
          >
            <div class="evidence-left">
              <span class="urgency-dot" :class="`dot-${evidence.urgency}`" />
              <div class="evidence-info">
                <span class="evidence-name">{{ evidence.name }}</span>
                <span class="evidence-reason">{{ evidence.reason }}</span>
              </div>
            </div>
            <el-button
              size="small"
              type="primary"
              link
              @click="handleUploadEvidence(evidence)"
            >
              上传 →
            </el-button>
          </div>
        </div>
      </div>

      <!-- 4️⃣ 下一步建议 (增强版) -->
      <div v-if="showAIFeatures" class="next-steps-section">
        <div class="section-header">
          <el-icon :size="16" color="#3b82f6"><List /></el-icon>
          <span class="text-sm font-bold text-gray-800">下一步计划</span>
        </div>
        <div class="space-y-2">
          <div
            v-for="(step, index) in nextSteps"
            :key="index"
            class="step-item"
          >
            <div class="step-left">
              <span
                class="step-number"
                :class="{ 'step-completed': step.status === 'completed', 'step-scheduled': step.status === 'scheduled' }"
              >
                {{ step.step }}
              </span>
              <div class="step-info">
                <span class="step-action">{{ step.action }}</span>
                <div class="step-meta">
                  <span class="step-date">
                    <el-icon :size="12"><Calendar /></el-icon>
                    截止: {{ step.targetDate }}
                  </span>
                  <el-tag size="small" :type="stepStatusType(step.status)" round effect="plain">
                    {{ stepStatusLabel(step.status) }}
                  </el-tag>
                  <span class="step-assignee">{{ step.assignee }}</span>
                </div>
              </div>
            </div>
            <span v-if="getCountdown(step.targetDate) !== null" class="countdown" :class="countdownClass(getCountdown(step.targetDate)!)">
              {{ getCountdownLabel(getCountdown(step.targetDate)!) }}
            </span>
          </div>
        </div>
      </div>

      <!-- 原有 Guide 内容 (向下兼容) -->
      <div v-if="!showAIFeatures">
        <div class="pt-2">
          <h4 class="text-lg font-bold text-gray-900">{{ guideContent.title }}</h4>
        </div>

        <div class="flex items-center gap-2 mt-3">
          <span
            class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium"
            :class="urgencyClasses"
          >
            <span v-if="guideContent.urgency === 'high'" class="relative flex h-1.5 w-1.5">
              <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75" />
              <span class="relative inline-flex rounded-full h-1.5 w-1.5 bg-red-500" />
            </span>
            {{ urgencyLabel }}
          </span>
        </div>

        <ul class="space-y-2.5 mt-4">
          <li
            v-for="(step, index) in guideContent.steps"
            :key="index"
            class="flex items-start gap-3 group"
          >
            <span
              class="flex-shrink-0 w-6 h-6 rounded-full flex items-center justify-center text-xs font-semibold transition-colors"
              :class="index === 0 ? 'bg-gradient-to-br from-violet-500 to-purple-600 text-white shadow-md' : 'bg-gray-100 text-gray-500 group-hover:bg-gray-200'"
            >
              <svg v-if="index === 0" xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
              </svg>
              <span v-else>{{ index + 1 }}</span>
            </span>
            <span class="text-sm leading-relaxed pt-0.5" :class="index === 0 ? 'text-gray-900 font-medium' : 'text-gray-600'">
              {{ step }}
            </span>
          </li>
        </ul>

        <div class="flex items-start gap-2.5 p-3.5 rounded-xl bg-blue-50/80 border border-blue-100 mt-4">
          <el-icon :size="16" class="flex-shrink-0 mt-0.5 text-blue-500"><InfoFilled /></el-icon>
          <p class="text-sm text-blue-700 leading-relaxed">{{ guideContent.tip }}</p>
        </div>
      </div>

      <!-- 5️⃣ AI 对话入口 -->
      <div class="ai-chat-entry">
        <div class="quick-questions">
          <span class="text-xs text-gray-500 mb-2 block">快捷提问：</span>
          <div class="flex flex-wrap gap-2">
            <el-button
              v-for="(question, index) in quickQuestions"
              :key="index"
              size="small"
              round
              @click="handleQuickQuestion(question)"
            >
              {{ question }}
            </el-button>
          </div>
        </div>
        <button
          class="w-full inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-violet-500 to-purple-600 text-white text-sm font-medium shadow-lg shadow-purple-200 hover:shadow-xl hover:shadow-purple-300 hover:from-violet-600 hover:to-purple-700 transition-all duration-200 active:scale-[0.98] mt-3"
          @click="handleAskAI"
        >
          <el-icon><ChatDotRound /></el-icon>
          向 AI 提问
        </button>
      </div>

      <!-- Current Status Badge -->
      <div class="pt-3 border-t border-gray-100">
        <div class="flex items-center justify-between">
          <span class="text-xs text-gray-500">当前阶段</span>
          <span
            class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs font-medium"
            :class="[currentStateDefinition.bgColor, currentStateDefinition.color]"
          >
            {{ currentStateDefinition.label }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import {
  ChatDotRound,
  InfoFilled,
  ArrowRight,
  DataAnalysis,
  Warning,
  WarningFilled,
  CircleCheck,
  DocumentDelete,
  List,
  Calendar,
} from '@element-plus/icons-vue'
import { useCaseWorkflowStore } from '@/lib/stores/caseWorkflow'
import { useDocumentWorkflowStore } from '@/lib/stores/documentWorkflow'
import { CaseStatus } from '@/lib/constants/caseLifecycle'
import { ElMessage, ElMessageBox } from 'element-plus'

interface GuideContent {
  title: string
  steps: string[]
  tip: string
  urgency: 'high' | 'normal' | 'waiting' | 'completed'
}

interface DisputedIssue {
  issue: string
  plaintiffPosition: string
  defendantPosition: string
  evidenceGap?: string
  suggestion?: string
  priority: 'high' | 'medium' | 'low'
}

interface MissingEvidence {
  name: string
  urgency: 'high' | 'medium' | 'low'
  reason: string
}

interface NextStep {
  step: number
  action: string
  targetDate: string
  assignee: string
  status: 'pending' | 'scheduled' | 'completed'
}

const caseStore = useCaseWorkflowStore()
const documentStore = useDocumentWorkflowStore()

const currentStatus = computed(() => caseStore.currentStatus)
const currentStateDefinition = computed(() => caseStore.currentStateDefinition)

const showAIFeatures = ref(true)

const caseSummary = ref({
  caseType: '合同纠纷',
  parties: {
    plaintiff: '杭州某某科技有限公司',
    defendant: '上海某某贸易有限公司',
  },
  keyFacts: [
    '涉案金额: ¥1,250,000',
    '合同签订日期: 2023-03-15',
    '争议焦点: 货物质量不符合约定',
  ],
  riskAssessment: '中等风险 - 建议尽快收集补充证据',
})

const disputedIssues = ref<DisputedIssue[]>([
  {
    issue: '货物质量问题',
    plaintiffPosition: '被告交付的产品存在严重质量缺陷，不符合合同约定的技术标准',
    defendantPosition: '产品符合合同约定的质量标准，已通过出厂检验',
    evidenceGap: '双方均未提供权威质检报告',
    suggestion: '建议申请司法鉴定，由专业机构进行质量检测',
    priority: 'high',
  },
  {
    issue: '违约责任认定',
    plaintiffPosition: '被告延迟交货构成根本违约，应承担全部违约责任',
    defendantPosition: '延迟系因不可抗力所致，已及时通知原告并采取措施减少损失',
    evidenceGap: '缺少不可抗力的充分证明材料',
    suggestion: '收集相关气象、政策等不可抗力证明文件',
    priority: 'high',
  },
  {
    issue: '损失赔偿金额',
    plaintiffPosition: '因被告违约造成的直接损失和间接损失共计 ¥1,250,000',
    defendantPosition: '原告主张的损失金额过高，部分损失与违约行为无因果关系',
    suggestion: '准备详细的损失计算依据和相关凭证',
    priority: 'medium',
  },
])

const missingEvidences = ref<MissingEvidence[]>([
  { name: '产品质量检验报告', urgency: 'high', reason: '判定责任的关键证据' },
  { name: '合同履行沟通记录', urgency: 'medium', reason: '证明违约事实的重要材料' },
  { name: '损失证明材料', urgency: 'medium', reason: '支持赔偿请求的依据' },
  { name: '不可抗力证明文件', urgency: 'low', reason: '核实免责事由' },
])

const nextSteps = ref<NextStep[]>([
  { step: 1, action: '补充质检报告', targetDate: '2024-01-22', assignee: '原告', status: 'pending' },
  { step: 2, action: '准备庭审陈述', targetDate: '2024-01-25', assignee: '律师', status: 'pending' },
  { step: 3, action: '参加庭前会议', targetDate: '2024-01-28', assignee: '双方', status: 'scheduled' },
])

const quickQuestions = [
  '这个案件的胜算有多大？',
  '我还需要准备哪些材料？',
  '庭审时我应该注意什么？',
  '帮我草拟一份陈述纲要',
]

const guideMap: Record<string, GuideContent> = {
  [CaseStatus.DRAFT]: {
    title: '✍️ 完善案件信息',
    steps: ['填写案件基本信息', '添加当事人信息', '上传相关材料', '提交审核'],
    tip: '草稿阶段可以随时保存，确认无误后提交审核',
    urgency: 'normal',
  },
  [CaseStatus.PENDING_REVIEW]: {
    title: '⏳ 等待法官审核',
    steps: ['法官正在审核您的案件', '通常 1-3 个工作日内完成', '审核通过后自动进入下一阶段', '请留意系统通知'],
    tip: '如有补充材料需求，法官会主动联系您',
    urgency: 'waiting',
  },
  [CaseStatus.ACCEPTED]: {
    title: '✅ 案件已受理',
    steps: ['案件已正式立案成功', '等待法官分配举证任务', '准备相关证据材料', '关注后续举证通知'],
    tip: '已受理意味着您的案件符合立案条件，接下来将进入证据收集阶段',
    urgency: 'normal',
  },
  [CaseStatus.EVIDENCE_CLOSING]: {
    title: '📁 提交证据材料',
    steps: ['上传合同原件扫描件', '提交交易记录证明', '提供沟通记录截图', '确保证据链完整'],
    tip: '建议在截止日期前 2 天完成举证，留出补正时间',
    urgency: 'high',
  },
  [CaseStatus.TRIAL_SCHEDULED]: {
    title: '📅 庭审已排期',
    steps: ['确认庭审时间和地点', '准备庭审陈述材料', '整理证据原件备查', '提前到达法院等候'],
    tip: '请至少提前 30 分钟到达法庭，携带身份证件及所有证据原件',
    urgency: 'high',
  },
  [CaseStatus.IN_TRIAL]: {
    title: '⚖️ 庭审进行中',
    steps: ['庭审正在有序进行', '配合法官询问如实回答', '按程序进行质证辩论', '等待庭审结束'],
    tip: '保持冷静，按照法官指引参与庭审流程，如有疑问可当庭提出',
    urgency: 'high',
  },
  [CaseStatus.JUDGED]: {
    title: '📜 已作出判决',
    steps: ['法院已完成判决书撰写', '等待判决书送达', '仔细阅读判决内容', '决定是否上诉'],
    tip: '收到判决书之日起 15 日内可提起上诉，逾期判决生效',
    urgency: 'normal',
  },
  [CaseStatus.CLOSED]: {
    title: '🎉 案件已结案',
    steps: ['案件已正式结案归档', '所有法律文书已送达', '执行程序可能启动', '可申请查阅案卷'],
    tip: '结案后如需查询案件材料，可向法院档案室提出申请',
    urgency: 'completed',
  },
  [CaseStatus.REJECTED]: {
    title: '❌ 案件已驳回',
    steps: ['查看驳回原因说明', '补充缺失材料或信息', '可重新提交立案申请', '或向上级法院申诉'],
    tip: '被驳回不代表无法重新申请，根据驳回原因补正后可再次提交',
    urgency: 'high',
  },
}

const guideContent = computed<GuideContent>(() => {
  return guideMap[currentStatus.value] || guideMap[CaseStatus.DRAFT]
})

const urgencyClasses = computed(() => {
  const map = {
    high: 'bg-red-50 text-red-700 border border-red-200',
    normal: 'bg-yellow-50 text-yellow-700 border border-yellow-200',
    waiting: 'bg-gray-50 text-gray-600 border border-gray-200',
    completed: 'bg-green-50 text-green-700 border border-green-200',
  }
  return map[guideContent.value.urgency]
})

const urgencyLabel = computed(() => {
  const map = {
    high: '需要关注',
    normal: '正常推进',
    waiting: '等待处理',
    completed: '已完成',
  }
  return map[guideContent.value.urgency]
})

const riskLevelType = computed<'success' | 'warning' | 'error' | 'info'>(() => {
  const assessment = caseSummary.value.riskAssessment.toLowerCase()
  if (assessment.includes('高')) return 'error'
  if (assessment.includes('中')) return 'warning'
  if (assessment.includes('低')) return 'success'
  return 'info'
})

function priorityTagType(priority: string): 'danger' | 'warning' | 'info' {
  const map: Record<string, 'danger' | 'warning' | 'info'> = {
    high: 'danger',
    medium: 'warning',
    low: 'info',
  }
  return map[priority] || 'info'
}

function priorityLabel(priority: string): string {
  const map: Record<string, string> = {
    high: '高优先',
    medium: '中优先',
    low: '低优先',
  }
  return map[priority] || priority
}

function stepStatusType(status: string): 'primary' | 'warning' | 'success' | 'info' {
  const map: Record<string, 'primary' | 'warning' | 'success' | 'info'> = {
    pending: 'warning',
    scheduled: 'primary',
    completed: 'success',
  }
  return map[status] || 'info'
}

function stepStatusLabel(status: string): string {
  const map: Record<string, string> = {
    pending: '待办',
    scheduled: '已安排',
    completed: '已完成',
  }
  return map[status] || status
}

function getCountdown(targetDate: string): number | null {
  const target = new Date(targetDate)
  const now = new Date()
  const diff = target.getTime() - now.getTime()
  const days = Math.ceil(diff / (1000 * 60 * 60 * 24))
  return days >= 0 ? days : null
}

function getCountdownLabel(days: number): string {
  if (days === 0) return '今天'
  if (days === 1) return '明天'
  return `${days}天后`
}

function countdownClass(days: number): string {
  if (days <= 2) return 'countdown-urgent'
  if (days <= 5) return 'countdown-warning'
  return 'countdown-normal'
}

function handleAskAI(): void {
  // TODO: 对接真实 AI 服务接口
  // 预留接口：POST /api/ai/chat
  // 请求体：{ message: string, context: { caseId, documentId, ... } }
  // 响应：{ reply: string, suggestions: string[] }

  ElMessageBox.alert(
    '<div style="text-align: left;">' +
    '<p>🚀 AI 对话功能即将上线</p>' +
    '<p style="color: #666; margin-top: 8px;">当前为演示模式，后续将对接以下 AI 服务：</p>' +
    '<ul style="margin-top: 8px; padding-left: 20px; color: #666;">' +
    '<li>• 智能法律问答</li>' +
    '<li>• 案情分析报告生成</li>' +
    '<li>• 文书自动草拟</li>' +
    '<li>• 证据链完整性检测</li>' +
    '</ul></div>',
    'AI 助手',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '我知道了',
    }
  )
}

function handleQuickQuestion(question: string): void {
  // TODO: 将快捷问题发送给 AI 服务
  console.log('快捷问题:', question)
  handleAskAI()
}

function handleUploadEvidence(evidence: MissingEvidence): void {
  // TODO: 跳转到证据上传面板
  ElMessage.info(`正在跳转到上传页面: ${evidence.name}`)
}
</script>

<style scoped>
.ai-guide-panel {
  box-shadow:
    0 4px 6px -1px rgba(139, 92, 246, 0.08),
    0 10px 15px -3px rgba(139, 92, 246, 0.06),
    0 0 0 1px rgba(139, 92, 246, 0.08);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 6px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
  margin-bottom: 12px;
}

.summary-section .summary-card {
  background: linear-gradient(135deg, #faf5ff, #f0f9ff);
  border-radius: 12px;
  padding: 12px;
  border: 1px solid #e9d5ff;
}

.disputed-section .dispute-card {
  background: #fff;
  border-radius: 10px;
  padding: 12px;
  border: 1px solid #f0f0f0;
  transition: all 0.25s ease;
}

.disputed-section .dispute-card:hover {
  border-color: #fbbf24;
  box-shadow: 0 2px 8px rgba(251, 191, 36, 0.15);
}

.dispute-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.dispute-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

.dispute-content {
  space-y: 6px;
}

.position-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 12px;
}

.position-label {
  flex-shrink: 0;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
  font-size: 11px;
}

.position-label.plaintiff {
  background: #eff6ff;
  color: #2563eb;
}

.position-label.defendant {
  background: #fff7ed;
  color: #ea580c;
}

.position-text {
  color: #606266;
  line-height: 1.5;
}

.evidence-gap {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  background: #fef2f2;
  border-radius: 6px;
  font-size: 11px;
  color: #dc2626;
  margin-top: 6px;
}

.suggestion {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  background: #f0fdf4;
  border-radius: 6px;
  font-size: 11px;
  color: #16a34a;
  margin-top: 6px;
}

.missing-evidence-section .evidence-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: all 0.2s ease;
}

.evidence-item:hover {
  border-color: #d1d5db;
  background: #fafafa;
}

.evidence-item.urgency-high {
  border-left: 3px solid #ef4444;
}

.evidence-item.urgency-medium {
  border-left: 3px solid #f59e0b;
}

.evidence-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.urgency-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.dot-high {
  background: #ef4444;
  animation: pulse-dot 2s ease-in-out infinite;
}

.dot-medium {
  background: #f59e0b;
}

.dot-low {
  background: #9ca3af;
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.2); }
}

.evidence-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.evidence-name {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}

.evidence-reason {
  font-size: 11px;
  color: #909399;
  margin-top: 2px;
}

.next-steps-section .step-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: all 0.2s ease;
}

.step-item:hover {
  border-color: #bfdbfe;
  background: #eff6ff;
}

.step-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.step-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #f3f4f6;
  color: #6b7280;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.step-number.step-completed {
  background: #10b981;
  color: white;
}

.step-number.step-scheduled {
  background: #3b82f6;
  color: white;
}

.step-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.step-action {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}

.step-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 3px;
  font-size: 11px;
  color: #909399;
}

.step-date {
  display: flex;
  align-items: center;
  gap: 3px;
}

.step-assignee {
  padding: 1px 6px;
  background: #f3f4f6;
  border-radius: 4px;
  font-size: 10px;
}

.countdown {
  font-size: 11px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 10px;
  flex-shrink: 0;
}

.countdown-urgent {
  background: #fef2f2;
  color: #dc2626;
}

.countdown-warning {
  background: #fffbeb;
  color: #d97706;
}

.countdown-normal {
  background: #f0fdf4;
  color: #16a34a;
}

.ai-chat-entry {
  margin-top: 4px;
}

.quick-questions {
  padding: 12px;
  background: #fafafa;
  border-radius: 10px;
  border: 1px dashed #e5e7eb;
}

@media (max-width: 640px) {
  .dispute-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }

  .step-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .evidence-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
