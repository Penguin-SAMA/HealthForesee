import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Appointment from './pages/Appointment';
import UserProfile from './pages/UserProfile';
import AcceptInvitation from './pages/AcceptInvitation';
import HealthDataEntry from './pages/HealthDataEntry';

function PrivateRoute({ children }) {
  const token = localStorage.getItem('token');
  return token ? children : <Navigate to="/login" />;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route 
          path="/" 
          element={
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          } 
        />
        <Route 
          path="/appointment" 
          element={
            <PrivateRoute>
              <Appointment />
            </PrivateRoute>
          } 
        />
        <Route 
          path="/profile" 
          element={
            <PrivateRoute>
              <UserProfile />
            </PrivateRoute>
          } 
        />
        <Route path="/accept-invitation" element={<AcceptInvitation />} />
        <Route 
          path="/health-data-entry" 
          element={
            <PrivateRoute>
              <HealthDataEntry />
            </PrivateRoute>
          } 
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;