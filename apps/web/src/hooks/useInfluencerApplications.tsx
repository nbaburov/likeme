/* eslint-disable @typescript-eslint/no-explicit-any */
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { axiosInstance, publicAxiosInstance } from "@/lib/axios";
import {
	InfluencerApplicationResponse,
	CreateInfluencerApplicationRequest,
	UpdateInfluencerApplicationRequest,
} from "../dto/InfluencerApplicationDTO";
import { ApiError } from "../dto/ErrorDTO";
import { useErrorToast } from "./useErrorToast";
import { toast } from "react-toastify";

const APPLICATIONS_QUERY_KEY = "influencerApplications";

// New public hook for unauthenticated operations
export const usePublicInfluencerApplications = () => {
	const { showError } = useErrorToast();

	const createMutation = useMutation<
		InfluencerApplicationResponse,
		ApiError,
		CreateInfluencerApplicationRequest
	>({
		mutationFn: async (application) => {
			try {
				const { data } = await publicAxiosInstance.post(
					"/influencers/applications",
					application
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_CREATE_APPLICATION",
					"Failed to create application",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			toast.success("Application submitted successfully");
		},
		onError: showError,
	});

	return {
		create: createMutation.mutateAsync,
		isCreating: createMutation.isPending,
		createError: createMutation.error,
	};
};

export const useInfluencerApplications = () => {
	const queryClient = useQueryClient();
	const { showError } = useErrorToast();

	// Queries
	const applicationsQuery = useQuery<
		InfluencerApplicationResponse[],
		ApiError
	>({
		queryKey: [APPLICATIONS_QUERY_KEY],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<
					InfluencerApplicationResponse[]
				>("/influencers/applications");
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_APPLICATIONS",
					"Failed to fetch applications",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});

	// Protected Mutations
	const updateMutation = useMutation<
		InfluencerApplicationResponse,
		ApiError,
		{ id: number; application: UpdateInfluencerApplicationRequest }
	>({
		mutationFn: async ({ id, application }) => {
			try {
				const { data } = await axiosInstance.put(
					`/influencers/applications/${id}`,
					application
				);
				return data;
			} catch (error: any) {
				console.log(error);
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_UPDATE_APPLICATION",
					"Failed to update application",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: [APPLICATIONS_QUERY_KEY],
			});
			toast.success("Application updated successfully");
		},
		onError: showError,
	});

	const deleteMutation = useMutation<void, ApiError, number>({
		mutationFn: async (id) => {
			try {
				await axiosInstance.delete(`/influencers/applications/${id}`);
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_DELETE_APPLICATION",
					"Failed to delete application",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: [APPLICATIONS_QUERY_KEY],
			});
			toast.success("Application deleted successfully");
		},
		onError: showError,
	});

	return {
		// Queries
		data: applicationsQuery.data ?? [],
		isLoading: applicationsQuery.isLoading,
		error: applicationsQuery.error,

		// Protected Mutations
		update: updateMutation.mutateAsync,
		delete: deleteMutation.mutateAsync,

		// Mutation states
		isUpdating: updateMutation.isPending,
		isDeleting: deleteMutation.isPending,

		// Mutation errors
		updateError: updateMutation.error,
		deleteError: deleteMutation.error,
	};
};

// Single application query
export const useInfluencerApplicationById = (id: number) => {
	return useQuery<InfluencerApplicationResponse, ApiError>({
		queryKey: [APPLICATIONS_QUERY_KEY, id],
		queryFn: async () => {
			try {
				const { data } =
					await axiosInstance.get<InfluencerApplicationResponse>(
						`/influencers/applications/${id}`
					);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_APPLICATION",
					"Failed to fetch application",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};
