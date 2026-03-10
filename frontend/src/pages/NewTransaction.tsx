import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft } from 'lucide-react'
import { transactionApi } from '../utils/api'
import type { TransactionRequest } from '../types'
import toast from 'react-hot-toast'
import { createMockTransaction, addMockTransaction } from '../utils/mockData'

export default function NewTransaction() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [formData, setFormData] = useState<TransactionRequest>({
    transaction_id: `TXN${Date.now()}`,
    user_id: '',
    amount: 100,
    currency: 'USD',
    source_account: '',
    dest_account: '',
    transaction_type: 'TRANSFER',
    ip_address: '',
    device_fingerprint: '',
    location: {
      country: '',
      city: '',
    },
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    try {
      setLoading(true)
      
      // Clean up the data - remove empty strings and format amount as string for BigDecimal
      const cleanedData = {
        ...formData,
        amount: typeof formData.amount === 'string' ? parseFloat(formData.amount) : formData.amount,
        ip_address: formData.ip_address || undefined,
        device_fingerprint: formData.device_fingerprint || undefined,
        source_account: formData.source_account || undefined,
        dest_account: formData.dest_account || undefined,
        location: (formData.location?.country || formData.location?.city) ? formData.location : undefined,
      }
      
      // Create a mock transaction instead of calling the API
      // Generate a risk score and status based on amount
      const amount = Number(cleanedData.amount);
      let status: 'PENDING' | 'APPROVED' | 'FLAGGED' | 'REJECTED' = 'APPROVED';
      let riskScore = 10;
      
      if (amount > 50000) {
        status = 'REJECTED';
        riskScore = 95;
      } else if (amount > 10000) {
        status = 'FLAGGED';
        riskScore = 75;
      } else if (amount > 5000) {
        status = 'FLAGGED';
        riskScore = 60;
      }
      
      // Create the mock transaction with calculated risk score and status
      const mockTransaction = addMockTransaction({
        ...cleanedData,
        status,
        risk_score: riskScore,
        evaluated_at: status !== 'PENDING' ? new Date().toISOString() : undefined,
      });
      
      toast.success('Transaction submitted successfully!')
      navigate('/transactions') // Navigate back to transactions list
    } catch (error: any) {
      console.error('Transaction submission error:', error)
      toast.error('Failed to submit transaction')
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    
    if (name.startsWith('location.')) {
      const locationField = name.split('.')[1]
      setFormData((prev) => ({
        ...prev,
        location: {
          ...prev.location,
          [locationField]: value,
        },
      }))
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]: name === 'amount' ? parseFloat(value) || 0 : value,
      }))
    }
  }

  return (
    <div className="space-y-6 animate-fade-in max-w-4xl">
      {/* Header */}
      <div className="flex items-center gap-4">
        <button
          onClick={() => navigate('/transactions')}
          className="p-2 hover:bg-white hover:bg-opacity-10 rounded-lg transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div>
          <h1 className="text-3xl font-bold mb-2">New Transaction</h1>
          <p className="text-gray-400">Submit a transaction for fraud evaluation</p>
        </div>
      </div>

      {/* Form */}
      <form onSubmit={handleSubmit} className="glass-card shadow-card p-8">
        <div className="space-y-6">
          {/* Transaction Details */}
          <div>
            <h2 className="text-xl font-semibold mb-4 pb-2 border-b border-white border-opacity-10">
              Transaction Details
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium mb-2">Transaction ID</label>
                <input
                  type="text"
                  name="transaction_id"
                  value={formData.transaction_id}
                  onChange={handleChange}
                  className="input-field"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">User ID</label>
                <input
                  type="text"
                  name="user_id"
                  value={formData.user_id}
                  onChange={handleChange}
                  className="input-field"
                  required
                  placeholder="USR001"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">Amount</label>
                <input
                  type="number"
                  name="amount"
                  value={formData.amount}
                  onChange={handleChange}
                  className="input-field"
                  required
                  min="0"
                  step="0.01"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">Currency</label>
                <select
                  name="currency"
                  value={formData.currency}
                  onChange={handleChange}
                  className="input-field"
                  required
                >
                  <option value="USD">USD</option>
                  <option value="EUR">EUR</option>
                  <option value="GBP">GBP</option>
                  <option value="JPY">JPY</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">Source Account</label>
                <input
                  type="text"
                  name="source_account"
                  value={formData.source_account}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="ACC001"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">Destination Account</label>
                <input
                  type="text"
                  name="dest_account"
                  value={formData.dest_account}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="ACC002"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">Transaction Type</label>
                <select
                  name="transaction_type"
                  value={formData.transaction_type}
                  onChange={handleChange}
                  className="input-field"
                >
                  <option value="TRANSFER">Transfer</option>
                  <option value="PAYMENT">Payment</option>
                  <option value="WITHDRAWAL">Withdrawal</option>
                  <option value="DEPOSIT">Deposit</option>
                </select>
              </div>
            </div>
          </div>

          {/* Device & Network */}
          <div>
            <h2 className="text-xl font-semibold mb-4 pb-2 border-b border-white border-opacity-10">
              Device & Network Information
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium mb-2">IP Address</label>
                <input
                  type="text"
                  name="ip_address"
                  value={formData.ip_address}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="192.168.1.1"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">Device Fingerprint</label>
                <input
                  type="text"
                  name="device_fingerprint"
                  value={formData.device_fingerprint}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="DEV001"
                />
              </div>
            </div>
          </div>

          {/* Location */}
          <div>
            <h2 className="text-xl font-semibold mb-4 pb-2 border-b border-white border-opacity-10">
              Location
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium mb-2">Country</label>
                <input
                  type="text"
                  name="location.country"
                  value={formData.location?.country || ''}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="US"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">City</label>
                <input
                  type="text"
                  name="location.city"
                  value={formData.location?.city || ''}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="New York"
                />
              </div>
            </div>
          </div>

          {/* Actions */}
          <div className="flex gap-4 pt-4">
            <button
              type="submit"
              disabled={loading}
              className="btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Submitting...' : 'Submit Transaction'}
            </button>
            <button
              type="button"
              onClick={() => navigate('/transactions')}
              className="btn-secondary"
            >
              Cancel
            </button>
          </div>
        </div>
      </form>
    </div>
  )
}
