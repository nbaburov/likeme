/* eslint-disable @typescript-eslint/no-unused-vars */
import { ChangeEvent, useState } from "react";
import { Button } from "@material-tailwind/react";
import { Input } from "@components/ui/Input";
import { Select } from "@components/ui/Select";
import { FileUpload } from "@components/ui/FileUpload";
import { OfferResponse, UpdateOfferRequest, OfferType } from "@/dto/OfferDTO";
import { usePublicInfluencer } from "@/hooks/useInfluencer";

interface EditOfferFormProps {
	offer: OfferResponse;
	onSubmit: (data: UpdateOfferRequest) => Promise<void>;
	onClose: () => void;
	isLoading: boolean;
	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
}

export const EditOfferForm = ({
	offer,
	onSubmit,
	onClose,
	isLoading,
	handleUpload,
	isUploading,
}: EditOfferFormProps) => {
	const { data: influencers = [], isLoading: influencersLoading } = usePublicInfluencer();
	const [formData, setFormData] = useState<UpdateOfferRequest>({
		title: offer.title,
		description: offer.description,
		type: offer.type,
		isActive: offer.isActive,
		price: offer.price,
		coverPhotoPath: offer.coverPhotoPath ?? undefined,
		updatedById: offer.createdById,
	});

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
			[name]: name === "updatedById" ? Number(value) : 
					name === "isActive" ? value === "true" : 
					value,
		}));
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
				required
				disabled={isLoading}
			/>
			<Input
				label="Description"
				name="description"
				type="textarea"
				value={formData.description}
				onChange={handleInputChange}
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
				disabled={isLoading}
			/>
			<Input
				label="Price"
				name="price"
				type="number"
				value={formData.price?.toString() ?? ""}
				onChange={handleInputChange}
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
				label="Influencer"
				name="updatedById"
				value={formData.updatedById.toString()}
				onChange={handleInputChange}
				options={influencerOptions}
				required
				disabled={isLoading || influencersLoading}
			/>
			<FileUpload
				label="Cover Photo"
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
					disabled={isLoading || isUploading || influencersLoading}
				>
					{isLoading || isUploading ? "Processing..." : "Save Changes"}
				</Button>
			</div>
		</form>
	);
};
