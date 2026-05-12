import React, { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import authService from '../services/auth';

const Confirm = () => {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState('loading'); // loading, success, error
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    const token = searchParams.get('token');
    if (!token) {
      setStatus('error');
      setErrorMsg('Token de confirmación no encontrado.');
      return;
    }

    authService.confirmAccount(token)
      .then(() => setStatus('success'))
      .catch(err => {
        setStatus('error');
        setErrorMsg(err.response?.data?.message || 'El enlace ha expirado o no es válido.');
      });
  }, [searchParams]);

  return (
    <div className="auth-container">
      <div className="auth-blob" style={{ top: '20%', left: '20%', width: '400px', height: '400px' }}></div>

      <div className="glass-card" style={{ textAlign: 'center', maxWidth: '550px' }}>
        <div style={{ marginBottom: '2.5rem' }}>
          <h1 className="brand" style={{ fontSize: '2.2rem' }}>Inventary</h1>
        </div>

        {status === 'loading' && (
          <div id="loading" style={{ animation: 'pulse 2s infinite' }}>
            <div style={{ width: '80px', height: '80px', borderRadius: '50%', border: '4px solid var(--border)', borderTopColor: 'var(--accent)', margin: '0 auto 2rem', animation: 'spin 1s linear infinite' }}></div>
            <h2 style={{ marginBottom: '1rem' }}>Verificando cuenta</h2>
            <p style={{ color: 'var(--text-secondary)' }}>Estamos procesando tu enlace de confirmación. Un momento...</p>
          </div>
        )}

        {status === 'success' && (
          <div id="success">
            <div style={{ width: '80px', height: '80px', background: 'rgba(0, 245, 160, 0.1)', color: 'var(--success)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 2rem', fontSize: '2.5rem', border: '1px solid rgba(0, 245, 160, 0.3)' }}>
                ✓
            </div>
            <h2 style={{ color: 'var(--success)', marginBottom: '1rem' }}>¡Cuenta Activada!</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '2.5rem' }}>Tu cuenta ha sido confirmada con éxito. Ya tienes acceso completo al sistema de gestión.</p>
            <Link to="/login"><button style={{ padding: '1.2rem 3rem' }}>Ir al Panel de Control</button></Link>
          </div>
        )}

        {status === 'error' && (
          <div id="error">
            <div style={{ width: '80px', height: '80px', background: 'rgba(255, 75, 92, 0.1)', color: 'var(--error)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 2rem', fontSize: '2.5rem', border: '1px solid rgba(255, 75, 92, 0.3)' }}>
                ✕
            </div>
            <h2 style={{ color: 'var(--error)', marginBottom: '1rem' }}>Enlace no válido</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '2.5rem' }}>{errorMsg}</p>
            <Link to="/login" style={{ color: 'var(--accent)', fontWeight: 700, textDecoration: 'none', display: 'block' }}>Volver al inicio</Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default Confirm;
