// 🌐 Configuración Global de la API
const API_BASE_URL = 'http://localhost:8080/api'; // Puerto del Gateway

const api = {
    // Helper para obtener cabeceras con JWT automáticamente
    _headers(isJson = true) {
        const token = localStorage.getItem('token');
        const headers = {};
        if (isJson) headers['Content-Type'] = 'application/json';
        if (token) headers['Authorization'] = `Bearer ${token}`;
        return headers;
    },

    async _handle(response) {
        if (response.status === 401) {
            localStorage.clear();
            if (!window.location.pathname.endsWith('index.html')) {
                window.location.href = 'index.html';
            }
            return;
        }

        const text = await response.text();
        let data;
        try {
            data = JSON.parse(text);
        } catch {
            data = { message: text };
        }

        if (!response.ok) {
            // Si el backend no devuelve un campo 'message', buscamos en 'error' o lanzamos el status
            const errorMsg = data.message || data.error || `Error ${response.status}: ${response.statusText}`;
            return { error: true, message: errorMsg, status: response.status };
        }

        return { ...data, success: true };
    },

    // POST
    async post(endpoint, data) {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: this._headers(),
            body: JSON.stringify(data)
        });
        return this._handle(response);
    },

    // GET
    async get(endpoint) {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            headers: this._headers(false)
        });
        return this._handle(response);
    },

    // PUT
    async put(endpoint, data) {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'PUT',
            headers: this._headers(),
            body: JSON.stringify(data)
        });
        return this._handle(response);
    },

    // DELETE
    async delete(endpoint) {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'DELETE',
            headers: this._headers(false)
        });
        return this._handle(response);
    }
};
