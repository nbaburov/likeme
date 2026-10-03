/* eslint-disable @typescript-eslint/no-explicit-any */
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { axiosInstance } from "@/lib/axios";
import { InvoiceResponse, UpdateInvoiceRequest } from "../dto/InvoiceDTO";
import { ApiError } from "../dto/ErrorDTO";
import { useErrorToast } from "./useErrorToast";
import { toast } from "react-toastify";

const INVOICES_QUERY_KEY = "invoices";

export const useInvoices = () => {
	const queryClient = useQueryClient();
	const { showError } = useErrorToast();

	// Queries
	const invoicesQuery = useQuery<InvoiceResponse[], ApiError>({
		queryKey: [INVOICES_QUERY_KEY],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<InvoiceResponse[]>(
					"/invoices"
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_INVOICES",
					"Failed to fetch invoices",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});

	// Mutations
	const updateMutation = useMutation<
		InvoiceResponse,
		ApiError,
		{ id: number; invoice: UpdateInvoiceRequest }
	>({
		mutationFn: async ({ id, invoice }) => {
			try {
				const { data } = await axiosInstance.put(
					`/invoices/${id}`,
					invoice
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_UPDATE_INVOICE",
					"Failed to update invoice",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [INVOICES_QUERY_KEY] });
			toast.success("Invoice updated successfully");
		},
		onError: showError,
	});

	const deleteMutation = useMutation<void, ApiError, number>({
		mutationFn: async (id) => {
			try {
				await axiosInstance.delete(`/invoices/${id}`);
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_DELETE_INVOICE",
					"Failed to delete invoice",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [INVOICES_QUERY_KEY] });
			toast.success("Invoice deleted successfully");
		},
		onError: showError,
	});

	const payMutation = useMutation<InvoiceResponse, ApiError, number>({
		mutationFn: async (id) => {
			try {
				const { data } = await axiosInstance.post<InvoiceResponse>(
					`/invoices/${id}/pay`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_PAY_INVOICE",
					"Failed to pay invoice",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [INVOICES_QUERY_KEY] });
			toast.success("Invoice paid successfully");
		},
		onError: showError,
	});

	return {
		// Queries
		data: invoicesQuery.data ?? [],
		isLoading: invoicesQuery.isLoading,
		error: invoicesQuery.error,

		// Mutations
		update: updateMutation.mutateAsync,
		delete: deleteMutation.mutateAsync,
		pay: payMutation.mutateAsync,

		// Mutation states
		isUpdating: updateMutation.isPending,
		isDeleting: deleteMutation.isPending,
		isPaying: payMutation.isPending,

		// Mutation errors
		updateError: updateMutation.error,
		deleteError: deleteMutation.error,
		payError: payMutation.error,
	};
};

// Single invoice queries
export const useInvoiceById = (id: number) => {
	return useQuery<InvoiceResponse, ApiError>({
		queryKey: [INVOICES_QUERY_KEY, id],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<InvoiceResponse>(
					`/invoices/${id}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_INVOICE",
					"Failed to fetch invoice",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};

export const useInvoiceByOrderId = (orderId: number) => {
	return useQuery<InvoiceResponse, ApiError>({
		queryKey: [INVOICES_QUERY_KEY, "order", orderId],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<InvoiceResponse>(
					`/invoices/order/${orderId}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_INVOICE_BY_ORDER",
					"Failed to fetch invoice by order",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};

export const useInvoicesByClientId = (clientId: number) => {
	return useQuery<InvoiceResponse[], ApiError>({
		queryKey: [INVOICES_QUERY_KEY, 'client', clientId],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<InvoiceResponse[]>(
					`/invoices/client/${clientId}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_CLIENT_INVOICES",
					"Failed to fetch client invoices",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};

export const useInvoicesByInfluencer = (influencerId: number) => {
	return useQuery<InvoiceResponse[], ApiError>({
		queryKey: [INVOICES_QUERY_KEY, 'influencer', influencerId],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<InvoiceResponse[]>(
					`/invoices/influencer/${influencerId}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_INFLUENCER_INVOICES",
					"Failed to fetch influencer invoices",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};
