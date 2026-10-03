/* eslint-disable @typescript-eslint/no-unused-vars */
import { ChangeEvent, useState } from "react";
import { Alert, Button } from "@material-tailwind/react";
import { Input } from "@components/ui/Input";
import { Select } from "@components/ui/Select";
import { FileUpload } from "@components/ui/FileUpload";
import { CreateAdminRequest } from "@/dto/AdminDTO";
import { ApiError } from "@/dto/ErrorDTO";

interface AddAdminFormProps {
	onSubmit: (data: CreateAdminRequest) => Promise<void>;
	onClose: () => void;
	isLoading: boolean;
	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
}

export const AddAdminForm = ({
	onSubmit,
	onClose,
	isLoading,
	handleUpload,
	isUploading,
}: AddAdminFormProps) => {
	const [formData, setFormData] = useState<CreateAdminRequest>({
		username: "",
		email: "",
		password: "",
		permissions: "MODERATOR",
		profilePhotoPath: undefined,
	});

	const handleInputChange = (
		e: ChangeEvent<
			HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
		>
	) => {
		const { name, value } = e.target;
		setFormData((prev) => ({ ...prev, [name]: value }));
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
			<Select
				label="Permissions"
				name="permissions"
				value={formData.permissions}
				onChange={handleInputChange}
				options={[
					{ value: "MODERATOR", label: "Moderator" },
					{ value: "FULL", label: "Full" },
				]}
				required
				disabled={isLoading}
			/>
			<FileUpload
				label="Profile Photo"
				onUpload={handleFileUpload}
				accept="image/*"
				disabled={isLoading || isUploading}
			/>
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
						: "Create Admin"}
				</Button>
			</div>
		</form>
	);
};
