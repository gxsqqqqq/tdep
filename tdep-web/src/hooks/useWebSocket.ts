import { onBeforeUnmount, watch } from 'vue'

import { useAuthStore } from '@/stores/auth'
import { useNotifyStore } from '@/stores/notify'
import { useAgentStore } from '@/stores/agent'
import { notifySocketClient } from '@/websocket/notify-socket'

export function useWebSocket() {
  const authStore = useAuthStore()
  const notifyStore = useNotifyStore()
  const agentStore = useAgentStore()

  watch(
    () => authStore.accessToken,
    (token) => {
      if (token) {
        notifySocketClient.connect(token)
      } else {
        notifySocketClient.disconnect()
      }
    },
    { immediate: true }
  )

  watch(
    () => notifyStore.unreadCount,
    (count) => {
      agentStore.updateUnreadCount(count)
    },
    { immediate: true }
  )

  onBeforeUnmount(() => notifySocketClient.disconnect())
}
