import { useNotifyStore } from '@/stores/notify'

import type { NotifySocketMessage } from './types'

export class NotifySocketClient {
  private socket: WebSocket | null = null
  private heartbeatTimer = 0
  private reconnectTimer = 0
  private reconnectAttempts = 0

  connect(token: string) {
    if (!token || this.socket?.readyState === WebSocket.OPEN) return
    const notifyStore = useNotifyStore()
    notifyStore.setSocketStatus('CONNECTING')
    const protocol = location.protocol === 'https:' ? 'wss' : 'ws'
    const url = `${protocol}://${location.host}/ws/notify?token=${encodeURIComponent(token)}`
    this.socket = new WebSocket(url)

    this.socket.onopen = () => {
      this.reconnectAttempts = 0
      notifyStore.setSocketStatus('OPEN')
      this.startHeartbeat()
    }
    this.socket.onmessage = (event) => {
      if (event.data === 'PONG') return
      try {
        notifyStore.push(JSON.parse(event.data) as NotifySocketMessage)
      } catch {
        // Ignore malformed local development messages.
      }
    }
    this.socket.onclose = () => {
      notifyStore.setSocketStatus('CLOSED')
      this.stopHeartbeat()
      this.reconnect(token)
    }
    this.socket.onerror = () => {
      this.socket?.close()
    }
  }

  disconnect() {
    this.stopHeartbeat()
    window.clearTimeout(this.reconnectTimer)
    this.socket?.close()
    this.socket = null
  }

  private startHeartbeat() {
    this.stopHeartbeat()
    this.heartbeatTimer = window.setInterval(() => {
      if (this.socket?.readyState === WebSocket.OPEN) {
        this.socket.send('PING')
      }
    }, 30000)
  }

  private stopHeartbeat() {
    window.clearInterval(this.heartbeatTimer)
  }

  private reconnect(token: string) {
    if (this.reconnectAttempts >= 5) return
    const delay = Math.min(30000, 1000 * 2 ** this.reconnectAttempts)
    this.reconnectAttempts += 1
    this.reconnectTimer = window.setTimeout(() => this.connect(token), delay)
  }
}

export const notifySocketClient = new NotifySocketClient()
