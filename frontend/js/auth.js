// 🔐 Seguridad y Sesión
(function() {
    const token = localStorage.getItem('token');
    const path = window.location.pathname;
    const isLoginPage = path.endsWith('index.html') || path.endsWith('/') || path.endsWith('register.html') || path.endsWith('confirmacion.html');

    // Si no hay token y no estamos en una página pública, redirigir al login
    if (!token && !isLoginPage) {
        window.location.href = 'index.html';
        return;
    }

    // Si ya hay token e intentamos ir al login, saltar al dashboard
    if (token && isLoginPage) {
        window.location.href = 'dashboard.html';
        return;
    }

    // Función de Logout
    window.logout = function() {
        localStorage.clear();
        window.location.href = 'index.html';
    };

    // Inicializar elementos de UI comunes al cargar
    document.addEventListener('DOMContentLoaded', () => {
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', (e) => {
                e.preventDefault();
                window.logout();
            });
        }

        // Cargar nombre de usuario si existe
        const userNameEl = document.getElementById('userName');
        if (userNameEl) {
            const user = JSON.parse(localStorage.getItem('user')) || { nombre: 'Usuario' };
            userNameEl.textContent = user.nombre;
        }
    });
})();
