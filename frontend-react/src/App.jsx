import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
        
        {/* Rutas Privadas */}
        <Route path="/" element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }>
          <Route index element={<Navigate to="/dashboard" />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="productos" element={<div>Página de Productos (En construcción)</div>} />
          <Route path="proveedores" element={<div>Página de Proveedores (En construcción)</div>} />
          <Route path="inventario" element={<div>Página de Inventario (En construcción)</div>} />
          <Route path="auditoria" element={<div>Página de Auditoría (En construcción)</div>} />
        </Route>

        {/* Catch all */}
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;
