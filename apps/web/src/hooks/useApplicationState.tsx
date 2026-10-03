/* eslint-disable @typescript-eslint/no-explicit-any */
import { useState, useEffect } from "react";
import Cookies from "js-cookie";
import { InfluencerApplicationResponse } from "@/dto/InfluencerApplicationDTO";
import { ApiError } from "../dto/ErrorDTO";
import { useErrorToast } from "./useErrorToast";
import { toast } from "react-toastify";

const APPLICATION_QUERY_KEY = "submitted_application";

export const useApplicationState = () => {
	const { showError } = useErrorToast();
	const [submittedApplication, setSubmittedApplication] =
		useState<InfluencerApplicationResponse | null>(null);

	// Initial load from cookie
	useEffect(() => {
		try {
			const savedApp = Cookies.get(APPLICATION_QUERY_KEY);
			if (savedApp) {
				const parsedApp = JSON.parse(savedApp);
				setSubmittedApplication(parsedApp);
			}
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_LOAD_APPLICATION",
				"Failed to load application",
				error.message,
				new Date().toISOString()
			);
			Cookies.remove(APPLICATION_QUERY_KEY);
		}
	}, [showError]);

	const saveApplication = async (
		application: InfluencerApplicationResponse
	) => {
		try {
			Cookies.set(APPLICATION_QUERY_KEY, JSON.stringify(application), {
				expires: 7,
			});
			setSubmittedApplication(application);
			toast.success("Application saved successfully");
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_SAVE_APPLICATION",
				"Failed to save application",
				error.message,
				new Date().toISOString()
			);
		}
	};

	const clearApplication = async () => {
		try {
			Cookies.remove(APPLICATION_QUERY_KEY);
			setSubmittedApplication(null);
			toast.success("Application cleared successfully");
		} catch (error: any) {
			throw new ApiError(
				"FAILED_TO_CLEAR_APPLICATION",
				"Failed to clear application",
				error.message,
				new Date().toISOString()
			);
		}
	};

	return {
		// Data
		data: submittedApplication,

		// Operations
		save: saveApplication,
		clear: clearApplication,

		// States
		isSaved: !!submittedApplication,
	};
};
