/* eslint-disable @next/next/no-img-element */
import { Button } from "@material-tailwind/react";
import {
	InfluencerResponse,
	UpdateInfluencerRequest,
} from "@/dto/InfluencerDTO";
import { useState } from "react";
import { EditInfluencerForm } from "./EditInfluencerForm";
import { IoIosArrowBack } from "react-icons/io";
import { useRouter } from "next/navigation";
import { getPhotoPath } from "@utils/photoPaths";
interface InfluencerInfoProps {
	influencer: InfluencerResponse;
	onDelete: () => Promise<void>;
	onUpdate: (data: UpdateInfluencerRequest) => Promise<void>;
	onBack: () => void;
	isUpdating: boolean;
	isDeleting: boolean;
	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
}

export const InfluencerInfo = ({
	influencer,
	onDelete,
	onUpdate,
	onBack,
	isUpdating,
	isDeleting,
	handleUpload,
	isUploading,
}: InfluencerInfoProps) => {
	const [isEditing, setIsEditing] = useState(false);
	const router = useRouter();

	if (isEditing) {
		return (
			<div className="max-w-2xl mx-auto">
				<div className="flex justify-between items-center mb-6">
					<h3 className="text-base font-semibold leading-7 text-gray-900">
						Edit Influencer
					</h3>
					<Button
						color="secondary"
						onClick={() => setIsEditing(false)}
						disabled={isUpdating || isDeleting}
					>
						Cancel
					</Button>
				</div>
				<EditInfluencerForm
					influencer={influencer}
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
						Influencer Information
					</h3>
				</div>
				<div className="flex gap-2">
					<Button
						color="secondary"
						onClick={() => setIsEditing(true)}
						disabled={isUpdating}
					>
						Edit
					</Button>
					<Button
						color="error"
						onClick={onDelete}
						disabled={isDeleting}
					>
						Delete
					</Button>
				</div>
			</div>

			<div className="mt-6 border-t border-gray-100">
				<dl className="divide-y divide-gray-100">
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Profile Photo
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.profilePhotoPath && (
								<img
									src={getPhotoPath(
										influencer.application.profilePhotoPath
									)}
									alt={influencer.application.username}
									className="h-20 w-20 rounded-full object-cover"
								/>
							)}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Cover Photo
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.coverPhotoPath && (
								<img
									src={getPhotoPath(
										influencer.application.coverPhotoPath
									)}
									alt="Cover"
									className="h-32 w-full object-cover rounded"
								/>
							)}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Id
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.id}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Application
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							<Button
								color="secondary"
								onClick={() =>
									router.push(
										`/admin/influencers/applications/${influencer.application.id}`
									)
								}
							>
								Go To Application
							</Button>
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Full Name
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.billingDetails.firstName}{" "}
							{influencer.application.billingDetails.lastName}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Username
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.username}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							About
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.about}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Email
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.email}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Phone Number
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.phoneNumber}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Instagram Handle
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.instagramHandle}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Address
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{
								influencer.application.billingDetails
									.streetAddress
							}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Country
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.billingDetails.country}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							City
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.billingDetails.city}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							State
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.billingDetails.state}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Zip Code
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.billingDetails.zipCode}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Instagram Connected
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.isInstagramConnected ? "Yes" : "No"}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Instagram Access Token
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.instagramAccessToken}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Application Status
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.application.isApproved
								? "Approved"
								: "Pending"}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Status
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.status}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Is Active
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.isActive ? "Yes" : "No"}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Account Updated
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{new Date(influencer.updatedOn).toLocaleString()}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Account Created
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{new Date(influencer.createdOn).toLocaleString()}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Last Login
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{influencer.lastLoginOn
								? new Date(
										influencer.lastLoginOn
								  ).toLocaleDateString()
								: "Never"}
						</dd>
					</div>
				</dl>
			</div>
		</div>
	);
};
