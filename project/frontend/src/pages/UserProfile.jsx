import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/request';
import { ArrowLeft, User } from 'lucide-react';

export default function UserProfile() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState({
    name: '',
    role: 'CHILD',
    gender: 'MALE',
    birthday: '',
    idNumberLast4: ''
  });
  
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 从localStorage获取当前用户信息作为初始值
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    // 加载用户的完整资料
    loadUserProfile();
  }, []);

  const loadUserProfile = async () => {
    // 在实际应用中，这里应该调用API获取用户的完整资料
    // 目前我们使用localStorage中的信息作为默认值
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    setProfile(prev => ({
      ...prev,
      name: user.name || '',
    }));
    setLoading(false);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProfile(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await api.put('/user/profile', profile);
      if (res.code === 0) {
        alert('资料更新成功');
        // 更新localStorage中的用户信息
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        user.name = profile.name;
        localStorage.setItem('user', JSON.stringify(user));
        navigate('/');
      } else {
        alert(res.message || '更新失败');
      }
    } catch (err) {
      console.error('更新失败:', err);
      alert('更新失败，请稍后重试');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div>加载中...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white p-4 shadow-sm flex items-center gap-3">
        <button onClick={() => navigate(-1)} className="p-1 hover:bg-gray-100 rounded">
          <ArrowLeft size={20} />
        </button>
        <h1 className="font-bold text-lg">个人资料</h1>
      </div>

      <div className="max-w-md mx-auto p-4 mt-4">
        <div className="bg-white p-6 rounded-xl shadow-sm">
          <div className="flex justify-center mb-6">
            <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center text-blue-600">
              <User size={32} />
            </div>
          </div>
          
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-gray-700 text-sm font-bold mb-2">姓名</label>
              <input
                type="text"
                name="name"
                value={profile.name}
                onChange={handleChange}
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="请输入姓名"
                required
              />
            </div>
            
            <div>
              <label className="block text-gray-700 text-sm font-bold mb-2">角色</label>
              <select
                name="role"
                value={profile.role}
                onChange={handleChange}
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="CHILD">子女</option>
                <option value="PARENT">父母</option>
              </select>
            </div>
            
            <div>
              <label className="block text-gray-700 text-sm font-bold mb-2">性别</label>
              <select
                name="gender"
                value={profile.gender}
                onChange={handleChange}
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="MALE">男</option>
                <option value="FEMALE">女</option>
              </select>
            </div>
            
            <div>
              <label className="block text-gray-700 text-sm font-bold mb-2">生日</label>
              <input
                type="date"
                name="birthday"
                value={profile.birthday}
                onChange={handleChange}
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            
            <div>
              <label className="block text-gray-700 text-sm font-bold mb-2">身份证后四位</label>
              <input
                type="text"
                name="idNumberLast4"
                value={profile.idNumberLast4}
                onChange={handleChange}
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="请输入身份证后四位"
                maxLength="4"
              />
            </div>
            
            <button 
              type="submit" 
              className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition font-medium"
            >
              保存资料
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}