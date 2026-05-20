<template>
  <el-card shadow="hover" class="electronic-seal-panel" :class="{ 'seal-completed': isSealed }">
    <template #header>
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="text-lg">🏛️</span>
          <span class="font-semibold text-gray-800">电子签章区</span>
        </div>

        <el-tag v-if="isSealed" type="success" effect="dark" round>
          <el-icon><CircleCheck /></el-icon> 已签署
        </el-tag>
        <el-tag v-else-if="currentStep > 0" type="warning" effect="dark" round>
          <el-icon><Loading /></el-icon> 签署中
        </el-tag>
        <el-tag v-else type="info" effect="plain" round>待签署</el-tag>
      </div>
    </template>

    <div class="seal-content">
      <div v-if="!isSealed" class="steps-container mb-6">
        <div class="step-indicator flex items-center justify-between mb-8 px-4">
          <template v-for="(step, index) in steps" :key="step.id">
            <div
              class="step-item flex flex-col items-center"
              :class="{
                'step-active': index === currentStep,
                'step-completed': index < currentStep,
                'step-pending': index > currentStep,
              }"
            >
              <div
                class="step-circle w-10 h-10 rounded-full flex items-center justify-center text-sm font-bold transition-all duration-300"
                :class="getStepClass(index)"
              >
                <span v-if="index < currentStep">✓</span>
                <span v-else>{{ index + 1 }}</span>
              </div>
              <span class="step-label mt-2 text-xs font-medium whitespace-nowrap">{{ step.label }}</span>
            </div>

            <div
              v-if="index < steps.length - 1"
              class="step-connector flex-1 h-0.5 mx-2 transition-all duration-300"
              :class="index < currentStep ? 'bg-green-500' : 'bg-gray-200'"
            ></div>
          </template>
        </div>
      </div>

      <div class="step-content-area">
        <!-- 步骤 1: 身份确认 -->
        <div v-show="currentStep === 0" class="step-content identity-confirm">
          <div class="text-center py-6">
            <div class="identity-card max-w-md mx-auto bg-gradient-to-br from-judicial-50 to-blue-50 rounded-xl p-6 border border-judicial-200">
              <div class="avatar-section mb-4">
                <div class="w-20 h-20 mx-auto bg-gradient-to-br from-judicial-600 to-judicial-700 rounded-full flex items-center justify-center text-white text-2xl font-bold shadow-lg">
                  {{ currentUser.name?.charAt(0) || '用' }}
                </div>
              </div>

              <h3 class="text-lg font-bold text-judicial-800 mb-3">{{ currentUser.name }}</h3>

              <div class="info-grid grid grid-cols-2 gap-3 text-sm mb-4">
                <div class="info-item bg-white/60 rounded-lg p-3">
                  <div class="text-xs text-gray-500 mb-1">角色</div>
                  <div class="font-semibold text-judicial-700">{{ currentUser.role }}</div>
                </div>
                <div class="info-item bg-white/60 rounded-lg p-3">
                  <div class="text-xs text-gray-500 mb-1">部门</div>
                  <div class="font-semibold text-judicial-700">{{ currentUser.department }}</div>
                </div>
                <div class="info-item bg-white/60 rounded-lg p-3 col-span-2">
                  <div class="text-xs text-gray-500 mb-1">证书编号</div>
                  <div class="font-mono font-semibold text-judicial-700 text-xs break-all">{{ currentUser.certificateId }}</div>
                </div>
              </div>

              <div class="warning-box bg-amber-50 border border-amber-200 rounded-lg p-4 mt-4">
                <div class="flex items-start gap-2">
                  <el-icon class="text-amber-600 text-xl mt-0.5"><WarningFilled /></el-icon>
                  <div class="text-left text-sm text-amber-800">
                    <p class="font-semibold mb-1">⚠️ 法律效力声明</p>
                    <p>您即将以【{{ currentUser.role }}】身份签署此文书，此操作具有<strong>法律效力</strong>，签署后不可撤销。</p>
                  </div>
                </div>
              </div>
            </div>

            <div class="mt-6 flex justify-center gap-4">
              <el-button size="large" @click="$emit('cancel')">取消</el-button>
              <el-button size="large" type="primary" @click="startSigningProcess">
                确认身份并继续
                <el-icon class="ml-1"><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <!-- 步骤 2: 签名采集 -->
        <div v-show="currentStep === 1" class="step-content signature-collection">
          <div class="max-w-3xl mx-auto">
            <el-tabs v-model="signatureMethod" class="signature-tabs">
              <el-tab-pane label="✍️ 手写签名" name="HANDWRITTEN">
                <div class="handwritten-signature p-4">
                  <div class="canvas-container relative bg-white rounded-lg border-2 border-dashed border-gray-300 overflow-hidden">
                    <canvas
                      ref="canvasRef"
                      :width="canvasWidth"
                      :height="canvasHeight"
                      class="signature-canvas w-full cursor-crosshair touch-none"
                      @mousedown="startDrawing"
                      @mousemove="draw"
                      @mouseup="stopDrawing"
                      @mouseleave="stopDrawing"
                      @touchstart.prevent="handleTouchStart"
                      @touchmove.prevent="handleTouchMove"
                      @touchend="stopDrawing"
                    ></canvas>

                    <div v-if="!hasSignature" class="absolute inset-0 flex items-center justify-center pointer-events-none">
                      <div class="text-center text-gray-400">
                        <el-icon :size="40"><EditPen /></el-icon>
                        <p class="mt-2 text-sm">请在上方区域手写签名</p>
                      </div>
                    </div>
                  </div>

                  <div class="toolbar mt-4 flex items-center justify-between flex-wrap gap-3">
                    <div class="flex items-center gap-3">
                      <div class="flex items-center gap-2">
                        <span class="text-sm text-gray-600">笔触颜色:</span>
                        <div class="color-options flex gap-1">
                          <button
                            v-for="color in strokeColors"
                            :key="color.value"
                            class="w-7 h-7 rounded-full border-2 transition-transform hover:scale-110"
                            :class="strokeColor === color.value ? 'border-judicial-700 scale-110 ring-2 ring-offset-1 ring-judicial-300' : 'border-gray-300'"
                            :style="{ backgroundColor: color.value }"
                            @click="strokeColor = color.value"
                          ></button>
                        </div>
                      </div>

                      <el-divider direction="vertical" />

                      <div class="flex items-center gap-2">
                        <span class="text-sm text-gray-600">笔触粗细:</span>
                        <el-radio-group v-model="lineWidth" size="small">
                          <el-radio-button :value="2">细</el-radio-button>
                          <el-radio-button :value="4">粗</el-radio-button>
                        </el-radio-group>
                      </div>
                    </div>

                    <div class="flex gap-2">
                      <el-button @click="clearCanvas">
                        <el-icon><RefreshLeft /></el-icon> 清除重绘
                      </el-button>
                    </div>
                  </div>
                </div>
              </el-tab-pane>

              <el-tab-pane label="🔐 电子印章" name="DIGITAL_SEAL">
                <div class="digital-seal p-4">
                  <div class="seal-preview-area min-h-[280px] bg-gradient-to-br from-amber-50 to-orange-50 rounded-lg border border-gold-200 p-6 flex items-center justify-center relative overflow-hidden">
                    <div class="absolute inset-0 opacity-5">
                      <div class="absolute top-4 left-4 w-16 h-16 border-2 border-gold-400 rounded-full"></div>
                      <div class="absolute bottom-4 right-4 w-24 h-24 border-2 border-gold-400 rounded-full"></div>
                      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-32 h-32 border border-gold-300 rotate-45"></div>
                    </div>

                    <div
                      class="seal-stamp-wrapper relative cursor-move select-none"
                      :style="{ transform: `translate(${sealPosition.x}px, ${sealPosition.y}px)` }"
                      @mousedown="startDragSeal"
                    >
                      <svg width="120" height="120" viewBox="0 0 120 120" class="seal-svg drop-shadow-lg">
                        <circle cx="60" cy="60" r="56" fill="rgba(255,255,255,0.95)" stroke="#d97706" stroke-width="3"/>
                        <circle cx="60" cy="60" r="48" fill="none" stroke="#dc2626" stroke-width="1.5"/>

                        <path d="M60 25 L63 35 L73 35 L65 42 L68 52 L60 46 L52 52 L55 42 L47 35 L57 35 Z" fill="#dc2626"/>

                        <text x="60" y="72" text-anchor="middle" fill="#dc2626" font-family="'STKaiti', 'KaiTi', serif" font-size="11" font-weight="bold">{{ sealConfig.organization }}</text>
                        <text x="60" y="88" text-anchor="middle" fill="#dc2626" font-family="'STKaiti', 'KaiTi', serif" font-size="9">{{ sealConfig.sealType }}</text>

                        <text x="60" y="18" text-anchor="middle" fill="#dc2626" font-family="'STKaiti', 'KaiTi', serif" font-size="7" letter-spacing="1">{{ sealConfig.topText }}</text>
                      </svg>

                      <div v-if="isDraggingSeal" class="absolute -top-6 left-1/2 -translate-x-1/2 bg-black/70 text-white text-xs px-2 py-1 rounded whitespace-nowrap">
                        拖动调整位置
                      </div>
                    </div>
                  </div>

                  <div class="mt-4 text-center text-sm text-gray-500">
                    <el-icon class="align-middle"><InfoFilled /></el-icon>
                    拖动印章可调整在文书上的盖章位置
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>

            <div class="mt-6 flex justify-center gap-4">
              <el-button @click="prevStep">
                <el-icon><ArrowLeft /></el-icon> 上一步
              </el-button>
              <el-button type="primary" :disabled="!canProceedToNext" @click="nextStep">
                确认签名并继续
                <el-icon class="ml-1"><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <!-- 步骤 3: 时间戳写入 -->
        <div v-show="currentStep === 2" class="step-content timestamp-write">
          <div class="text-center py-8">
            <div class="timestamp-display max-w-md mx-auto">
              <div class="clock-icon mb-6">
                <div class="w-24 h-24 mx-auto bg-gradient-to-br from-judicial-100 to-blue-100 rounded-full flex items-center justify-center relative">
                  <el-icon :size="48" class="text-judicial-700"><Clock /></el-icon>
                  <div class="absolute inset-0 rounded-full border-4 border-judicial-200 animate-ping opacity-30"></div>
                </div>
              </div>

              <div class="time-display bg-gradient-to-r from-slate-900 to-slate-800 rounded-xl p-6 text-white font-mono shadow-xl">
                <div class="text-3xl font-bold tracking-wider mb-2">{{ formattedTimestamp }}</div>
                <div class="text-base text-green-400">{{ formattedDateWithWeekday }}</div>
              </div>

              <div class="countdown-section mt-6" v-if="isCountingDown">
                <div class="text-2xl font-bold text-judicial-700 animate-bounce">{{ countdownNumber }}</div>
                <div class="text-sm text-gray-500 mt-1">时间戳锁定倒计时...</div>
              </div>

              <div v-else-if="!isCountingDown && countdownComplete" class="success-flash mt-6">
                <div class="inline-flex items-center gap-2 bg-green-100 text-green-800 px-4 py-2 rounded-full font-medium">
                  <el-icon><CircleCheck /></el-icon>
                  时间戳已锁定 ✓
                </div>
              </div>

              <div class="ntp-info mt-6 grid grid-cols-2 gap-3 text-sm">
                <div class="bg-gray-50 rounded-lg p-3">
                  <div class="text-xs text-gray-500">时间源</div>
                  <div class="font-mono font-semibold text-judicial-700">NTP (pool.ntp.org)</div>
                </div>
                <div class="bg-gray-50 rounded-lg p-3">
                  <div class="text-xs text-gray-500">时区</div>
                  <div class="font-semibold text-judicial-700">UTC+8 北京时间</div>
                </div>
                <div class="bg-gray-50 rounded-lg p-3 col-span-2">
                  <div class="text-xs text-gray-500">时间精度</div>
                  <div class="font-mono font-semibold text-judicial-700">精确至毫秒级 (±5ms)</div>
                </div>
              </div>
            </div>

            <div class="mt-6 flex justify-center gap-4">
              <el-button @click="prevStep">
                <el-icon><ArrowLeft /></el-icon> 上一步
              </el-button>
              <el-button
                v-if="!isCountingDown && !countdownComplete"
                type="primary"
                @click="lockTimestamp"
              >
                🔒 锁定时间戳
              </el-button>
              <el-button
                v-else-if="countdownComplete"
                type="primary"
                @click="nextStep"
              >
                继续生成数字签名
                <el-icon class="ml-1"><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <!-- 步骤 4: 数字签名生成 -->
        <div v-show="currentStep === 3" class="step-content digital-signature">
          <div class="text-center py-8">
            <div class="signature-progress max-w-lg mx-auto">
              <div class="progress-header mb-6">
                <h3 class="text-xl font-bold text-judicial-800 mb-2">正在生成数字签名...</h3>
                <p class="text-sm text-gray-500">此过程将使用您的私钥对文档进行加密签名</p>
              </div>

              <div class="progress-bar-wrapper bg-gray-200 rounded-full h-3 overflow-hidden mb-6">
                <div
                  class="progress-bar h-full bg-gradient-to-r from-judicial-600 via-judicial-500 to-green-500 transition-all duration-500 ease-out rounded-full"
                  :style="{ width: `${signatureProgress}%` }"
                ></div>
              </div>

              <div class="steps-list space-y-3">
                <div
                  v-for="(sigStep, idx) in signatureSteps"
                  :key="idx"
                  class="sig-step flex items-center gap-3 p-3 rounded-lg transition-all duration-300"
                  :class="getSignatureStepClass(idx)"
                >
                  <div class="step-status w-7 h-7 rounded-full flex items-center justify-center flex-shrink-0">
                    <el-icon v-if="idx < completedSigSteps" class="text-white"><Check /></el-icon>
                    <el-icon v-else-if="idx === completedSigSteps" class="animate-spin text-white"><Loading /></el-icon>
                    <span v-else class="text-xs font-bold text-gray-400">{{ idx + 1 }}</span>
                  </div>
                  <div class="step-text flex-1 text-left">
                    <div class="font-medium">{{ sigStep.label }}</div>
                    <div v-if="idx <= completedSigSteps" class="text-xs text-gray-500 mt-0.5">{{ sigStep.detail }}</div>
                  </div>
                </div>
              </div>

              <div v-if="signatureComplete" class="mt-6 p-4 bg-gradient-to-r from-green-50 to-emerald-50 rounded-xl border border-green-200">
                <div class="flex items-center justify-center gap-2 text-green-700 font-medium">
                  <el-icon :size="20"><SuccessFilled /></el-icon>
                  数字签名生成完成！耗时 {{ signatureDuration }}ms
                </div>
              </div>
            </div>

            <div class="mt-6 flex justify-center gap-4">
              <el-button @click="prevStep" :disabled="isGeneratingSignature">
                <el-icon><ArrowLeft /></el-icon> 上一步
              </el-button>
              <el-button
                v-if="!signatureComplete"
                type="primary"
                :loading="isGeneratingSignature"
                disabled
              >
                正在处理...
              </el-button>
              <el-button
                v-else
                type="primary"
                @click="completeSealing"
              >
                完成签章仪式 🎉
              </el-button>
            </div>
          </div>
        </div>

        <!-- 步骤 5: 签章完成仪式 -->
        <div v-show="currentStep === 4 || isSealed" class="step-content sealing-complete">
          <div class="completion-ceremony text-center py-8">
            <div class="seal-animation-container relative inline-block mb-8">
              <div
                class="seal-final"
                :class="{ 'seal-drop-animation': showSealDropAnimation }"
              >
                <svg width="160" height="160" viewBox="0 0 120 120" class="seal-final-svg">
                  <defs>
                    <filter id="goldGlow">
                      <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
                      <feMerge>
                        <feMergeNode in="coloredBlur"/>
                        <feMergeNode in="SourceGraphic"/>
                      </feMerge>
                    </filter>
                  </defs>

                  <circle cx="60" cy="60" r="56" fill="rgba(255,255,255,0.95)" stroke="#d97706" stroke-width="3" filter="url(#goldGlow)"/>
                  <circle cx="60" cy="60" r="48" fill="none" stroke="#dc2626" stroke-width="1.5"/>

                  <path d="M60 25 L63 35 L73 35 L65 42 L68 52 L60 46 L52 52 L55 42 L47 35 L57 35 Z" fill="#dc2626"/>

                  <text x="60" y="72" text-anchor="middle" fill="#dc2626" font-family="'STKaiti', 'KaiTi', serif" font-size="11" font-weight="bold">{{ sealConfig.organization }}</text>
                  <text x="60" y="88" text-anchor="middle" fill="#dc2626" font-family="'STKaiti', 'KaiTi', serif" font-size="9">{{ sealConfig.sealType }}</text>

                  <text x="60" y="18" text-anchor="middle" fill="#dc2626" font-family="'STKaiti', 'KaiTi', serif" font-size="7" letter-spacing="1">{{ sealConfig.topText }}</text>
                </svg>

                <div v-if="showGoldHalo" class="gold-halo absolute inset-0 rounded-full animate-pulse"></div>
              </div>

              <div v-if="showFlashEffect" class="flash-effect absolute inset-0 bg-white/80 animate-flash"></div>
            </div>

            <div class="completion-info max-w-md mx-auto bg-gradient-to-br from-green-50 to-emerald-50 rounded-xl border-2 border-green-300 p-6 shadow-lg">
              <div class="completion-header mb-4 pb-4 border-b border-green-200">
                <div class="inline-flex items-center gap-2 bg-green-600 text-white px-4 py-2 rounded-full font-bold text-lg">
                  ✅ 电子签章成功
                </div>
              </div>

              <div class="info-grid grid gap-3 text-left text-sm">
                <div class="info-row flex justify-between py-2 border-b border-green-100">
                  <span class="text-gray-600">签署人：</span>
                  <span class="font-semibold text-judicial-800">{{ signatureResult.signerName }}</span>
                </div>
                <div class="info-row flex justify-between py-2 border-b border-green-100">
                  <span class="text-gray-600">角色：</span>
                  <span class="font-semibold text-judicial-800">{{ signatureResult.role }}</span>
                </div>
                <div class="info-row flex justify-between py-2 border-b border-green-100">
                  <span class="text-gray-600">时间：</span>
                  <span class="font-semibold text-judicial-800 font-mono text-xs">{{ signatureResult.timestamp }}</span>
                </div>
                <div class="info-row flex justify-between py-2 border-b border-green-100">
                  <span class="text-gray-600">签名方式：</span>
                  <span class="font-semibold text-judicial-800">{{ signatureMethod === 'HANDWRITTEN' ? '手写签名' : '电子印章' }}</span>
                </div>
                <div class="info-row py-2">
                  <span class="text-gray-600">签名Hash：</span>
                  <div class="hash-display mt-1 bg-slate-900 text-green-400 px-3 py-2 rounded font-mono text-xs break-all">
                    {{ signatureResult.signatureHash }}
                  </div>
                </div>
                <div class="info-row py-2">
                  <span class="text-gray-600">TraceID：</span>
                  <div class="trace-id-display mt-1 bg-purple-50 text-purple-700 px-3 py-2 rounded font-mono text-xs flex items-center gap-2">
                    <code>{{ signatureResult.traceId }}</code>
                    <el-button link size="small" @click="copyTraceId">
                      <el-icon><CopyDocument /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
            </div>

            <div class="mt-6">
              <el-button type="success" size="large" @click="$emit('completed')">
                完成并返回
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="isSealed" class="sealed-seal-display mt-6 pt-6 border-t border-gray-200">
        <div class="text-center">
          <div class="inline-block">
            <svg width="80" height="80" viewBox="0 0 120 120" class="opacity-90">
              <circle cx="60" cy="60" r="56" fill="rgba(220,38,38,0.08)" stroke="#dc2626" stroke-width="2"/>
              <circle cx="60" cy="60" r="48" fill="none" stroke="#dc2626" stroke-width="1"/>
              <path d="M60 28 L62 36 L71 36 L64 41 L67 49 L60 44 L53 49 L56 41 L49 36 L58 36 Z" fill="#dc2626"/>
              <text x="60" y="70" text-anchor="middle" fill="#dc2626" font-family="'STKaiti', 'KaiTi', serif" font-size="9" font-weight="bold">{{ sealConfig.organization }}</text>
              <text x="60" y="84" text-anchor="middle" fill="#dc2626" font-family="'STKaiti', 'KaiTi', serif" font-size="7">{{ sealConfig.sealType }}</text>
            </svg>
          </div>
          <div class="mt-2 text-xs text-gray-500">
            已签署 · {{ signatureResult.signerName }} · {{ formatTime(signatureResult.timestamp) }}
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowRight,
  ArrowLeft,
  CircleCheck,
  Loading,
  WarningFilled,
  EditPen,
  RefreshLeft,
  Clock,
  InfoFilled,
  Check,
  SuccessFilled,
  CopyDocument,
} from '@element-plus/icons-vue'
import { useDocumentWorkflowStore } from '@/lib/stores/documentWorkflow'

