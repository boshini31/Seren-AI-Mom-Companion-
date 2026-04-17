import axios from 'axios';

const API_BASE = process.env.REACT_APP_API_URL || '/api';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
  timeout: 30000,
});

export const sendMessage = async (message, sessionId) => {
  const response = await api.post('/chat', { message, sessionId });
  return response.data;
};

export const getHistory = async (sessionId) => {
  const response = await api.get(`/history/${sessionId}`);
  return response.data;
};

export const clearHistory = async (sessionId) => {
  const response = await api.delete(`/history/${sessionId}`);
  return response.data;
};

export default api;
