import { useEffect, useState } from 'react';
import api from '../api/request';
import { Activity, Heart, Users, Plus, Stethoscope, User, ActivityIcon, Copy, X } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
  const [family, setFamily] = useState([]);
  const [healthData, setHealthData] = useState([]);
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [showInvitationLink, setShowInvitationLink] = useState(false);
  const [invitationLink, setInvitationLink] = useState('');
  const [inviteData, setInviteData] = useState({
    inviteePhone: '',
    relation: 'FATHER'
  });
  const navigate = useNavigate();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    const famRes = await api.get('/family/members');
    if (famRes.code === 0) setFamily(famRes.data.members);

    const healthRes = await api.get('/dashboard/children');
    if (healthRes.code === 0) setHealthData(healthRes.data.parents);
  };

  const handleInviteChange = (e) => {
    const { name, value } = e.target;
    setInviteData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleInviteSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/family/invite', inviteData);
      if (res.code === 0) {
        setInvitationLink(res.data.invitationLink);
        setShowInviteModal(false);
        setShowInvitationLink(true);
        setInviteData({
          inviteePhone: '',
          relation: 'FATHER'
        });
      } else {
        alert(res.message || '邀请发送失败');
      }
    } catch (err) {
      console.error('邀请失败:', err);
      alert('邀请发送失败，请稍后重试');
    }
  };

  const handleRemoveFamilyMember = async (relationId, memberName) => {
    if (!window.confirm(`确定要删除家庭成员 ${memberName} 吗？`)) {
      return;
    }
    
    try {
      const res = await api.delete(`/family/members/${relationId}`);
      if (res.code === 0) {
        // 重新获取家庭成员列表
        fetchData();
        alert('家庭成员删除成功');
      } else {
        alert(res.message || '删除失败');
      }
    } catch (err) {
      console.error('删除失败:', err);
      alert('删除失败，请稍后重试');
    }
  };

  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text);
    alert('邀请链接已复制到剪贴板');
  };

  return (
    <div className="min-h-screen pb-10">
      <nav className="bg-white shadow-sm p-4 mb-6">
        <div className="max-w-5xl mx-auto flex justify-between items-center">
          <h1 className="text-xl font-bold text-blue-600 flex items-center gap-2">
            <ShieldIcon /> 银发守护
          </h1>
          <div className="flex items-center gap-4">
            <button 
              onClick={() => navigate('/health-data-entry')}
              className="text-sm text-gray-600 hover:text-blue-600 flex items-center gap-1"
            >
              <ActivityIcon size={16} />
              录入健康数据
            </button>
            <button 
              onClick={() => navigate('/profile')}
              className="text-sm text-gray-600 hover:text-blue-600 flex items-center gap-1"
            >
              <User size={16} />
              个人资料
            </button>
            <button 
              onClick={() => { localStorage.clear(); navigate('/login'); }}
              className="text-sm text-gray-500 hover:text-red-500"
            >
              退出
            </button>
          </div>
        </div>
      </nav>

      <div className="max-w-5xl mx-auto px-4 space-y-8">
        {/* 健康预警区 */}
        <section>
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-bold text-gray-800 flex items-center gap-2">
              <Activity className="text-red-500" /> 健康实时监控
            </h2>
            <button 
              onClick={() => navigate('/appointment')}
              className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm flex items-center gap-2 hover:bg-blue-700"
            >
              <Stethoscope size={16} /> 一键挂号
            </button>
          </div>
          
          <div className="grid md:grid-cols-2 gap-4">
            {healthData.map(parent => (
              <div key={parent.userId} className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <h3 className="text-xl font-bold">{parent.name}</h3>
                    <span className={`text-xs px-2 py-1 rounded-full mt-1 inline-block ${
                      parent.healthStatus === 'WARNING' ? 'bg-red-100 text-red-600' : 'bg-green-100 text-green-600'
                    }`}>
                      {parent.healthStatus === 'WARNING' ? '⚠️ 需要关注' : '✅ 状态良好'}
                    </span>
                  </div>
                  <Heart className={`${parent.healthStatus === 'WARNING' ? 'text-red-500 animate-pulse' : 'text-gray-300'}`} />
                </div>
                <div className="grid grid-cols-2 gap-4 text-sm">
                  <div className="bg-gray-50 p-3 rounded-lg">
                    <p className="text-gray-500">心率</p>
                    <p className="text-2xl font-semibold">{parent.latestMetrics.heartRate} <span className="text-xs font-normal text-gray-400">bpm</span></p>
                  </div>
                  <div className="bg-gray-50 p-3 rounded-lg">
                    <p className="text-gray-500">血糖</p>
                    <p className="text-2xl font-semibold">{parent.latestMetrics.bloodSugar} <span className="text-xs font-normal text-gray-400">mmol/L</span></p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* 家庭成员列表 */}
        <section>
          <h2 className="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
            <Users className="text-blue-500" /> 家庭成员
          </h2>
          <div className="bg-white rounded-xl shadow-sm overflow-hidden">
            {family.map((member) => (
              <div key={member.relationId} className="p-4 border-b last:border-0 flex justify-between items-center hover:bg-gray-50">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 font-bold">
                    {member.name[0]}
                  </div>
                  <div>
                    <p className="font-medium">{member.name}</p>
                    <p className="text-xs text-gray-500">{member.relation}</p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <span className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded">已绑定</span>
                  <button 
                    onClick={() => handleRemoveFamilyMember(member.relationId, member.name)}
                    className="text-gray-400 hover:text-red-500"
                  >
                    <X size={16} />
                  </button>
                </div>
              </div>
            ))}
            <button 
              onClick={() => setShowInviteModal(true)}
              className="w-full py-3 text-blue-600 text-sm font-medium hover:bg-blue-50 flex items-center justify-center gap-2"
            >
              <Plus size={16} /> 邀请新成员
            </button>
          </div>
        </section>
      </div>

      {/* 邀请模态框 */}
      {showInviteModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl shadow-lg w-full max-w-md">
            <div className="p-6">
              <h3 className="text-lg font-bold mb-4">邀请家庭成员</h3>
              <form onSubmit={handleInviteSubmit}>
                <div className="mb-4">
                  <label className="block text-gray-700 text-sm font-bold mb-2">手机号</label>
                  <input
                    type="tel"
                    name="inviteePhone"
                    value={inviteData.inviteePhone}
                    onChange={handleInviteChange}
                    className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="请输入被邀请人的手机号"
                    required
                  />
                </div>
                <div className="mb-6">
                  <label className="block text-gray-700 text-sm font-bold mb-2">关系</label>
                  <select
                    name="relation"
                    value={inviteData.relation}
                    onChange={handleInviteChange}
                    className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="FATHER">父亲</option>
                    <option value="MOTHER">母亲</option>
                    <option value="GRANDFATHER">祖父</option>
                    <option value="GRANDMOTHER">祖母</option>
                    <option value="OTHER">其他</option>
                  </select>
                </div>
                <div className="flex justify-end gap-3">
                  <button
                    type="button"
                    onClick={() => setShowInviteModal(false)}
                    className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
                  >
                    取消
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                  >
                    发送邀请
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* 邀请链接展示模态框 */}
      {showInvitationLink && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl shadow-lg w-full max-w-md">
            <div className="p-6">
              <h3 className="text-lg font-bold mb-4">邀请已生成</h3>
              <p className="text-gray-600 mb-4">请将以下链接发送给被邀请人：</p>
              <div className="bg-gray-50 p-3 rounded-lg mb-4 flex justify-between items-center">
                <div className="text-sm font-mono break-all">{invitationLink}</div>
                <button 
                  onClick={() => copyToClipboard(invitationLink)}
                  className="ml-2 p-2 text-gray-500 hover:text-blue-600"
                >
                  <Copy size={16} />
                </button>
              </div>
              <div className="flex justify-end">
                <button
                  onClick={() => setShowInvitationLink(false)}
                  className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  确定
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

function ShieldIcon() {
  return (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
    </svg>
  );
}