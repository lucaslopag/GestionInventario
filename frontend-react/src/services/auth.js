import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

const login = async (credentials) => {
  const response = await axios.post(`${API_URL}/login`, credentials);
  if (response.data.token) {
    sessionStorage.setItem('token', response.data.token);
  }
  return response.data;
};

const register = (userData) => {
  return axios.post(`${API_URL}/register`, userData);
};

const confirmAccount = (token) => {
  return axios.get(`${API_URL}/confirmar?token=${token}`);
};

const logout = () => {
  sessionStorage.removeItem('token');
  window.location.href = '/login';
};

const getToken = () => sessionStorage.getItem('token');

const isLoggedIn = () => !!getToken();

const getMe = async () => {
  const token = getToken();
  if (!token) return null;
  const response = await axios.get(`${API_URL}/me`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return response.data;
};

export default {
  login,
  register,
  confirmAccount,
  logout,
  getToken,
  isLoggedIn,
  getMe
};
