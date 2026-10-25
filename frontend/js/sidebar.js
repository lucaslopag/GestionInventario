// Shared sidebar initialization: user profile, role-based nav, logout
(function () {
    document.addEventListener('DOMContentLoaded', () => {
        const user = JSON.parse(localStorage.getItem('user')) || {};
        const rol = (user.rol || 'EMPLEADO').toUpperCase();

        // --- User Profile Widget ---
        const profileEl = document.getElementById('sidebarUserProfile');
        if (profileEl) {
            profileEl.innerHTML = `
                <div class="sidebar-profile" id="profileToggle">
                    <div class="profile-info">
                        <span class="profile-name">${esc(user.nombre || 'Usuario')}</span>
                        <span class="profile-role">${esc(rol)}</span>
                    </div>
                </div>
                <div class="profile-dropdown oculto" id="profileDropdown">
                    <button class="profile-action" id="openProfileModalBtn">
                        Mi Perfil
                    </button>
                    <button class="profile-action logout-action" id="logoutBtnSidebar">
                        Cerrar Sesión
                    </button>
                </div>
            `;

            document.getElementById('profileToggle').addEventListener('click', () => {
                const dd = document.getElementById('profileDropdown');
                dd.classList.toggle('oculto');
            });

            document.getElementById('logoutBtnSidebar').addEventListener('click', () => {
                localStorage.clear();
                window.location.href = 'index.html';
            });

            const openProfileBtn = document.getElementById('openProfileModalBtn');
            if (openProfileBtn) {
                openProfileBtn.addEventListener('click', () => {
                    const dd = document.getElementById('profileDropdown');
                    dd.classList.add('oculto');
                    openProfileModal(user);
                });
            }
        }

        // --- Show Usuarios link only for ADMIN ---
        const usuariosLink = document.getElementById('navUsuarios');
        if (usuariosLink) {
            usuariosLink.style.display = rol === 'ADMIN' ? 'block' : 'none';
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
            <div class="modal">
                <div class="modal-header">
                    <h3>Mi Perfil</h3>
                    <button class="close-modal" id="closeProfileModal">Cerrar</button>
                </div>
                <div id="profileMsg" class="alert mb-1"></div>
                <form id="profileForm">
                    <div class="form-group">
                        <label>Nombre</label>
                        <input type="text" id="profileNombre" value="${esc(user.nombre || '')}" required>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input class="deshabilitado" type="email" value="${esc(user.email || '')}" disabled>
                    </div>
                    <div class="form-group">
                        <label>Rol actual</label>
                        <input class="deshabilitado" type="text" value="${esc((user.rol || 'EMPLEADO').toUpperCase())}" disabled>
                    </div>
                    <hr>
                    <p class="texto-suave texto-chico mb-1">Cambiar contraseña (opcional)</p>
                    <div class="form-group">
                        <label>Nueva Contraseña</label>
                        <input type="password" id="profilePass" placeholder="Mínimo 8 caracteres, letras y números">
                    </div>
                    <div class="form-group">
                        <label>Confirmar Contraseña</label>
                        <input type="password" id="profilePassConfirm" placeholder="Repite la nueva contraseña">
                    </div>
                    <button class="mt-1" type="submit">Guardar Cambios</button>
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
            if (pass && !/^(?=.*[A-Za-z])(?=.*\d).{8,}$/.test(pass)) {
                showMsg(msgEl, 'La contraseña debe tener al menos 8 caracteres, un dígito y una letra.', 'error'); return;
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
