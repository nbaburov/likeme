/* eslint-disable @typescript-eslint/no-unused-vars */
import { ChangeEvent, useState, useEffect } from "react";
import { Button } from "@material-tailwind/react";
import { Input } from "@components/ui/Input";
import { Select } from "@components/ui/Select";
import { FileUpload } from "@components/ui/FileUpload";
import { CreateOfferRequest, OfferType } from "@/dto/OfferDTO";
import { usePublicInfluencer } from "@/hooks/useInfluencer";

interface AddOfferFormProps {
	onSubmit: (data: CreateOfferRequest) => Promise<void>;
	onClose: () => void;
	isLoading: boolean;
	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
}

export const AddOfferForm = ({
	onSubmit,
	onClose,
	isLoading,
	handleUpload,
	isUploading,
}: AddOfferFormProps) => {
	const { data: influencers = [], isLoading: influencersLoading } =
		usePublicInfluencer();
	const [formData, setFormData] = useState<CreateOfferRequest>({
		title: "",
		description: "",
		coverPhotoPath: "",
		type: OfferType.LIKE,
		price: 0,
		createdById: 0,
	});
	const [errors, setErrors] = useState<Record<string, string>>({});

	// Set default createdById when influencers data loads
	useEffect(() => {
		if (influencers.length > 0 && formData.createdById === 0) {
			setFormData(prev => ({
				...prev,
				createdById: influencers[0].id
			}));
		}
	}, [influencers]);

	// Transform influencers for select options
	const influencerOptions = influencers.map((inf) => ({
		value: inf.id.toString(),
		label: `${inf.application.billingDetails.firstName} ${inf.application.billingDetails.lastName} (${inf.application.instagramHandle})`,
	}));

	const handleInputChange = (
		e: ChangeEvent<
			HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
		>
	) => {
		const { name, value } = e.target;
		setFormData((prev) => ({
			...prev,
			[name]: name === "createdById" ? Number(value) : value,
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
			/>
			<Select
				label="Influencer"
				name="createdById"
				value={formData.createdById.toString()}
				onChange={handleInputChange}
				options={influencerOptions}
				error={errors.createdById}
				required
				disabled={isLoading || influencersLoading}
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
					disabled={isLoading || isUploading || influencersLoading}
				>
					{isLoading || isUploading
						? "Processing..."
						: "Create Offer"}
				</Button>
			</div>
		</form>
	);
};
