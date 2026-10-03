/* eslint-disable @typescript-eslint/no-explicit-any */
import Cookies from "js-cookie";
import { toast } from "react-toastify";
import { CreateInfluencerApplicationRequest } from "@/dto/InfluencerApplicationDTO";
import { ApiError } from "../dto/ErrorDTO";
import { useErrorToast } from "./useErrorToast";
import { useMutation } from "@tanstack/react-query";

const FORM_DATA_COOKIE = "influencer_application_form";

export const useFormPersistence = () => {
	const { showError } = useErrorToast();

	const loadMutation = useMutation<
		CreateInfluencerApplicationRequest,
		ApiError,
		void
	>({
		mutationFn: async () => {
			try {
				const saved = Cookies.get(FORM_DATA_COOKIE);
				if (!saved) return null;
				return JSON.parse(saved);
			} catch (error: any) {
				throw new ApiError(
					"FAILED_TO_LOAD_FORM",
					"Failed to load saved form",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onError: showError,
	});

	const saveMutation = useMutation<
		void,
		ApiError,
		CreateInfluencerApplicationRequest
	>({
		mutationFn: async (formData: any) => {
			try {
				Cookies.set(FORM_DATA_COOKIE, JSON.stringify(formData), {
					expires: 7,
				});
			} catch (error: any) {
				throw new ApiError(
					"FAILED_TO_SAVE_FORM",
					"Failed to save form",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			toast.success("Form data saved for 7 days");
		},
		onError: showError,
	});

	const clearMutation = useMutation<void, ApiError, void>({
		mutationFn: async () => {
			try {
				Cookies.remove(FORM_DATA_COOKIE);
			} catch (error: any) {
				throw new ApiError(
					"FAILED_TO_CLEAR_FORM",
					"Failed to clear form",
					error.message,
					new Date().toISOString()
				);
			}
		},
		onSuccess: () => {
			toast.success("Form data cleared");
		},
		onError: showError,
	});

	return {
		// Data
		data: loadMutation.data,

		// Mutations
		load: loadMutation.mutateAsync,
		save: saveMutation.mutateAsync,
		clear: clearMutation.mutateAsync,

		// Mutation states
		isLoading: loadMutation.isPending,
		isSaving: saveMutation.isPending,
		isClearing: clearMutation.isPending,

		// Mutation errors
		loadError: loadMutation.error,
		saveError: saveMutation.error,
		clearError: clearMutation.error,
	};
};