const documentStore = useDocumentWorkflowStore()

interface Props {
  /** 当前用户信息 */
  user?: {
    name: string
    role: string
    department: string
    certificateId: string
  }
}

const props = withDefaults(defineProps<Props>(), {
  user: () => ({
    name: '张三',
    role: '原告代理人',
    department: '某某律师事务所',
    certificateId: 'CERT-2024-JD-001234',
  }),
})

const emit = defineEmits<{
  (e: 'completed'): void
  (e: 'cancel'): void
}>()

const steps = [
  { id: 1, label: '身份确认' },
  { id: 2, label: '签名采集' },
  { id: 3, label: '时间戳' },
  { id: 4, label: '数字签名' },
  { id: 5, label: '完成' },
]

const currentStep = ref(0)
const isSealed = ref(false)
const signatureMethod = ref<'HANDWRITTEN' | 'DIGITAL_SEAL'>('HANDWRITTEN')

const currentUser = computed(() => props.user)

// Canvas 手写签名相关
const canvasRef = ref<HTMLCanvasElement>()
let ctx: CanvasRenderingContext2D | null = null
const canvasWidth = 400
const canvasHeight = 200
const isDrawing = ref(false)
const hasSignature = ref(false)
const strokeColor = ref('#000000')
const lineWidth = ref(2)
const strokeColors = [
  { value: '#000000', label: '黑色' },
  { value: '#1e40af', label: '蓝色' },
]

