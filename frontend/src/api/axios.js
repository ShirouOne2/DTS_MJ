import axios from "axios";

// Point this at your Spring Boot backend.
// Set VITE_API_BASE_URL in a .env file (e.g. .env.local) to override the default.
// Example .env.local:
//   VITE_API_BASE_URL=http://localhost:8080/api
const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",
    headers: {
        "Content-Type": "application/json",
    },
});

// TODO: once you add authentication, attach the token here, e.g.:
// api.interceptors.request.use((config) => {
//     const token = localStorage.getItem("token");
//     if (token) config.headers.Authorization = `Bearer ${token}`;
//     return config;
// });

export default api;
