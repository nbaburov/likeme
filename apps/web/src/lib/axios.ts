import axios from "axios";
import { AuthResponse } from "@/dto/AuthDTO";

const baseURL = process.env.NEXT_PUBLIC_API_URL;

export const axiosInstance = axios.create({
	baseURL,
	headers: {
		"Content-Type": "application/json",
	},
});

// Create a separate axios instance for public endpoints
export const publicAxiosInstance = axios.create({
	baseURL: process.env.NEXT_PUBLIC_API_URL,
	headers: {
		"Content-Type": "application/json",
	},
});

// Yo, remember: interceptors.request runs BEFORE the request is sent
// Like a bouncer checking IDs before letting people in
axiosInstance.interceptors.request.use(
	async (config) => {
		const token = localStorage.getItem("likeme_access_token");
		if (!token) {
			localStorage.removeItem("likeme_refresh_token");
			return config;
		}

		try {
			// btw atob() is just base64 decoder - "a-to-b" = ascii to binary
			// window.btob is deprecated but works for now, might need to switch to Buffer.from later
			const payload = JSON.parse(atob(token.split(".")[1]));

			// JWT exp is in seconds, gotta multiply by 1000 for JS timestamp (milliseconds)
			const expirationTime = payload.exp * 1000;
			const currentTime = Date.now();

			if (expirationTime <= currentTime) {
				localStorage.removeItem("likeme_access_token");
				localStorage.removeItem("likeme_refresh_token");
				window.location.href = "/sign-in";
				return config;
			}

			if (expirationTime - currentTime < 300000) {
				const refreshToken = localStorage.getItem(
					"likeme_refresh_token"
				);
				if (!refreshToken) {
					localStorage.removeItem("likeme_access_token");
					window.location.href = "/sign-in";
					return config;
				}

				const { data } = await axios.post<AuthResponse>(
					`${baseURL}/auth/refresh`,
					{ refreshToken }
				);
				localStorage.setItem("likeme_access_token", data.accessToken);
				localStorage.setItem("likeme_refresh_token", data.refreshToken);
				config.headers.Authorization = `Bearer ${data.accessToken}`;
				return config;
			}
		} catch (error) {
			console.error("Token validation error:", error);
			localStorage.removeItem("likeme_access_token");
			localStorage.removeItem("likeme_refresh_token");
			window.location.href = "/sign-in";
			return config;
		}

		config.headers.Authorization = `Bearer ${token}`;
		return config;
	},
	(error) => Promise.reject(error)
);

// interceptors.response runs AFTER we get the response
// Like checking if your ID got rejected at the club and trying your backup
axiosInstance.interceptors.response.use(
	(response) => response,
	async (error) => {
		const originalRequest = error.config;

		// _retry flag prevents infinite loops if refresh token also fails
		// learned this the hard way - browser crashed without it lol
		if (error.response?.status === 401 && !originalRequest._retry) {
			originalRequest._retry = true;

			try {
				const refreshToken = localStorage.getItem(
					"likeme_refresh_token"
				);
				if (!refreshToken)
					throw new Error("No refresh token available");

				const { data } = await axios.post<AuthResponse>(
					`${baseURL}/auth/refresh`,
					{
						refreshToken,
					}
				);

				localStorage.setItem("likeme_access_token", data.accessToken);
				localStorage.setItem("likeme_refresh_token", data.refreshToken);

				originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
				return axiosInstance(originalRequest);
			} catch (refreshError) {
				localStorage.removeItem("likeme_access_token");
				localStorage.removeItem("likeme_refresh_token");
				window.location.href = "/sign-in";
				return Promise.reject(refreshError);
			}
		}

		return Promise.reject(error);
	}
);
