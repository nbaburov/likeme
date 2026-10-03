/* eslint-disable @typescript-eslint/no-unused-vars */
import { ChangeEvent, FormEvent, useState } from "react";
import { Button } from "@material-tailwind/react";
import { Input } from "@components/ui/Input";
import { Select } from "@components/ui/Select";
import { FileUpload } from "@components/ui/FileUpload";
import { ClientResponse, UpdateClientRequest } from "@/dto/ClientDTO";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import { countries } from "@/utils/countries";

interface EditClientFormProps {
	client: ClientResponse;
	onSubmit: (data: UpdateClientRequest) => Promise<void>;
	onCancel: () => void;
	isLoading?: boolean;
}

export const EditClientForm = ({
	client,
	onSubmit,
	onCancel,
	isLoading,
}: EditClientFormProps) => {
	const { upload, isUploading } = usePublicFileUpload();
	const [formData, setFormData] = useState<UpdateClientRequest>({
		username: client.username,
		email: client.email,
		password: "",
		instagramHandle: client.instagramHandle,
		profilePhotoPath: client.profilePhotoPath,
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
	const [errors, setErrors] = useState<Record<string, string>>({});

	const handleInputChange = (
		e: ChangeEvent<
			HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
		>
	) => {
		const { name, value } = e.target;
		if (name.includes(".")) {
			const [parent, child] = name.split(".");
			setFormData((prev) => ({
				...prev,
				[parent]: {
					...((prev[parent as keyof UpdateClientRequest] as Record<
						string,
						string
					>) ?? {}),
					[child]: value,
				},
			}));
		} else {
			setFormData((prev) => ({ ...prev, [name]: value }));
		}
		if (errors[name]) {
			setErrors((prev) => {
				const newErrors = { ...prev };
				delete newErrors[name];
				return newErrors;
			});
		}
	};

	const handleFileUpload = async (file: File) => {
		try {
			const response = await upload({ file, prefix: "client-profiles" });
			setFormData((prev) => ({
				...prev,
				profilePhotoPath: response.fileName,
			}));
			setErrors((prev) => {
				const newErrors = { ...prev };
				delete newErrors.file;
				return newErrors;
			});
		} catch (error) {
			setErrors((prev) => ({
				...prev,
				file: "Failed to upload file",
			}));
		}
	};

	const handleSubmit = async (e: FormEvent) => {
		e.preventDefault();
		const submitData = { ...formData };
		if (!submitData.password) {
			delete submitData.password;
		}
		await onSubmit(submitData);
	};

	return (
		<form onSubmit={handleSubmit} className="space-y-6">
			<Input
				label="Username"
				name="username"
				value={formData.username}
				onChange={handleInputChange}
				error={errors.username}
				disabled={isLoading}
			/>
			<Input
				label="Email"
				name="email"
				type="email"
				value={formData.email}
				onChange={handleInputChange}
				error={errors.email}
				disabled={isLoading}
			/>
			<Input
				label="Password"
				name="password"
				type="password"
				value={formData.password}
				onChange={handleInputChange}
				error={errors.password}
				placeholder="Leave blank to keep current password"
				disabled={isLoading}
			/>
			<Input
				label="Instagram Handle"
				name="instagramHandle"
				value={formData.instagramHandle}
				onChange={handleInputChange}
				error={errors.instagramHandle}
				disabled={isLoading}
			/>
			<FileUpload
				label="Profile Photo"
				onUpload={handleFileUpload}
				accept="image/*"
				disabled={isLoading || isUploading}
				error={errors.file}
			/>

			<div className="grid grid-cols-2 gap-4">
				<Input
					label="First Name"
					name="billingDetails.firstName"
					value={formData.billingDetails?.firstName}
					onChange={handleInputChange}
					error={errors["billingDetails.firstName"]}
					disabled={isLoading}
				/>
				<Input
					label="Last Name"
					name="billingDetails.lastName"
					value={formData.billingDetails?.lastName}
					onChange={handleInputChange}
					error={errors["billingDetails.lastName"]}
					disabled={isLoading}
				/>
			</div>

			<Select
				label="Country"
				name="billingDetails.country"
				value={formData.billingDetails?.country}
				onChange={handleInputChange}
				options={countries}
				error={errors["billingDetails.country"]}
				disabled={isLoading}
			/>

			<Input
				label="Street Address"
				name="billingDetails.streetAddress"
				value={formData.billingDetails?.streetAddress}
				onChange={handleInputChange}
				error={errors["billingDetails.streetAddress"]}
				disabled={isLoading}
			/>

			<div className="grid grid-cols-3 gap-4">
				<Input
					label="City"
					name="billingDetails.city"
					value={formData.billingDetails?.city}
					onChange={handleInputChange}
					error={errors["billingDetails.city"]}
					disabled={isLoading}
				/>
				<Input
					label="State"
					name="billingDetails.state"
					value={formData.billingDetails?.state}
					onChange={handleInputChange}
					error={errors["billingDetails.state"]}
					disabled={isLoading}
				/>
				<Input
					label="ZIP Code"
					name="billingDetails.zipCode"
					value={formData.billingDetails?.zipCode}
					onChange={handleInputChange}
					error={errors["billingDetails.zipCode"]}
					disabled={isLoading}
				/>
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
				<Button type="submit" disabled={isLoading || isUploading}>
					{isLoading || isUploading ? "Saving..." : "Save Changes"}
				</Button>
			</div>
		</form>
	);
};
