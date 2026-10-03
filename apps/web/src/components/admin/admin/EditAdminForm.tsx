/* eslint-disable @typescript-eslint/no-unused-vars */
import { ChangeEvent, useState } from "react";
import { Button } from "@material-tailwind/react";
import { Input } from "@components/ui/Input";
import { Select } from "@components/ui/Select";
import { FileUpload } from "@components/ui/FileUpload";
import { AdminResponse, UpdateAdminRequest } from "@/dto/AdminDTO";

interface EditAdminFormProps {
	admin: AdminResponse;
	onSubmit: (data: UpdateAdminRequest) => Promise<void>;
	onClose: () => void;
	isLoading: boolean;

	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
}

export const EditAdminForm = ({
	admin,
	onSubmit,
	onClose,
	isLoading,

	handleUpload,
	isUploading,
}: EditAdminFormProps) => {
	const [formData, setFormData] = useState<UpdateAdminRequest>({
		username: admin.username,
		email: admin.email,
		permissions: admin.permissions,
		isActive: admin.isActive,
		profilePhotoPath: admin.profilePhotoPath ?? undefined,
	});

	const handleInputChange = (
		e: ChangeEvent<
			HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
		>
	) => {
		const { name, value } = e.target;
		setFormData((prev) => ({
			...prev,
			[name]: name === "isActive" ? value === "true" : value,
		}));
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
				value={formData.password ?? ""}
				onChange={handleInputChange}
				placeholder="Leave blank to keep current password"
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
						: "Save Changes"}
				</Button>
			</div>
		</form>
	);
};
