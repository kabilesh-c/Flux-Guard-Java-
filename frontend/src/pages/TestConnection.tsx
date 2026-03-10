import { useEffect, useState } from 'react'
import axios from 'axios'

export default function TestConnection() {
  const [status, setStatus] = useState('Testing...')
  const [details, setDetails] = useState<any>(null)

  useEffect(() => {
    testConnection()
  }, [])

  const testConnection = async () => {
    try {
      // Test 1: Backend health
      const healthRes = await axios.get('http://localhost:8080/actuator/health')
      console.log('Health check:', healthRes.data)
      
      // Test 2: Dashboard API
      const dashboardRes = await axios.get('http://localhost:8080/api/dashboard/summary')
      console.log('Dashboard API:', dashboardRes.data)
      
      setStatus('✅ Connection successful!')
      setDetails({
        health: healthRes.data,
        dashboard: dashboardRes.data
      })
    } catch (error: any) {
      console.error('Connection error:', error)
      setStatus('❌ Connection failed!')
      setDetails({
        error: error.message,
        response: error.response?.data,
        status: error.response?.status
      })
    }
  }

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mb-4">{status}</h1>
      <pre className="bg-gray-800 p-4 rounded text-sm overflow-auto">
        {JSON.stringify(details, null, 2)}
      </pre>
      <button 
        onClick={testConnection}
        className="mt-4 px-4 py-2 bg-blue-500 text-white rounded"
      >
        Test Again
      </button>
    </div>
  )
}
