/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";
import { useState, useEffect } from "react";
import ApplicationForm from "@components/influencer/client/ApplicationForm";
import InfoPageLayout from "@components/layout/client/InfoPageLayout";
import { usePublicInfluencerApplications } from "@hooks/useInfluencerApplications";
import { usePublicFileUpload } from "@hooks/useFileUpload";
import { useFormPersistence } from "@hooks/useFormPersistence";
import { useApplicationState } from "@hooks/useApplicationState";
import { CreateInfluencerApplicationRequest } from "@/dto/InfluencerApplicationDTO";
import { CustomSkeleton } from "@/components/ui/CustomSkeleton";
import { Alert } from "@material-tailwind/react";

function InfluencersApply() {
	const {
		create: createApplication,
		isCreating,
	} = usePublicInfluencerApplications();

	const {
		upload: uploadFile,
		isUploading,
	} = usePublicFileUpload();

	const {
		data: savedForm,
		load: loadSavedForm,
		save: saveFormData,
		clear: clearSavedForm,
		isLoading: isLoadingForm,
		loadError,
	} = useFormPersistence();

	const {
		data: submittedApplication,
		save: saveSubmittedApplication,
		isSaved,
	} = useApplicationState();

	const [formData, setFormData] =
		useState<CreateInfluencerApplicationRequest>({
			username: "",
			email: "",
			phoneNumber: "",
			about: "",
			instagramHandle: "",
			profilePhotoPath: "",
			coverPhotoPath: "",
			billingDetails: {
				firstName: "",
				lastName: "",
				country: "United States",
				streetAddress: "",
				city: "",
				state: "",
				zipCode: "",
			},
		});


	// Load saved form data on mount
	useEffect(() => {
		loadSavedForm();
	}, [loadSavedForm]);

	// Load saved form data on initial render
	useEffect(() => {
		if (savedForm && !isSaved) {
			setFormData(savedForm);
		}
	}, [savedForm, isSaved]);

	// Handle validation errors

	const handleInputChange = (
		e: React.ChangeEvent<
			HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
		>
	) => {
		const { name, value } = e.target;
		if (name in formData.billingDetails) {
			setFormData((prev) => ({
				...prev,
				billingDetails: {
					...prev.billingDetails,
					[name]: value,
				},
			}));
		} else {
			setFormData((prev) => ({
				...prev,
				[name]: value,
			}));
		}
	};

	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		const response = await createApplication(formData);
		await clearSavedForm();
		await saveSubmittedApplication(response);
	};

	const handleFileUpload = async (
		e: React.ChangeEvent<HTMLInputElement>,
		type: "profile" | "cover"
	) => {
		const file = e.target.files?.[0];
		if (!file) return;

		const response = await uploadFile({
			file,
			prefix: type === "profile" ? "profiles" : "covers",
		});

		setFormData((prev) => ({
			...prev,
			[type === "profile" ? "profilePhotoPath" : "coverPhotoPath"]:
				response.fileName,
		}));
	};

	const handleSaveForLater = async () => {
		await saveFormData(formData);
	};

	const handleReset = async () => {
		await clearSavedForm();
		setFormData({
			username: "",
			email: "",
			phoneNumber: "",
			about: "",
			instagramHandle: "",
			profilePhotoPath: "",
			coverPhotoPath: "",
			billingDetails: {
				firstName: "",
				lastName: "",
				country: "United States",
				streetAddress: "",
				city: "",
				state: "",
				zipCode: "",
			},
		});
	};

	if (isLoadingForm) {
		return (
			<InfoPageLayout title="Influencers Applications">
				<CustomSkeleton />
			</InfoPageLayout>
		);
	}

	if (loadError) {
		return (
			<InfoPageLayout title="Influencers Applications">
				<Alert color="error" className="mt-4">
					{loadError.message}
				</Alert>
			</InfoPageLayout>
		);
	}

	return (
		<InfoPageLayout title="Influencers Applications">
			<ApplicationForm
				formData={formData}
				submittedApplication={submittedApplication}
				isSubmitting={isCreating}
				isUploading={isUploading}
				onInputChange={handleInputChange}
				onSubmit={handleSubmit}
				onFileUpload={handleFileUpload}
				onSaveForLater={handleSaveForLater}
				onReset={handleReset}
			/>
		</InfoPageLayout>
	);
}

export default InfluencersApply;
