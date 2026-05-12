import React from 'react';

const Dashboard = () => {
  const stats = {
    totalProducts: 124,
    lowStock: 8,
    totalSuppliers: 12
  };

  return (
    <>
      <header style={{ marginBottom: '4rem' }}>
          <h2 style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>Panel de Control</h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>Visión general de tu inventario en tiempo real.</p>
      </header>

      <div className="stats-grid">
          <div className="stat-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <p className="stat-label">Total Productos</p>
                  <div style={{ padding: '8px', borderRadius: '12px', background: 'rgba(0, 210, 255, 0.1)', color: 'var(--accent)' }}>
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path></svg>
                  </div>
              </div>
              <p className="stat-value">{stats.totalProducts}</p>
              <p style={{ color: 'var(--success)', fontSize: '0.85rem', marginTop: '1rem', fontWeight: 600 }}>+12% desde el último mes</p>
          </div>

          <div className="stat-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <p className="stat-label">Stock Bajo</p>
                  <div style={{ padding: '8px', borderRadius: '12px', background: 'rgba(255, 75, 92, 0.1)', color: 'var(--error)' }}>
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
                  </div>
              </div>
              <p className="stat-value" style={{ color: 'var(--error)' }}>{stats.lowStock}</p>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', marginTop: '1rem' }}>Requiere atención inmediata</p>
          </div>

          <div className="stat-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <p className="stat-label">Proveedores</p>
                  <div style={{ padding: '8px', borderRadius: '12px', background: 'rgba(0, 245, 160, 0.1)', color: 'var(--success)' }}>
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle></svg>
                  </div>
              </div>
              <p className="stat-value">{stats.totalSuppliers}</p>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', marginTop: '1rem' }}>Activos en la plataforma</p>
          </div>
      </div>

      <div className="table-container" style={{ marginTop: '3rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
              <h3 style={{ fontSize: '1.5rem' }}>Actividad Reciente</h3>
              <button style={{ width: 'auto', padding: '0.6rem 1.2rem', fontSize: '0.85rem', background: 'rgba(255,255,255,0.05)', boxShadow: 'none' }}>Ver todo el historial</button>
          </div>
          <p style={{ color: 'var(--text-secondary)', lineHeight: 1.6 }}>
              Monitorización en tiempo real activada. Conectado al microservicio de auditoría para registrar cada movimiento de stock y cambios en el catálogo.
          </p>
      </div>
    </>
  );
};

export default Dashboard;
