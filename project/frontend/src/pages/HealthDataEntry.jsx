import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/request';
import { ArrowLeft, Heart, Activity } from 'lucide-react';

export default function HealthDataEntry() {
  const navigate = useNavigate();
  const [healthData, setHealthData] = useState({
    username: '',
    heartRate: '',
    bloodSugar: '',
    healthStatus: 'NORMAL'
  });
  
  const [familyMembers, setFamilyMembers] = useState([]);

  useEffect(() => {
    // 获取家庭成员列表，用于下拉选择
    const fetchFamilyMembers = async () => {
      try {
        const res = await api.get('/family/members');
        if (res.code === 0) {
          setFamilyMembers(res.data.members);
        }
      } catch (err) {
        console.error('获取家庭成员失败:', err);
      }
    };
    
    fetchFamilyMembers();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setHealthData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/dashboard/health-data', healthData);
      if (res.code === 0) {
        alert('健康数据录入成功');
        navigate('/');
      } else {
        alert(res.message || '录入失败');
      }
    } catch (err) {
      console.error('录入失败:', err);
      alert('录入失败，请稍后重试');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white p-4 shadow-sm flex items-center gap-3">
        <button onClick={() => navigate(-1)} className="p-1 hover:bg-gray-100 rounded">
          <ArrowLeft size={20} />
        </button>
        <h1 className="font-bold text-lg">健康数据录入</h1>
      </div>

      <div className="max-w-md mx-auto p-4 mt-4">
        <div className="bg-white p-6 rounded-xl shadow-sm">
          <div className="flex justify-center mb-6">
            <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center text-red-600">
              <Heart size={32} />
            </div>
          </div>
          
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-gray-700 text-sm font-bold mb-2">用户</label>
              {familyMembers.length > 0 ? (
                <select
                  name="username"
                  value={healthData.username}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                >
                  <option value="">请选择用户</option>
                  {familyMembers.map(member => (
                    <option key={member.userId} value={member.name}>
                      {member.name} ({member.relation})
                    </option>
                  ))}
                </select>
              ) : (
                <input
                  type="text"
                  name="username"
                  value={healthData.username}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="请输入用户名"
                  required
                />
              )}
            </div>
            
            <div>
              <label className="block text-gray-700 text-sm font-bold mb-2">心率 (bpm)</label>
              <input
                type="number"
                name="heartRate"
                value={healthData.heartRate}
                onChange={handleChange}
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="请输入心率"
              />
            </div>
            
            <div>
              <label className="block text-gray-700 text-sm font-bold mb-2">血糖 (mmol/L)</label>
              <input
                type="number"
                step="0.1"
                name="bloodSugar"
                value={healthData.bloodSugar}
                onChange={handleChange}
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="请输入血糖值"
              />
            </div>
            
            <div>
              <label className="block text-gray-700 text-sm font-bold mb-2">健康状态</label>
              <select
                name="healthStatus"
                value={healthData.healthStatus}
                onChange={handleChange}
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="NORMAL">正常</option>
                <option value="WARNING">警告</option>
                <option value="DANGER">危险</option>
              </select>
            </div>
            
            <button 
              type="submit" 
              className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition font-medium"
            >
              录入数据
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}