/* eslint-disable @typescript-eslint/no-unused-vars */
import { ChangeEvent, useState } from "react";
import { Button } from "@material-tailwind/react";
import { Input } from "@components/ui/Input";
import { FileUpload } from "@components/ui/FileUpload";
import { CreateClientRequest } from "@/dto/ClientDTO";
import { countries } from "@utils/countries";
import { Select } from "@components/ui/Select";

interface AddClientFormProps {
	onSubmit: (data: CreateClientRequest) => Promise<void>;
	onClose: () => void;
	isLoading: boolean;
	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
}

export const AddClientForm = ({
	onSubmit,
	onClose,
	isLoading,
	handleUpload,
	isUploading,
}: AddClientFormProps) => {
	const [formData, setFormData] = useState<CreateClientRequest>({
		username: "",
		email: "",
		password: "",
		profilePhotoPath: "",
		instagramHandle: "",
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

	const handleInputChange = (
		e: ChangeEvent<
			HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
		>
	) => {
		const { name, value } = e.target;
		if (name.startsWith("billing.")) {
			const billingField = name.split(".")[1];
			setFormData((prev) => ({
				...prev,
				billingDetails: {
					...prev.billingDetails,
					[billingField]: value,
				},
			}));
		} else {
			setFormData((prev) => ({ ...prev, [name]: value }));
		}
	};

	const handleFileUpload = async (file: File) => {
		const fileName = await handleUpload(file);
		if (fileName) {
			setFormData((prev) => ({
				...prev,
				profilePhotoPath: fileName,
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
					name="username"
					value={formData.username}
					onChange={handleInputChange}
					required
					disabled={isLoading}
				/>
				<Input
					label="Email"
					name="email"
					type="email"
					value={formData.email}
					onChange={handleInputChange}
					required
					disabled={isLoading}
				/>
				<Input
					label="Password"
					name="password"
					type="password"
					value={formData.password}
					onChange={handleInputChange}
					required
					disabled={isLoading}
				/>
				<Input
					label="Instagram Handle"
					name="instagramHandle"
					value={formData.instagramHandle}
					onChange={handleInputChange}
					required
					disabled={isLoading}
				/>
				<FileUpload
					label="Profile Photo"
					onUpload={handleFileUpload}
					accept="image/*"
					disabled={isLoading || isUploading}
				/>
			</div>

			<div className="space-y-4">
				<h3 className="text-lg font-semibold">Billing Details</h3>
				<div className="grid grid-cols-2 gap-4">
					<Input
						label="First Name"
						name="billing.firstName"
						value={formData.billingDetails.firstName}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="Last Name"
						name="billing.lastName"
						value={formData.billingDetails.lastName}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Select
						label="Country"
						name="billing.country"
						value={formData.billingDetails.country}
						onChange={handleInputChange}
						options={countries}
						required
						disabled={isLoading}
					/>
					<Input
						label="Street Address"
						name="billing.streetAddress"
						value={formData.billingDetails.streetAddress}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="City"
						name="billing.city"
						value={formData.billingDetails.city}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="State"
						name="billing.state"
						value={formData.billingDetails.state}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="ZIP Code"
						name="billing.zipCode"
						value={formData.billingDetails.zipCode}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
				</div>
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
						: "Create Client"}
				</Button>
			</div>
		</form>
	);
};
