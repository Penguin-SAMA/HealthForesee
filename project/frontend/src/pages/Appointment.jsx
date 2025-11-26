import { useState } from 'react';
import api from '../api/request';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, MapPin } from 'lucide-react';

export default function Appointment() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [symptom, setSymptom] = useState('');
  const [recs, setRecs] = useState([]);

  // 1. 获取推荐
  const handleRecommend = async () => {
    if (!symptom) return;
    const res = await api.post('/appointments/recommendations', {
      patientUserId: 'u_parent_1',
      symptomText: symptom
    });
    if (res.code === 0) {
      setRecs(res.data.recommendations);
      setStep(2);
    }
  };

  // 2. 确认挂号
  const handleBook = async (hospitalId, departmentId) => {
    const res = await api.post('/appointments/quick', {
      patientUserId: 'u_parent_1',
      hospitalId,
      departmentId,
      symptomText: symptom
    });
    
    if (res.code === 0) {
      alert(`挂号成功！单号: ${res.data.appointmentId}`);
      navigate('/');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white p-4 shadow-sm flex items-center gap-3">
        <button onClick={() => navigate(-1)} className="p-1 hover:bg-gray-100 rounded">
          <ArrowLeft size={20} />
        </button>
        <h1 className="font-bold text-lg">智能导诊</h1>
      </div>

      <div className="max-w-md mx-auto p-4 mt-4">
        {step === 1 && (
          <div className="bg-white p-6 rounded-xl shadow-sm">
            <h2 className="font-bold text-lg mb-4">请描述老人的症状</h2>
            <textarea
              className="w-full p-3 border rounded-lg mb-4 h-32 resize-none focus:ring-2 focus:ring-blue-500 focus:outline-none"
              placeholder="例如：最近总是感觉胸闷，上楼梯喘气..."
              value={symptom}
              onChange={(e) => setSymptom(e.target.value)}
            ></textarea>
            <button 
              onClick={handleRecommend}
              disabled={!symptom}
              className="w-full bg-blue-600 text-white py-3 rounded-lg font-medium disabled:bg-gray-300"
            >
              分析并推荐医院
            </button>
          </div>
        )}

        {step === 2 && (
          <div>
            <h2 className="font-bold text-gray-700 mb-4 px-1">为您推荐的方案</h2>
            <div className="space-y-3">
              {recs.map((rec, idx) => (
                <div key={idx} className="bg-white p-5 rounded-xl shadow-sm border border-gray-100">
                  <div className="flex justify-between items-start mb-2">
                    <h3 className="font-bold text-lg">{rec.hospitalName}</h3>
                    <span className="text-blue-600 text-sm font-medium bg-blue-50 px-2 py-1 rounded">
                      {rec.departmentName}
                    </span>
                  </div>
                  <div className="text-gray-500 text-sm mb-4 flex items-center gap-1">
                    <MapPin size={14} /> 距离 2.5km
                  </div>
                  <div className="bg-yellow-50 text-yellow-800 text-xs p-2 rounded mb-4">
                    推荐理由: {rec.reason}
                  </div>
                  <button 
                    onClick={() => handleBook(rec.hospitalId, rec.departmentId)}
                    className="w-full border border-blue-600 text-blue-600 py-2 rounded-lg font-medium hover:bg-blue-50"
                  >
                    立即挂号
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}