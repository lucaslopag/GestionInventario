// 🌐 Configuración Global de la API
// Este archivo gestionará las llamadas al Gateway en el futuro

const API_BASE_URL = 'http://localhost:8080/api'; // Puerto del Gateway

const api = {
    // Helper para peticiones POST
    async post(endpoint, data) {
        const token = localStorage.getItem('token');
        const headers = {
            'Content-Type': 'application/json'
        };
        
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(data)
        });

        return response.json();
    },

    // Helper para peticiones GET
    async get(endpoint) {
        const token = localStorage.getItem('token');
        const headers = {};
        
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            headers: headers
        });

        return response.json();
    }
};
