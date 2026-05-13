import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import authService from '../services/auth';

const Login = () => {
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrorMsg('');

    try {
      await authService.login(credentials);
      navigate('/dashboard');
    } catch (err) {
      setLoading(false);
      setErrorMsg(err.response?.data?.message || 'Error al iniciar sesión. Comprueba tus credenciales.');
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-blob" style={{ top: '-10%', left: '-10%' }}></div>
      <div className="auth-blob" style={{ bottom: '-10%', right: '-10%', background: 'rgba(58, 123, 213, 0.2)' }}></div>

      <div className="glass-card">
        <div style={{ textAlign: 'center', marginBottom: '3rem' }}>
          <h1 className="brand">Inventary</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem', fontWeight: 500 }}>Gestión Inteligente de Inventario</p>
        </div>

        <h2 style={{ marginBottom: '2rem', fontSize: '1.8rem' }}>Bienvenido de nuevo</h2>

        {errorMsg && (
          <div className="alert alert-error" style={{ display: 'block' }}>
            {errorMsg}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group" style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', marginBottom: '0.8rem', fontSize: '0.9rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Correo Electrónico</label>
            <div className="input-with-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
              <input type="email" name="email" value={credentials.email} onChange={handleChange} placeholder="nombre@ejemplo.com" required />
            </div>
          </div>
          
          <div className="form-group" style={{ marginBottom: '2.5rem' }}>
            <label style={{ display: 'block', marginBottom: '0.8rem', fontSize: '0.9rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Contraseña</label>
            <div className="input-with-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
              <input type="password" name="password" value={credentials.password} onChange={handleChange} placeholder="••••••••" required />
            </div>
          </div>

          <button type="submit" disabled={loading}>
            {loading ? 'Iniciando sesión...' : 'Entrar al Sistema'}
          </button>
        </form>

        <div className="footer-text" style={{ marginTop: '2.5rem', textAlign: 'center', color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
        </div>
      </div>
    </div>
  );
};

export default Login;
