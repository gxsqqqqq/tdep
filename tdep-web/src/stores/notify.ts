import { defineStore } from 'pinia'

export interface NotifyMessage {
  messageId: number
  type: string
  title: string
  content: string
  timestamp: string
}

export const useNotifyStore = defineStore('notify', {
  state: () => ({
    unreadCount: 0,
    messages: [] as NotifyMessage[],
    socketStatus: 'CLOSED' as 'CONNECTING' | 'OPEN' | 'CLOSED'
  }),
  actions: {
    push(message: NotifyMessage) {
      this.messages.unshift(message)
      this.unreadCount += 1
    },
    setSocketStatus(status: 'CONNECTING' | 'OPEN' | 'CLOSED') {
      this.socketStatus = status
    },
    clearUnread() {
      this.unreadCount = 0
    }
  }
})
