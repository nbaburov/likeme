/* eslint-disable @typescript-eslint/no-explicit-any */
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { axiosInstance, publicAxiosInstance } from "@/lib/axios";
import {
	OfferResponse,
	CreateOfferRequest,
	UpdateOfferRequest,
} from "../dto/OfferDTO";
import { ApiError } from "../dto/ErrorDTO";
import { useErrorToast } from "./useErrorToast";
import { toast } from "react-toastify";

const OFFERS_QUERY_KEY = "offers";

export const useOffers = () => {
	const queryClient = useQueryClient();
	const { showError } = useErrorToast();

	// Public Queries
	const publicOffersQuery = useQuery<OfferResponse[], ApiError>({
		queryKey: ["public", OFFERS_QUERY_KEY],
		queryFn: async () => {
			try {
				const { data } = await publicAxiosInstance.get<OfferResponse[]>(
					"/offers"
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_PUBLIC_OFFERS",
					"Failed to fetch public offers",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});

	// Protected Mutations
	const createMutation = useMutation<
		OfferResponse,
		ApiError,
		CreateOfferRequest
	>({
		mutationFn: async (offer) => {
			try {
				const { data } = await axiosInstance.post("/offers", offer);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_CREATE_OFFER",
					"Failed to create offer",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [OFFERS_QUERY_KEY] });
			toast.success("Offer created successfully");
		},
		onError: showError,
	});

	const updateMutation = useMutation<
		OfferResponse,
		ApiError,
		{ id: number; offer: UpdateOfferRequest }
	>({
		mutationFn: async ({ id, offer }) => {
			try {
				const { data } = await axiosInstance.put(
					`/offers/${id}`,
					offer
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_UPDATE_OFFER",
					"Failed to update offer",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [OFFERS_QUERY_KEY] });
			toast.success("Offer updated successfully");
		},
		onError: showError,
	});

	const deleteMutation = useMutation<void, ApiError, number>({
		mutationFn: async (id) => {
			try {
				await axiosInstance.delete(`/offers/${id}`);
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_DELETE_OFFER",
					"Failed to delete offer",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: [OFFERS_QUERY_KEY] });
			toast.success("Offer deleted successfully");
		},
		onError: showError,
	});

	return {
		// Queries
		data: publicOffersQuery.data ?? [],
		isLoading: publicOffersQuery.isLoading,
		error: publicOffersQuery.error,

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

export const useOfferById = (id: number) => {
	return useQuery<OfferResponse, ApiError>({
		queryKey: ["public", OFFERS_QUERY_KEY, id],
		queryFn: async () => {
			try {
				const { data } = await publicAxiosInstance.get<OfferResponse>(
					`/offers/${id}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_PUBLIC_OFFER",
					"Failed to fetch public offer",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};

export const useOffersByInfluencerId = (influencerId: number) => {

	return useQuery<OfferResponse[], ApiError>({
		queryKey: ["public", OFFERS_QUERY_KEY, "influencer", influencerId],
		queryFn: async () => {
			try {
				const { data } = await publicAxiosInstance.get<OfferResponse[]>(
					`/offers/influencer/${influencerId}`
				);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_INFLUENCER_OFFERS",
					"Failed to fetch influencer offers",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};
