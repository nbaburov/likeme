/* eslint-disable @typescript-eslint/no-explicit-any */
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { axiosInstance, publicAxiosInstance } from "@/lib/axios";
import {
	ClientResponse,
	CreateClientRequest,
	UpdateClientRequest,
} from "../dto/ClientDTO";
import { ApiError } from "../dto/ErrorDTO";
import { useErrorToast } from "./useErrorToast";
import { toast } from "react-toastify";
// import { useAuth } from "@/hooks/useAuth";

const CLIENTS_QUERY_KEY = "clients";

export const useClient = () => {
	const queryClient = useQueryClient();
	const { showError } = useErrorToast();

	// Queries
	const clientsQuery = useQuery<ClientResponse[], ApiError>({
		queryKey: [CLIENTS_QUERY_KEY],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<ClientResponse[]>(
					"/clients"
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_CLIENTS",
					"Failed to fetch clients",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});

	// Mutations
	const updateMutation = useMutation<
		ClientResponse,
		ApiError,
		{ id: number; client: UpdateClientRequest }
	>({
		mutationFn: async ({ id, client }) => {
			try {
				const { data } = await axiosInstance.put<ClientResponse>(
					`/clients/${id}`,
					client
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_UPDATE_CLIENT",
					"Failed to update client",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [CLIENTS_QUERY_KEY] });
			toast.success("Client updated successfully");
		},
		onError: showError,
	});

	const deleteMutation = useMutation<void, ApiError, number>({
		mutationFn: async (id) => {
			try {
				await axiosInstance.delete(`/clients/${id}`);
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_DELETE_CLIENT",
					"Failed to delete client",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [CLIENTS_QUERY_KEY] });
			toast.success("Client deleted successfully");
		},
		onError: showError,
	});

	return {
		// Queries
		data: clientsQuery.data ?? [],
		isLoading: clientsQuery.isLoading,
		error: clientsQuery.error,

		// Mutations
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

// Public operations
export const usePublicClient = () => {
	const { showError } = useErrorToast();
	// const { login } = useAuth();

	const createMutation = useMutation<
		ClientResponse,
		ApiError,
		CreateClientRequest
	>({
		mutationFn: async (request) => {
			try {
				const { data } = await publicAxiosInstance.post<ClientResponse>(
					"/clients",
					request
				);
				// login({
				// 	username: request.username,
				// 	password: request.password,
				// });
				return data;
			} catch (error: any) {
				console.log(error);
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_CREATE_CLIENT",
					"Failed to create client account",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			toast.success("Account created successfully");
		},
		onError: showError,
	});

	return {
		// Mutations
		create: createMutation.mutateAsync,

		// Mutation states
		isCreating: createMutation.isPending,

		// Mutation errors
		createError: createMutation.error,
	};
};

// Single client query
export const useClientById = (id: number) => {
	return useQuery<ClientResponse, ApiError>({
		queryKey: [CLIENTS_QUERY_KEY, id],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<ClientResponse>(
					`/clients/${id}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_CLIENT",
					"Failed to fetch client",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};
