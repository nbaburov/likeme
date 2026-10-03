/* eslint-disable @typescript-eslint/no-explicit-any */
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { axiosInstance } from "@/lib/axios";
import {
	PlatformSettingsResponse,
	UpdateSettingsRequest,
	PlatformSettingType,
} from "../dto/PlatformDTO";
import { ApiError } from "../dto/ErrorDTO";
import { useErrorToast } from "./useErrorToast";
import { toast } from "react-toastify";

const SETTINGS_QUERY_KEY = "platformSettings";

export const usePlatformSettings = () => {
	const queryClient = useQueryClient();
	const { showError } = useErrorToast();

	// Queries
	const settingsQuery = useQuery<PlatformSettingsResponse[], ApiError>({
		queryKey: [SETTINGS_QUERY_KEY],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<
					PlatformSettingsResponse[]
				>("/platform/settings");
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_SETTINGS",
					"Failed to fetch platform settings",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});

	// Mutations
	const updateSettingsMutation = useMutation<
		PlatformSettingsResponse[],
		ApiError,
		UpdateSettingsRequest
	>({
		mutationFn: async (request) => {
			try {
				const { data } = await axiosInstance.put(
					"/platform/settings",
					request
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_UPDATE_SETTINGS",
					"Failed to update settings",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [SETTINGS_QUERY_KEY] });
			toast.success("Settings updated successfully");
		},
		onError: showError,
	});

	const updateSingleSettingMutation = useMutation<
		PlatformSettingsResponse,
		ApiError,
		{ type: PlatformSettingType; value: string }
	>({
		mutationFn: async ({ type, value }) => {
			try {
				const { data } = await axiosInstance.put(
					`/platform/settings/${type}`,
					value,
					{
						headers: { "Content-Type": "text/plain" },
					}
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_UPDATE_SETTING",
					`Failed to update setting: ${type}`,
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [SETTINGS_QUERY_KEY] });
			toast.success("Setting updated successfully");
		},
		onError: showError,
	});

	return {
		// Queries
		data: settingsQuery.data ?? [],
		isLoading: settingsQuery.isLoading,
		error: settingsQuery.error,

		// Mutations
		updateSettings: updateSettingsMutation.mutateAsync,
		updateSetting: updateSingleSettingMutation.mutateAsync,

		// Mutation states
		isUpdating:
			updateSettingsMutation.isPending ||
			updateSingleSettingMutation.isPending,

		// Mutation errors
		updateError:
			updateSettingsMutation.error || updateSingleSettingMutation.error,
	};
};

// Single setting query
export const useSettingByType = (type: PlatformSettingType) => {
	return useQuery<PlatformSettingsResponse, ApiError>({
		queryKey: [SETTINGS_QUERY_KEY, type],
		queryFn: async () => {
			try {
				const { data } =
					await axiosInstance.get<PlatformSettingsResponse>(
						`/platform/settings/${type}`
					);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_SETTING",
					`Failed to fetch setting: ${type}`,
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};
