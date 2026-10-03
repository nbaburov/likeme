/* eslint-disable @typescript-eslint/no-unused-vars */
import { ChangeEvent, useState } from "react";
import { Button } from "@material-tailwind/react";
import { Input } from "@components/ui/Input";
import { Select } from "@components/ui/Select";
import { FileUpload } from "@components/ui/FileUpload";
import { ClientResponse, UpdateClientRequest } from "@/dto/ClientDTO";
import { countries } from "@utils/countries";

interface EditClientFormProps {
	client: ClientResponse;
	onSubmit: (data: UpdateClientRequest) => Promise<void>;
	onClose: () => void;
	isLoading: boolean;
	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
}

export const EditClientForm = ({
	client,
	onSubmit,
	onClose,
	isLoading,
	handleUpload,
	isUploading,
}: EditClientFormProps) => {
	const [formData, setFormData] = useState<UpdateClientRequest>({
		username: client.username,
		email: client.email,
		profilePhotoPath: client.profilePhotoPath ?? undefined,
		instagramHandle: client.instagramHandle,
		isInstagramConnected: client.isInstagramConnected,
		isActive: client.isActive,
		billingDetails: {
			firstName: client.billingDetails.firstName,
			lastName: client.billingDetails.lastName,
			country: client.billingDetails.country,
			streetAddress: client.billingDetails.streetAddress,
			city: client.billingDetails.city,
			state: client.billingDetails.state,
			zipCode: client.billingDetails.zipCode,
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
			setFormData((prev) => ({
				...prev,
				[name]:
					name === "isActive" || name === "isInstagramConnected"
						? value === "true"
						: value,
			}));
		}
	};

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		await onSubmit(formData);
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
					value={formData.password ?? ""}
					onChange={handleInputChange}
					placeholder="Leave blank to keep current password"
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
				<Select
					label="Status"
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
						value={formData.billingDetails?.firstName}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="Last Name"
						name="billing.lastName"
						value={formData.billingDetails?.lastName}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
				</div>
				<Input
					label="Street Address"
					name="billing.streetAddress"
					value={formData.billingDetails?.streetAddress}
					onChange={handleInputChange}
					required
					disabled={isLoading}
				/>
				<div className="grid grid-cols-3 gap-4">
					<Input
						label="City"
						name="billing.city"
						value={formData.billingDetails?.city}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="State"
						name="billing.state"
						value={formData.billingDetails?.state}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="ZIP Code"
						name="billing.zipCode"
						value={formData.billingDetails?.zipCode}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
				</div>
				<Select
					label="Country"
					name="billing.country"
					value={formData.billingDetails?.country}
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
