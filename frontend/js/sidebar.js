// Shared sidebar initialization: user profile, role-based nav, logout
(function () {
    document.addEventListener('DOMContentLoaded', () => {
        const user = JSON.parse(localStorage.getItem('user')) || {};
        const rol = (user.rol || 'EMPLEADO').toUpperCase();

        // --- User Profile Widget ---
        const profileEl = document.getElementById('sidebarUserProfile');
        if (profileEl) {
            const initials = (user.nombre || 'U').split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
            profileEl.innerHTML = `
                <div class="sidebar-profile" id="profileToggle">
                    <div class="profile-avatar">${initials}</div>
                    <div class="profile-info">
                        <span class="profile-name">${user.nombre || 'Usuario'}</span>
                        <span class="profile-role">${rol}</span>
                    </div>
                    <i data-lucide="chevron-down" class="profile-chevron"></i>
                </div>
                <div class="profile-dropdown" id="profileDropdown" style="display:none;">
                    <button class="profile-action" id="openProfileModalBtn">
                        <i data-lucide="user"></i> Mi Perfil
                    </button>
                    <button class="profile-action logout-action" id="logoutBtnSidebar">
                        <i data-lucide="log-out"></i> Cerrar Sesión
                    </button>
                </div>
            `;

            document.getElementById('profileToggle').addEventListener('click', () => {
                const dd = document.getElementById('profileDropdown');
                dd.style.display = dd.style.display === 'none' ? 'block' : 'none';
            });

            document.getElementById('logoutBtnSidebar').addEventListener('click', () => {
                localStorage.clear();
                window.location.href = 'index.html';
            });

            const openProfileBtn = document.getElementById('openProfileModalBtn');
            if (openProfileBtn) {
                openProfileBtn.addEventListener('click', () => {
                    const dd = document.getElementById('profileDropdown');
                    dd.style.display = 'none';
                    openProfileModal(user);
                });
            }
        }

        // --- Show Usuarios link only for ADMIN ---
        const usuariosLink = document.getElementById('navUsuarios');
        if (usuariosLink) {
            usuariosLink.style.display = rol === 'ADMIN' ? 'flex' : 'none';
        }

        // --- Legacy logout button support ---
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', (e) => {
                e.preventDefault();
                localStorage.clear();
                window.location.href = 'index.html';
            });
        }
    });

    // --- Profile Modal ---
    function openProfileModal(user) {
        const existing = document.getElementById('profileModal');
        if (existing) { existing.style.display = 'flex'; return; }

        const overlay = document.createElement('div');
        overlay.id = 'profileModal';
        overlay.className = 'modal-overlay';
        overlay.style.display = 'flex';
        overlay.innerHTML = `
            <div class="modal" style="max-width: 440px;">
                <div class="modal-header">
                    <h3>Mi Perfil</h3>
                    <button class="close-modal" id="closeProfileModal">&times;</button>
                </div>
                <div id="profileMsg" class="alert" style="margin-bottom:1rem;"></div>
                <form id="profileForm">
                    <div class="form-group">
                        <label>Nombre</label>
                        <input type="text" id="profileNombre" value="${user.nombre || ''}" required>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" value="${user.email || ''}" disabled style="opacity:0.5; cursor: not-allowed;">
                    </div>
                    <div class="form-group">
                        <label>Rol actual</label>
                        <input type="text" value="${(user.rol || 'EMPLEADO').toUpperCase()}" disabled style="opacity:0.5; cursor: not-allowed;">
                    </div>
                    <hr style="border-color: var(--border); margin: 1.5rem 0;">
                    <p style="color: var(--text-secondary); font-size: 0.9rem; margin-bottom: 1rem;">Cambiar contraseña (opcional)</p>
                    <div class="form-group">
                        <label>Nueva Contraseña</label>
                        <input type="password" id="profilePass" placeholder="Mínimo 8 caracteres, letras y números">
                    </div>
                    <div class="form-group">
                        <label>Confirmar Contraseña</label>
                        <input type="password" id="profilePassConfirm" placeholder="Repite la nueva contraseña">
                    </div>
                    <button type="submit" style="margin-top:1rem;">Guardar Cambios</button>
                </form>
            </div>
        `;
        document.body.appendChild(overlay);

        overlay.querySelector('#closeProfileModal').onclick = () => overlay.style.display = 'none';
        overlay.addEventListener('click', (e) => { if (e.target === overlay) overlay.style.display = 'none'; });

        overlay.querySelector('#profileForm').onsubmit = async (e) => {
            e.preventDefault();
            const msgEl = document.getElementById('profileMsg');
            const nombre = document.getElementById('profileNombre').value.trim();
            const pass = document.getElementById('profilePass').value;
            const passConfirm = document.getElementById('profilePassConfirm').value;

            if (pass && pass !== passConfirm) {
                showMsg(msgEl, 'Las contraseñas no coinciden.', 'error'); return;
            }
            if (pass && pass.length < 8) {
                showMsg(msgEl, 'La contraseña debe tener al menos 8 caracteres.', 'error'); return;
            }

            const body = { nombre };
            if (pass) body.password = pass;

            try {
                const res = await api.put('/usuarios/me', body);
                if (res.error) { showMsg(msgEl, res.message, 'error'); return; }
                const updatedUser = { ...user, nombre };
                localStorage.setItem('user', JSON.stringify(updatedUser));
                showMsg(msgEl, 'Perfil actualizado correctamente.', 'success');
                setTimeout(() => location.reload(), 1200);
            } catch {
                showMsg(msgEl, 'Error al actualizar el perfil.', 'error');
            }
        };
    }

    function showMsg(el, text, type) {
        el.textContent = text;
        el.className = `alert alert-${type}`;
        el.style.display = 'block';
    }

    window._openProfileModal = openProfileModal;
})();
