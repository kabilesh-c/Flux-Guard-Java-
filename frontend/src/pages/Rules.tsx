import { useEffect, useState } from 'react'
import { Plus, Edit, Trash2, Power, PowerOff } from 'lucide-react'
import { ruleApi } from '../utils/api'
import type { Rule } from '../types'
import toast from 'react-hot-toast'

export default function Rules() {
  const [rules, setRules] = useState<Rule[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadRules()
  }, [])

  const loadRules = async () => {
    try {
      setLoading(true)
      const response = await ruleApi.getAll()
      setRules(response.data)
    } catch (error) {
      console.error('Error loading rules:', error)
      toast.error('Failed to load rules')
    } finally {
      setLoading(false)
    }
  }

  const toggleRuleStatus = async (rule: Rule) => {
    try {
      await ruleApi.update(rule.id, { ...rule, active: !rule.active } as any)
      toast.success(`Rule ${rule.active ? 'deactivated' : 'activated'}`)
      loadRules()
    } catch (error) {
      toast.error('Failed to update rule')
    }
  }

  const deleteRule = async (id: string) => {
    if (!confirm('Are you sure you want to delete this rule?')) return
    
    try {
      await ruleApi.delete(id)
      toast.success('Rule deleted successfully')
      loadRules()
    } catch (error) {
      toast.error('Failed to delete rule')
    }
  }

  const getSeverityColor = (severity: string) => {
    switch (severity) {
      case 'CRITICAL':
        return 'badge-danger'
      case 'HIGH':
        return 'badge-warning'
      case 'MEDIUM':
        return 'badge-info'
      default:
        return 'badge-success'
    }
  }

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold mb-2">Fraud Detection Rules</h1>
          <p className="text-gray-400">Manage and configure detection rules</p>
        </div>
        <button className="btn-primary flex items-center gap-2">
          <Plus className="w-5 h-5" />
          New Rule
        </button>
      </div>

      {/* Rules Grid */}
      <div className="grid grid-cols-1 gap-6">
        {loading ? (
          <div className="text-center py-12 text-gray-400">Loading rules...</div>
        ) : rules.length === 0 ? (
          <div className="text-center py-12 text-gray-400">No rules found</div>
        ) : (
          rules.map((rule) => (
            <div
              key={rule.id}
              className={`glass-card shadow-card p-6 transition-all ${
                !rule.active ? 'opacity-50' : ''
              }`}
            >
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-2">
                    <h3 className="text-xl font-semibold">{rule.name}</h3>
                    <span className={`badge ${getSeverityColor(rule.severity)}`}>
                      {rule.severity}
                    </span>
                    <span className="badge-info">Weight: {rule.weight}</span>
                    {rule.active ? (
                      <span className="badge-success">Active</span>
                    ) : (
                      <span className="badge text-gray-400">Inactive</span>
                    )}
                  </div>
                  <p className="text-gray-400 text-sm mb-3">{rule.description}</p>
                  <div className="bg-black bg-opacity-30 rounded-lg p-3 font-mono text-sm">
                    {rule.expression}
                  </div>
                </div>
                <div className="flex gap-2 ml-4">
                  <button
                    onClick={() => toggleRuleStatus(rule)}
                    className="p-2 hover:bg-white hover:bg-opacity-10 rounded-lg transition-colors"
                    title={rule.active ? 'Deactivate' : 'Activate'}
                  >
                    {rule.active ? (
                      <PowerOff className="w-5 h-5 text-warning" />
                    ) : (
                      <Power className="w-5 h-5 text-success" />
                    )}
                  </button>
                  <button
                    className="p-2 hover:bg-white hover:bg-opacity-10 rounded-lg transition-colors"
                    title="Edit"
                  >
                    <Edit className="w-5 h-5 text-primary-500" />
                  </button>
                  <button
                    onClick={() => deleteRule(rule.id)}
                    className="p-2 hover:bg-white hover:bg-opacity-10 rounded-lg transition-colors"
                    title="Delete"
                  >
                    <Trash2 className="w-5 h-5 text-danger" />
                  </button>
                </div>
              </div>
              <div className="flex items-center gap-4 text-sm text-gray-400">
                <span>Action: {rule.action}</span>
                <span>•</span>
                <span>ID: {rule.rule_id}</span>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}
