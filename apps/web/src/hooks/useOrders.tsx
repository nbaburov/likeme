/* eslint-disable @typescript-eslint/no-explicit-any */
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { axiosInstance } from "@/lib/axios";
import { OrderResponse, CreateOrderRequest } from "../dto/OrderDTO";
import { ApiError } from "../dto/ErrorDTO";
import { useErrorToast } from "./useErrorToast";
import { toast } from "react-toastify";

const ORDERS_QUERY_KEY = "orders";

export const useOrders = () => {
	const queryClient = useQueryClient();
	const { showError } = useErrorToast();

	// Queries
	const ordersQuery = useQuery<OrderResponse[], ApiError>({
		queryKey: [ORDERS_QUERY_KEY],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<OrderResponse[]>(
					"/orders"
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_ORDERS",
					"Failed to fetch orders",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});

	// Mutations
	const createMutation = useMutation<
		OrderResponse,
		ApiError,
		CreateOrderRequest
	>({
		mutationFn: async (order) => {
			try {
				const { data } = await axiosInstance.post("/orders", order);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_CREATE_ORDER",
					"Failed to create order",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [ORDERS_QUERY_KEY] });
			toast.success("Order created successfully");
		},
		onError: showError,
	});

	const completeMutation = useMutation<OrderResponse, ApiError, number>({
		mutationFn: async (orderId) => {
			try {
				const { data } = await axiosInstance.put(
					`/orders/${orderId}/complete`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_COMPLETE_ORDER",
					"Failed to complete order",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [ORDERS_QUERY_KEY] });
			toast.success("Order completed successfully");
		},
		onError: showError,
	});

	const deleteMutation = useMutation<void, ApiError, number>({
		mutationFn: async (id) => {
			try {
				await axiosInstance.delete(`/orders/${id}`);
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_DELETE_ORDER",
					"Failed to delete order",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [ORDERS_QUERY_KEY] });
			toast.success("Order deleted successfully");
		},
		onError: showError,
	});

	return {
		// Queries
		data: ordersQuery.data ?? [],
		isLoading: ordersQuery.isLoading,
		error: ordersQuery.error,

		// Mutations
		create: createMutation.mutateAsync,
		complete: completeMutation.mutateAsync,
		delete: deleteMutation.mutateAsync,

		// Mutation states
		isCreating: createMutation.isPending,
		isCompleting: completeMutation.isPending,
		isDeleting: deleteMutation.isPending,

		// Mutation errors
		createError: createMutation.error,
		completeError: completeMutation.error,
		deleteError: deleteMutation.error,
	};
};

// Single resource queries
export const useOrderById = (id: number) => {
	return useQuery<OrderResponse, ApiError>({
		queryKey: [ORDERS_QUERY_KEY, id],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<OrderResponse>(
					`/orders/${id}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_ORDER",
					"Failed to fetch order",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};

export const useOrdersByOffer = (offerId: number) => {
	return useQuery<OrderResponse[], ApiError>({
		queryKey: [ORDERS_QUERY_KEY, "offer", offerId],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<OrderResponse[]>(
					`/orders/offer/${offerId}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_ORDERS_BY_OFFER",
					"Failed to fetch orders by offer",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};

export const useOrdersByClient = (clientId: number) => {
	return useQuery<OrderResponse[], ApiError>({
		queryKey: [ORDERS_QUERY_KEY, "client", clientId],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<OrderResponse[]>(
					`/orders/client/${clientId}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_ORDERS_BY_CLIENT",
					"Failed to fetch orders by client",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};

export const useOrdersByInfluencer = (influencerId: number) => {
	return useQuery<OrderResponse[], ApiError>({
		queryKey: [ORDERS_QUERY_KEY, "influencer", influencerId],
		queryFn: async () => {
			try {
				const { data } = await axiosInstance.get<OrderResponse[]>(
					`/orders/influencer/${influencerId}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_ORDERS_BY_INFLUENCER",
					"Failed to fetch orders by influencer",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};
