import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import authService from '../services/auth';

const Register = () => {
  const [user, setUser] = useState({ nombre: '', email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrorMsg('');
    setSuccessMsg('');

    try {
      await authService.register(user);
      setLoading(false);
      setSuccessMsg('¡Registro enviado! Revisa tu email para confirmar tu cuenta.');
      setUser({ nombre: '', email: '', password: '' });
    } catch (err) {
      setLoading(false);
      setErrorMsg(err.response?.data?.message || 'Error al registrarse. Inténtalo de nuevo.');
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-blob" style={{ top: '-15%', right: '-10%' }}></div>
      <div className="auth-blob" style={{ bottom: '-15%', left: '-10%', background: 'rgba(58, 123, 213, 0.2)' }}></div>

      <div className="glass-card">
        <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
          <h1 className="brand" style={{ fontSize: '2rem' }}>Inventary</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem', fontWeight: 500 }}>Crea tu cuenta de administrador</p>
        </div>

        <h2 style={{ marginBottom: '2rem', fontSize: '1.6rem' }}>Únete al sistema</h2>

        {errorMsg && <div className="alert alert-error" style={{ display: 'block' }}>{errorMsg}</div>}
        {successMsg && <div className="alert alert-success" style={{ display: 'block' }}>{successMsg}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group" style={{ marginBottom: '1.2rem' }}>
            <label style={{ display: 'block', marginBottom: '0.8rem', fontSize: '0.9rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Nombre Completo</label>
            <div className="input-with-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
              <input type="text" name="nombre" value={user.nombre} onChange={handleChange} placeholder="Juan Pérez" required />
            </div>
          </div>

          <div className="form-group" style={{ marginBottom: '1.2rem' }}>
            <label style={{ display: 'block', marginBottom: '0.8rem', fontSize: '0.9rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Correo Electrónico</label>
            <div className="input-with-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
              <input type="email" name="email" value={user.email} onChange={handleChange} placeholder="nombre@ejemplo.com" required />
            </div>
          </div>
          
          <div className="form-group" style={{ marginBottom: '2rem' }}>
            <label style={{ display: 'block', marginBottom: '0.8rem', fontSize: '0.9rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Contraseña Segura</label>
            <div className="input-with-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
              <input type="password" name="password" value={user.password} onChange={handleChange} placeholder="Mínimo 8 caracteres" required />
            </div>
          </div>

          <button type="submit" disabled={loading}>
            {loading ? 'Creando cuenta...' : 'Registrarme ahora'}
          </button>
        </form>

        <div className="footer-text" style={{ marginTop: '2rem', textAlign: 'center', color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
            ¿Ya tienes cuenta? <Link to="/login" style={{ color: 'var(--accent)', fontWeight: 700, textDecoration: 'none' }}>Inicia sesión</Link>
        </div>
      </div>
    </div>
  );
};

export default Register;
