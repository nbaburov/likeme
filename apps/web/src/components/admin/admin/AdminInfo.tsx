/* eslint-disable @next/next/no-img-element */
import { Button } from "@material-tailwind/react";
import { AdminResponse, UpdateAdminRequest } from "@/dto/AdminDTO";
import { useState } from "react";
import { EditAdminForm } from "./EditAdminForm";
import { IoIosArrowBack } from "react-icons/io";
import { getPhotoPath } from "@utils/photoPaths";

interface AdminInfoProps {
	admin: AdminResponse;
	onDelete: () => Promise<void>;
	onUpdate: (data: UpdateAdminRequest) => Promise<void>;
	onBack: () => void;
	isUpdating: boolean;
	isDeleting: boolean;
	handleUpload: (file: File) => Promise<string>;
	isUploading: boolean;
}

export const AdminInfo = ({
	admin,
	onDelete,
	onUpdate,
	onBack,
	isUpdating,
	isDeleting,
	handleUpload,
	isUploading,
}: AdminInfoProps) => {
	const [isEditing, setIsEditing] = useState(false);

	if (isEditing) {
		return (
			<div className="max-w-2xl mx-auto">
				<div className="flex justify-between items-center mb-6">
					<h3 className="text-base font-semibold leading-7 text-gray-900">
						Edit Admin
					</h3>
					<Button
						color="secondary"
						onClick={() => setIsEditing(false)}
						disabled={isUpdating || isDeleting}
					>
						Cancel
					</Button>
				</div>
				<EditAdminForm
					admin={admin}
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
						Admin Information
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
							Profile Photo
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{admin.profilePhotoPath && (
								<img
									src={getPhotoPath(admin.profilePhotoPath)}
									alt={admin.username}
									className="h-20 w-20 rounded-full object-cover"
								/>
							)}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Username
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{admin.username}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Email
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{admin.email}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Permissions
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{admin.permissions}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Status
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{admin.isActive ? "Active" : "Inactive"}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Created On
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{new Date(admin.createdOn).toLocaleDateString()}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Updated On
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{new Date(admin.updatedOn).toLocaleDateString()}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Last Login
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{admin.lastLoginOn
								? new Date(
										admin.lastLoginOn
								  ).toLocaleDateString()
								: "Never"}
						</dd>
					</div>
				</dl>
			</div>
		</div>
	);
};
