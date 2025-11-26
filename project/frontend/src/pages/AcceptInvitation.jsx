import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import api from '../api/request';
import { Shield } from 'lucide-react';

export default function AcceptInvitation() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [invitationInfo, setInvitationInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [accepted, setAccepted] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchInvitationInfo = async () => {
      const token = searchParams.get('token');
      if (!token) {
        setLoading(false);
        setError('无效的邀请链接');
        return;
      }

      try {
        const res = await api.get(`/family/invitation-info?token=${token}`);
        if (res.code === 0) {
          setInvitationInfo(res.data);
        } else {
          setError(res.message || '获取邀请信息失败');
        }
      } catch (err) {
        console.error('获取邀请信息失败:', err);
        setError('获取邀请信息失败，请稍后重试');
      } finally {
        setLoading(false);
      }
    };

    fetchInvitationInfo();
  }, [searchParams]);

  const handleAccept = async () => {
    const token = searchParams.get('token');
    if (!token) {
      alert('无效的邀请链接');
      return;
    }

    try {
      const res = await api.post('/family/accept-invitation', { token });
      if (res.code === 0) {
        setAccepted(true);
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        alert(res.message || '接受邀请失败');
      }
    } catch (err) {
      console.error('接受邀请失败:', err);
      alert('接受邀请失败，请稍后重试');
    }
  };

  const handleReject = () => {
    alert('您已拒绝该邀请');
    navigate('/login');
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div>加载中...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-md text-center">
          <h2 className="text-xl font-bold text-red-500 mb-4">错误</h2>
          <p className="text-gray-600 mb-6">{error}</p>
          <button
            onClick={() => navigate('/login')}
            className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition"
          >
            返回登录
          </button>
        </div>
      </div>
    );
  }

  if (accepted) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-md text-center">
          <div className="text-green-500 text-5xl mb-4">✓</div>
          <h2 className="text-xl font-bold text-gray-800 mb-4">接受邀请成功</h2>
          <p className="text-gray-600 mb-6">您已成功加入 {invitationInfo?.inviterName} 的家庭组</p>
          <p className="text-gray-500 text-sm">即将跳转到登录页面...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-md">
        <div className="flex justify-center mb-6 text-blue-600">
          <Shield size={64} />
        </div>
        <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">家庭邀请</h2>
        
        {invitationInfo ? (
          <div className="space-y-6">
            <div className="bg-blue-50 p-4 rounded-lg">
              <p className="text-gray-700 text-center">
                <span className="font-bold">{invitationInfo.inviterName}</span> 邀请您作为他的
                <span className="font-bold"> {invitationInfo.relation} </span>
                加入银发守护家庭组
              </p>
            </div>
            
            <div className="bg-gray-50 p-4 rounded-lg text-sm">
              <p className="text-gray-600 mb-2">邀请信息：</p>
              <p className="text-gray-800">手机号：{invitationInfo.inviteePhone}</p>
            </div>
            
            <div className="flex flex-col gap-3">
              <button
                onClick={handleAccept}
                className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 transition font-medium"
              >
                接受邀请
              </button>
              <button
                onClick={handleReject}
                className="w-full bg-gray-200 text-gray-700 py-3 rounded-lg hover:bg-gray-300 transition font-medium"
              >
                拒绝邀请
              </button>
            </div>
          </div>
        ) : (
          <div className="text-center py-8">
            <p className="text-gray-600">无法获取邀请信息</p>
          </div>
        )}
      </div>
    </div>
  );
}