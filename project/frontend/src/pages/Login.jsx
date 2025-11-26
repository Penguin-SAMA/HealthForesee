import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/request';
import { Shield } from 'lucide-react';

export default function Login() {
  const [username, setUsername] = useState('testuser');
  const [password, setPassword] = useState('123456');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/auth/login', { username, password });
      if (res.code === 0) {
        localStorage.setItem('token', res.data.token);
        localStorage.setItem('user', JSON.stringify(res.data.user));
        navigate('/');
      } else {
        alert(res.message);
      }
    } catch (err) {
      alert('登录失败，请检查网络');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-blue-50">
      <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-md">
        <div className="flex justify-center mb-6 text-blue-600">
          <Shield size={64} />
        </div>
        <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">银发守护系统</h2>
        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">用户名</label>
            <input 
              type="text" 
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">密码</label>
            <input 
              type="password" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <button 
            type="submit" 
            className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition"
          >
            登录
          </button>
        </form>
        <div className="mt-4 text-center">
          <p className="text-gray-600">
            还没有账号？<Link to="/register" className="text-blue-600 hover:underline">立即注册</Link>
          </p>
        </div>
        <p className="mt-4 text-center text-sm text-gray-500">测试账号: testuser / 123456</p>
      </div>
    </div>
  );
}