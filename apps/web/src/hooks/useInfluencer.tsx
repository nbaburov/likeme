/* eslint-disable @typescript-eslint/no-explicit-any */
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { axiosInstance, publicAxiosInstance } from "@/lib/axios";
import {
	InfluencerResponse,
	CreateInfluencerRequest,
	UpdateInfluencerRequest,
	ConnectInstagramRequest,
	CompleteSetupRequest,
} from "../dto/InfluencerDTO";
import { ApiError } from "../dto/ErrorDTO";
import { useErrorToast } from "./useErrorToast";
import { toast } from "react-toastify";
import { useAuth } from "@/hooks/useAuth";

const INFLUENCERS_QUERY_KEY = "influencers";
const PUBLIC_INFLUENCERS_QUERY_KEY = "public-influencers";

export const useInfluencer = () => {
	const queryClient = useQueryClient();
	const { showError } = useErrorToast();

	// Queries
	const influencersQuery = useQuery<InfluencerResponse[], ApiError>({
		queryKey: [INFLUENCERS_QUERY_KEY],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<InfluencerResponse[]>(
					"/influencers"
				);
				return data;
			} catch (error: any) {
				console.log(error);
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_INFLUENCERS",
					"Failed to fetch influencers",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});

	// Mutations
	const createMutation = useMutation<
		InfluencerResponse,
		ApiError,
		CreateInfluencerRequest
	>({
		mutationFn: async (influencer) => {
			try {
				const { data } = await axiosInstance.post(
					"/influencers",
					influencer
				);
				return data;
			} catch (error: any) {
				console.log(error);
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_CREATE_INFLUENCER",
					"Failed to create influencer",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: [INFLUENCERS_QUERY_KEY],
			});
			toast.success("Influencer created successfully");
		},
		onError: showError,
	});

	const updateMutation = useMutation<
		InfluencerResponse,
		ApiError,
		{ id: number; influencer: UpdateInfluencerRequest }
	>({
		mutationFn: async ({ id, influencer }) => {
			try {
				const { data } = await axiosInstance.put(
					`/influencers/${id}`,
					influencer
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_UPDATE_INFLUENCER",
					"Failed to update influencer",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: [INFLUENCERS_QUERY_KEY],
			});
			toast.success("Influencer updated successfully");
		},
		onError: showError,
	});

	const deleteMutation = useMutation<InfluencerResponse, ApiError, number>({
		mutationFn: async (id) => {
			try {
				const { data } = await axiosInstance.delete(
					`/influencers/${id}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_DELETE_INFLUENCER",
					"Failed to delete influencer",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: [INFLUENCERS_QUERY_KEY],
			});
			toast.success("Influencer deleted successfully");
		},
		onError: showError,
	});

	const connectInstagramMutation = useMutation<
		InfluencerResponse,
		ApiError,
		{ id: number; request: ConnectInstagramRequest }
	>({
		mutationFn: async ({ id, request }) => {
			try {
				const { data } = await axiosInstance.post(
					`/influencers/setup/instagram/${id}`,
					request
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_CONNECT_INSTAGRAM",
					"Failed to connect Instagram",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: [INFLUENCERS_QUERY_KEY],
			});
			toast.success("Instagram connected successfully");
		},
		onError: showError,
	});

	return {
		// Queries
		data: influencersQuery.data ?? [],
		isLoading: influencersQuery.isLoading,
		error: influencersQuery.error,

		// Mutations
		create: createMutation.mutateAsync,
		update: updateMutation.mutateAsync,
		delete: deleteMutation.mutateAsync,
		connectInstagram: connectInstagramMutation.mutateAsync,

		// Mutation states
		isCreating: createMutation.isPending,
		isUpdating: updateMutation.isPending,
		isConnectingInstagram: connectInstagramMutation.isPending,
		isDeleting: deleteMutation.isPending,
		// Mutation errors
		createError: createMutation.error,
		updateError: updateMutation.error,
		deleteError: deleteMutation.error,
		connectInstagramError: connectInstagramMutation.error,
	};
};

// Public operations
export const usePublicInfluencer = () => {
	const { showError } = useErrorToast();
	const { login } = useAuth();

	const publicInfluencersQuery = useQuery<InfluencerResponse[], ApiError>({
		queryKey: [PUBLIC_INFLUENCERS_QUERY_KEY],
		queryFn: async () => {
			try {
				const { data } = await publicAxiosInstance.get<
					InfluencerResponse[]
				>("/influencers");
				return data.filter(
					(influencer) => influencer.status === "ACTIVE"
				);
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_PUBLIC_INFLUENCERS",
					"Failed to fetch public influencers",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});

	const setupMutation = useMutation<
		InfluencerResponse,
		ApiError,
		CompleteSetupRequest
	>({
		mutationFn: async (request) => {
			try {
				const { data } = await publicAxiosInstance.post(
					"/influencers/setup/account",
					request
				);
				login({
					username: data.application.username,
					password: request.password,
				});
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_COMPLETE_SETUP",
					"Failed to complete setup",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			toast.success("Account setup completed successfully");
		},
		onError: showError,
	});

	return {
		// Queries
		data: publicInfluencersQuery.data ?? [],
		isLoading: publicInfluencersQuery.isLoading,
		error: publicInfluencersQuery.error,

		// Mutations
		setup: setupMutation.mutateAsync,

		// Mutation states
		isSettingUp: setupMutation.isPending,

		// Mutation errors
		setupError: setupMutation.error,
	};
};

// Single influencer query
export const useInfluencerById = (id: number) => {
	return useQuery<InfluencerResponse, ApiError>({
		queryKey: [INFLUENCERS_QUERY_KEY, id],
		queryFn: async () => {
			try {
				const { data } =
					await publicAxiosInstance.get<InfluencerResponse>(
						`/influencers/${id}`
					);
				return data;
			} catch (error: any) {
				console.log(error);
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_INFLUENCER",
					"Failed to fetch influencer",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};
