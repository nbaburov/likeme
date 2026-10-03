// src/components/influencer/offer/AddOfferForm.tsx
import { ChangeEvent, useState } from "react";
import { Button } from "@material-tailwind/react";
import { Input } from "@components/ui/Input";
import { Select } from "@components/ui/Select";
import { FileUpload } from "@components/ui/FileUpload";
import { CreateOfferRequest, OfferType } from "@/dto/OfferDTO";

interface AddOfferFormProps {
	onSubmit: (data: CreateOfferRequest) => Promise<void>;
	onClose: () => void;
	isLoading: boolean;
	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
	influencerId: number;
}

export const AddOfferForm = ({
	onSubmit,
	onClose,
	isLoading,
	handleUpload,
	isUploading,
	influencerId,
}: AddOfferFormProps) => {
	const [formData, setFormData] = useState<CreateOfferRequest>({
		title: "",
		description: "",
		coverPhotoPath: "",
		type: OfferType.LIKE,
		price: 0,
		createdById: influencerId,
	});
	const [errors, setErrors] = useState<Record<string, string>>({});

	const handleInputChange = (
		e: ChangeEvent<
			HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
		>
	) => {
		const { name, value } = e.target;
		setFormData((prev) => ({
			...prev,
			[name]: name === "price" ? Number(value) : value,
		}));
		if (errors[name]) {
			setErrors((prev) => {
				const newErrors = { ...prev };
				delete newErrors[name];
				return newErrors;
			});
		}
	};

	const handleFileUpload = async (file: File) => {
		const fileUrl = await handleUpload(file);
		if (fileUrl) {
			setFormData((prev) => ({
				...prev,
				coverPhotoPath: fileUrl,
			}));
		}
	};

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		await onSubmit(formData);
	};

	return (
		<form onSubmit={handleSubmit} className="space-y-6">
			<Input
				label="Title"
				name="title"
				value={formData.title}
				onChange={handleInputChange}
				error={errors.title}
				required
				disabled={isLoading}
			/>
			<Input
				label="Description"
				name="description"
				type="textarea"
				value={formData.description}
				onChange={handleInputChange}
				error={errors.description}
				required
				disabled={isLoading}
			/>
			<Select
				label="Type"
				name="type"
				value={formData.type}
				onChange={handleInputChange}
				options={[
					{ value: OfferType.LIKE, label: "Like" },
					{ value: OfferType.COMMENT, label: "Comment" },
					{ value: OfferType.FOLLOW, label: "Follow" },
				]}
				required
				disabled={isLoading}
			/>
			<Input
				label="Price"
				name="price"
				type="number"
				value={formData.price.toString()}
				onChange={handleInputChange}
				error={errors.price}
				required
				disabled={isLoading}
			/>
			<FileUpload
				label="Cover Photo"
				onUpload={handleFileUpload}
				accept="image/*"
				disabled={isLoading || isUploading}
				error={errors.file}
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
						: "Create Offer"}
				</Button>
			</div>
		</form>
	);
};