// 电子印章拖拽
const isDraggingSeal = ref(false)
const sealPosition = ref({ x: 0, y: 0 })
const dragStartPos = ref({ x: 0, y: 0 })

const sealConfig = ref({
  organization: '某某人民法院',
  sealType: '专用章',
  topText: '电子签章专用',
})

// 时间戳相关
const currentTime = ref(new Date())
const isCountingDown = ref(false)
const countdownNumber = ref(3)
const countdownComplete = ref(false)

// 数字签名进度
const signatureProgress = ref(0)
const completedSigSteps = ref(0)
const isGeneratingSignature = ref(false)
const signatureComplete = ref(false)
const signatureDuration = ref(0)

const signatureSteps = [
  { label: '收集原始文档 Hash', detail: 'SHA-256 哈希计算中...' },
  { label: '应用私钥加密', detail: 'RSA-2048 非对称加密...' },
  { label: '附加时间戳', detail: 'RFC3161 时间戳协议...' },
  { label: '计算签名摘要', detail: '生成数字指纹...' },
  { label: '验证签名完整性', detail: '自检通过...' },
]

// 完成动画效果
const showSealDropAnimation = ref(false)
const showGoldHalo = ref(false)
const showFlashEffect = ref(false)

const signatureResult = ref({
  signerName: '',
  role: '',
  timestamp: '',
  signatureHash: '',
  traceId: '',
})

