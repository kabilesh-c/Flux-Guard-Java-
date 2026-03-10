import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

const WS_URL = import.meta.env.VITE_WS_URL || 'http://localhost:8080/ws'

export class WebSocketService {
  private client: Client | null = null
  private subscribers: Map<string, (message: any) => void> = new Map()

  connect(onConnect?: () => void, onError?: (error: any) => void) {
    this.client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (str) => {
        console.log('[WebSocket]', str)
      },
      onConnect: () => {
        console.log('WebSocket connected')
        onConnect?.()
        this.resubscribeAll()
      },
      onStompError: (frame) => {
        console.error('WebSocket error:', frame)
        onError?.(frame)
      },
      onWebSocketError: (error) => {
        console.error('WebSocket connection error:', error)
        onError?.(error)
      },
    })

    this.client.activate()
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate()
      this.client = null
      this.subscribers.clear()
    }
  }

  subscribe(destination: string, callback: (message: any) => void) {
    if (!this.client || !this.client.connected) {
      console.warn('WebSocket not connected, storing subscription for later')
      this.subscribers.set(destination, callback)
      return
    }

    this.subscribers.set(destination, callback)

    this.client.subscribe(destination, (message) => {
      try {
        const data = JSON.parse(message.body)
        callback(data)
      } catch (error) {
        console.error('Error parsing WebSocket message:', error)
      }
    })
  }

  private resubscribeAll() {
    if (!this.client || !this.client.connected) return

    this.subscribers.forEach((callback, destination) => {
      this.client!.subscribe(destination, (message) => {
        try {
          const data = JSON.parse(message.body)
          callback(data)
        } catch (error) {
          console.error('Error parsing WebSocket message:', error)
        }
      })
    })
  }

  send(destination: string, body: any) {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected')
      return
    }

    this.client.publish({
      destination,
      body: JSON.stringify(body),
    })
  }
}

export const wsService = new WebSocketService()
