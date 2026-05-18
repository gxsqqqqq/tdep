<template>
  <div class="agent-float" :class="{ 'agent-float--expanded': isExpanded }">
    <!-- Expanded Panel -->
    <Transition name="agent-panel">
      <div v-if="isExpanded" class="agent-panel">
        <div class="agent-panel__header">
          <div class="agent-panel__title">
            <span class="agent-panel__icon">🤖</span>
            <span>智能助手</span>
          </div>
          <button class="agent-panel__close" @click="collapse">✕</button>
        </div>

        <div class="agent-panel__body" ref="panelBodyRef">
          <div v-if="agentStore.visibleMessages.length === 0" class="agent-panel__empty">
            <div class="agent-panel__empty-icon">💬</div>
            <p>暂无消息，系统会在需要时为您提供引导。</p>
          </div>

          <TransitionGroup name="agent-msg" tag="div" class="agent-panel__messages">
            <div
              v-for="msg in agentStore.visibleMessages"
              :key="msg.id"
              class="agent-msg"
              :class="`agent-msg--${msg.type}`"
            >
              <div class="agent-msg__header">
                <span class="agent-msg__type-icon">{{ typeIcon(msg.type) }}</span>
                <span class="agent-msg__title">{{ msg.title }}</span>
                <button
                  v-if="msg.dismissible"
                  class="agent-msg__dismiss"
                  @click="agentStore.dismissMessage(msg.id)"
                >✕</button>
              </div>
              <div class="agent-msg__content">{{ msg.content }}</div>
              <div v-if="msg.action" class="agent-msg__action">
                <button class="agent-msg__action-btn" @click="handleAction(msg)">
                  {{ msg.action.label }} →
                </button>
              </div>
            </div>
          </TransitionGroup>
        </div>
      </div>
    </Transition>

    <!-- Floating Bubble -->
    <button
      class="agent-bubble"
      :class="{
        'agent-bubble--has-msg': agentStore.unreadCount > 0,
        'agent-bubble--pulse': shouldPulse
      }"
      @click="toggle"
    >
      <span class="agent-bubble__icon">🤖</span>
      <span v-if="agentStore.unreadCount > 0" class="agent-bubble__badge">
        {{ agentStore.unreadCount > 9 ? '9+' : agentStore.unreadCount }}
      </span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAgentStore } from '@/stores/agent'

const router = useRouter()
const agentStore = useAgentStore()
const panelBodyRef = ref<HTMLElement>()

const isExpanded = ref(false)
const shouldPulse = ref(false)

const toggle = () => {
  isExpanded.value = !isExpanded.value
  if (isExpanded.value) {
    shouldPulse.value = false
  }
}

const collapse = () => {
  isExpanded.value = false
}

const handleAction = (msg: { action?: { route?: string; callback?: string } }) => {
  if (msg.action?.route) {
    router.push(msg.action.route)
    collapse()
  }
}

const typeIcon = (type: string) => {
  const icons: Record<string, string> = {
    hint: '💡',
    warning: '⚠️',
    success: '✅',
    info: 'ℹ️',
    action: '⚡'
  }
  return icons[type] || '💡'
}

// Pulse when new messages arrive and panel is closed
watch(
  () => agentStore.unreadCount,
  (newVal, oldVal) => {
    if (newVal > oldVal && !isExpanded.value) {
      shouldPulse.value = true
      setTimeout(() => {
        shouldPulse.value = false
      }, 3000)
    }
  }
)

// Auto-scroll to bottom when new messages arrive
watch(
  () => agentStore.messages.length,
  async () => {
    if (isExpanded.value && panelBodyRef.value) {
      await nextTick()
      panelBodyRef.value.scrollTop = panelBodyRef.value.scrollHeight
    }
  }
)
</script>

