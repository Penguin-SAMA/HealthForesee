import axios from 'axios';

const api = axios.create({
  baseURL: '/api', // 配合 vite proxy
  timeout: 5000
});

// 请求拦截器：添加 Token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器
api.interceptors.response.use(
  res => res.data,
  err => {
    console.error('API Error:', err);
    if (err.response && err.response.status === 401) {
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

export default api;