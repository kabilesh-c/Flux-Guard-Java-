import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Shield } from 'lucide-react'
import { authApi } from '../utils/api'
import { useStore } from '../state/store'
import toast from 'react-hot-toast'

export default function Login() {
  const navigate = useNavigate()
  const { setUser } = useStore()
  const [loading, setLoading] = useState(false)
  const [formData, setFormData] = useState({
    email: 'admin@fraud-detection.com',
    password: 'Admin@123',
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    try {
      setLoading(true)
      const response = await authApi.login(formData.email, formData.password)
      localStorage.setItem('auth_token', response.data.token)
      setUser(response.data.user)
      toast.success('Login successful!')
      navigate('/dashboard')
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-card flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* Logo & Title */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-primary rounded-2xl mb-4">
            <Shield className="w-8 h-8 text-white" />
          </div>
          <h1 className="text-3xl font-bold mb-2 bg-gradient-primary bg-clip-text text-transparent">
            Fraud Detection System
          </h1>
          <p className="text-gray-400">Sign in to access your dashboard</p>
        </div>

        {/* Login Form */}
        <form onSubmit={handleSubmit} className="glass-card shadow-card p-8">
          <div className="space-y-6">
            <div>
              <label className="block text-sm font-medium mb-2">Email Address</label>
              <input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                className="input-field"
                required
                placeholder="admin@fraud-detection.com"
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">Password</label>
              <input
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                className="input-field"
                required
                placeholder="Enter your password"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Signing in...' : 'Sign In'}
            </button>
          </div>
        </form>

        {/* Demo Credentials */}
        <div className="mt-6 glass-card p-4 text-center">
          <p className="text-sm text-gray-400 mb-2">Demo Credentials:</p>
          <p className="text-xs text-gray-500 font-mono">
            admin@fraud-detection.com / Admin@123
          </p>
        </div>

        {/* Register Link */}
        <div className="mt-4 text-center">
          <p className="text-sm text-gray-400">
            Don't have an account?{' '}
            <Link to="/register" className="text-primary-500 hover:text-primary-400 font-medium">
              Create one
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