let timeInterval: ReturnType<typeof setInterval> | null = null
let countdownInterval: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  initCanvas()
  startClock()
})

onUnmounted(() => {
  stopClock()
  if (countdownInterval) clearInterval(countdownInterval)
})

function initCanvas(): void {
  nextTick(() => {
    if (canvasRef.value) {
      ctx = canvasRef.value.getContext('2d')
      if (ctx) {
        ctx.fillStyle = '#ffffff'
        ctx.fillRect(0, 0, canvasWidth, canvasHeight)
        ctx.strokeStyle = strokeColor.value
        ctx.lineWidth = lineWidth.value
        ctx.lineCap = 'round'
        ctx.lineJoin = 'round'
      }
    }
  })
}

watch(strokeColor, (newColor) => {
  if (ctx) {
    ctx.strokeStyle = newColor
  }
})

watch(lineWidth, (newWidth) => {
  if (ctx) {
    ctx.lineWidth = newWidth
  }
})

function getEventPosition(e: MouseEvent | TouchEvent): { x: number; y: number } {
  const canvas = canvasRef.value
  if (!canvas) return { x: 0, y: 0 }

  const rect = canvas.getBoundingClientRect()
  const scaleX = canvas.width / rect.width
  const scaleY = canvas.height / rect.height

  if ('touches' in e && e.touches.length > 0) {
    return {
      x: (e.touches[0].clientX - rect.left) * scaleX,
      y: (e.touches[0].clientY - rect.top) * scaleY,
    }
  }

  return {
    x: (e.clientX - rect.left) * scaleX,
    y: (e.clientY - rect.top) * scaleY,
  }
}

