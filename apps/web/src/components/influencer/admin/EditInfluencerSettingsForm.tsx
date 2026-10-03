/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-unused-vars */
import { ChangeEvent, FormEvent, useState } from "react";
import { Button } from "@material-tailwind/react";
import { Input } from "@components/ui/Input";
import { Select } from "@components/ui/Select";
import { FileUpload } from "@components/ui/FileUpload";
import {
	InfluencerResponse,
	UpdateInfluencerRequest,
} from "@/dto/InfluencerDTO";
import { countries } from "@/utils/countries";

interface EditInfluencerSettingsFormProps {
	influencer: InfluencerResponse;
	onSubmit: (data: UpdateInfluencerRequest) => Promise<void>;
	onCancel: () => void;
	isLoading?: boolean;
	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
}

export const EditInfluencerSettingsForm = ({
	influencer,
	onSubmit,
	onCancel,
	isLoading,
	handleUpload,
	isUploading,
}: EditInfluencerSettingsFormProps) => {
	const [formData, setFormData] = useState<UpdateInfluencerRequest>({
		application: {
			username: influencer.application.username,
			email: influencer.application.email,
			phoneNumber: influencer.application.phoneNumber,
			about: influencer.application.about,
			instagramHandle: influencer.application.instagramHandle,
			profilePhotoPath: influencer.application.profilePhotoPath,
			coverPhotoPath: influencer.application.coverPhotoPath,
			isApproved: influencer.application.isApproved,
			billingDetails: {
				firstName: influencer.application.billingDetails.firstName,
				lastName: influencer.application.billingDetails.lastName,
				country: influencer.application.billingDetails.country,
				streetAddress:
					influencer.application.billingDetails.streetAddress,
				city: influencer.application.billingDetails.city,
				state: influencer.application.billingDetails.state,
				zipCode: influencer.application.billingDetails.zipCode,
			},
		},
		password: "",
		status: influencer.status,
		isActive: influencer.isActive,
		isInstagramConnected: influencer.isInstagramConnected,
		instagramAccessToken: influencer.instagramAccessToken,
	});
	const [errors, setErrors] = useState<Record<string, string>>({});

	const handleInputChange = (
		e: ChangeEvent<
			HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
		>
	) => {
		const { name, value } = e.target;
		setFormData((prev) => {
			const newData = { ...prev };
			if (name.startsWith("application.billingDetails.")) {
				const field = name.replace("application.billingDetails.", "");
				newData.application = {
					...newData.application,
					billingDetails: {
						...newData.application?.billingDetails,
						[field]: value,
					},
				};
			} else if (name.startsWith("application.")) {
				const field = name.replace("application.", "");
				newData.application = {
					...newData.application,
					[field]: value,
				};
			} else {
				(newData as any)[name] = value;
			}
			return newData;
		});
	};

	const handleFileUpload = async (file: File, type: "profile" | "cover") => {
		try {
			const fileName = await handleUpload(file);
			setFormData((prev) => ({
				...prev,
				application: {
					...prev.application!,
					[type === "profile"
						? "profilePhotoPath"
						: "coverPhotoPath"]: fileName,
				},
			}));
		} catch (error) {
			// Error handling is now managed by the hook
		}
	};

	const validateForm = () => {
		const newErrors: Record<string, string> = {};

		if (!formData.application?.email) newErrors.email = "Email is required";
		if (!formData.application?.phoneNumber)
			newErrors.phoneNumber = "Phone number is required";
		if (!formData.application?.billingDetails?.firstName)
			newErrors["application.billingDetails.firstName"] =
				"First name is required";
		if (!formData.application?.billingDetails?.lastName)
			newErrors["application.billingDetails.lastName"] =
				"Last name is required";

		setErrors(newErrors);
		return Object.keys(newErrors).length === 0;
	};

	const handleSubmit = async (e: FormEvent) => {
		e.preventDefault();
		if (!validateForm()) return;

		// Create a copy of form data
		const submitData = { ...formData };

		// Remove password if empty
		if (!submitData.password) {
			delete submitData.password;
		}

		await onSubmit(submitData);
	};

	return (
		<form onSubmit={handleSubmit} className="space-y-6">
			{/* Basic Information */}
			<div className="grid grid-cols-1 md:grid-cols-2 gap-4">
				<Input
					label="Email"
					name="application.email"
					type="email"
					value={formData.application?.email}
					onChange={handleInputChange}
					error={errors.email}
					required
				/>
				<Input
					label="Phone Number"
					name="application.phoneNumber"
					value={formData.application?.phoneNumber}
					onChange={handleInputChange}
					error={errors.phoneNumber}
					required
				/>
				<Input
					label="Instagram Handle"
					name="application.instagramHandle"
					value={formData.application?.instagramHandle}
					onChange={handleInputChange}
				/>
				<Input
					label="About"
					name="application.about"
					type="textarea"
					value={formData.application?.about}
					onChange={handleInputChange}
				/>
			</div>

			{/* Password Update */}
			<div className="border-t pt-4">
				<h3 className="text-lg font-medium mb-4">Password</h3>
				<Input
					label="Password"
					name="password"
					type="password"
					placeholder="Leave blank to keep current password"
					value={formData.password}
					onChange={handleInputChange}
					error={errors.password}
				/>
			</div>

			{/* Billing Details */}
			<div className="border-t pt-4">
				<h3 className="text-lg font-medium mb-4">Billing Details</h3>
				<div className="grid grid-cols-1 md:grid-cols-2 gap-4">
					<Input
						label="First Name"
						name="application.billingDetails.firstName"
						value={formData.application?.billingDetails?.firstName}
						onChange={handleInputChange}
						error={errors["application.billingDetails.firstName"]}
						required
					/>
					<Input
						label="Last Name"
						name="application.billingDetails.lastName"
						value={formData.application?.billingDetails?.lastName}
						onChange={handleInputChange}
						error={errors["application.billingDetails.lastName"]}
						required
					/>
					<Select
						label="Country"
						name="application.billingDetails.country"
						value={formData.application?.billingDetails?.country}
						onChange={handleInputChange}
						options={countries}
						required
					/>
					<Input
						label="Street Address"
						name="application.billingDetails.streetAddress"
						value={
							formData.application?.billingDetails?.streetAddress
						}
						onChange={handleInputChange}
						required
					/>
					<Input
						label="City"
						name="application.billingDetails.city"
						value={formData.application?.billingDetails?.city}
						onChange={handleInputChange}
						required
					/>
					<Input
						label="State"
						name="application.billingDetails.state"
						value={formData.application?.billingDetails?.state}
						onChange={handleInputChange}
						required
					/>
					<Input
						label="Zip Code"
						name="application.billingDetails.zipCode"
						value={formData.application?.billingDetails?.zipCode}
						onChange={handleInputChange}
						required
					/>
				</div>
			</div>

			{/* File Uploads */}
			<div className="border-t pt-4">
				<h3 className="text-lg font-medium mb-4">Profile Photos</h3>
				<div className="space-y-4">
					<FileUpload
						label="Profile Photo"
						onUpload={(file) => handleFileUpload(file, "profile")}
						error={errors.profilePhoto}
					/>
					<FileUpload
						label="Cover Photo"
						onUpload={(file) => handleFileUpload(file, "cover")}
						error={errors.coverPhoto}
					/>
				</div>
			</div>

			<div className="flex justify-end gap-4">
				<Button
					type="button"
					onClick={onCancel}
					color="secondary"
					disabled={isLoading || isUploading}
				>
					Cancel
				</Button>
				<Button
					type="submit"
					color="primary"
					disabled={isLoading || isUploading}
				>
					{isLoading || isUploading ? "Saving..." : "Save Changes"}
				</Button>
			</div>
		</form>
	);
};
