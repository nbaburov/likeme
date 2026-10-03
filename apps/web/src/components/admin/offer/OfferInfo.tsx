/* eslint-disable @next/next/no-img-element */
import { Button } from "@material-tailwind/react";
import { OfferResponse, UpdateOfferRequest } from "@/dto/OfferDTO";
import { useState } from "react";
import { EditOfferForm } from "./EditOfferForm";
import { IoIosArrowBack } from "react-icons/io";
import { getPhotoPath } from "@utils/photoPaths";

interface OfferInfoProps {
	offer: OfferResponse;
	onDelete: () => Promise<void>;
	onUpdate: (data: UpdateOfferRequest) => Promise<void>;
	onBack: () => void;
	isUpdating: boolean;
	isDeleting: boolean;
	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
}

export const OfferInfo = ({
	offer,
	onDelete,
	onUpdate,
	onBack,
	isUpdating,
	isDeleting,
	handleUpload,
	isUploading,
}: OfferInfoProps) => {
	const [isEditing, setIsEditing] = useState(false);

	if (isEditing) {
		return (
			<div className="max-w-2xl mx-auto">
				<div className="flex justify-between items-center mb-6">
					<h3 className="text-base font-semibold leading-7 text-gray-900">
						Edit Offer
					</h3>
					<Button
						color="secondary"
						onClick={() => setIsEditing(false)}
						disabled={isUpdating || isDeleting}
					>
						Cancel
					</Button>
				</div>
				<EditOfferForm
					offer={offer}
					onSubmit={async (data) => {
						await onUpdate(data);
						setIsEditing(false);
					}}
					onClose={() => setIsEditing(false)}
					isLoading={isUpdating || isDeleting}
					handleUpload={handleUpload}
					isUploading={isUploading}
				/>
			</div>
		);
	}

	return (
		<div>
			<div className="px-4 sm:px-0 flex justify-between items-center">
				<div className="flex items-center">
					<Button color="secondary" onClick={onBack} className="mr-2">
						<IoIosArrowBack className="mr-1" />
						Back
					</Button>
					<h3 className="text-base font-semibold leading-7 text-gray-900">
						Offer Information
					</h3>
				</div>
				<div className="flex gap-2">
					<Button
						color="secondary"
						onClick={() => setIsEditing(true)}
						disabled={isUpdating || isDeleting}
					>
						Edit
					</Button>
					<Button
						color="error"
						onClick={onDelete}
						disabled={isUpdating || isDeleting}
					>
						Delete
					</Button>
				</div>
			</div>

			<div className="mt-6 border-t border-gray-100">
				<dl className="divide-y divide-gray-100">
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Cover Photo
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{offer.coverPhotoPath && (
								<img
									src={getPhotoPath(offer.coverPhotoPath)}
									alt={offer.title}
									className="h-40 w-full object-contain rounded-lg"
								/>
							)}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Title
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{offer.title}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Description
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{offer.description}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Type
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{offer.type}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Price
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							${offer.price}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Status
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{offer.isActive ? "Active" : "Inactive"}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Created By
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{offer.createdById}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Updated By
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{offer.updatedById}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Created On
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{new Date(offer.createdOn).toLocaleDateString()}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Updated On
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{new Date(offer.updatedOn).toLocaleDateString()}
						</dd>
					</div>
				</dl>
			</div>
		</div>
	);
};