<style scoped>
.agent-float {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

/* ── Bubble ── */
.agent-bubble {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #1a73e8, #0d47a1);
  color: white;
  font-size: 24px;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(26, 115, 232, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.agent-bubble:hover {
  transform: scale(1.08);
  box-shadow: 0 6px 20px rgba(26, 115, 232, 0.5);
}

.agent-bubble--pulse {
  animation: agent-pulse 1.5s ease-in-out infinite;
}

.agent-bubble__icon {
  line-height: 1;
}

.agent-bubble__badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  background: #ea4335;
  color: white;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  box-shadow: 0 2px 6px rgba(234, 67, 53, 0.4);
}

/* ── Panel ── */
.agent-panel {
  width: 380px;
  max-height: 520px;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.agent-panel__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(135deg, #1a73e8, #0d47a1);
  color: white;
}

.agent-panel__title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 15px;
}

.agent-panel__icon {
  font-size: 20px;
}

.agent-panel__close {
  background: none;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  opacity: 0.8;
  padding: 4px;
  line-height: 1;
}

.agent-panel__close:hover {
  opacity: 1;
}

.agent-panel__body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  max-height: 420px;
}

.agent-panel__empty {
  text-align: center;
  padding: 32px 16px;
  color: #9aa0a6;
}

.agent-panel__empty-icon {
  font-size: 40px;
  margin-bottom: 8px;
}

.agent-panel__empty p {
  margin: 0;
  font-size: 13px;
}

.agent-panel__messages {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* ── Message ── */
.agent-msg {
  padding: 14px;
  border-radius: 12px;
  background: #f8f9fa;
  border-left: 3px solid #e0e4e8;
}

.agent-msg--hint   { border-left-color: #fbbc04; background: #fffdf0; }
.agent-msg--warning { border-left-color: #ea4335; background: #fff5f5; }
.agent-msg--success { border-left-color: #34a853; background: #f0fff4; }
.agent-msg--info   { border-left-color: #1a73e8; background: #f0f7ff; }
.agent-msg--action { border-left-color: #1a73e8; background: #f0f7ff; }

.agent-msg__header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}

.agent-msg__type-icon {
  font-size: 14px;
  line-height: 1;
}

.agent-msg__title {
  flex: 1;
  font-weight: 600;
  font-size: 14px;
  color: #202124;
}

.agent-msg__dismiss {
  background: none;
  border: none;
  color: #9aa0a6;
  cursor: pointer;
  font-size: 12px;
  padding: 2px 4px;
  line-height: 1;
}

.agent-msg__dismiss:hover {
  color: #5f6368;
}

.agent-msg__content {
  font-size: 13px;
  color: #5f6368;
  line-height: 1.6;
}

.agent-msg__action {
  margin-top: 10px;
}

.agent-msg__action-btn {
  background: none;
  border: none;
  color: #1a73e8;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  padding: 0;
}

.agent-msg__action-btn:hover {
  text-decoration: underline;
}

/* ── Transitions ── */
.agent-panel-enter-active,
.agent-panel-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.agent-panel-enter-from {
  opacity: 0;
  transform: translateY(16px) scale(0.95);
}

.agent-panel-leave-to {
  opacity: 0;
  transform: translateY(8px) scale(0.98);
}

.agent-msg-enter-active {
  transition: all 0.3s ease;
}

.agent-msg-leave-active {
  transition: all 0.2s ease;
}

.agent-msg-enter-from {
  opacity: 0;
  transform: translateY(-8px);
}

.agent-msg-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

@keyframes agent-pulse {
  0%, 100% { box-shadow: 0 4px 16px rgba(26, 115, 232, 0.4); }
  50% { box-shadow: 0 4px 24px rgba(26, 115, 232, 0.7), 0 0 0 8px rgba(26, 115, 232, 0.15); }
}

/* ── Dark mode ── */
:root.dark .agent-panel {
  background: #1a2332;
  border-color: #2d3748;
}

:root.dark .agent-msg {
  background: #1e293b;
}

:root.dark .agent-msg--hint   { background: #2d2a1a; }
:root.dark .agent-msg--warning { background: #2d1a1a; }
:root.dark .agent-msg--success { background: #1a2d1e; }
:root.dark .agent-msg--info   { background: #1a2035; }
:root.dark .agent-msg--action { background: #1a2035; }

:root.dark .agent-msg__title {
  color: #e2e8f0;
}

:root.dark .agent-msg__content {
  color: #94a3b8;
}
</style>
