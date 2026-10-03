/* eslint-disable @typescript-eslint/no-explicit-any */
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { AuthRequest, AuthResponse, RefreshTokenRequest } from "../dto/AuthDTO";
import { publicAxiosInstance } from "@/lib/axios";
import { ApiError } from "../dto/ErrorDTO";
import { useErrorToast } from "./useErrorToast";

const baseUrl = process.env.NEXT_PUBLIC_API_URL;
const TOKEN_KEY = process.env.NEXT_PUBLIC_TOKEN_KEY || "likeme_access_token";
const REFRESH_TOKEN_KEY =
	process.env.NEXT_PUBLIC_REFRESH_TOKEN_KEY || "likeme_refresh_token";

export type UserRole = "ADMIN" | "INFLUENCER" | "CLIENT";

interface DecodedToken {
	sub: string;
	role: UserRole;
	exp: number;
	userId: number;
	permissions?: string;
}

export const useAuth = () => {
	const router = useRouter();
	const queryClient = useQueryClient();
	const { showError } = useErrorToast();

	const decodeToken = (token: string): DecodedToken => {
		try {
			const base64Url = token.split(".")[1];
			const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
			return JSON.parse(window.atob(base64));
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_DECODE_TOKEN",
				"Failed to decode token",
				error.message,
				new Date().toISOString()
			);
		}
	};

	const getStoredToken = () => localStorage.getItem(TOKEN_KEY);
	const getStoredRefreshToken = () => localStorage.getItem(REFRESH_TOKEN_KEY);

	const setTokens = (tokens: AuthResponse) => {
		try {
			localStorage.setItem(TOKEN_KEY, tokens.accessToken);
			localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_STORE_TOKENS",
				"Failed to store tokens",
				error.message,
				new Date().toISOString()
			);
		}
	};

	const clearTokens = () => {
		try {
			localStorage.removeItem(TOKEN_KEY);
			localStorage.removeItem(REFRESH_TOKEN_KEY);
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_CLEAR_TOKENS",
				"Failed to clear tokens",
				error.message,
				new Date().toISOString()
			);
		}
	};

	const getUserId = (): number | null => {
		const token = getStoredToken();
		if (!token) return null;

		try {
			const decoded = decodeToken(token);
			return decoded.userId;
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_GET_USER_ID",
				"Failed to get user ID",
				error.message,
				new Date().toISOString()
			);
		}
	};

	const getUsername = (): string | null => {
		const token = getStoredToken();
		if (!token) return null;

		try {
			const decoded = decodeToken(token);
			return decoded.sub;
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_GET_USERNAME",
				"Failed to get username",
				error.message,
				new Date().toISOString()
			);
		}
	};

	const getUserRole = (): UserRole | null => {
		const token = getStoredToken();
		if (!token) return null;

		try {
			const decoded = decodeToken(token);
			return decoded.role;
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_GET_USER_ROLE",
				"Failed to get user role",
				error.message,
				new Date().toISOString()
			);
		}
	};

	const getAdminPermissions = (): string | null => {
		const token = getStoredToken();
		if (!token) return null;

		try {
			const decoded = decodeToken(token);
			return decoded.permissions || null;
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_GET_PERMISSIONS",
				"Failed to get admin permissions",
				error.message,
				new Date().toISOString()
			);
		}
	};

	const shouldRefreshToken = (): boolean => {
		const token = getStoredToken();
		if (!token) return false;

		try {
			const decoded = decodeToken(token);
			const timeUntilExpiry = decoded.exp * 1000 - Date.now();
			return (
				timeUntilExpiry <
				Number(process.env.NEXT_PUBLIC_TOKEN_EXPIRATION) / 4
			);
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_CHECK_TOKEN_EXPIRY",
				"Failed to check token expiration",
				error.message,
				new Date().toISOString()
			);
		}
	};

	const isAuthenticated = async (): Promise<boolean> => {
		const token = getStoredToken();
		if (!token) return false;

		try {
			const decoded = decodeToken(token);
			const isValid = decoded.exp * 1000 > Date.now();

			if (isValid && shouldRefreshToken()) {
				await refreshAccessToken();
			}

			return isValid;
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_CHECK_AUTH",
				"Failed to check authentication status",
				error.message,
				new Date().toISOString()
			);
		}
	};

	const login = async (credentials: AuthRequest): Promise<void> => {
		try {
			const { data } = await publicAxiosInstance.post<AuthResponse>(
				`${baseUrl}/auth/login`,
				credentials
			);
			setTokens(data);
			const decoded = decodeToken(data.accessToken);

			switch (decoded.role) {
				case "ADMIN":
					router.push("/admin/");
					break;
				case "INFLUENCER":
					router.push("/influencer/");
					break;
				case "CLIENT":
					router.push("/account/");
					break;
				default:
					throw new ApiError(
						"INVALID_USER_ROLE",
						"Invalid user role",
						"User role not recognized",
						new Date().toISOString()
					);
			}
		} catch (error: any) {
			clearTokens();
			if (error.response?.data) {
				throw ApiError.fromDTO(error.response.data);
			}
			throw new ApiError(
				"FAILED_TO_LOGIN",
				"Failed to login",
				error.message,
				new Date().toISOString()
			);
		}
	};

	const refreshAccessToken = async (): Promise<string> => {
		const refreshToken = getStoredRefreshToken();
		if (!refreshToken) {
			throw new ApiError(
				"NO_REFRESH_TOKEN",
				"No refresh token available",
				"Refresh token not found in storage",
				new Date().toISOString()
			);
		}

		try {
			const { data } = await publicAxiosInstance.post<AuthResponse>(
				`${baseUrl}/auth/refresh`,
				{
					refreshToken,
				} as RefreshTokenRequest
			);
			setTokens(data);
			return data.accessToken;
		} catch (error: any) {
			clearTokens();
			router.push("/sign-in");
			if (error.response?.data) {
				throw ApiError.fromDTO(error.response.data);
			}
			throw new ApiError(
				"FAILED_TO_REFRESH_TOKEN",
				"Failed to refresh token",
				error.message,
				new Date().toISOString()
			);
		}
	};

	const loginMutation = useMutation<void, ApiError, AuthRequest>({
		mutationFn: login,
		onError: showError,
	});

	const logout = () => {
		try {
			clearTokens();
			queryClient.clear();
			router.push("/");
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_LOGOUT",
				"Failed to logout",
				error.message,
				new Date().toISOString()
			);
		}
	};

	return {
		login: loginMutation.mutate,
		error: loginMutation.error,
		logout,
		isAuthenticated,
		getUserId,
		getUsername,
		getUserRole,
		getAdminPermissions,
		refreshAccessToken,
		isLoading: loginMutation.status === "pending",
		getStoredToken,
	};
};