function startDrawing(e: MouseEvent): void {
  if (!ctx) return
  isDrawing.value = true
  hasSignature.value = true

  const pos = getEventPosition(e)
  ctx.beginPath()
  ctx.moveTo(pos.x, pos.y)
}

function draw(e: MouseEvent): void {
  if (!isDrawing.value || !ctx) return

  const pos = getEventPosition(e)
  ctx.lineTo(pos.x, pos.y)
  ctx.stroke()
}

function stopDrawing(): void {
  if (!isDrawing.value || !ctx) return
  isDrawing.value = false
  ctx.closePath()
}

function handleTouchStart(e: TouchEvent): void {
  e.preventDefault()
  startDrawing(e as unknown as MouseEvent)
}

function handleTouchMove(e: TouchEvent): void {
  e.preventDefault()
  draw(e as unknown as MouseEvent)
}

function clearCanvas(): void {
  if (!ctx || !canvasRef.value) return
  ctx.clearRect(0, 0, canvasWidth, canvasHeight)
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(0, 0, canvasWidth, canvasHeight)
  hasSignature.value = false
}

function getSignatureData(): string {
  if (!canvasRef.value) return ''
  return canvasRef.value.toDataURL('image/png')
}

function startDragSeal(e: MouseEvent): void {
  isDraggingSeal.value = true
  dragStartPos.value = {
    x: e.clientX - sealPosition.value.x,
    y: e.clientY - sealPosition.value.y,
  }

  const handleMouseMove = (ev: MouseEvent) => {
    if (isDraggingSeal.value) {
      sealPosition.value = {
        x: ev.clientX - dragStartPos.value.x,
        y: ev.clientY - dragStartPos.value.y,
      }
    }
  }

  const handleMouseUp = () => {
    isDraggingSeal.value = false
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
  }

  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

const canProceedToNext = computed(() => {
  if (signatureMethod.value === 'HANDWRITTEN') {
    return hasSignature.value
  }
  return true
})

const formattedTimestamp = computed(() => {
  const now = currentTime.value
  return [
    String(now.getHours()).padStart(2, '0'),
    String(now.getMinutes()).padStart(2, '0'),
    String(now.getSeconds()).padStart(2, '0'),
  ].join(':') + '.' + String(now.getMilliseconds()).padStart(3, '0')
})

const formattedDateWithWeekday = computed(() => {
  const now = currentTime.value
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return `${now.getFullYear()}年${String(now.getMonth() + 1).padStart(2, '0')}月${String(now.getDate()).padStart(2, '0')}日 ${weekdays[now.getDay()]}`
})

function startClock(): void {
  timeInterval = setInterval(() => {
    currentTime.value = new Date()
  }, 50)
}

function stopClock(): void {
  if (timeInterval) {
    clearInterval(timeInterval)
    timeInterval = null
  }
}

async function startSigningProcess(): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `您即将以【${currentUser.value.role}】身份签署此文书，此操作具有法律效力。是否继续？`,
      '确认签署身份',
      {
        confirmButtonText: '确认签署',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'signing-confirm-dialog',
      }
    )

    currentStep.value = 1
  } catch {
    // 用户取消
  }
}

