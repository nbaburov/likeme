/* eslint-disable @typescript-eslint/no-unused-vars */
import { ChangeEvent, useState } from "react";
import { Button } from "@material-tailwind/react";
import { Input } from "@components/ui/Input";
import { Select } from "@components/ui/Select";
import { FileUpload } from "@components/ui/FileUpload";
import {
	InfluencerResponse,
	UpdateInfluencerRequest,
} from "@/dto/InfluencerDTO";
import { countries } from "@utils/countries";

interface EditInfluencerFormProps {
	influencer: InfluencerResponse;
	onSubmit: (data: UpdateInfluencerRequest) => Promise<void>;
	onClose: () => void;
	isLoading: boolean;
	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
}

const statusOptions = [
	{ value: "PENDING_SETUP", label: "Pending Setup" },
	{ value: "PENDING_INSTAGRAM", label: "Pending Instagram" },
	{ value: "ACTIVE", label: "Active" },
	{ value: "INACTIVE", label: "Inactive" },
];

export const EditInfluencerForm = ({
	influencer,
	onSubmit,
	onClose,
	isLoading,
	handleUpload,
	isUploading,
}: EditInfluencerFormProps) => {
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
		status: influencer.status,
		isActive: influencer.isActive,
		isInstagramConnected: influencer.isInstagramConnected,
		instagramAccessToken: influencer.instagramAccessToken,
	});

	const handleInputChange = (
		e: ChangeEvent<
			HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
		>
	) => {
		const { name, value } = e.target;
		if (name.startsWith("application.billingDetails.")) {
			const field = name.replace("application.billingDetails.", "");
			setFormData((prev) => ({
				...prev,
				application: {
					...prev.application!,
					billingDetails: {
						...prev.application!.billingDetails!,
						[field]: value,
					},
				},
			}));
		} else if (name.startsWith("application.")) {
			const field = name.replace("application.", "");
			setFormData((prev) => ({
				...prev,
				application: {
					...prev.application!,
					[field]: value,
				},
			}));
		} else {
			setFormData((prev) => ({
				...prev,
				[name]:
					name === "isActive" || name === "isInstagramConnected"
						? value === "true"
						: value,
			}));
		}
	};

	const handleFileUpload = async (file: File, type: "profile" | "cover") => {
		const fileName = await handleUpload(file);
		if (fileName) {
			setFormData((prev) => ({
				...prev,
				application: {
					...prev.application!,
					[type === "profile"
						? "profilePhotoPath"
						: "coverPhotoPath"]: fileName,
				},
			}));
		}
	};

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		await onSubmit(formData);
	};

	return (
		<form onSubmit={handleSubmit} className="space-y-6">
			<div className="space-y-4">
				<h3 className="text-lg font-semibold">Basic Information</h3>
				<Input
					label="Username"
					name="application.username"
					value={formData.application?.username}
					onChange={handleInputChange}
					required
					disabled={isLoading}
				/>
				<Input
					label="Email"
					name="application.email"
					type="email"
					value={formData.application?.email}
					onChange={handleInputChange}
					required
					disabled={isLoading}
				/>
				<Input
					label="Password"
					name="password"
					type="password"
					value={formData.password ?? ""}
					onChange={handleInputChange}
					placeholder="Leave blank to keep current password"
					disabled={isLoading}
				/>
				<Input
					label="Phone Number"
					name="application.phoneNumber"
					value={formData.application?.phoneNumber}
					onChange={handleInputChange}
					required
					disabled={isLoading}
				/>
				<Input
					label="Instagram Handle"
					name="application.instagramHandle"
					value={formData.application?.instagramHandle}
					onChange={handleInputChange}
					required
					disabled={isLoading}
				/>
				<Input
					label="About"
					name="application.about"
					type="textarea"
					value={formData.application?.about}
					onChange={handleInputChange}
					required
					disabled={isLoading}
				/>
				<Select
					label="Status"
					name="status"
					value={formData.status}
					onChange={handleInputChange}
					options={statusOptions}
					disabled={isLoading}
				/>
				<Select
					label="Account Status"
					name="isActive"
					value={String(formData.isActive)}
					onChange={handleInputChange}
					options={[
						{ value: "true", label: "Active" },
						{ value: "false", label: "Inactive" },
					]}
					disabled={isLoading}
				/>
				<Select
					label="Instagram Connection"
					name="isInstagramConnected"
					value={String(formData.isInstagramConnected)}
					onChange={handleInputChange}
					options={[
						{ value: "true", label: "Connected" },
						{ value: "false", label: "Not Connected" },
					]}
					disabled={isLoading}
				/>
				<FileUpload
					label="Profile Photo"
					onUpload={(file) => handleFileUpload(file, "profile")}
					accept="image/*"
					disabled={isLoading || isUploading}
				/>
				<FileUpload
					label="Cover Photo"
					onUpload={(file) => handleFileUpload(file, "cover")}
					accept="image/*"
					disabled={isLoading || isUploading}
				/>
			</div>

			<div className="space-y-4">
				<h3 className="text-lg font-semibold">Billing Details</h3>
				<div className="grid grid-cols-2 gap-4">
					<Input
						label="First Name"
						name="application.billingDetails.firstName"
						value={formData.application?.billingDetails?.firstName}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="Last Name"
						name="application.billingDetails.lastName"
						value={formData.application?.billingDetails?.lastName}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
				</div>
				<Input
					label="Street Address"
					name="application.billingDetails.streetAddress"
					value={formData.application?.billingDetails?.streetAddress}
					onChange={handleInputChange}
					required
					disabled={isLoading}
				/>
				<div className="grid grid-cols-3 gap-4">
					<Input
						label="City"
						name="application.billingDetails.city"
						value={formData.application?.billingDetails?.city}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="State"
						name="application.billingDetails.state"
						value={formData.application?.billingDetails?.state}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="ZIP Code"
						name="application.billingDetails.zipCode"
						value={formData.application?.billingDetails?.zipCode}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
				</div>
				<Select
					label="Country"
					name="application.billingDetails.country"
					value={formData.application?.billingDetails?.country}
					onChange={handleInputChange}
					options={countries}
					required
					disabled={isLoading}
				/>
			</div>

			<div className="flex gap-4">
				<Button
					type="button"
					onClick={onClose}
					className="w-full"
					color="secondary"
					disabled={isLoading || isUploading}
				>
					Cancel
				</Button>
				<Button
					type="submit"
					color="primary"
					className="w-full"
					disabled={isLoading || isUploading}
				>
					{isLoading || isUploading
						? "Processing..."
						: "Save Changes"}
				</Button>
			</div>
		</form>
	);
};
