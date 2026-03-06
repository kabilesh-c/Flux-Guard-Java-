// Type definitions for the Fraud Detection System

export interface Transaction {
  id: string
  transaction_id: string
  user_id: string
  amount: number
  currency: string
  source_account?: string
  dest_account?: string
  transaction_type?: string
  ip_address?: string
  device_fingerprint?: string
  location?: {
    country?: string
    city?: string
    lat?: number
    lon?: number
  }
  metadata?: Record<string, any>
  status: 'PENDING' | 'APPROVED' | 'FLAGGED' | 'REJECTED'
  risk_score: number
  evaluated_at?: string
  created_at: string
  updated_at: string
  rule_evaluations?: RuleEvaluation[]
}

export interface RuleEvaluation {
  rule_id: string
  rule_name: string
  matched: boolean
  weight: number
  reason: string
}

export interface Rule {
  id: string
  rule_id: string
  name: string
  description: string
  expression: string
  weight: number
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
  action: 'FLAG' | 'REJECT' | 'NOTIFY' | 'BLOCK'
  active: boolean
  created_at: string
  updated_at: string
}

export interface Alert {
  id: string
  transaction_id: string
  level: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
  title: string
  message: string
  status: 'NEW' | 'READ' | 'IN_PROGRESS' | 'RESOLVED' | 'DISMISSED'
  created_at: string
  read_at?: string
  resolved_at?: string
}

export interface DashboardSummary {
  totalTransactions: number
  flaggedTransactions: number
  rejectedTransactions: number
  approvedTransactions: number
  unreadAlerts: number
}

export interface TimeSeriesData {
  timestamp: string
  total_count: number
  flagged_count: number
  rejected_count: number
  approved_count: number
  avg_risk_score: number
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface TransactionRequest {
  transaction_id: string
  user_id: string
  amount: number | string  // Allow both number (for form state) and string (for API submission)
  currency: string
  source_account?: string
  dest_account?: string
  transaction_type?: string
  ip_address?: string
  device_fingerprint?: string
  location?: {
    country?: string
    city?: string
    lat?: number
    lon?: number
  }
  metadata?: Record<string, any>
}

export interface RuleRequest {
  rule_id: string
  name: string
  description: string
  expression: string
  weight: number
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
  action: 'FLAG' | 'REJECT' | 'NOTIFY' | 'BLOCK'
  active: boolean
}
