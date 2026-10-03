import { useState, ChangeEvent } from "react";
import { Button } from "@material-tailwind/react";
import { CreateInfluencerRequest } from "@/dto/InfluencerDTO";
import { Input } from "@components/ui/Input";
import { Select } from "@components/ui/Select";
import { FileUpload } from "@components/ui/FileUpload";
import { countries } from "@utils/countries";

interface SelectOption {
	value: string;
	label: string;
}

interface AddInfluencerFormProps {
	onSubmit: (data: CreateInfluencerRequest) => Promise<void>;
	onClose: () => void;
	isLoading: boolean;
	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
}

const statusOptions: SelectOption[] = [
	{ value: "PENDING_SETUP", label: "Pending Setup" },
	{ value: "PENDING_INSTAGRAM", label: "Pending Instagram" },
	{ value: "ACTIVE", label: "Active" },
	{ value: "INACTIVE", label: "Inactive" },
];

export const AddInfluencerForm = ({
	onSubmit,
	onClose,
	isLoading,
	handleUpload,
	isUploading,
}: AddInfluencerFormProps) => {
	const [formData, setFormData] = useState<CreateInfluencerRequest>({
		application: {
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
		},
		password: "",
		status: "PENDING_SETUP",
		isInstagramConnected: false,
		instagramAccessToken: "",
	});

	const handleInputChange = (
		e: ChangeEvent<
			HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
		>
	) => {
		const { name, value } = e.target;
		if (name.startsWith("application.billingDetails.")) {
			const billingField = name.split(".")[2];
			setFormData((prev) => ({
				...prev,
				application: {
					...prev.application,
					billingDetails: {
						...prev.application.billingDetails,
						[billingField]: value,
					},
				},
			}));
		} else if (name.startsWith("application.")) {
			const field = name.split(".")[1];
			setFormData((prev) => ({
				...prev,
				application: {
					...prev.application,
					[field]: value,
				},
			}));
		} else {
			setFormData((prev) => ({
				...prev,
				[name]:
					value === "true" ? true : value === "false" ? false : value,
			}));
		}
	};

	const handleFileUpload = async (file: File, type: "profile" | "cover") => {
		const fileName = await handleUpload(file);
		if (fileName) {
			setFormData((prev) => ({
				...prev,
				application: {
					...prev.application,
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
		<form onSubmit={handleSubmit} className="space-y-4">
			<div className="grid grid-cols-2 gap-4">
				{/* Basic Information */}
				<Input
					label="Username"
					name="application.username"
					value={formData.application.username}
					onChange={handleInputChange}
					required
				/>
				<Input
					label="Email"
					name="application.email"
					type="email"
					value={formData.application.email}
					onChange={handleInputChange}
					required
				/>
				<Input
					label="Password"
					name="password"
					type="password"
					value={formData.password}
					onChange={handleInputChange}
					required
				/>
				<Input
					label="Phone Number"
					name="application.phoneNumber"
					value={formData.application.phoneNumber}
					onChange={handleInputChange}
					required
				/>

				{/* Instagram Information */}
				<Input
					label="Instagram Handle"
					name="application.instagramHandle"
					value={formData.application.instagramHandle}
					onChange={handleInputChange}
					required
				/>
				<Select
					label="Status"
					name="status"
					value={formData.status}
					onChange={handleInputChange}
					options={statusOptions}
					required
				/>
				<Input
					label="Instagram Access Token"
					name="instagramAccessToken"
					value={formData.instagramAccessToken}
					onChange={handleInputChange}
				/>
				<Select
					label="Instagram Connected"
					name="isInstagramConnected"
					value={formData.isInstagramConnected ? "true" : "false"}
					onChange={handleInputChange}
					options={[
						{ value: "false", label: "No" },
						{ value: "true", label: "Yes" },
					]}
				/>

				{/* Billing Details */}
				<Input
					label="First Name"
					name="application.billingDetails.firstName"
					value={formData.application.billingDetails.firstName}
					onChange={handleInputChange}
					required
				/>
				<Input
					label="Last Name"
					name="application.billingDetails.lastName"
					value={formData.application.billingDetails.lastName}
					onChange={handleInputChange}
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
					value={formData.application.billingDetails.streetAddress}
					onChange={handleInputChange}
					required
				/>
				<Input
					label="City"
					name="application.billingDetails.city"
					value={formData.application.billingDetails.city}
					onChange={handleInputChange}
					required
				/>
				<Input
					label="State"
					name="application.billingDetails.state"
					value={formData.application.billingDetails.state}
					onChange={handleInputChange}
					required
				/>
				<Input
					label="Zip Code"
					name="application.billingDetails.zipCode"
					value={formData.application.billingDetails.zipCode}
					onChange={handleInputChange}
					required
				/>

				{/* About Section */}
				<div className="col-span-2">
					<Input
						label="About"
						name="application.about"
						type="textarea"
						value={formData.application.about}
						onChange={handleInputChange}
						required
					/>
				</div>

				{/* File Uploads */}
				<div className="col-span-2">
					<FileUpload
						label="Profile Photo"
						onUpload={async (file) => {
							await handleFileUpload(file, "profile");
						}}
					/>
				</div>
				<div className="col-span-2">
					<FileUpload
						label="Cover Photo"
						onUpload={async (file) => {
							await handleFileUpload(file, "cover");
						}}
					/>
				</div>
			</div>

			<div className="flex justify-end gap-4 mt-6">
				<Button color="secondary" onClick={onClose}>
					Cancel
				</Button>
				<Button
					color="primary"
					type="submit"
					disabled={isLoading || isUploading}
				>
					{isLoading || isUploading ? "Creating..." : "Create"}
				</Button>
			</div>
		</form>
	);
};