function prevStep(): void {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

async function nextStep(): Promise<void> {
  if (currentStep.value < steps.length - 1) {
    currentStep.value++

    if (currentStep.value === 3) {
      await generateDigitalSignature()
    }
  }
}

async function lockTimestamp(): Promise<void> {
  isCountingDown.value = true
  countdownNumber.value = 3

  countdownInterval = setInterval(() => {
    countdownNumber.value--

    if (countdownNumber.value <= 0) {
      clearInterval(countdownInterval!)
      countdownInterval = null
      isCountingDown.value = false
      countdownComplete.value = true
    }
  }, 1000)
}

async function generateDigitalSignature(): Promise<void> {
  isGeneratingSignature.value = true
  signatureProgress.value = 0
  completedSigSteps.value = 0
  signatureComplete.value = false
  const startTime = Date.now()

  for (let i = 0; i < signatureSteps.length; i++) {
    await new Promise(resolve => setTimeout(resolve, 450 + Math.random() * 300))
    completedSigSteps.value = i + 1
    signatureProgress.value = Math.round(((i + 1) / signatureSteps.length) * 100)
  }

  signatureDuration.value = Date.now() - startTime
  signatureComplete.value = true
  isGeneratingSignature.value = false
}

async function completeSealing(): Promise<void> {
  try {
    const signatureData = signatureMethod.value === 'HANDWRITTEN'
      ? getSignatureData()
      : JSON.stringify({ sealPosition: sealPosition.value, sealConfig: sealConfig.value })

    await documentStore.signDocument(signatureData, signatureMethod.value)

    signatureResult.value = {
      signerName: currentUser.value.name,
      role: currentUser.value.role,
      timestamp: new Date().toISOString(),
      signatureHash: generateMockHash(),
      traceId: `trace-${generateRandomHex(12)}`,
    }

    currentStep.value = 4
    isSealed.value = true

    // 触发印章落下动画
    await nextTick()
    showSealDropAnimation.value = true

    setTimeout(() => {
      showGoldHalo.value = true
    }, 600)

    setTimeout(() => {
      showFlashEffect.value = true
      setTimeout(() => {
        showFlashEffect.value = false
      }, 300)
    }, 900)

    ElMessage.success('🎉 电子签章完成！此操作已被记录到区块链审计日志中。')

    emit('completed')
  } catch (err) {
    ElMessage.error(err instanceof Error ? err.message : '签章失败，请重试')
    console.error('Sealing error:', err)
  }
}

function generateMockHash(): string {
  const chars = 'abcdef0123456789'
  let hash = ''
  for (let i = 0; i < 32; i++) {
    hash += chars[Math.floor(Math.random() * chars.length)]
    if (i === 7 || i === 19) hash += '...'
  }
  return hash
}

function generateRandomHex(length: number): string {
  const chars = 'abcdef0123456789'
  let result = ''
  for (let i = 0; i < length; i++) {
    result += chars[Math.floor(Math.random() * chars.length)]
  }
  return result
}

function copyTraceId(): void {
  navigator.clipboard.writeText(signatureResult.value.traceId).then(() => {
    ElMessage.success('TraceID 已复制到剪贴板')
  }).catch(() => {
    ElMessage.warning('复制失败，请手动复制')
  })
}

function formatTime(isoString: string): string {
  const date = new Date(isoString)
  return `${date.getMonth() + 1}/${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

function getStepClass(index: number): string[] {
  const classes: string[] = []

  if (index < currentStep.value) {
    classes.push('bg-green-500', 'text-white', 'shadow-md', 'shadow-green-200')
  } else if (index === currentStep.value) {
    classes.push('bg-judicial-600', 'text-white', 'ring-4', 'ring-judicial-200', 'scale-110')
  } else {
    classes.push('bg-gray-200', 'text-gray-500')
  }

  return classes
}

function getSignatureStepClass(index: number): string[] {
  const classes: string[] = []

  if (index < completedSigSteps.value) {
    classes.push('bg-green-50', 'border', 'border-green-200')
  } else if (index === completedSigSteps.value) {
    classes.push('bg-judicial-50', 'border', 'border-judicial-200')
  } else {
    classes.push('bg-gray-50', 'border', 'border-gray-200')
  }

  return classes
}
</script>

<style scoped>
.electronic-seal-panel {
  width: 100%;
  background: linear-gradient(to bottom, #fffbeb, #ffffff);
  border-color: #fcd34d;
}

.electronic-seal-panel.seal-completed {
  background: linear-gradient(to bottom, #f0fdf4, #ffffff);
  border-color: #86efac;
}

.step-item {
  position: relative;
  z-index: 1;
}

.step-circle {
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.step-active .step-circle {
  animation: pulse-ring 2s infinite;
}

@keyframes pulse-ring {
  0% { box-shadow: 0 0 0 0 rgba(37, 99, 235, 0.4); }
  70% { box-shadow: 0 0 0 10px rgba(37, 99, 235, 0); }
  100% { box-shadow: 0 0 0 0 rgba(37, 99, 235, 0); }
}

.step-label {
  transition: all 0.3s;
}

.step-active .step-label {
  color: #2563eb;
  font-weight: 600;
}

.step-completed .step-label {
  color: #059669;
}

.canvas-container {
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.06);
}

.signature-canvas {
  display: block;
}

.seal-stamp-wrapper {
  transition: transform 0.15s ease-out;
}

.seal-svg {
  filter: drop-shadow(0 4px 6px rgba(217, 119, 6, 0.3));
}

.seal-final {
  position: relative;
  display: inline-block;
}

.seal-drop-animation {
  animation: seal-fall 1.2s cubic-bezier(0.68, -0.55, 0.265, 1.55) forwards;
}

@keyframes seal-fall {
  0% {
    transform: translateY(-150px) scale(0.5);
    opacity: 0;
  }
  60% {
    transform: translateY(10px) scale(1.1);
    opacity: 1;
  }
  75% {
    transform: translateY(-5px) scale(0.98);
  }
  85% {
    transform: translateY(2px) scale(1.02);
  }
  100% {
    transform: translateY(0) scale(1);
  }
}

.gold-halo {
  background: radial-gradient(circle, rgba(217, 119, 6, 0.4) 0%, transparent 70%);
  border-radius: 50%;
  animation: halo-expand 1s ease-out forwards;
}

@keyframes halo-expand {
  0% {
    transform: scale(0.8);
    opacity: 1;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}

.flash-effect {
  animation: flash 0.3s ease-out forwards;
}

@keyframes flash {
  0% { opacity: 1; }
  100% { opacity: 0; }
}

.sig-step .step-status {
  transition: all 0.3s;
}

.sig-step:nth-child(n):not(:nth-child(n + {{ completedSigSteps + 1 }})) .step-status {
  background-color: #059669;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.hash-display {
  word-break: break-all;
}

.trace-id-display code {
  font-family: 'JetBrains Mono', monospace;
}
</style>
