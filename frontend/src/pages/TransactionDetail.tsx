import { useEffect, useState } from 'react'
import { useParams, useNavigate, useLocation } from 'react-router-dom'
import { ArrowLeft, RefreshCw, RotateCcw, CheckCircle, XCircle } from 'lucide-react'
import { transactionApi } from '../utils/api'
import type { Transaction } from '../types'
import { format } from 'date-fns'
import toast from 'react-hot-toast'
import { generateMockTransactions } from '../utils/mockData'

export default function TransactionDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [transaction, setTransaction] = useState<Transaction | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (id) {
      loadTransaction()
    }
  }, [id])

  const loadTransaction = async () => {
    try {
      setLoading(true)
      
      // Instead of API call, generate a mock transaction
      const mockTransactions = generateMockTransactions(10);
      // Find a random transaction or create one if ID doesn't match
      const mockTransaction = mockTransactions.find(tx => tx.id === id) || mockTransactions[0];
      
      // Add some rule evaluations for detailed view
      if (!mockTransaction.rule_evaluations) {
        mockTransaction.rule_evaluations = [
          {
            rule_id: "rule_high_amount_usd",
            rule_name: "High Amount Transaction",
            matched: true,
            weight: 85,
            reason: `Transaction amount ${mockTransaction.amount} ${mockTransaction.currency} exceeds threshold`
          }
        ];
        
        // Add more rules if high risk score
        if (mockTransaction.risk_score > 70) {
          mockTransaction.rule_evaluations.push({
            rule_id: "rule_unusual_location",
            rule_name: "Unusual Location",
            matched: true,
            weight: 65,
            reason: `Transaction from unusual location (${mockTransaction.location?.country})`
          });
        }
        
        // Add even more rules if very high risk
        if (mockTransaction.risk_score > 90) {
          mockTransaction.rule_evaluations.push({
            rule_id: "rule_multiple_devices",
            rule_name: "Multiple Devices in 24h",
            matched: true,
            weight: 60,
            reason: "User accessed from more than 3 devices in 24 hours"
          });
        }
      }
      
      setTransaction(mockTransaction)
    } catch (error) {
      console.error('Error loading transaction:', error)
      toast.error('Failed to load transaction')
    } finally {
      setLoading(false)
    }
  }

  const handleRetry = async () => {
    try {
      if (transaction) {
        // Mock re-evaluation: generate new risk score and potentially change status
        const newRiskScore = Math.floor(Math.random() * 100);
        let newStatus: 'PENDING' | 'APPROVED' | 'FLAGGED' | 'REJECTED' = transaction.status;
        
        // Determine new status based on risk score
        if (newRiskScore >= 80) {
          newStatus = 'REJECTED';
        } else if (newRiskScore >= 50) {
          newStatus = 'FLAGGED';
        } else {
          newStatus = 'APPROVED';
        }
        
        const updatedTransaction = {
          ...transaction,
          risk_score: newRiskScore,
          status: newStatus,
          evaluated_at: new Date().toISOString()
        };
        
        setTransaction(updatedTransaction);
        toast.success('Transaction re-evaluated successfully');
      }
    } catch (error) {
      toast.error('Failed to retry transaction');
    }
  }

  const handleReset = async () => {
    try {
      if (transaction) {
        // Reset to pending state with no risk score
        const resetTransaction = {
          ...transaction,
          status: 'PENDING' as 'PENDING', // Type assertion to match the enum
          risk_score: 0,
          evaluated_at: undefined,
          rule_evaluations: []
        };
        
        setTransaction(resetTransaction);
        toast.success('Transaction reset successfully');
      }
    } catch (error) {
      toast.error('Failed to reset transaction');
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="animate-pulse-slow text-gray-400">Loading transaction...</div>
      </div>
    )
  }

  if (!transaction) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-400">Transaction not found</p>
      </div>
    )
  }

  const getRiskColor = (score: number) => {
    if (score >= 80) return 'text-danger'
    if (score >= 50) return 'text-warning'
    return 'text-success'
  }

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'APPROVED':
        return <CheckCircle className="w-6 h-6 text-success" />
      case 'REJECTED':
        return <XCircle className="w-6 h-6 text-danger" />
      default:
        return null
    }
  }

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button
            onClick={() => navigate('/transactions')}
            className="p-2 hover:bg-white hover:bg-opacity-10 rounded-lg transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div>
            <h1 className="text-3xl font-bold mb-2">Transaction Details</h1>
            <p className="text-gray-400 font-mono">{transaction.transaction_id}</p>
          </div>
        </div>
        <div className="flex gap-3">
          <button onClick={handleRetry} className="btn-secondary flex items-center gap-2">
            <RefreshCw className="w-4 h-4" />
            Retry
          </button>
          <button onClick={handleReset} className="btn-secondary flex items-center gap-2">
            <RotateCcw className="w-4 h-4" />
            Reset
          </button>
        </div>
      </div>

      {/* Risk Score Card */}
      <div className="glass-card shadow-card p-8 text-center">
        <div className="inline-flex items-center justify-center w-32 h-32 rounded-full bg-gradient-primary bg-opacity-20 mb-4">
          <div className={`text-5xl font-bold ${getRiskColor(transaction.risk_score)}`}>
            {transaction.risk_score}
          </div>
        </div>
        <h2 className="text-2xl font-semibold mb-2">Risk Score</h2>
        <div className="flex items-center justify-center gap-2">
          {getStatusIcon(transaction.status)}
          <span className={`text-lg font-medium ${
            transaction.status === 'APPROVED' ? 'text-success' :
            transaction.status === 'REJECTED' ? 'text-danger' :
            transaction.status === 'FLAGGED' ? 'text-warning' :
            'text-gray-400'
          }`}>
            {transaction.status}
          </span>
        </div>
      </div>

      {/* Transaction Info */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="glass-card shadow-card p-6">
          <h3 className="text-xl font-semibold mb-4">Transaction Information</h3>
          <dl className="space-y-3">
            <div className="flex justify-between">
              <dt className="text-gray-400">User ID</dt>
              <dd className="font-medium">{transaction.user_id}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-400">Amount</dt>
              <dd className="font-semibold text-lg">
                {transaction.amount.toLocaleString()} {transaction.currency}
              </dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-400">Type</dt>
              <dd className="font-medium">{transaction.transaction_type || 'N/A'}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-400">Source Account</dt>
              <dd className="font-mono text-sm">{transaction.source_account || 'N/A'}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-400">Dest Account</dt>
              <dd className="font-mono text-sm">{transaction.dest_account || 'N/A'}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-400">Created</dt>
              <dd className="text-sm">{format(new Date(transaction.created_at), 'PPpp')}</dd>
            </div>
            {transaction.evaluated_at && (
              <div className="flex justify-between">
                <dt className="text-gray-400">Evaluated</dt>
                <dd className="text-sm">{format(new Date(transaction.evaluated_at), 'PPpp')}</dd>
              </div>
            )}
          </dl>
        </div>

        <div className="glass-card shadow-card p-6">
          <h3 className="text-xl font-semibold mb-4">Device & Network</h3>
          <dl className="space-y-3">
            <div className="flex justify-between">
              <dt className="text-gray-400">IP Address</dt>
              <dd className="font-mono text-sm">{transaction.ip_address || 'N/A'}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-gray-400">Device Fingerprint</dt>
              <dd className="font-mono text-sm">{transaction.device_fingerprint || 'N/A'}</dd>
            </div>
            {transaction.location && (
              <>
                <div className="flex justify-between">
                  <dt className="text-gray-400">Country</dt>
                  <dd className="font-medium">{transaction.location.country || 'N/A'}</dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-gray-400">City</dt>
                  <dd className="font-medium">{transaction.location.city || 'N/A'}</dd>
                </div>
              </>
            )}
          </dl>
        </div>
      </div>

      {/* Rule Evaluations */}
      {transaction.rule_evaluations && transaction.rule_evaluations.length > 0 && (
        <div className="glass-card shadow-card p-6">
          <h3 className="text-xl font-semibold mb-4">Triggered Rules</h3>
          <div className="space-y-3">
            {transaction.rule_evaluations.map((evaluation, index) => (
              <div
                key={index}
                className="p-4 bg-white bg-opacity-5 rounded-lg border-l-4 border-warning"
              >
                <div className="flex items-start justify-between mb-2">
                  <h4 className="font-semibold">{evaluation.rule_name}</h4>
                  <span className="badge-warning">Weight: {evaluation.weight}</span>
                </div>
                <p className="text-sm text-gray-400">{evaluation.reason}</p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
