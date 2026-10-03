/* eslint-disable @typescript-eslint/no-explicit-any */
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { axiosInstance } from "@/lib/axios";
import {
	AdminResponse,
	CreateAdminRequest,
	UpdateAdminRequest,
} from "../dto/AdminDTO";
import { ApiError } from "../dto/ErrorDTO";
import { useErrorToast } from "./useErrorToast";
import { toast } from "react-toastify";

const ADMINS_QUERY_KEY = "admins";

export const useAdmin = () => {
	const queryClient = useQueryClient();
	const { showError } = useErrorToast();

	// Queries
	const adminsQuery = useQuery<AdminResponse[], ApiError>({
		queryKey: [ADMINS_QUERY_KEY],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<AdminResponse[]>(
					"/admins"
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_ADMINS",
					"Failed to fetch admins",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});

	// Mutations
	const createMutation = useMutation<
		AdminResponse,
		ApiError,
		CreateAdminRequest
	>({
		mutationFn: async (admin) => {
			try {
				const { data } = await axiosInstance.post<AdminResponse>(
					"/admins",
					admin
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_CREATE_ADMIN",
					"Failed to create admin",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [ADMINS_QUERY_KEY] });
			toast.success("Admin created successfully");
		},
		onError: showError,
	});

	const updateMutation = useMutation<
		AdminResponse,
		ApiError,
		{ id: number; admin: UpdateAdminRequest }
	>({
		mutationFn: async ({ id, admin }) => {
			try {
				const { data } = await axiosInstance.put<AdminResponse>(
					`/admins/${id}`,
					admin
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_UPDATE_ADMIN",
					"Failed to update admin",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [ADMINS_QUERY_KEY] });
			toast.success("Admin updated successfully");
		},
		onError: showError,
	});

	const deleteMutation = useMutation<void, ApiError, number>({
		mutationFn: async (id) => {
			try {
				await axiosInstance.delete(`/admins/${id}`);
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_DELETE_ADMIN",
					"Failed to delete admin",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [ADMINS_QUERY_KEY] });
			toast.success("Admin deleted successfully");
		},
		onError: showError,
	});

	return {
		// Queries
		data: adminsQuery.data ?? [],
		isLoading: adminsQuery.isLoading,
		error: adminsQuery.error,

		// Mutations
		create: createMutation.mutateAsync,
		update: updateMutation.mutateAsync,
		delete: deleteMutation.mutateAsync,

		// Mutation states
		isCreating: createMutation.isPending,
		isUpdating: updateMutation.isPending,
		isDeleting: deleteMutation.isPending,

		// Mutation errors
		createError: createMutation.error,
		updateError: updateMutation.error,
		deleteError: deleteMutation.error,
	};
};

export const useAdminById = (id: number) => {
	return useQuery<AdminResponse, ApiError>({
		queryKey: [ADMINS_QUERY_KEY, id],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<AdminResponse>(
					`/admins/${id}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_ADMIN",
					"Failed to fetch admin",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};
