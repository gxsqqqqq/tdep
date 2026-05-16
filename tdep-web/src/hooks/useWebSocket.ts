import { onBeforeUnmount, watch } from 'vue'

import { useAuthStore } from '@/stores/auth'
import { notifySocketClient } from '@/websocket/notify-socket'

export function useWebSocket() {
  const authStore = useAuthStore()

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

  onBeforeUnmount(() => notifySocketClient.disconnect())
}
