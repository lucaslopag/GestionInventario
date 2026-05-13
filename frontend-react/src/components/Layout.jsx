import React, { useEffect, useState } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import authService from '../services/auth';

const Layout = () => {
  const [user, setUser] = useState({ nombre: 'Usuario', rol: 'Admin' });
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    // Verificar sesión (Opcional por ahora)
    // if (!authService.isLoggedIn()) navigate('/login');
  }, []);

  const onLogout = () => {
    authService.logout();
  };

  const navLinks = [
    { path: '/dashboard', label: 'Dashboard', icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
    )},
    { path: '/productos', label: 'Productos', icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path><polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline><line x1="12" y1="22.08" x2="12" y2="12"></line></svg>
    )},
    { path: '/proveedores', label: 'Proveedores', icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle></svg>
    )},
    { path: '/inventario', label: 'Inventario', icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="23 4 23 10 17 10"></polyline><polyline points="1 20 1 14 7 14"></polyline><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path></svg>
    )},
    { path: '/auditoria', label: 'Historial', icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline></svg>
    )}
  ];

  return (
    <div className="dashboard-container">
      <header className="navbar">
        <div style={{ display: 'flex', alignItems: 'center', gap: '3rem' }}>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <h1 className="brand" style={{ fontSize: '1.8rem', lineHeight: 1 }}>Inventary</h1>
            <span style={{ color: 'var(--text-secondary)', fontSize: '0.65rem', fontWeight: 700, letterSpacing: '2px', textTransform: 'uppercase' }}>Smart Admin</span>
          </div>
          
          <nav className="nav-links-container">
            {navLinks.map(link => (
              <Link 
                key={link.path} 
                to={link.path} 
                className={`nav-link ${location.pathname === link.path ? 'active' : ''}`}
              >
                {link.icon}
                {link.label}
              </Link>
            ))}
          </nav>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', padding: '0.5rem 1rem', background: 'rgba(255,255,255,0.03)', borderRadius: '16px', border: '1px solid var(--border)' }}>
            <div style={{ width: '32px', height: '32px', borderRadius: '10px', background: 'var(--accent-gradient)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 800, fontFamily: 'Outfit', fontSize: '0.9rem' }}>
              {user.nombre.charAt(0)}
            </div>
            <div style={{ display: 'flex', flexDirection: 'column' }}>
              <p style={{ fontSize: '0.85rem', fontWeight: 700, lineHeight: 1 }}>{user.nombre}</p>
              <p style={{ fontSize: '0.7rem', color: 'var(--text-secondary)', marginTop: '2px' }}>{user.rol}</p>
            </div>
          </div>
          <button onClick={onLogout} className="btn-icon" style={{ color: 'var(--error)', background: 'rgba(255, 75, 92, 0.1)', borderRadius: '12px', width: '40px', height: '40px', display: 'flex', alignItems: 'center', justifyContent: 'center', border: 'none', cursor: 'pointer' }} title="Cerrar Sesión">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
          </button>
        </div>
      </header>

      <main className="main-content" style={{ padding: '4rem' }}>
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;
